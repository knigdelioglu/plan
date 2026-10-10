# Android P2 — Bileşen ve ürün doğruluk denetimi

> Kapsam: `feat/android-p2-tablet-a11y-simplification-20261010` (ilk P2 aşaması).
> Kaynak veri: `plans/**` **değişmez**. Android uygulaması Kotlin + Jetpack Compose'tur.

## Ürün sınırları

Kişisel öğretmen uygulamasında yalnızca **Haftalık, Aylık, Temalar, İlerleme** ana bölümleri vardır. Yıllık, Karşılaştırma, Doğrulama & JSON, Rehber & SSS görünümleri, komutları ve ilgili ekran fonksiyonları kaldırılmıştır. `Signature Pad` kişisel haftalık ekranda P0 sırasında kaldırılmıştır. Kullanıcı notlarının geçmiş kayıtlarını silme veya kaynak plan JSON'larını değiştirme işlemi yapılmamıştır.

## Ölçüm metodolojisi

`COMPONENTS_MATRIX.md` içindeki 70 Arc bileşeni ile `PlannerScreen.kt` içindeki doğrudan fonksiyon çağrılarını kıyasladık. Sonuç: **29/70** bileşen ana ekrandan doğrudan çağrılıyor; **50/70** doğrudan çağrılmıyor. Bu sayım bir bileşenin başka bir bileşen içinde dolaylı kullanılmadığını kanıtlamaz ve **Arc ile davranış eşdeğerliği testi değildir**. Kaynak dosyanın varlığı "uygulamada çalışıyor" anlamına gelmez.

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

Bu bileşenlerin bazıları hâlen yeniden kullanılabilir `core/design` kaynakları olarak bulunabilir. Uygulamada varmış gibi belgelemek doğru değildir.

## Kalan P2 işleri

- 70 bileşenin tam davranış/erişilebilirlik eşdeğerliği **tamamlanmadı**. Öncelik gerçek kullanılan bileşenlerde
- Samsung DeX, Galaxy tablet yatay/dikey, çoklu pencere ve ekran okuyucu canlı UI testleri
- Hareket azaltma/animasyon ölçeği sistem tercihi uyumu ile performans ölçümü
- Karmaşık Arc bileşenleri için gerçek jest, odak, hata ve yeniden oluşturma testleri
- Kapsam matrisi satırlarını "kaynak var / üründe kullanılıyor / UI testli / cihaz testli" olarak etiketleyerek tam doğruluk

**Test notu:** `testDebugUnitTest assembleDebug` Gradle CI geçmesi derlenebilirlik ve birim testlerini kanıtlar; gerçek cihaz üzerinde fonksiyon ve erişilebilirlik eşdeğerliğini kanıtlamaz.
