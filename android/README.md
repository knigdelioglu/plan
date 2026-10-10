# Plan — Android Native Uygulaması (Kotlin + Jetpack Compose)

`plan` deposundaki yıllık ders planı JSON arşivini öğretmenlerin günlük kullanımına uygun, etkileşimli haftalık ders planlarına dönüştüren tamamen yerel (native) Android uygulamasıdır.

Tasarım sistemi olarak [Arc Library](https://github.com/kuratlielia/arc-library) (MIT Lisansı, Telif Hakkı © 2026 Elia Kuratli) Jetpack Compose'a yerel olarak uyarlanmıştır.

## Özellikler

- **Tablet Öncelikli & Adaptif Tasarım:** Samsung DeX, katlanabilir cihazlar, tablet (yatay/dikey) ve telefonlar için dinamik adaptif yerleşim. `ArcResizablePanels` ile sürüklenebilir çok bölmeli çalışma alanı.
- **70 Arc Bileşeni:** 30 Çekirdek, 25 Gelişmiş Öğretmen İş Akışı ve 15 Genişletilmiş Özellik bileşeni.
- **9 Hazır Arayüz Bloğu:** Page Header, Command Palette (Cmd+K), Empty States, Notification Center, File Upload, Changelog Feed, Comparison Table, Stats Band, FAQ Section.
- **Kaynak Sadakati:** Depodaki 2026–2027 Türk Dili ve Edebiyatı 11. Sınıf Anadolu ve Fen Lisesi planlarının 37 haftası ve 45 ders bölümü kayıpsız okunur. 180 doğrulanmış saat ve 5 varsayımsal saat net olarak ayrılır.
- **3+2 Saat Dağılımı:** Okuma ve Edebiyat Atölyesi (Yazma/Konuşma) gibi çok bölümlü haftalar bağımsız alt ders kayıtları olarak gösterilir.
- **15 Temmuz Açılış Anması:** 15 Temmuz Demokrasi ve Millî Birlik Günü'nün eğitim yılı açılış haftasına (14-18 Eylül) planlı yerleşimi takvim uyuşmazlığı olarak etiketlenmeden doğru açıklamayla sunulur.
- **Kişisel Veri İzolasyonu:** Öğretmenin ders tamamlama işaretlemeleri, zengin metin notları ve kalemle (stylus/S-Pen) çizilmiş el yazısı notları cihazda yerel saklanır; kaynak `plans/**` arşivine dokunulmaz. Sınıflar arası ilerleme birbirine karışmaz.
- **Fiziksel Klavye & Fare Desteği:** `Cmd+K` / `Ctrl+K` ile açılan Command Palette, yön tuşlarıyla gezinme, hover efektleri.

## Derleme ve Test

Android dizinine geçin:
```bash
cd android
```

Birim testlerini ve JSON doğrulama testlerini çalıştırın:
```bash
./gradlew test
```

Debug APK üretin:
```bash
./gradlew assembleDebug
```
Üretilen APK: `android/app/build/outputs/apk/debug/app-debug.apk`

Ayrıntılı bileşen listesi ve kullanım alanları için [`COMPONENTS_MATRIX.md`](COMPONENTS_MATRIX.md) dosyasına bakınız.
