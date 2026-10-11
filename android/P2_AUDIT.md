# Android P2 — Bileşen ve ürün doğruluk denetimi

> Kapsam: güncel `origin/main` tabanında 11 Ekim 2026 bağımsız kod doğrulaması; önceki aşama bulguları aşağıda tarihsel kayıt olarak korunur.
> Kaynak veri: `plans/**` **değişmez**. Android uygulaması Kotlin + Jetpack Compose'tur.

## Ürün sınırları

Kişisel öğretmen uygulamasında yalnızca **Haftalık, Aylık, Temalar, İlerleme** ana bölümleri vardır. Yıllık, Karşılaştırma, Doğrulama & JSON, Rehber & SSS görünümleri, komutları ve ilgili ekran fonksiyonları kaldırılmıştır. `Signature Pad` kişisel haftalık ekranda P0 sırasında kaldırılmıştır. Kullanıcı notlarının geçmiş kayıtlarını silme veya kaynak plan JSON'larını değiştirme işlemi yapılmamıştır.

## Ölçüm metodolojisi

`COMPONENTS_MATRIX.md` tarihli envanterinde 70 bileşen ve 9 blok bulunur. Güncel Arc Library GitHub README'si 108 bileşen ve 22 blok bildirir; yerel envanter ile kaynak arasındaki sayısal fark 38 bileşen ve 13 bloktur. Adlandırma birebir olmadığı ve upstream kod React/web olduğu için bu fark bir eksik uygulama listesi veya parite kanıtı değildir.

11 Ekim 2026 kod taramasında 70/70 bileşen ve 9/9 blok için Android kaynak karşılığı bulundu. Statik çağrı taraması `PlannerScreen` girişinden 24/70 bileşeni doğrudan, 28/70 bileşeni doğrudan veya erişilebilir Compose çağrıları üzerinden üründe erişilebilir buldu; geri kalanlar yalnızca ortak kaynakta bulunuyor. Tarama dinamik çağrıları kanıtlamaz. Önceki rapordaki 25/70 sayısı güncel değildir.

Kaynakta bulunma, üründe erişilebilir olma, upstream ile davranış uyumu, otomatik test ve gerçek cihaz doğrulaması ayrı ölçümlerdir. Aşağıdaki özellik tablosu yalnızca bu istekte incelenen bileşenlerin kanıtını verir.

P2 kriterleri her önemli bileşen için ayrı doğrulanacak: 1) kaynak davranış karşılaştırması, 2) ürün içinde gerçek kullanım, 3) erişilebilirlik/klavye/dokunma, 4) kompakt ve tablet pencere testi, 5) CI birim testleri, 6) cihaz/emülatör Compose UI testleri.

## Bu PR'da yapılanlar

- `ArcSegmentedControl`: dört geçerli sekme, 48dp minimum seçme alanı, Tab rolü, yatay kaydırma
- `ArcCheckbox`: 48×48dp hedef, Checkbox rolü ve gerçek toggle semantiği
- `ArcExpandableCard` / `ArcAccordion`: 48dp açma alanı ve açık/kapalı erişilebilirlik durumu
- `ArcCalendar`: gün hücreleri 48dp yüksekliğinde; tablet yan panel için daha güvenli adaptif eşik
- `ArcCommandPalette`: Ctrl/Cmd+K, otomatik giriş odağı, kaynaktan üretilen hafta komutları; gereksiz komutlar kaldırıldı
- `ArcActivityHeatmap`: gerçek sınıf ilerlemesi, ekran genişliğine göre 5/10 sütun, 48dp hedef ve erişilebilir durum açıklaması
- `AnalyticsAndChartsView`: dar ekranda grafikler alt alta, geniş ekranda yan yana; ısı haritası İlerleme bölümünde
- Eski modları içeren `teacher-state.json` verisi notlar silinmeden Haftalık görünüme uyarlanır

## Üründen çıkarılan, kütüphane kodu silinmeyen bileşenler

- `JSON Viewer`
- `Signature Pad`
- `Sortable Data Table`
- `Timeline`
- `File Upload`
- `Changelog Feed`
- `Comparison Table`
- `FAQ Section`

## 11 Ekim 2026 — Arc entegrasyon bulguları

| Native uyarlama | Kaynakta var | Üründe kullanım | Arc davranışına göre durum | Otomatik kanıt | Gerçek cihaz |
|---|---|---|---|---|---|
| `ArcExpandingSearch` / `PlanSearchPanel` | Evet | `PlannerScreen` çalışma alanında | Upstream'ın yerel sonuç listesi yaklaşımına uyarlandı: plan haftaları, tema/konu/metin/kazanım/kod araması, hafta seçimi, boş durum, Escape ve temizleme. Upstream gruplama/anahtar sözcük sıralaması ve ok tuşuyla sonuç seçimi yok. | JVM plan arama testleri + Compose sonuç/boş durum testi | Galaxy/DeX doğrulaması yok |
| `PlanNotAvailable` | Evet | Haftalık görünümde | Gerçek eylem bulunmadığında eylem düğmesi gizleniyor; sahte plan üretimi veya sınıf değiştirme yok. | Compose boş durum testi | Galaxy/DeX doğrulaması yok |
| `ArcRichTextEditor` | Evet | Haftalık öğretmen notu | Seçim biçimlendirmesi, imleç koruması, Markdown görüntü stili ve Ctrl/Cmd+B/I eklendi. Markdown metin saklama biçimi korunuyor. Upstream HTML çıktısı, slash menüsü ve undo/redo eklenmedi. | Compose metin seçimi/biçimlendirme testi; mevcut Activity durdurma ve yeni repository kayıt testi | S-Pen ve TalkBack cihaz testi yok |
| `ArcResizablePanels` | Evet | Geniş tablet/DeX iki paneli | İki panel için dp sınırı, kaydedilen oran, dokunma/fare sürükleme, ok tuşları, Shift ile büyük adım, Home/End ve TalkBack ayarlama eylemleri var. Upstream'ın birden çok paneli, daraltma/geri yükleme, çift tık sıfırlama ve Escape ile sürükleme iptali yok. | Compose klavye/limit testi; önceki responsive pencere emülatör testi | Fiziksel tablet/DeX testi yok |
| `ArcSwipeActions` | Evet | Haftalık ders bölümleri | Eşik artık kayıt yapmıyor, yön eylemini açıyor; kullanıcı düğmeye dokunarak uygular. Ekran okuyucu eylemleri ve ders checkbox'ı alternatif. Upstream menü/full-swipe yaklaşımı yok. | Compose açma-onay/idempotent davranış testi + repository idempotence unit testi | Dokunma, TalkBack ve DeX cihaz testi yok |

### Test kapsamı ile ilgili sınırlar

- Bu dalda `testDebugUnitTest` (35 test), `assembleDebug` ve `assembleDebugAndroidTest` başarılıdır.
- `connectedDebugAndroidTest`, `SM-X230 - Android 16` cihazında 15/15 testi `No compose hierarchies found in the app` hatasıyla başarısız bitirdi; arama testlerini JUnit4 v2 ile tek başına çalıştırmak da aynı sonucu verdi. Test assertion'ları çalışmadığı için bu sonuç değişen etkileşimlerin başarısı veya başarısızlığı olarak yorumlanamaz. Cihaz loglarında uygulama çökmesi görülmedi; CI emülatör koşusu ayrıca gereklidir.
- Eski PR'larda geçen Actions bağlantıları bu dala ait test sonucu sayılmaz. PR Actions çalışmaları aşağıda eklenecektir.
- Responsive UI suite kompakt, portre ve yatay emülatör pencerelerinde çalışabilir; bu gerçek ekran döndürmenin, Samsung DeX'in, TalkBack'in veya S-Pen'in fiziksel cihazda doğrulandığını göstermez.
- 70 bileşenin tamamının upstream davranış paritesi test edilmemiştir. Güncel uygulama kapsamı 28/70 erişilebilir bileşendir; diğer bileşenler kullanım oranı için eklenmemiştir.

Bu bileşenlerin bazıları hâlen yeniden kullanılabilir `core/design` kaynakları olarak bulunabilir. Uygulamada varmış gibi belgelemek doğru değildir.

## Kalan P2 işleri

- 70 bileşenin tam davranış/erişilebilirlik eşdeğerliği **tamamlanmadı**. Öncelik gerçek kullanılan bileşenlerde
- Samsung DeX, Galaxy tablet yatay/dikey, çoklu pencere ve ekran okuyucu canlı UI testleri
- Sistem animasyon ölçeği sıfırlandığında azaltılmış hareket modu **uygulandı ve JVM/Compose UI düzeyinde test edildi**; gerçek Samsung cihazında ayarı canlı değiştirerek doğrulama ve performans ölçümü kaldı
- Karmaşık Arc bileşenleri için gerçek jest, odak, hata ve yeniden oluşturma testleri
- Kapsam matrisi satırlarını "kaynak var / üründe kullanılıyor / UI testli / cihaz testli" olarak etiketleyerek tam doğruluk

**Test notu:** `testDebugUnitTest assembleDebug` Gradle CI geçmesi derlenebilirlik ve birim testlerini kanıtlar; gerçek cihaz üzerinde fonksiyon ve erişilebilirlik eşdeğerliğini kanıtlamaz.

## P2 ikinci aşama — native hareket ve emülatör testi

- `AndroidMotionPreference`, Android'in `Settings.Global.ANIMATOR_DURATION_SCALE` değerini gözlemler; `ArcTheme` üzerinden `LocalReducedMotion` sağlar. Bu ölçek kapatıldığında içerik ve kontrol animasyonları anında son duruma geçer.
- `ArcExpandableCard`, `ArcAccordion`, `ArcTreeView`, `ArcCheckbox`, `ArcThemeSwitch`, `ArcSwipeActions` ve `ArcExpandingSearch` kullanıcı animasyon tercihine bağlandı.
- `PlannerWindowPolicy`, 950dp genişlik eşiği, 390dp yan panel, 480dp çalışma alanı ve 24dp erişilebilir panel ayırıcı koşullarını içerir. Panel ayırıcı fare/dokunma sürüklemesinin yanı sıra klavye sol/sağ tuşlarıyla da ayarlanabilir.
- `P2MotionAndWindowPolicyTest` ile animasyon/ekran ölçüsü politikası birim test edildi.
- `ArcInteractionTest` aracılığıyla dört gerçek Compose UI etkileşimi Android 35 Google APIs x86_64 emülatörde **çalıştırıldı**: dört sekmeli seçici, checkbox, açılır ders kartı, hafta ilerlemesi. [Emülatör Actions — başarılı](https://github.com/knigdelioglu/plan/actions/runs/38086715721)
- [Android CI — başarılı](https://github.com/knigdelioglu/plan/actions/runs/38086715735) içinde birim testleri ve debug APK derlemesi çalıştırıldı.

**Sınır:** Bu emülatör bir Samsung Galaxy tablet veya DeX oturumu değildir. DeX'in pencere davranışı, gerçek S-Pen, TalkBack ve performans testleri henüz tamamlanmış sayılmaz. Tüm 70 Arc bileşeninin davranış eşdeğerliği de bu testlerle kanıtlanmamıştır.

## P2 altıncı aşama — eşzamanlı yazma ve not kurtarma

- `NoteDraftBuffer` artık **iki kilit** kullanıyor: kısa süreli taslak kilidi ve ayrı yazıcı kilidi. AtomicFile yazması sürerken yazılan yeni harfler disk işleminin bitmesini beklemiyor.
- Yazma sürerken aynı sınıf/haftaya gelen daha güncel sürüm eski kayıt tamamlandığında **tampondan silinmiyor**. Birbirine paralel flush işlemleri yazıcı kilidiyle doğru sırada ilerliyor.
- Uygulama durdurulurken veya sınıf/hafta değiştirilirken devam eden sürümler tamamen diske geçirilene kadar flush yineleniyor; hata durumunda taslak elde tutuluyor ve ekran geçişi engelleniyor.
- `P2NoteDraftConcurrencyTest` geciktirilmiş disk, eşzamanlı düzenleme/flush ve 100 KB üstü başarısız kaydın yeniden denenmesini test ediyor.
- `P2NoteLifecycleTest`, Android Activity durmuşken yeni bir `TeacherRepository` açarak notun atomik kayıtta bulunduğunu denetliyor.
- `PlanTeacherAtomicWrite` sistem izleme işareti (Android Studio Profiler / Perfetto) eklendi. Bu, gerçek cihazda dosyaya yazma süresini ölçmek için altyapıdır; **ölçülmüş bir performans sonucu değildir**.

**Sınır:** Ani process kill/force-stop, 650 ms'lik bekleme sırasında Android'in lifecycle callback'lerini çalıştırmayabilir. Bu durumda henüz diske geçmemiş bir taslağın korunacağı iddia edilmiyor. Tam sıfır veri kaybı garantisi için duraklama tamponu yerine her düzenlemeyi kalıcı journal'a yazma gereklidir; bu da performans/güvenilirlik dengesini yeniden değerlendirmeyi gerektirir. Fiziksel Galaxy Tab A11+ üzerinde ölçüm yapılmadı.
