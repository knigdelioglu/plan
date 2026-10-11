# Compose Design System → Plan uygulaması entegrasyon raporu

**Tarih:** 11 Ekim 2026  
**Durum:** İlk entegrasyon uygulandı ve derlendi.

## Yapılanlar

`compose-design-system` deposunun Compose kaynakları Plan uygulamasının Android modülüne bağlandı. Tema, `MainActivity` içinde dış katmana eklendi; Plan’ın mevcut teması içeride bırakıldığı için uygulama renkleri, tipografisi ve açık/koyu tema tercihi çalışmayı sürdürüyor. Kütüphane nötr vurgu paletiyle kullanılıyor.

Plan’daki mevcut bileşen API’lerini koruyan ince uyarlamalarla şu kütüphane bileşenleri kullanılıyor:

- `ArcButton`, `ArcActionButton` ve `ArcCard`
- `ArcCheckbox`, `ArcInput` tabanlı arama alanı ve `ArcSelect`
- `ArcSegmentedControl`, `ArcDialog` ve `ArcAlert`
- `ArcBadge` (uygulamanın outline ve nokta seçenekleri yerel çizimde tutuluyor)
- `ArcStatsBand` / `ArcStatItem`
- `ArcScheduleCalendar`

Takvim adaptörü `PlanWeek` tarihlerini, hafta numaralarını, özel gün işaretlerini ve `AcademicCalendarBreak` aralıklarını kütüphanenin UI modellerine dönüştürüyor. Kaynak JSON veya plan modelleri değiştirilmedi. İstatistik bandında doğrulanmış ve varsayımsal ders saatleri ayrı metinlerle gösteriliyor.

## Bağımlılık ve sürüm kararı

Kütüphanede Maven yayın koordinatı bulunmadığı ve kütüphane modülünün AGP 8.13.2 yapılandırması uygulamanın Gradle 9.7.1 / AGP 9.4.0 araç zinciriyle doğrudan uyumlu olmadığı için Gradle composite build veya ayrı Android library modülü kullanılmadı. Uygulama, kütüphanenin `design-system/src/main/java` Kotlin kaynaklarını AGP 9’un `kotlin.directories` kaynağı olarak derliyor; Compose sürümleri Plan uygulamasının BOM’u tarafından sağlanıyor.

Varsayılan yol `../../compose-design-system/design-system/src/main/java` şeklinde, iki deponun yan yana bulunduğu mevcut çalışma alanına göredir. Başka bir checkout konumunda yol şu Gradle özelliğiyle değiştirilebilir:

```shell
./gradlew :app:assembleDebug -PcomposeDesignSystemSources=/tam/yol/compose-design-system/design-system/src/main/java
```

Kaynak dizini bulunamazsa Gradle artık hangi özelliğin ayarlanacağını belirten hata veriyor. Bu kurulum her iki deponun da çalışma alanında bulunmasını gerektirir; henüz sürümlenmiş Maven bağımlılığı değildir.

## Şimdilik uygulamada kalanlar

- Not editörü; öğretmen notu taslak yaşam döngüsü, metin seçimi/Markdown kısayolları ve mevcut test etiketi korunuyor.
- Bölme paneli; saklanan `sidebarRatio` ve dp tabanlı minimum genişlikler kütüphane API’sinde karşılanmadığı için korunuyor.
- Bildirim merkezi; kapatma/temizleme eylemleri uygulamaya özel.
- Komut paleti; ikonlu komutlar ve odak yönetimi mevcut ürün akışına bağlı.
- İlerleme grafikleri, doğrulanmış/varsayımsal saat ayrımı ve yıllık hafta ısı haritası; kütüphane görsellerinin veri anlamı mevcut ekranla bire bir örtüşmediği için korunuyor.

Kütüphanedeki OTP, parola, imza, faturalama ve benzeri bileşenler Plan’daki yıllık planlama iş akışında karşılık bulmadığından eklenmedi. Yeni bileşen eklemek için yalnızca sayıyı artırmak, uygulamaya gereksiz form akışları katardı.

## Doğrulama

- `:app:assembleDebug --offline` — başarılı; debug APK üretildi.
- `:app:assembleDebugAndroidTest --offline` — başarılı; Android test APK’sı derlendi. Testler çalıştırılmadı ve emülatör açılmadı.
- Debug APK bağlı Galaxy Tab’a ADB üzerinden yüklendi; uygulama başlatma isteği gönderildi. Tablet o sırada uyku durumundaydı; bu nedenle canlı ekranda görsel kontrol tamamlanmadı. Son AndroidRuntime hata günlüğünde uygulama çökmesi görülmedi.
- Derleme, kütüphane kaynaklarındaki birkaç Compose deprecated API uyarısı verdi; hata üretmedi.

## Değişen dosyalar

- `app/build.gradle.kts` — kütüphane Kotlin kaynak dizini ve konum özelliği.
- `app/src/main/java/com/plan/app/MainActivity.kt` — tema köprüsü.
- `app/src/main/java/com/plan/app/core/design/components/core/ArcButton.kt`
- `app/src/main/java/com/plan/app/core/design/components/core/ArcCards.kt`
- `app/src/main/java/com/plan/app/core/design/components/core/ArcControls.kt`
- `app/src/main/java/com/plan/app/core/design/components/core/ArcIndicators.kt`
- `app/src/main/java/com/plan/app/core/design/components/core/ArcCalendar.kt`
- `app/src/main/java/com/plan/app/core/design/blocks/ArcStatsBand.kt`

Kök plan JSON’ları ve `compose-design-system` deposu değiştirilmedi. Lisans engeli veya ek atıf zorunluluğu varsayılmadı; iki deponun da kullanıcıya ait olduğu bilgisi esas alındı.
