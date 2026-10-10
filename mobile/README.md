# Haftalık Plan · Expo

Türk Dili ve Edebiyatı öğretmenleri için **React Native + Expo (Android / iOS)** uygulaması.
Bu kod **`mobile/**`** alanındadır. Yıllık plan arşivi **`../plans/**`** salt okunurdur.

## Kurulum

Node.js 22+ ve pnpm 10 önerilir. Depo kökünden:

```sh
cd mobile
npx pnpm@10.19.0 install
npx pnpm@10.19.0 run start
```

Expo Go ile Android/iOS'ta açın veya geliştirme derlemesi kullanın. Ayrı Android/iOS paketlemek için EAS kurulumu ve uygulama imzalama ayrıca gerekir; henüz APK/IPA yayımlanmamıştır.

```sh
npx pnpm@10.19.0 run test
npx pnpm@10.19.0 run typecheck
npx pnpm@10.19.0 exec expo install --check
```

### Native Motion bağımlılığı

Uygulama tasarımı `@native-motion/react-native` paketinin **doğrudan kaynak kodunu** kullanır.
Paket GitHub monoreposunun `packages/react-native` alt dizininden, **sabit commit** üzerinden
pnpm Git subdirectory (`&path:/packages/react-native`) protokolüyle kurulur. Bu sayede kütüphanenin yeni sürümlerine geçmek
yalnızca commit referansını değiştirmeyi ve UI uyarlayıcılarını doğrulamayı gerektirir.

- Kaynak: [knigdelioglu/native-motion](https://github.com/knigdelioglu/native-motion)
- Pin: `802fed948881e149d5f26cd367b05740db7733bd`
- Kullandığımız bileşenler: `MotionButton`, `MotionIconButton`, `MotionSegmentedControl`,
  `MotionExpandableCard`, `MotionProgress`, `MotionStatusIndicator`,
  `MotionTextField`, `MotionSearchField`, `MotionSelect`.
- Ortak ekran yüzeyleri `src/components/ui.tsx` içinde adaptörlerle sunulur. Uygulamaya özgü
  SQLite, rota ve veri iş kuralları Native Motion'a yazılmaz.
- Native Motion halen deneysel; büyük yazı, RTL, TalkBack/VoiceOver, animasyonlar ve fiziksel
  cihaz etkileşimleri için ayrı QA gerekir.

### Veriler

`scripts/build-catalog.mjs`, yıllık plan arşivini yalnızca **okuyarak**
`src/features/catalog/catalog.json` dosyasını üretir. Bu dosya uygulamaya
dahildir; cihaz açıldığında ağ bağlantısı gerekmez. Kaynak/okul türü uyuşmazlığı,
eksik doğrulama ve hatalı ders saatleri bulunan planlar seçim listesine alınmaz.
Başlangıç durumunda 11. sınıf Fen Lisesi planı erişilebilirdir; Anadolu Lisesi
planı RQ-013 nedeniyle doğrulama beklemektedir. 15 Temmuz okul açılış haftası
anması veri hatası olarak değerlendirilmez.

Kataloğu yenilemek için:

```sh
npx pnpm@10.19.0 run catalog
npx pnpm@10.19.0 run test
```

Şubeler ve kişisel ders kayıtları `expo-sqlite` ile cihazın **uygulamaya özel SQLite**
veritabanında tutulur; `plans/**` veya katalog JSON'una yazılmaz.
Şubeler aynı planı paylaşsa bile saatleri/notları farklıdır. Bölünmüş
haftalarda her ders bölümü ayrı kaydedilir. Seçilen başlangıç haftasından
öncesi işlenmiş kabul edilmez.

### Manuel yedekleme ve geri yükleme

**Sınıflarım → Yedekleme ve geri yükleme** alanından:
1. **Yedeği dışa aktar:** Şube ve ders ilerlemelerini JSON biçiminde paylaşım ekranına verir; dosyayı Dosyalar/Drive gibi güvenli bir yere siz kaydedersiniz. Paylaşım ekranının kapanması, hedefte gerçekten saklandığını kanıtlamaz.
2. **Dosyadan geri yükle:** Dosyayı önce bütünlük ve plan doğrulamasından geçirir, etkilenecek şube/kayıt sayılarını gösterir ve açık onay ister. **Mevcut yerel kayıtların tamamını değiştirir.** İşlem SQLite transaction'ıyla yürütülür; hata durumunda geri alınır.

JSON yedeği **şifreli değildir** ve kişisel ders notlarını içerir. Güvenli bir yerde saklayın; başkalarıyla paylaşmayın. Doğrulaması tamamlanmamış veya artık katalogda bulunmayan plana ait yedekler veri kaybını önlemek amacıyla reddedilir.

**Sınır:** Henüz otomatik/bulut eşitlemesi ve kullanıcı hesabı yoktur. Uygulamayı veya cihaz verilerini silmeden önce yedek dosyasının gerçekten kaydedildiğini doğrulayın.


### Android APK önizlemesi

`eas.json` içinde **preview** profili Android APK üretecek şekilde yapılandırıldı.
Bu repo değişikliği tek başına bir APK veya GitHub Release oluşturmaz; Expo/EAS
hesabıyla giriş yapıp bulutta bir derleme başlatılması gerekir:

```sh
cd mobile
npx eas-cli login
npx eas-cli build --platform android --profile preview
```

Üretilen APK'yı gerçek Android cihazda test edip sonra paylaşın. Şu anda fiziksel
Android/iOS cihaz smoke testi ve tam TypeScript derlemesi bağımlılıklar kurulamadığı
için doğrulanmış değildir. Projedeki testler veri iş kuralları ve yedek doğrulama
senaryolarını kapsar; cihaz davranışlarının yerine geçmez.

### Geliştirme alanı

```text
mobile/
├── app/                         Expo Router tab ekranları
├── src/app/AppProvider.tsx       Yerel sınıf/ilerleme durumu
├── src/components/ui.tsx        Native Motion adaptörleri
├── src/features/catalog/        Derlenmiş salt okunur planlar
├── src/features/progress/       Hesaplama ve şube ilerleme kuralları
├── src/features/weeks/          Haftalık ders/çıktı/not arayüzü
├── src/lib/storage/database.ts  Yerel SQLite
├── src/theme/                   Native Motion tasarım token'ları
├── scripts/build-catalog.mjs    Planları yalnızca okuyan dönüştürücü
└── tests/                       Kaynak güvenliği ve ilerleme testleri
```

Deponun kapsam sınırları: [AGENTS.md](AGENTS.md), [SCOPES.md](../SCOPES.md).
