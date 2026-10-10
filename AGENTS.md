# Depo Kapsamı (Scope) ve Çalışma Sınırları

Bu depo iki ayrı çalışma alanını barındırır. Görevin kapsamını işe başlamadan belirle; karşı alanı sadece gerekli olduğunda **salt okunur** incele.

| Görev kapsamı | Yazılabilir konum | Salt okunur konum | Yasak değişiklik |
| --- | --- | --- | --- |
| `data` / yıllık plan ve JSON aktarımı | `plans/**` | `plans/**` | `pwa/**` |
| `pwa` / uygulama geliştirme | `pwa/**` | `plans/**` | `plans/**` |

- Kullanıcı **yalnızca veri** çalışması istiyorsa PWA kaynak koduna, yapılandırmasına, testlerine veya derlenmiş çıktılarına dokunma.
- Kullanıcı **yalnızca PWA** çalışması istiyorsa yıllık plan JSON'larını, şemalarını, doğrulama raporlarını ve inceleme kuyruklarını **asla değiştirme**. Gerekirse veriyi salt okunur kullan ve dönüşümü `pwa/**` altında yap.
- Kök `AGENTS.md`, `README.md`, `SCOPES.md`, `.scope.json` ve `.github/**` paylaşılan yönetişim dosyalarıdır. Bunları ancak kullanıcı açıkça istediğinde değiştir.
- Bir görev iki alanı da gerektiriyorsa kapsamları **ayrı değişiklik kümelerine / ayrı PR'lara** böl. Kullanıcının açık kapsam genişletmesi olmadan başka alana yazma.
- Depo genelinde biçimlendirme, dosya taşıma veya otomatik düzeltme yaparken diğer kapsamda değişiklik üretme; teslimden önce değişen yolları kontrol et.
- Ek yerel kurallar için `plans/AGENTS.md` ve `pwa/AGENTS.md` dosyalarını oku. Bu sınırlar alt klasörler için de geçerlidir.
- Kapsam tanımları: [SCOPES.md](SCOPES.md) ve [`.scope.json`](.scope.json). CI, PR'da veri ve PWA uygulama değişikliklerinin karışmasını reddeder.

---

# Yıllık Plan Aktarım İlkeleri

Bu depo kaynak Excel/PDF planlarının izlenebilir JSON arşividir. Yeni veri eklerken veya düzeltirken aşağıdaki kuralları uygula.

- **Kaynak ve okul türü:** Önce öğretim yılı, ders, sınıf, okul türü ve özgün belge başlığını eşleştir. Bir planı farklı okul türü klasörüne kopyalayarak yeni okul türüne aitmiş gibi etiketleme. Klasör ile kaynak farklıysa bunu açık uyarı ve inceleme kaydı olarak koru; doğru kaynak olmadan kimlik alanlarını değiştirme.
- **Anlamsal yapı:** Excel'in görsel hiyerarşisini, birleşik hücrelerin çapa/aralık bilgilerini ve haftaya ait birden fazla ders bölümünü koru. Satır sayısını hafta sayısıyla eşitleme. `3+2` saatleri ve çok satırlı haftaları doğru alt kayıtlara bağla.
- **Kaynak sadakati:** Özgün metinleri `raw` alanlarında kayıpsız sakla. Normalleştirme, ayrıştırma, varsayım ve kaynak bilgisi ayrı tutulmalı; kaynakta olmayan etkinlik, kazanım, uygulama tarihi veya ders saati uydurulmamalıdır.
- **Özel günler:** Bir anma gününün takvim tarihi ile okulda uygulanacağı hafta ayrı kavramlardır. **15 Temmuz Demokrasi ve Millî Birlik Günü**, yaz tatiline rastladığı için eğitim öğretim yılının açılış haftasında planlandığında bu yerleşimi takvim hatası olarak işaretleme. Takvimdeki günü ve planlanan haftayı ayrı kaydet; kesin uygulama günü bilinmiyorsa `null` bırak. `is_calendar_aligned` tarihlerin birebir örtüşmesini, `has_calendar_discrepancy` ise açıklanmamış/gerçek planlama sorununu ifade eder. Tarih farkı tek başına hata değildir. Diğer özel günler (ör. 12 Mart, 29 Mayıs) için tatil, hafta sonu ve yıllık plan uygulamasını ayrıca değerlendir; kanıt olmadan hepsine aynı istisnayı uygulama.
- **Doğrulama:** Haftalar, ders bölümleri, saatler, kazanımlar ve süreç bileşenleri için yalnızca sayı kontrolü değil, kaynak hücrelerle metin ve ilişki karşılaştırması yap. Varsayımsal toplamları doğrulanmış saatlerden ayrı raporla. JSON şemasını, `validation-report.md` ve `review-queue.json` dosyalarını verideki değişikliklerle senkronize tut.

İçeriği başka okul türüne uyarlamak yerine doğru kaynağı edinmek esastır.
