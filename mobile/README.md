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
npx expo install --check
```

### Native Motion bağımlılığı

Uygulama tasarımı `@native-motion/react-native` paketinin **doğrudan kaynak kodunu** kullanır.
Paket GitHub monoreposunun `packages/react-native` alt dizininden, **sabit commit** üzerinden
pnpm Git subdirectory protokolüyle kurulur. Bu sayede kütüphanenin yeni sürümlerine geçmek
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

**Sınır:** Henüz bulut senkronizasyonu, veri dışa aktarma/yedekleme ve hesap
sistemi yoktur. Uygulamayı silmek veya cihaz verilerini temizlemek ilerleme
kayıtlarını kaybettirebilir. Bu nedenle üretim kullanımından önce yedekleme
eklenmelidir.

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
