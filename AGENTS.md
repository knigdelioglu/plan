# Depo Kapsamı (Scope) ve Çalışma Sınırları

Bu depoda kaynak yıllık plan arşivi ile React Native + Expo uygulaması iki **ayrı çalışma alanıdır**. Bir görev başlamadan önce kapsamı belirle; diğer alanı gerekirse yalnızca **salt okunur** incele.

| Görev kapsamı | Yazılabilir konum | Salt okunur konum | Yasak değişiklik |
| --- | --- | --- | --- |
| `data` / yıllık plan, kaynak Excel/PDF ve JSON aktarımı | `plans/**` | `plans/**` | `mobile/**` |
| `mobile` / React Native + Expo uygulaması | `mobile/**` | `plans/**` | `plans/**` |

- **Veri görevi:** Yalnızca `plans/**` içindeki kaynak arşiv üzerinde çalış. Expo uygulamasını, bağımlılıklarını, testlerini ve yapılandırmasını değiştirme.
- **Expo uygulaması görevi:** Yalnızca `mobile/**` altında çalış. `plans/**` içindeki JSON'ları, şemaları, doğrulama raporlarını ve inceleme kuyruklarını hiçbir gerekçeyle yeniden yazma, biçimlendirme, silme veya taşıma. Gerekli kaynakları salt okunur kullan; türetilmiş katalogları ve dönüştürme kodunu uygulama alanında tut.
- Kişisel öğretmen/şube/ilerleme kayıtları ortak plan arşivine yazılamaz; uygulamanın kendi depolama katmanında tutulur.
- Kök `AGENTS.md`, `README.md`, `SCOPES.md`, `.scope.json` ve `.github/**` ortak yönetişim dosyalarıdır. Kullanıcı açıkça istemedikçe değiştirme.
- Her iki alanın değiştirilmesi istenirse işi ayrı değişiklik kümeleri / ayrı PR'lar hâlinde yürüt. Bir alan için görev verilmesi diğer alana yazma izni oluşturmaz.
- Repo genelinde otomatik biçimlendirme, taşıma, temizleme veya kod üretimi yaparken diğer kapsamı değiştirme; teslimde değişen dosya yollarını doğrula.
- Yerel kurallar: `plans/AGENTS.md`, `mobile/AGENTS.md`. Makine tarafından okunabilir sınırlar: `.scope.json`. Genel politika: `SCOPES.md`. CI, plan verisi ile mobil uygulama değişikliklerinin aynı PR'da karışmasını denetler.

---

# Yıllık Plan Aktarım İlkeleri

Bu depo kaynak Excel/PDF planlarının izlenebilir JSON arşividir. Yeni veri eklerken veya düzeltirken aşağıdaki kuralları uygula.

- **Kaynak ve okul türü:** Önce öğretim yılı, ders, sınıf, okul türü ve özgün belge başlığını eşleştir. Bir planı farklı okul türü klasörüne kopyalayarak yeni okul türüne aitmiş gibi etiketleme. Klasör ile kaynak farklıysa bunu açık uyarı ve inceleme kaydı olarak koru; doğru kaynak olmadan kimlik alanlarını değiştirme.
- **Anlamsal yapı:** Excel'in görsel hiyerarşisini, birleşik hücrelerin çapa/aralık bilgilerini ve haftaya ait birden fazla ders bölümünü koru. Satır sayısını hafta sayısıyla eşitleme. `3+2` saatleri ve çok satırlı haftaları doğru alt kayıtlara bağla.
- **Kaynak sadakati:** Özgün metinleri `raw` alanlarında kayıpsız sakla. Normalleştirme, ayrıştırma, varsayım ve kaynak bilgisi ayrı tutulmalı; kaynakta olmayan etkinlik, kazanım, uygulama tarihi veya ders saati uydurulmamalıdır.
- **Özel günler:** Bir anma gününün takvim tarihi ile okulda uygulanacağı hafta ayrı kavramlardır. **15 Temmuz Demokrasi ve Millî Birlik Günü**, yaz tatiline rastladığı için eğitim öğretim yılının açılış haftasında planlandığında bu yerleşimi takvim hatası olarak işaretleme. Takvimdeki günü ve planlanan haftayı ayrı kaydet; kesin uygulama günü bilinmiyorsa `null` bırak. `is_calendar_aligned` tarihlerin birebir örtüşmesini, `has_calendar_discrepancy` ise açıklanmamış/gerçek planlama sorununu ifade eder. Tarih farkı tek başına hata değildir. Diğer özel günler (ör. 12 Mart, 29 Mayıs) için tatil, hafta sonu ve yıllık plan uygulamasını ayrıca değerlendir; kanıt olmadan hepsine aynı istisnayı uygulama.
- **Doğrulama:** Haftalar, ders bölümleri, saatler, kazanımlar ve süreç bileşenleri için yalnızca sayı kontrolü değil, kaynak hücrelerle metin ve ilişki karşılaştırması yap. Varsayımsal toplamları doğrulanmış saatlerden ayrı raporla. JSON şemasını, `validation-report.md` ve `review-queue.json` dosyalarını verideki değişikliklerle senkronize tut.

İçeriği başka okul türüne uyarlamak yerine doğru kaynağı edinmek esastır.
