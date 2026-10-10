# Çalışma Alanı Sınırları

`knigdelioglu/plan` deposu iki ayrı çalışma alanına ve kesin izolasyon ilkelerine sahiptir:

| Scope | Konum | İçerik | Erişim Kuralı |
| --- | --- | --- | --- |
| **data** | `plans/**` | Kaynak yıllık plan JSON'ları, şemalar, doğrulama raporları ve inceleme kayıtları | Yalnızca veri aktarım ve doğrulama ajanları yazar. Android uygulama geliştiren ajanlar için salt okunurdur; asla değiştirilemez. |
| **android** | `android/**` | Android Native uygulaması (Kotlin + Jetpack Compose, Arc tasarım sistemi, testler) | Yalnızca Android uygulama geliştiren ajanlar yazar. Veri arşivine müdahale edemez. |
| **shared** | Kök `AGENTS.md`, `README.md`, `SCOPES.md` ve `.scope.json` | Genel açıklamalar ve çalışma ilkeleri | Yalnızca kullanıcının açık talimatıyla değiştirilir. |

### İzolasyon Kuralları

- **Veri Koruma:** `android/**` altındaki kodlar ve testler `plans/**` dizinini yalnızca okuyabilir (kaynak olarak paketler veya okur). `plans/**` içindeki JSON, schema, validation-report veya review-queue dosyaları hiçbir koşulda uygulama geliştirme sırasında değiştirilmez.
- **Kişisel Veri Ayrımı:** Öğretmenin ders ilerlemeleri, notları, çizimleri ve sınıf kayıtları Room veritabanında yerel olarak saklanır; kaynak arşiv dosyalarına yazılmaz.
- **Görev Sınırı:** Veri aktarımı yapan ajanlar `android/**` alanına müdahale edemez; Android geliştiren ajanlar `plans/**` arşivini değiştiremez.

Ayrıntılı kaynak sadakati kuralları: [`AGENTS.md`](AGENTS.md), [`plans/AGENTS.md`](plans/AGENTS.md) ve [`android/AGENTS.md`](android/AGENTS.md).
