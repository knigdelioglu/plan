# Çalışma Alanı Sınırları

`knigdelioglu/plan` şu anda **yalnızca yıllık plan veri arşividir**. Uygulama geliştirme çalışma alanı yoktur.

| Scope | Konum | İçerik |
| --- | --- | --- |
| **data** | `plans/**` | Kaynak yıllık plan JSON'ları, şemalar, doğrulama raporları ve inceleme kayıtları |
| **shared** | Kök `AGENTS.md`, `README.md`, `SCOPES.md` ve `.scope.json` | Genel açıklamalar ve ajan çalışma ilkeleri |

- Yıllık plan ekleme/düzeltme işleri yalnızca `plans/**` altında yapılır.
- Ortak kök dosyalar ancak kullanıcının açık talebiyle değiştirilir.
- Depoda uygulama paketi, mobil ya da web arayüzü, uygulama derleme sistemi bulunmaz. Yeni bir uygulama ancak ayrıca istenirse ayrı ve açıkça tanımlanmış kapsamda ele alınabilir.
- Bu yazılı kurallar GitHub'ın gerçek yazma izinlerinin yerine geçmez.

Ayrıntılı kaynak sadakati kuralları: [`AGENTS.md`](AGENTS.md) ve [`plans/AGENTS.md`](plans/AGENTS.md).
