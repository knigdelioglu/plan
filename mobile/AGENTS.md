# Mobil uygulama — Scope: mobile (React Native + Expo)

**Yazma izni: sadece `mobile/**`.** `plans/**` kesinlikle salt okunurdur.
JSON arşivi, şemalar, raporlar ve kuyruklar bu kapsamda taşınamaz, değiştirilemez,
biçimlendirilemez. Gerekli veri uyarlamaları sadece `mobile/scripts/**` ve
`mobile/src/**` altında yapılır.

- Uygulama React Native + Expo + TypeScript; Android/iOS öncelikli.
- Navigasyon `expo-router`, yerel kişisel kayıt `expo-sqlite`.
- Görsel bileşenleri uygulama ekranlarında `src/components/ui.tsx` içindeki
  uyarlayıcılar üzerinden kullan; Native Motion paketiyle doğrudan entegrasyon
  ancak uygun adapter yoksa ve açık sözleşmesi mevcutsa yapılır.
- Native Motion bağımlılığı monorepo alt paketi için sabit Git commit'ine pinlidir.
  Güncellemede önce API uyumluluğunu ve Expo sürümünü doğrula; değişiklikleri
  yalnızca `mobile/**` içinde tut.
- Aynı plandaki şubeler birbirinden bağımsız; `classId + weekNumber + lessonIndex`
  ders kaydı kimliğidir. `3+2` ders ayrımı ve sınıf başlangıç haftası korunur.
- Gerçek SQLite yazımı başarılı olmadan "Kaydedildi" durumunu gösterme.
- Anadolu Lisesi 11. sınıf JSON'u kaynak içerik doğrulaması tamamlanana dek
  yayımlanmaz. Kaynak farklarını `plans/**` içinde otomatik onarma.
- Kullanıcıya ait kayıtlar katalog JSON'una veya Git'e yazılmaz, yerel SQLite'da kalır.
- Yedekleme `src/features/backup/**` içinde sürümlü JSON sözleşmesi kullanır; geri yükleme bütün kayıtları **önce** doğrulamalı, kullanıcı onayından sonra **tek atomik transaction** ile değiştirmelidir. Başarı, veritabanı işlemi bitmeden bildirilmez. Kişisel yedek dosyaları şifreli değildir; otomatik paylaşım yapma.
- Expo arayüzlerini gerçek Android tablet/iOS ile test etmeden üretim onayı verme;
  Native Motion deneysel, Reduce Motion/RTL/erişilebilirlik ayrıca incelenmeli.
- Görev sonunda `git diff --name-only` ile `plans/**` verilerinin değişmediğini kontrol et.

Kök [AGENTS.md](../AGENTS.md) ve [SCOPES.md](../SCOPES.md) ortak kuralları geçerlidir.
