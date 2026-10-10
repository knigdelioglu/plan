# Android / Expo kabul kontrol listesi

Durum: **Henüz fiziksel cihazda uygulanmadı.** İşaretlemeler yalnızca gerçek cihazda yapılmalıdır.

## Kurulum ve kaynak
- [ ] `pnpm install`, `pnpm run test`, `pnpm run typecheck`, `pnpm exec expo install --check` başarılı
- [ ] Android emülatörü ve gerçek tablette Expo açılıyor
- [ ] Uçak modunda, uygulama yeniden başlatıldığında kaynak yıllık planlar çalışıyor
- [ ] 11. sınıf Fen Lisesi: 37 hafta; 3+2 bölünmüş haftanın parçaları ayrı
- [ ] Kaynak içeriği doğrulanmamış 11. sınıf Anadolu Lisesi seçilemiyor
- [ ] 15 Temmuz anması kaynak haftasıyla uyumlu gösteriliyor

## Şubeler / ilerleme
- [ ] 11/A ve 11/B aynı planla ekleniyor; başlangıç haftaları farklı olabiliyor
- [ ] 11/A saat kaydı 11/B'ye yansımıyor
- [ ] 3 saatlik ve 2 saatlik bölümlerin kayıtları bağımsız ve sınırları korunuyor
- [ ] Ders notu kaydediliyor; uygulama kapatılıp açılınca aynı not geri geliyor
- [ ] Kayıt sırasında açık hafta başka haftaya kendiliğinden geçmiyor
- [ ] Aynı plan ve aynı adı taşıyan şube ikinci kez eklenemiyor
- [ ] Silme onayı iptal edilince veri kalıyor; onaylanınca yalnızca ilgili şube siliniyor
- [ ] Başlangıç haftasından önce işlenmiş saat varsayılmıyor

## Yedekleme
- [ ] Sınıflarım → Yedekle: gerçek JSON dosyası güvenli bir hedefe kaydediliyor
- [ ] Yedek sonrası şube/saat/not dosyasında beklenen sayılarda
- [ ] Yeniden kurulu temiz uygulamaya dosyadan geri yükleme yapılıyor
- [ ] Önizlemede şube ve ders kaydı sayıları gösteriliyor; vazgeçmek veriyi değiştirmiyor
- [ ] Doğrulanmamış plan, bozuk JSON, tekrarlanan kayıt ve saat aşımı reddediliyor
- [ ] Geri yükleme sonrası uygulama yeniden başlatıldığında tüm veriler kalıyor
- [ ] SQLite yazımı yarıda hata alırsa eski kayıtlar korunuyor (fault injection)
- [ ] Dışa aktarılan JSON dosyası **şifreli değil**; hassas notlar için güvenli depolama uyarısı görülüyor

## Tablet / erişilebilirlik
- [ ] Galaxy Tab A11+ yatay/dikey yerleşim ve ekran döndürme
- [ ] 200% yazı ölçeğinde kontrol kırpılmaları yok
- [ ] Açık/koyu mod; yatay sekme kaydırma
- [ ] TalkBack seçim ve işlenen saat düğmelerini doğru anons ediyor
- [ ] iOS VoiceOver; Reduce Motion / hareket azaltma
- [ ] Ekran dışına kayan klavye, buton dokunma alanı ve modal odak yönetimi
- [ ] Uzun tema başlıkları ve çoklu öğrenme çıktıları sığdırılıyor

## APK önizleme
- [ ] `eas build -p android --profile preview` başarılı ve APK kuruluyor
- [ ] Expo Go dışında imzalı APK'da SQLite yedekleme ve paylaşım çalışıyor
- [ ] Android 13/14/15/16 üzerinde dosya seçicinin içerik URI izinleri doğrulanıyor

Bu kontrol listesi bir test sonucu raporu değildir; uygulandığında cihaz, Android sürümü,
Expo SDK sürümü, APK commit SHA'sı ve PASS/FAIL kanıtlarıyla güncellenmelidir.
