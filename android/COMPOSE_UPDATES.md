# Plan ↔ Compose Design System entegrasyonu

Plan, ortak kütüphanenin Kotlin/Jetpack Compose kaynaklarını derleme sırasında kullanır. Uygulama bileşenleri `com.plan.app.core.design` altında uyumluluk katmanları üzerinden erişir.

- **Tek commit kaynağı:** `.github/compose-design-system.ref` (40 karakterli sabit commit SHA).
- **Tek checkout/yol yapılandırması:** `.github/actions/checkout-compose-design-system/action.yml`. CI, Android UI testleri ve APK yayımı aynı action'ı çağırır.
- **CI'de kaynak dizini:** checkout'tan sonra `ORG_GRADLE_PROJECT_composeDesignSystemSources` Gradle özelliği otomatik ayarlanır.
- **Yerel geliştirme:** `plan` ve `compose-design-system` depoları kardeş klasörlerse Gradle'ın `../../compose-design-system/design-system/src/main/java` varsayılanı kullanılabilir. Farklı konumda `-PcomposeDesignSystemSources=<dizin>` verilir.

## Güncelleme kuralı

Sadece pin dosyasını yeni doğrulanmış **tam commit SHA** ile güncelle. Android derleme/birim testleri ve üç farklı ekran boyutundaki UI testleri başarılı olmadan ana dala alma; daha sonra yeni APK gerekiyorsa sürüm numarasını ve release akışını yükselt.

## 12 Ekim 2026

`f43ef2d0302fee1d5cfa50bd089441f96fe21018` ile yuvarlatılmış kontrollerin ripple sınırları ve gereksiz dikdörtgen dokunma efektleri düzenlendi. Plan'ın ortak `ArcCheckbox`, `ArcSelect` ve diğer kaynak bileşenleri bu sürümden derlenir. Plan'ın yerel `ArcSwitch` bileşeninde de yuvarlatılmış etkileşim alanı korunur. Tasarım farkları ve sistemin yönettiği alanlar için ayrıca cihaz testi gereklidir.
