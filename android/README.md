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

## Kişisel veri güvenliği ve APK yükseltme

- **Ayarlar → Yedeği dışa aktar:** Sınıflar, notlar, işlenen dersler ve tercihler kullanıcı tarafından seçilen konuma JSON dosyası olarak yazılır.
- **Ayarlar → Yedekten geri yükle:** JSON dosyası seçilir; mevcut kayıtlar değiştirilmeden önce açık onay istenir. Desteklenmeyen sürüm ve geçersiz sınıf kimlikleri reddedilir.
- Ana kayıt ve kurtarma kopyası birlikte okunamıyorsa uygulama **salt okunur kurtarma ekranı** açar: gezinme ve düzenleme eski dosyaların üzerine yazmaz. Yalnızca doğrulanan bir kişisel veri yedeği geri yüklendikten sonra normal kullanım devam eder. Geri yükleme önce diske kaydedilir, sonra ekrandaki notlar ve sınıf durumları değiştirilir.
- Yedekten geri yükleme 10 MiB karakter sınırıyla okunur; aşırı büyük dosyalar belleğe bütünüyle alınmadan reddedilir.
- Normal kaydetme işlemleri tek iş parçacıklı bir IO yazıcısında sıralanır. Her başarılı kayıttan sonra `teacher-state.json.recovery.json` adlı kurtarma kopyası da atomik olarak yenilenir. Okunamayan asıl kayıt üzerine yazmadan önce özgün dosyanın `.corrupt-...` kopyası korunur.
- **Önemli:** v1.0.5 ve önceki test APK'ları debug anahtarıyla imzalanmış olabilir. Farklı imzalı APK mevcut uygulamanın üzerine kurulamaz. Önce yedek dışa aktarılmalı, ardından eski APK kaldırılıp imzalı sürüm kurulmalı ve yedek geri yüklenmelidir. Android uygulamayı kaldırırken yerel verileri silebilir.

### Eski debug APK'da yedek düğmesi yoksa

Yeni, farklı imzalı APK'yı yükleyebilmek için **eski uygulamayı kaldırmadan önce** kişisel veriyi yedeklemek gerekir. Cihazdaki mevcut kurulum `debuggable` ise USB hata ayıklama ve Android SDK Platform Tools ile şu salt okunur komut kullanılabilir:

```bash
adb exec-out run-as com.plan.app cat files/teacher-state.json > plan-ogretmen-yedegi.json
python3 -m json.tool plan-ogretmen-yedegi.json > /dev/null
```

İşlem başarısızsa veya JSON doğrulanamıyorsa uygulamayı kaldırmayın. Önce mevcut imza ile uyumlu bir APK üzerinden yedekleme sağlanmalıdır. Başarılı yedeği güvenli bir konumda saklayın; yeni uygulamanın Ayarlar ekranında **Yedekten geri yükle** seçeneğini kullanın.

## Güvenli dağıtım

GitHub Release artık yalnızca **kalıcı anahtarla imzalanmış release** APK için kullanılacaktır. Yeniden üretilebilir CI testleri için `assembleDebug` kullanılabilir; debug APK'ları kullanıcı güncelleme kanalı olarak yayımlanmaz.

Depo yöneticisi GitHub Actions Secrets bölümünde şu gizli değerleri tanımlamalıdır:

- `PLAN_SIGNING_KEYSTORE_BASE64`: Aynı kalıcı JKS/keystore dosyasının Base64 karşılığı.
- `PLAN_SIGNING_KEY_ALIAS`
- `PLAN_SIGNING_STORE_PASSWORD`
- `PLAN_SIGNING_KEY_PASSWORD`

Bu gizli değerler **Git deposuna, issue'ya veya PR'a yazılmamalıdır**. Kurulumdan sonra Actions repository variable `PLAN_SIGNED_RELEASES_ENABLED=true` ayarlanmalıdır. Bu bayrak kapalıyken yayımlama işi atlanır; imzasız veya geçici anahtarlı APK için sessiz geri dönüş yoktur. Anahtar güvenli bir yerde yedeklenmelidir; imza anahtarının kaybı mevcut kurulumların üzerine güncelleme yapmayı engeller.

`main` dalında zorunlu kontrol önerileri: **Android CI**, **Android UI and Accessibility** (üç ekran profili), en az bir PR incelemesi ve doğrudan push engeli. GitHub branch protection / ruleset, depo yöneticisi tarafından GitHub Settings üzerinden etkinleştirilmelidir.

## Mimari

- `plans/**` salt okunur kaynak arşividir; uygulama kişisel verileri bu dizine yazmaz.
- `PlanRepository` varlık ve JSON kataloglarını önbellekler; `PlannerViewModel` planı IO iş parçacığında yükler.
- `TeacherRepository` tek bir sürümlü JSON görüntüsünü, sıralı yazar ve atomik kurtarma kopyasıyla saklar.
- Compose Design System tema tokenlarının ana kaynağıdır. Uygulamadaki `core/design/theme/ArcTheme`, geriye dönük uyumluluk adaptörüdür; ikinci bir MaterialTheme oluşturmaz.
- Ana plan arayüzü, ayrı öğretmen notu ve analitik bileşenleriyle bölünmüştür.
