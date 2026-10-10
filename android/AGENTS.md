# Android Native Uygulaması — Ajan Çalışma Kuralları

Bu çalışma alanı (`android/**`), `plan` deposundaki yıllık ders planlarını öğretmenlerin günlük kullanımına uygun haftalık planlara dönüştüren **Kotlin + Jetpack Compose** native Android uygulamasını içerir.

### Temel İlkeler ve Kurallar

1. **Veri İzolasyonu:**
   - `plans/**` dizini salt okunurdur. Kaynak JSON, şema, doğrulama ve inceleme dosyaları ASLA değiştirilmez.
   - Veri okuma katmanı (`PlanRepository` / `PlanAssetDataSource`), `plans/**` içeriğini doğrudan okur veya asset olarak kullanır.
   - Öğretmenin kişisel notları, sınıf ilerlemeleri ve tercihleri Room veritabanı ve DataStore üzerinde yerel saklanır.

2. **Arc Tasarım Sistemi Sadakati:**
   - Arc Library (`arc-foundation`, `arc-motion-tokens`, 70 bileşen ve 9 blok) Jetpack Compose ile yerel olarak uygulanır.
   - Bileşenler `com.plan.app.core.design` altında genel ve yeniden kullanılabilir biçimde tutulur; plan alan modellerine doğrudan bağımlı kılınmaz.
   - MIT lisansı ve telif hakkı atfı (`Elia Kuratli`) korunur.

3. **Tablet Öncelikli & Adaptif Deneyim:**
   - Android tabletler birincil hedeftir; Samsung DeX, katlanabilir cihazlar, bölünmüş ekran, telefon, fare, klavye ve kalem (stylus) etkileşimleri desteklenir.
   - Dar pencerelerde ve telefonlarda kontroller kesilmez veya taşmaz.

4. **Kaynak Sadakati:**
   - Doğrulanmış saatler (180) ve varsayılan saatler (5) kaynakta belirtildiği şekilde ayrı tutulur.
   - 15 Temmuz açılış haftası yerleşimi gibi planlı uygulamalar hata olarak etiketlenmez.
   - Eksik veya henüz eklenmemiş sınıflar için uydurma veri üretilmez; şık boş durum (Empty State) ekranları gösterilir.
