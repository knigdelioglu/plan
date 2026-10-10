# Çalışma Alanı Sınırları

Aynı GitHub deposunda iki bağımsız çalışma alanı vardır:

| Scope | Konum | Sahip olduğu içerik |
| --- | --- | --- |
| **data** | `plans/**` | Yıllık plan kaynak JSON'ları, şemalar, doğrulama raporları ve inceleme kayıtları |
| **pwa** | `pwa/**` | Öğretmen PWA'sı, React bileşenleri, şube ilerlemesi ve uygulamaya özgü türetilmiş veriler |
| **shared** | Kökteki seçili belgeler ve `.github/**` | Ortak ajan talimatları, kapsam tanımı ve CI |

## Ayrım kuralı

Bir `data` görevi `pwa/**` yazma yetkisi vermez. Bir `pwa` görevi de `plans/**` yazma yetkisi vermez. Karşı alana salt okunur erişim mümkündür; örneğin PWA JSON planlarını okuyarak ekranlar üretir.

Her iki alanı ilgilendiren işler ayrı PR/commit kapsamında yürütülür. Paylaşılan yönetişim dosyalarına dokunmak için ayrıca açık talep gerekir.

`plans/AGENTS.md` ve `pwa/AGENTS.md`, o çalışma alanında çalışan LLM/ajanlar için yerel talimatları içerir. Kök `AGENTS.md` her iki alanı bağlar. `.scope.json` makine tarafından okunabilir kapsam tanımıdır; GitHub Actions PR koruması iki alanın uygulama/veri değişikliklerini aynı PR'da karıştırmayı tespit eder.

**Sınırlama:** Talimatlar ve PR kontrolü tek başına GitHub'daki yazma izinlerini teknik olarak kısıtlamaz. Zorunlu koruma için ilgili kontrolün branch protection/ruleset altında required status check yapılması gerekir.
