# Veri Alanı — Scope: data

Bu dosya `plans/**` kapsamındaki yıllık plan kaynakları için geçerlidir.

**Yazma izni:** Kullanıcı açıkça veri görevi verdiğinde yalnızca `plans/**` altındaki veri arşivi dosyaları.

**Kesin sınır:** Yıllık plan JSON, kaynak Excel/PDF aktarımı, JSON Schema, `validation-report.md` veya `review-queue.json` üzerinde çalışırken **`mobile/**` altında hiçbir dosyayı değiştirme**. React Native + Expo ekranlarını, kodunu, ayarlarını, bağımlılıklarını, testlerini ve çıktılarını veri çalışmasına dahil etme.

Kök `AGENTS.md` içindeki **Yıllık Plan Aktarım İlkeleri** geçerlidir: kaynak sadakatini, özgün `raw` alanlarını, birleştirilmiş hücreleri, bölünmüş ders saatlerini, özel günlerin takvim/uygulama ayrımını ve kaynak doğrulama kayıtlarını koru.

Kök paylaşılan belgeleri ve CI yapılandırmasını veri çalışması bahanesiyle değiştirme. Uygulama geliştirmesi gerekiyorsa bağımsız `mobile` kapsamlı görev olarak ele al.
