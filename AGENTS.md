# Yıllık Plan ve Android Native Uygulaması — Çalışma Kuralları

Bu depo iki temel çalışma alanından oluşur:
1. `plans/**`: Yıllık ders planlarının kaynak sadakatini koruyan JSON veri arşivi.
2. `android/**`: Öğretmenler için geliştirilen Kotlin + Jetpack Compose Android Native uygulaması (Arc tasarım sistemi uyarlaması).

### Çalışma Alanı ve İzolasyon Kuralları

- **Android Geliştirme Sınırı:** Android uygulaması geliştiren ajanlar yalnızca `android/**` altındaki dosyalarda değişiklik yapar. `plans/**` altındaki kaynak plan JSON'ları, şemalar, doğrulama raporları veya inceleme kayıtları Android geliştirme sürecinde **kesinlikle değiştirilemez**.
- **Kaynak Sadakati:** Uygulama `plans/**` verilerini kaynak olarak okur. Kaynakta olmayan bilgi üretilmez, varsayımlar ile doğrulanmış veriler net olarak ayrılır.
- **Kişisel Veri Bağımsızlığı:** Öğretmenin ders işaretlemeleri, notları, çizimleri ve sınıf ilerlemeleri cihazda yerel olarak (Room / DataStore) saklanır; kaynak arşiv dosyalarına dokunulmaz.
- **Kök Belgeler:** Kök `AGENTS.md`, `README.md`, `SCOPES.md`, `.scope.json` dosyaları yalnızca kullanıcı açıkça talimat verdiğinde güncellenir.
- Alt klasör kuralları: [`plans/AGENTS.md`](plans/AGENTS.md) ve [`android/AGENTS.md`](android/AGENTS.md). Kapsam tanımı: [`SCOPES.md`](SCOPES.md).

---

# Yıllık Plan Aktarım İlkeleri

Bu depo kaynak Excel/PDF planlarının izlenebilir JSON arşividir. Yeni veri eklerken veya düzeltirken aşağıdaki kuralları uygula.

- **Kaynak ve okul türü:** Önce öğretim yılı, ders, sınıf, okul türü ve özgün belge başlığını eşleştir. Bir planı farklı okul türü klasörüne kopyalayarak yeni okul türüne aitmiş gibi etiketleme. Klasör ile kaynak farklıysa bunu açık uyarı ve inceleme kaydı olarak koru; doğru kaynak olmadan kimlik alanlarını değiştirme.
- **Anlamsal yapı:** Excel'in görsel hiyerarşisini, birleşik hücrelerin çapa/aralık bilgilerini ve haftaya ait birden fazla ders bölümünü koru. Satır sayısını hafta sayısıyla eşitleme. `3+2` saatleri ve çok satırlı haftaları doğru alt kayıtlara bağla.
- **Kaynak sadakati:** Özgün metinleri `raw` alanlarında kayıpsız sakla. Normalleştirme, ayrıştırma, varsayım ve kaynak bilgisi ayrı tutulmalı; kaynakta olmayan etkinlik, kazanım, uygulama tarihi veya ders saati uydurulmamalıdır.
- **Özel günler:** Bir anma gününün takvim tarihi ile okulda uygulanacağı hafta ayrı kavramlardır. **15 Temmuz Demokrasi ve Millî Birlik Günü**, yaz tatiline rastladığı için eğitim öğretim yılının açılış haftasında planlandığında bu yerleşimi takvim hatası olarak işaretleme. Takvimdeki günü ve planlanan haftayı ayrı kaydet; kesin uygulama günü bilinmiyorsa `null` bırak. `is_calendar_aligned` tarihlerin birebir örtüşmesini, `has_calendar_discrepancy` ise açıklanmamış/gerçek planlama sorununu ifade eder. Tarih farkı tek başına hata değildir. Diğer özel günler (ör. 12 Mart, 29 Mayıs) için tatil, hafta sonu ve yıllık plan uygulamasını ayrıca değerlendir; kanıt olmadan hepsine aynı istisnayı uygulama.
- **Doğrulama:** Haftalar, ders bölümleri, saatler, kazanımlar ve süreç bileşenleri için yalnızca sayı kontrolü değil, kaynak hücrelerle metin ve ilişki karşılaştırması yap. Varsayımsal toplamları doğrulanmış saatlerden ayrı raporla. JSON şemasını, `validation-report.md` ve `review-queue.json` dosyalarını verideki değişikliklerle senkronize tut.

İçeriği başka okul türüne uyarlamak yerine doğru kaynağı edinmek esastır.
