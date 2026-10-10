# Mobil Uygulama — Scope: mobile

Bu kurallar `mobile/**` içindeki bütün React Native + Expo geliştirmeleri için geçerlidir.

**Yazılabilir tek alan: `mobile/**`.** `../plans/**` salt okunur kaynak veridir. Yıllık plan JSON'larını, şemaları, raporları, inceleme kuyruklarını ve diğer kaynak arşiv dosyalarını değiştirme. Türetilmiş katalog, normalizasyon, veri okuma veya önbellekleme kodunu `mobile/**` içinde geliştir.

## Teknoloji ve amaç

- **React Native + Expo + TypeScript** ile sıfırdan geliştirme; Android ve iOS önceliklidir. Expo Router, EAS veya yerel veri kütüphanesini ihtiyaç doğrulandıktan sonra uygulama alanında yapılandır.
- Ürün: öğretmenlere ders/sınıf/okul türüne göre haftalık yıllık planları gösterme, aynı yıllık planı kullanan şubelerin ilerlemesini **ayrı** izleme, ders bölümü ve işlenen saat düzeyinde takip, öğretmen notları ve çevrimdışı kullanım.
- Arşivdeki 3+2 gibi aynı haftaya bağlı birden fazla ders bölümü korunmalı. Takvim haftası ile öğretmen ilerlemesi farklı kavramlardır. Kaynak şüpheli veya doğrulanmamışsa bunu sessizce doğru kabul edip yayımlama.
- Kişisel öğretmen verilerini plan JSON'larına veya depo içine yazma; uygun uygulama depolama katmanında sakla.
- Veri kaynağını düzeltmek gerekiyorsa sorunu raporla, ayrı bir `data` görevi bekle. Uygulama görevi sırasında `plans/**` veya paylaşılan kök dosyaları değiştirme.
- Önceki web uygulamasındaki kodu otomatik taşımaya çalışma; bu yeni **native Expo** uygulamasıdır. Arayüz ve gezinme React Native'e özgü hazırlanır.
- Değişiklik tesliminde dosya farklarını denetle; `plans/**` için sıfır yazma/değişiklik olmalı.

**Mevcut durum:** Sadece çalışma alanı ve talimatlar var. Expo projesi henüz başlatılmadı; `package.json`, `app.json` ve çalışan ekranlar henüz oluşturulmadı.
