# Çalışma Alanı Sınırları

`knigdelioglu/plan` iki bağımsız çalışma alanı içerir:

| Scope | Konum | İçerik |
| --- | --- | --- |
| **data** | `plans/**` | Kaynak yıllık plan JSON'ları, şemaları, doğrulama raporları ve inceleme kayıtları |
| **mobile** | `mobile/**` | React Native + Expo (TypeScript) uygulamasının bütün kaynak kodu, ayarları, testleri ve uygulamaya özgü türetilmiş verileri |
| **shared** | Kök yönetişim belgeleri ve `.github/**` | Ortak ajan kuralları, kapsam sözleşmesi ve CI |

## Ayrım kuralı

- `data` görevi `mobile/**` yazma yetkisi vermez.
- `mobile` görevi `plans/**` yazma yetkisi vermez. Mobil uygulama yıllık planları yalnızca **okur**; uygulamaya gereken dönüştürme betiği, önbellek veya katalog `mobile/**` altında hazırlanır.
- Kişisel şubeler, öğretmen notları ve ders ilerleme kayıtları yıllık plan JSON'larına eklenmez; mobil uygulamanın yerel veri katmanına aittir.
- Bir görev iki kapsamı birden gerektirirse ayrı commit/PR ile ele alınır. Ortak yönetişim belgeleri ancak kullanıcı açık talep ettiğinde değiştirilir.

Kapsam kuralları kök `AGENTS.md`, `plans/AGENTS.md`, `mobile/AGENTS.md` ve `.scope.json` içinde yer alır. GitHub Actions PR denetimi veri arşivi ile mobil uygulama kaynaklarının karıştırılmasını yakalar.

**Sınır:** Bu belge ve CI, GitHub yazma izinlerinin yerine geçmez. Kontrolün zorunlu olması için GitHub branch protection/ruleset'te required status check olarak ayarlanması gerekir.
