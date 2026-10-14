# Plan ↔ Compose Design System entegrasyonu

## Güncel durum — 14 Ekim 2026

Plan, varsayılan olarak **`com.knigdelioglu:compose-design-system:0.1.2`** yayımlanmış Maven AAR paketini kullanır. Sürüm, `android/gradle.properties` içindeki `composePublishedVersion=0.1.2` özelliğiyle sabitlenmiştir. Paket deposu `android/settings.gradle.kts` içindeki `maven-repository` URL'sinden çözülür.

```bash
cd android
./gradlew testDebugUnitTest assembleDebug
```

`android/app/build.gradle.kts`, yayımlanmış sürüm ayarlı olduğunda Compose kaynak klasörünü `sourceSets` içine **eklemez**: aynı sınıfların kaynak koddan ve AAR'dan birlikte derlenmesini engeller. Kaynak revizyonu `.github/compose-design-system.ref` içinde yalnızca geçmişteki kaynak-temelli iş akışı ve olası geri dönüş için tutulmaktadır; **0.1.2 paket sürümü bu SHA ile temsil edilmez.**

Mevcut GitHub Actions kontrol iş akışları kaynak checkout adımını hâlâ çalıştırabilir, ancak Gradle 0.1.2 AAR'ı kullanır. Yayımlanmış paket için Android CI ve üç ekran profilli UI testlerinin sonuçları PR kontrollerinde ayrıca doğrulanmalıdır.

**Geçici geri dönüş:** `android/gradle.properties` içindeki `composePublishedVersion` satırını kaldırıp `bash android/scripts/prepare-compose-design-system.sh` çalıştırın. Bundan sonra kaynak-temelli Gradle yolunun SHA kontrolü yeniden devreye girer. Kalıcı geri dönüş için ilgili değişiklik commit'i geri alınmalıdır.

**Sürüm yükseltme:** Doğrulanmış yeni Maven sürümüne geçerken yalnızca `composePublishedVersion` değerini değiştirin, CI + UI testlerini çalıştırın. Uygulama için yeni bir APK yayımlanacaksa Android uygulama sürümünü ayrıca artırın. Compose deposuna yeni bir commit veya yeni sürüm gelmesi Plan'ı kendiliğinden yükseltmez.

---

## Önceki kaynak-temelli entegrasyon (tarihçe)

Plan'ın Compose kaynakları `.github/compose-design-system.ref` dosyasındaki **tam commit SHA** ile sürümlenir. Bu revizyon Plan'ın Android CI, emülatör testleri ve APK yayımlamasında aynı olmalıdır.

## CI ve GitHub Actions

- `.github/actions/checkout-compose-design-system/action.yml` tek checkout işlemi olarak kullanılır; ref dosyasını okur, gerçek checkout commit'ini kontrol eder ve `ORG_GRADLE_PROJECT_composeDesignSystemSources` değişkenini ayarlar.
- Android CI ve üç emülatör testi ref veya checkout action değişince çalışır.
- Release iş akışı **APK'nın `output-metadata.json` dosyasından sürüm numarasını okur**. Artık tag, APK ismi ve SHA-256 dosya adı workflow'a ayrı ayrı yazılmaz.
- Aynı sürüm etiketi daha önce yayımlandıysa yeniden yayımlama **hata vererek durur**. Yeni paket için `android/app/build.gradle.kts` içindeki `versionCode` ve `versionName` artırılır.
- `main` dalındaki uygulama sürümü veya release workflow'u değişince yayımlama tetiklenir; elle çalıştırmak için `workflow_dispatch` de desteklenir.

## Yerel geliştirme

**Önerilen kullanım** (Plan deposunun kök klasöründen):

```bash
bash android/scripts/prepare-compose-design-system.sh
cd android
./gradlew testDebugUnitTest assembleDebug
```

Hazırlama betiği ortak Compose deposunun **ayrı ve izole** kopyasını `android/.compose-deps/` altında hazırlar, kirli çalışma alanının üzerine yazmaz ve pin dosyasındaki SHA'yı doğrular. Bu klasör Git tarafından izlenmez.

Gradle önce bu sabitlenmiş kopyayı bulur; yoksa eski kardeş `compose-design-system` deposunu kullanmayı dener. **Kaynak yolu ayrıca belirtilmemişse** Gradle git HEAD ile pin dosyasını karşılaştırır. Yanlış sürümle yerel APK üretilmesine izin verilmez.

Başka bir kaynağı özellikle denemek için `-PcomposeDesignSystemSources=<tam-kaynak-yolu>` kullanılabilir. Bu açık tercih, git SHA kontrolünü devre dışı bırakır; deneysel derleme ile doğrulanmış sürümü karıştırmayın.

## Güncelleme

1. `.github/compose-design-system.ref` dosyasına doğrulanmış yeni Compose commit SHA'sını yaz.
2. Android CI ve telefon/dikey tablet/yatay tablet testlerini kontrol et.
3. Dağıtılacak değişiklikler için Android sürüm numarasını artır; Release yeni sürümü APK'dan otomatik okuyacaktır.

**Otomatik yükseltme yoktur:** Compose deposuna yeni commit atılması Plan'ın pin dosyasını kendiliğinden değiştirmez. Bu yaklaşım, doğrulanmamış tasarım değişikliklerinin üretim APK'sına sessizce geçmesini önler.

## 12 Ekim 2026

`f43ef2d0302fee1d5cfa50bd089441f96fe21018`: yuvarlatılmış kontrollerin dokunma/ripple sınırları düzeltildi. Plan'ın ortak `ArcCheckbox`, `ArcSelect` ve diğer kaynak bileşenleri bu sürümden derlenir. Plan'ın kendi `ArcSwitch` uygulamasında da yuvarlatılmış etkileşim sınırı bulunur. Ripple görünümünün gerçek piksel sonucu emülatör/cihaz üzerinde ayrıca incelenebilir.
