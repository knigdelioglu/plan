# Veri Alanı — Scope: data

Bu dosya `plans/**` altındaki yıllık plan dosyalarına uygulanır.

**Yazma izni:** Yalnızca kullanıcı açıkça veri ekleme/düzeltme/aktarım görevi verdiğinde, ilgili `plans/**` arşiv dosyaları.

- Bu depo yalnızca kaynak yıllık plan veritabanını barındırır; mobil/web uygulaması kodu ve bağımlılıkları bu veri görevinin parçası değildir.
- Kaynak `annual-plan.json`, JSON Schema, `validation-report.md` ve `review-queue.json` dosyalarını kaynağa uygun, izlenebilir ve birbiriyle tutarlı tut.
- Birleştirilmiş hücreleri, `3+2` ders saati gibi çok bölümlü haftaları ve özel günlerin anma tarihi / işlenme haftası ayrımını koru.
- Kök `AGENTS.md` içindeki **Yıllık Plan Aktarım İlkeleri** bağlayıcıdır.
- Kullanıcının açık talebi olmadan kök yönetişim dosyalarını değiştirme ve arşiv dışına uygulama kodu ekleme.
