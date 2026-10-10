# Veri Alanı — Scope: data

Bu dosya `plans/**` kapsamındaki bütün yıllık plan dosyaları için geçerlidir.

**Yazma izni:** Yalnızca `plans/**` altındaki veri arşivi dosyaları; kullanıcı açıkça bir veri görevi vermiş olmalıdır.

**Kesin sınır:** Yıllık plan, kaynak Excel/PDF dönüşümü, JSON Schema, `validation-report.md` veya `review-queue.json` üzerinde çalışırken **`pwa/**` altında hiçbir dosyayı değiştirme**. PWA bileşenlerini, kodunu, yapılandırmasını, testlerini ve çıktılarını otomatik olarak senkronize etme.

Kökteki `AGENTS.md` içindeki **Yıllık Plan Aktarım İlkeleri** veri işlerinin değişmez kurallarıdır; kaynak sadakati, birleştirilmiş hücreler, ders saati ayrımları, özel günlerin yerleşimi ve doğrulama kayıtlarını koru.

Kök düzeydeki ortak politika ve CI dosyalarını da veri işi bahanesiyle değiştirme. Ayrı PWA geliştirme ihtiyacı varsa bunu bu görevden bağımsız bir çalışma olarak ele al.
