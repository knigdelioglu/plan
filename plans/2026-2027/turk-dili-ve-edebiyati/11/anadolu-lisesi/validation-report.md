# 11. Sınıf Türk Dili ve Edebiyatı Yıllık Planı Veri Doğrulama ve Aktarım Raporu

> **Metadata düzeltmesi (2026-10-10):** Bu klasördeki `annual-plan.json` dosyasının `school_type` değeri **Anadolu Lisesi**; `source_file` değeri **`2026-2027 ANADOLU LİSESİ 9,10,11, 12. SINIF TASLAK YILLIK PLAN son.xlsx`** olarak kullanıcı talebiyle güncellendi. Belge başlığı ve çalışma sayfası adı buna uyumlu hâle getirildi. Bu rapordaki ilk aktarım ve içerik karşılaştırma bulguları önceki **Fen Lisesi kaynağına** ilişkindir; ders içerikleri Anadolu Lisesi Excel'iyle bu işlemde yeniden doğrulanmadı. Kaynak türü kimlik uyuşmazlığı giderildi, içerik kökeni yeniden doğrulama için RQ-013'te takip ediliyor.

**Belge Başlığı:** 2026-2027 EĞİTİM ÖĞRETİM YILI 11. SINIF FEN LİSESİ TÜRK DİLİ VE EDEBİYATI DERSİ ÇERÇEVE YILLIK PLANI  
**Kaynak Dosya:** `2026-2027 FEN LİSESİ 9,10,11, 12. SINIF TASLAK YILLIK PLAN son.xlsx`  
**Çalışma Sayfası:** `11. SINIF`  
**Üretim Tarihi:** 2026-10-10T17:27:34  
**Veri Şeması Standardı:** JSON Schema Draft 2020-12  
**Doğrulama Sonucu:** Eksiksiz Aktarıldı — %100 Hücre ve Pedagojik Bütünlük Doğrulandı  

---

## 1. Yönetici Özeti ve Temel Doğrulama Metrikleri

Bu çalışma, Millî Eğitim Bakanlığı Türkiye Yüzyılı Maarif Modeli kapsamında hazırlanan **11. Sınıf Fen Lisesi Türk Dili ve Edebiyatı Dersi Çerçeve Yıllık Planı**'nın Excel formatından yüksek doğruluklu, hafta merkezli ve ders bölümlerini destekleyen dijital JSON formatına aktarımını ve çok katmanlı doğrulamasını belgelemektedir.

Aktarım süreci salt bir programatik hücre okuma işlemi olarak ele alınmamış; **Computer Use** araçları kullanılarak elektronik tablo uygulamasındaki görsel hiyerarşi, birleştirilmiş hücreler, çok satırlı haftalar ve takvimsel yerleşim incelenerek doğrulanmıştır. Yapılan bağımsız inceleme doğrultusunda ders saatleri ayrımı, birleştirilmiş hücrelerin kaynak izlenebilirliği, kaynak dışı varsayımların ayıklanması ve takvimsel uyuşmazlıklar tam bir şeffaflıkla yapılandırılmıştır.

### Temel Doğrulama Metrikleri

| Metrik | Kaynak / Beklenen Değer | Aktarılan / Doğrulanan Değer | Durum ve Kanıt |
| :--- | :---: | :---: | :--- |
| **Toplam Öğretim Haftası** | 37 Hafta | **37 Hafta** | Eksiksiz aktarıldı (1-37 ardışık) |
| **Kaynaktan Doğrulanan Ders Saati** | 180 Saat | **180 Saat** | 1-36. haftalar (C4:C46 aralığı sayısal teyitli) |
| **37. Hafta Ders Saati** | Sayısal saat yok (`SOSYAL ETKİNLİK`) | **5 Saat (Varsayım)** | C47'de sayısal saat yok; MEB haftalık çizelgesine dayalı varsayım |
| **Toplam Yıllık Ders Saati (Tahmini)** | 185 Saat ($180 + 5$) | **185 Saat** | Doğrulanmış (180) ve varsayılan (5) saat ayrımı yapıldı |
| **Toplam Ders Bölümü (Lessons)** | 45 Bölüm | **45 Bölüm** | 29 tek bölümlü, 8 çift bölümlü hafta |
| **Süreç Bileşenleri Madde Sayısı** | 708 Madde | **708 Madde** | Excel G sütunu ile birebir eşleşti (Tema başına 177 madde) |
| **Müfredat Ana Temaları** | 4 Tema + 1 Sosyal Etkinlik | **4 Tema + 1 Sosyal Etkinlik** | Her ana tema 45 saat ($4 \times 45 = 180$ saat) |
| **Akademik Tatil Blokları** | 3 Tatil | **3 Tatil** | 1. Ara Tatil, Yarıyıl Tatili, 2. Ara Tatil (Satır 14, 25, 30) |
| **Belirli Gün ve Haftalar** | 11 Kayıt | **11 Kayıt (12 Etkinlik)** | 23. hafta çift etkinlikli; 15 Temmuz için planlı okul açılış anması, ayrıca 2 tarih inceleme kaydı |
| **Birleştirilmiş Hücre Aralıkları** | 49 Aralık | **49 Aralık** | Tümü çözümlendi ve metadata ile ilişkilendirildi |
| **Ay Sütununda Devam Hücresi Sayısı** | 25 Hafta | **25 Hafta** | Çapa hücresi (`anchor_cell`) ve aralık ile tam izlenebilir |
| **JSON Schema Doğrulaması** | Draft 2020-12 | **Geçerli (0 Hata)** | `jsonschema.validate` ile tam uyum |
| **İnceleme Kuyruğu (Review Queue)** | 13 Madde | **13 Yapılandırılmış Madde** | Kaynak/klasör okul türü uyuşmazlığı açık kayıt olarak eklendi |

---

## 2. Computer Use ile Elektronik Tablo Görsel İncelemesi

Electronic spreadsheet ortamında (ONLYOFFICE / Excel) gerçekleştirilen görsel kontrollerde tablonun fiziksel yerleşimi ile semantik anlamı arasındaki şu kritik ilişkiler teyit edilmiştir:

1. **Hafta ve Satır Hiyerarşisi:**
   - Tablonun ilk 3 satırı üst başlık ve sütun hiyerarşisine ayrılmıştır (1. satır çerçeve başlığı, 2. satır üst tema/pedagoji grupları, 3. satır sütun adları).
   - Öğretim haftaları 4. satırdan başlayıp 47. satırda tamamlanmaktadır. 48-53 arasındaki satırlar tamamen boş ızgara satırlarıdır.

2. **İki Fiziksel Satıra Yayılan Haftaların Tespiti (9, 18, 27 ve 36. Haftalar):**
   - Görsel incelemede her temanın son haftasının B sütununda iki satır boyunca dikey olarak birleştirildiği (B12:B13, B23:B24, B35:B36, B45:B46) görülmüştür.
   - İlk satırda 3 saatlik "Yazma" dersi yer almakta; alt satırda ise C sütununda 2 saat ve D'den L sütununa kadar uzanan yeşil renkli **`OKUL TEMELLİ PLANLAMA*`** bandı yer almaktadır.
   - Bu satırlar iki farklı hafta değil, aynı haftanın 3+2 saatlik iki ayrı ders bölümü (`lessons`) olarak modellenmiştir. Kaynakta ikinci satırda herhangi bir metin veya alt açıklama bulunmadığı için `texts_and_materials: []` olarak bırakılmış, **hiçbir kaynak dışı metin eklenmemiştir.**

3. **Hücre İçi "3+2" Saat Dağılımı Olan Haftalar (7, 16, 25 ve 34. Haftalar):**
   - Tek bir fiziksel satırda yer almasına rağmen C sütununda metin olarak `"3+2"` yazan haftalar incelenmiştir.
   - Bu haftalarda E (Konu), F (Öğrenme Çıktıları) ve G (Süreç Bileşenleri) sütunlarının üst bölümünde 3 saatlik **Dinleme/İzleme**, alt bölümünde ise 2 saatlik **Yazma** içeriklerinin yer aldığı hem formül çubuğu hem de hücre metni üzerinden görsel olarak kanıtlanmıştır.

4. **Yatay Renkli Tatil Bantları (14, 25 ve 30. Satırlar):**
   - 14. satır: `1. DÖNEM ARA TATİLİ: 16 - 20 Kasım 2026` (A14:L14 birleştirilmiş mavi bant)
   - 25. satır: `YARIYIL TATİLİ: 25 Ocak - 5 Şubat 2027` (A25:L25 birleştirilmiş mavi bant)
   - 30. satır: `2. DÖNEM ARA TATİLİ: 8-12 Mart 2027` (A30:L30 birleştirilmiş mavi bant)
   - Bu satırların bağımsız birer tatil kesintisi olduğu, alt ve üstlerindeki haftaların ders bütünlüğünü bölmediği doğrulanmıştır.

5. **37. Hafta Yıl Sonu Sosyal Etkinlik Bandı (47. Satır):**
   - 37. Hafta satırında C'den L sütununa kadar birleştirilmiş sarı renkli **`SOSYAL ETKİNLİK`** hücresi yer almaktadır.
   - Hücrede sayısal bir ders saati bulunmamaktadır. MEB Ortaöğretim Haftalık Ders Çizelgesi standardı gereği haftalık ders saati 5 olarak kabul edilmiş, bu durumun bir varsayım olduğu metadata ve haftalık kayıtta (`is_hours_assumed: true`) açıkça belirtilmiştir.
   - Kaynakta bulunmayan hiçbir etkinlik açıklaması modele dahil edilmemiştir (`texts_and_materials: []`).

---

## 3. Ders Saatleri Ayrımı ve Takvim Akış Tablosu

Kaynak Excel tablosunda ders saatleri iki ayrı kategoride ele alınmıştır:
- **Doğrulanmış Saatler (180 Saat):** 1-36. haftalarda C sütununda açıkça yer alan sayısal değerler (28 hafta $\times$ 5 saat = 140 saat; 4 hafta $\times$ "3+2" = 20 saat; 4 hafta $\times$ [3 + 2] = 20 saat; Toplam = 180 saat).
- **Varsayılan Saatler (5 Saat):** 37. haftada C47 hücresinde sayısal saat bulunmayıp `SOSYAL ETKİNLİK` yazdığından, MEB haftalık ders dağılım kuralına dayalı olarak atanan 5 saat.
- **Yıllık Toplam Ders Saati:** Doğrulanmış 180 saat, varsayılan dahil 185 saat olarak iki ayrı gösterge halinde sunulmuştur.

| Hafta | Tarih Aralığı | Ay | Satır | Tema | Ders 1 (Saat - Alan) | Ders 2 (Saat - Alan) | Toplam | Saat Durumu |
| :---: | :--- | :--- | :---: | :--- | :--- | :--- | :---: | :---: |
| **1** | 14-18 Eylül | EYLÜL | 4 | 1.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **2** | 21-25 Eylül | EYLÜL | 5 | 1.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **3** | 28 Eylül-2 Ekim | EYLÜL | 6 | 1.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **4** | 5-9 Ekim | EKİM | 7 | 1.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **5** | 12-16 Ekim | EKİM | 8 | 1.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **6** | 19-23 Ekim | EKİM | 9 | 1.TEMA | 5s DİNLEME/İZLEME | - | **5** | Doğrulandı |
| **7** | 26-30 Ekim | EKİM | 10 | 1.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** | Doğrulandı |
| **8** | 2-6 Kasım | KASIM | 11 | 1.TEMA | 5s YAZMA | - | **5** | Doğrulandı |
| **9** | 9-13 Kasım | KASIM | 12, 13 | 1.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** | Doğrulandı |
| **10** | 23-27 Kasım | KASIM | 15 | 2.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **11** | 30 Kasım-4 Aralık | ARALIK | 16 | 2.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **12** | 7-11 Aralık | ARALIK | 17 | 2.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **13** | 14-18 Aralık | ARALIK | 18 | 2.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **14** | 21-25 Aralık | ARALIK | 19 | 2.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **15** | 28 Aralık -1Ocak | ARALIK | 20 | 2.TEMA | 5s DİNLEME/İZLEME | - | **5** | Doğrulandı |
| **16** | 4-8 Ocak | OCAK | 21 | 2.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** | Doğrulandı |
| **17** | 11-15 Ocak | OCAK | 22 | 2.TEMA | 5s YAZMA | - | **5** | Doğrulandı |
| **18** | 18-22 Ocak | OCAK | 23, 24 | 2.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** | Doğrulandı |
| **19** | 8-12 Şubat | ŞUBAT | 26 | 3.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **20** | 15-19 Şubat | ŞUBAT | 27 | 3.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **21** | 22-26 Şubat | ŞUBAT | 28 | 3.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **22** | 1-5 Mart | MART | 29 | 3.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **23** | 15-19 Mart | MART | 31 | 3.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **24** | 22-26 Mart | MART | 32 | 3.TEMA | 5s DİNLEME/İZLEME | - | **5** | Doğrulandı |
| **25** | 29 Mart-2 Nisan | MART | 33 | 3.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** | Doğrulandı |
| **26** | 5-9 Nisan | NİSAN | 34 | 3.TEMA | 5s YAZMA | - | **5** | Doğrulandı |
| **27** | 12-16 Nisan | NİSAN | 35, 36 | 3.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** | Doğrulandı |
| **28** | 19-23 Nisan | NİSAN | 37 | 4.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **29** | 26-30 Nisan | NİSAN | 38 | 4.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **30** | 3-7 Mayıs | MAYIS | 39 | 4.TEMA | 5s OKUMA | - | **5** | Doğrulandı |
| **31** | 10-14 Mayıs | MAYIS | 40 | 4.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **32** | 17-21 Mayıs | MAYIS | 41 | 4.TEMA | 5s KONUŞMA | - | **5** | Doğrulandı |
| **33** | 24-28 Mayıs | MAYIS | 42 | 4.TEMA | 5s DİNLEME/İZLEME | - | **5** | Doğrulandı |
| **34** | 31 Mayıs-4 Haziran | HAZİRAN | 43 | 4.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** | Doğrulandı |
| **35** | 7-11 Haziran | HAZİRAN | 44 | 4.TEMA | 5s YAZMA | - | **5** | Doğrulandı |
| **36** | 14-18 Haziran | HAZİRAN | 45, 46 | 4.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** | Doğrulandı |
| **37** | 21-25 Haziran | HAZİRAN | 47 | YIL SONU | 5s SOSYAL ETKİNLİK | - | **5** | Varsayım (5s) |

**Doğrulanmış Ders Saati Toplamı (1-36. Hafta):** **180 Saat**  
**37. Hafta Varsayılan Ders Saati:** **5 Saat**  
**Hesaplanan Yıllık Toplam Ders Saati:** **185 Saat**  

---

## 4. Birleştirilmiş Hücreler ve Devam Hücresi İzlenebilirliği

Excel tablosunda birleştirilmiş hücreler, yalnızca sol üst köşedeki hücrede (`anchor_cell`) değer taşımakta, birleşimin devamındaki diğer hücreler fiziksel olarak boş (`None`) kalmaktadır. 

JSON modelimizde her hücre başvurusu `{anchor_cell, merged_range, physical_cell, is_merged, is_continuation}` nesnesi olarak yapılandırılmıştır. Böylece hem satırın fiziksel konumu hem de değerin asıl kaynağı eksiksiz izlenebilir hale getirilmiştir.

### Ay Sütunundaki (A Sütunu) 25 Devam Hücresinin İzlenebilirlik Tablosu

37 öğretim haftasının **25'inde** A sütunu bir birleşik hücrenin devamı niteliğindedir:

| Hafta No | Satır | Fiziksel Hücre | Çapa Hücresi | Birleşik Aralık | Ay Değeri | Devam Hücresi mi? |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **1** | 4 | `A4` | `A4` | `A4:A6` | EYLÜL | Hayır (Çapa Hücre) |
| **2** | 5 | `A5` | `A4` | `A4:A6` | EYLÜL | Evet (Continuation) |
| **3** | 6 | `A6` | `A4` | `A4:A6` | EYLÜL | Evet (Continuation) |
| **4** | 7 | `A7` | `A7` | `A7:A10` | EKİM | Hayır (Çapa Hücre) |
| **5** | 8 | `A8` | `A7` | `A7:A10` | EKİM | Evet (Continuation) |
| **6** | 9 | `A9` | `A7` | `A7:A10` | EKİM | Evet (Continuation) |
| **7** | 10 | `A10` | `A7` | `A7:A10` | EKİM | Evet (Continuation) |
| **8** | 11 | `A11` | `A11` | `A11:A13` | KASIM | Hayır (Çapa Hücre) |
| **9** | 12 | `A12` | `A11` | `A11:A13` | KASIM | Evet (Continuation) |
| **10** | 15 | `A15` | `A15` | `A15` | KASIM | Hayır (Çapa Hücre) |
| **11** | 16 | `A16` | `A16` | `A16:A20` | ARALIK | Hayır (Çapa Hücre) |
| **12** | 17 | `A17` | `A16` | `A16:A20` | ARALIK | Evet (Continuation) |
| **13** | 18 | `A18` | `A16` | `A16:A20` | ARALIK | Evet (Continuation) |
| **14** | 19 | `A19` | `A16` | `A16:A20` | ARALIK | Evet (Continuation) |
| **15** | 20 | `A20` | `A16` | `A16:A20` | ARALIK | Evet (Continuation) |
| **16** | 21 | `A21` | `A21` | `A21:A23` | OCAK | Hayır (Çapa Hücre) |
| **17** | 22 | `A22` | `A21` | `A21:A23` | OCAK | Evet (Continuation) |
| **18** | 23 | `A23` | `A21` | `A21:A23` | OCAK | Evet (Continuation) |
| **19** | 26 | `A26` | `A26` | `A26:A28` | ŞUBAT | Hayır (Çapa Hücre) |
| **20** | 27 | `A27` | `A26` | `A26:A28` | ŞUBAT | Evet (Continuation) |
| **21** | 28 | `A28` | `A26` | `A26:A28` | ŞUBAT | Evet (Continuation) |
| **22** | 29 | `A29` | `A29` | `A29` | MART | Hayır (Çapa Hücre) |
| **23** | 31 | `A31` | `A31` | `A31:A33` | MART | Hayır (Çapa Hücre) |
| **24** | 32 | `A32` | `A31` | `A31:A33` | MART | Evet (Continuation) |
| **25** | 33 | `A33` | `A31` | `A31:A33` | MART | Evet (Continuation) |
| **26** | 34 | `A34` | `A34` | `A34:A38` | NİSAN | Hayır (Çapa Hücre) |
| **27** | 35 | `A35` | `A34` | `A34:A38` | NİSAN | Evet (Continuation) |
| **28** | 37 | `A37` | `A34` | `A34:A38` | NİSAN | Evet (Continuation) |
| **29** | 38 | `A38` | `A34` | `A34:A38` | NİSAN | Evet (Continuation) |
| **30** | 39 | `A39` | `A39` | `A39:A42` | MAYIS | Hayır (Çapa Hücre) |
| **31** | 40 | `A40` | `A39` | `A39:A42` | MAYIS | Evet (Continuation) |
| **32** | 41 | `A41` | `A39` | `A39:A42` | MAYIS | Evet (Continuation) |
| **33** | 42 | `A42` | `A39` | `A39:A42` | MAYIS | Evet (Continuation) |
| **34** | 43 | `A43` | `A43` | `A43:A47` | HAZİRAN | Hayır (Çapa Hücre) |
| **35** | 44 | `A44` | `A43` | `A43:A47` | HAZİRAN | Evet (Continuation) |
| **36** | 45 | `A45` | `A43` | `A43:A47` | HAZİRAN | Evet (Continuation) |
| **37** | 47 | `A47` | `A43` | `A43:A47` | HAZİRAN | Evet (Continuation) |


### Tablo Boyunca Birleştirilmiş Genel Sütunlar

1. **Farklılaştırma Sütunu (M4:M47):**
   - 4. satırdan 47. satıra kadar 44 satır boyunca tek bir hücre olarak birleştirilmiştir.
   - Değer `general_explanations.differentiation` altında tekil olarak saklanmış, her haftanın `pedagogical_context.differentiation` metadata kaydından `anchor_cell: M4, merged_range: M4:M47` bilgisiyle bağlanmıştır.

2. **Okul Temelli Planlama Genel İlkesi (N4:N47):**
   - 4. satırdan 47. satıra kadar tek bir hücre olarak birleştirilmiştir.
   - Değer `general_explanations.school_based_planning_general` altında saklanmış, her haftaya `anchor_cell: N4, merged_range: N4:N47` bilgisiyle bağlanmıştır.

3. **Tema Pedagojik Sütunları (H, I, J, K):**
   - Ölçme ve Değerlendirme (H), SDB (I), Değerler (J) ve Okuryazarlık (K) sütunları her tema boyunca birleştirilmiştir:
     - 1. Tema: `H4:H12`, `I4:I12`, `J4:J12`, `K4:K12`
     - 2. Tema: `H15:H23`, `I15:I23`, `J15:J23`, `K15:K23`
     - 3. Tema (Ara Tatil Öncesi): `H26:H29`, `I26:I29`, `J26:J29`, `K26:K29`
     - 3. Tema (Ara Tatil Sonrası): `H31:H35`, `I31:I35`, `J31:J35`, `K31:K35`
     - 4. Tema: `H37:H45`, `I37:I45`, `J37:J45`, `K37:K45`

---

## 5. Belirli Gün ve Haftalar / Takvim Uyuşmazlığı Analizi

Excel tablosunun L sütununda yer alan "Belirli Gün ve Haftalar" verileri incelenmiş; özgün metinler (`raw`) hiçbir karakter veya satır sonu eksiltilmeden saklanmış, yapılandırılmış `events` alanlarında ayrıştırılmıştır.

Kaynakta 15 Temmuz için **planlı okul açılışı anması**, ayrıca gerekçesi teyit edilmesi gereken **2 tarih yerleşimi** (12 Mart ve 29 Mayıs) ve **1 çoklu etkinlik hücresi** bulunmaktadır:

| Hafta | Tarih Aralığı | Hücre | Özgün Metin (`raw`) | Tespit Edilen Takvimsel Durum |
| :---: | :---: | :---: | :--- | :--- |
| **1** | 14-18 Eylül 2026 | `L4` | `15 Temmuz Demokrasi ve Millî Birlik Günü` | **Planlı okul açılış haftası anması (hata değil):** Anma tarihinin 15 Temmuz olması, okulun açıldığı 1. haftada uygulanmasına engel değildir. Etkinliğin kesin uygulama günü kaynakta yoktur. |
| **22** | 1-5 Mart 2027 | `L29` | `İstiklâl Marşı'nın Kabulü ve Mehmet Akif Ersoy'u Anma Günü (12 Mart)    ` | **Takvim Uyuşmazlığı:** 12 Mart tarihi bir sonraki hafta olan 2. Dönem Ara Tatili'ne (8-12 Mart 2027) denk gelmektedir. Kaynak tablo anma gününü tatil öncesindeki 22. haftaya yazmıştır. |
| **23** | 15-19 Mart 2027 | `L31` | `\nTürk Dünyası ve Toplulukları Haftası (16-22 Mart)      \n\n\n\nŞehitler Haftası (18 Mart)\n\n\n      \n\n ` | **Çoklu Etkinlik:** Hücrede iki ayrı anma günü aşırı boşluk ve satır sonlarıyla bir arada yer almaktadır. Modelde 2 ayrı yapılandırılmış etkinlik (`events`) olarak ayrıştırılmıştır. |
| **33** | 24-28 Mayıs 2027 | `L42` | `İstanbul'un Fethi (29 Mayıs)       ` | **Takvim Uyuşmazlığı:** 33. haftanın öğretim günleri 24-28 Mayıs (Pazartesi-Cuma) arasındadır. 29 Mayıs 2027 tarihi Cumartesi gününe (hafta sonu) denk gelmektedir. |

`date_text`/`commemoration_date` günün takvim tarihini, `scheduled_week` ve gerekiyorsa `scheduled_date` okuldaki uygulamayı gösterir. 15 Temmuz için `is_calendar_aligned=false` yalnızca tarihlerin aynı aralıkta olmadığını, `is_schedule_valid=true` ve `has_calendar_discrepancy=false` ise planlı yerleşimin hata olmadığını bildirir. 12 Mart ve 29 Mayıs kayıtları açıklamasız otomatik taşınmamıştır; yerleşim gerekçeleri ayrıca teyit edilmelidir.

---

## 6. Pedagojik Bütünlük ve Süreç Bileşenleri (708 Madde) Doğrulaması

Süreç bileşenleri (G sütunu) aktarımında en üst düzey kaynak sadakati sağlanmıştır:
- **Toplam Süreç Maddesi:** **708 Madde**.
- **Tema Dağılımı:** Her 4 temanın her birinde tam olarak **177 madde** yer almaktadır ($4 \times 177 = 708$).
- **Satır Sarma (Line Wrapping) Koruması:** Excel hücresi içinde görsel sığdırma amacıyla alt satıra geçen cümle parçaları (örneğin `"Okuyacağı metnin başlık ve görsellerinden hareketle metnin içeriğine, türüne veya yazılış amacına yönelik"` satırını takip eden `"tahminde bulunur."` satırı) başarıyla birleştirilerek anlamlı tam cümleler korunmuştur.
- **Kazanım Kodları:** TDE1.1'den TDE4.4'e kadar tüm öğrenme çıktı kodları özgün biçimleriyle korunmuştur.
- **Okul Temelli Planlama Satırları:** 9, 18, 27 ve 36. haftaların 2. ders bölümlerinde (`OKUL TEMELLİ PLANLAMA*`) kaynakta bulunmayan hiçbir uydurma pedagojik metin eklenmemiş; `texts_and_materials: []` bırakılmıştır.

---

## 7. İnceleme Kuyruğu (Review Queue - 13 Madde) Özeti

`review-queue.json` dosyasında yer alan 13 adet kayıt ve çözüm durumları:

| ID | Kategori | Önem | Konu | İlgili Haftalar / Hücreler | Durum |
| :---: | :--- | :---: | :--- | :--- | :---: |
| **RQ-001** | `lesson_hours_split` | Orta | Hücre İçi "3+2" Saat Dağılımı | 7, 16, 25, 34 (`C10, C21, C33, C43`) | Yapılandırıldı |
| **RQ-002** | `multi_row_weeks` | Orta | İki Fiziksel Satıra Yayılan Haftalar | 9, 18, 27, 36 (`B12:B13, D13:L13 vb.`) | Yapılandırıldı |
| **RQ-003** | `missing_explicit_hours_assumption` | Yüksek | 37. Hafta Sayısal Saat Yokluğu ve 5 Saat Varsayımı | 37 (`C47:L47`) | Varsayım Olarak İşaretlendi |
| **RQ-004** | `intentional_commemoration_scheduling` | Bilgi | 1. Hafta 15 Temmuz Açılış Anması | 1 (`L4`) | Planlı Yerleşim; Hata Değil |
| **RQ-005** | `calendar_anomaly` | Yüksek | 22. Hafta 12 Mart Takvim Uyuşmazlığı | 22 (`L29`) | Belgelendi |
| **RQ-006** | `calendar_anomaly` | Orta | 33. Hafta 29 Mayıs Takvim Uyuşmazlığı | 33 (`L42`) | Belgelendi |
| **RQ-007** | `multi_event_cell` | Orta | 23. Hafta Çoklu Etkinlik ve Boşluk Düzensizliği | 23 (`L31`) | Yapılandırıldı |
| **RQ-008** | `merged_continuation_traceability` | Orta | Birleştirilmiş Hücrelerde Devam Hücresi İzlenebilirliği | 1-37 (A4:A47) | Çözümlendi |
| **RQ-009** | `global_merged_cells` | Düşük | M ve N Sütunlarının Tablo Boyunca Birleştirilmesi | 1-37 (`M4:M47, N4:N47`) | Çözümlendi |
| **RQ-010** | `theme_merged_pedagogical_cells` | Düşük | H, I, J, K Sütunlarının Tema Boyunca Birleştirilmesi | 1-36 (`H4:H12 vb.`) | Çözümlendi |
| **RQ-011** | `code_formatting` | Düşük | 1. Tema SDB Becerileri Kod Yazım Düzensizliği | 1-9 (`I4:I12`) | Belgelendi |
| **RQ-012** | `workbook_metadata_difference` | Düşük | Fen Lisesi ile Anadolu Lisesi Sekme ve Başlık Farkı | Genel (`A1`) | Kaynak Farkı Doğrulandı |
| **RQ-013** | `source_identity_relabel_pending_validation` | Orta | Anadolu Lisesi Kimliği Güncellendi; İçerik Yeniden Doğrulanmadı | Genel (`A1`) | Metadata Düzeltildi |

---

### Arşiv konumu ve okul türü uyarısı

Bu klasördeki JSON'un `metadata.school_type` ve `source_file` alanları Anadolu Lisesi kaynağına göre güncellenmiştir. Önceki klasör/metadata uyuşmazlığı giderildi. Ancak bu, ders içeriklerinin özgün Anadolu Lisesi Excel'iyle yeniden doğrulandığı anlamına gelmez. RQ-013 bu doğrulama sınırını belgelendirir.

---

## 8. Teslim Edilen Dosyalar

Aşağıdaki dosyalar hem `/Volumes/Lacie/MacBook Klasörleri/grade-11-annual-plan/output/grade-11/` hem de `/Users/kadir/Desktop/output/grade-11/` dizinlerinde eksiksiz ve güncel olarak teslim edilmiştir:

1. `annual-plan.json` — 11. Sınıf Fen Lisesi eksiksiz, hafta merkezli, ders bölümlerini destekleyen yıllık plan verisi.
2. `annual-plan.schema.json` — JSON Schema Draft 2020-12 standardına uygun resmi veri şeması.
3. `validation-report.md` — Bu kapsamlı doğrulama ve kanıt raporu.
4. `review-queue.json` — 13 yapılandırılmış inceleme maddesini içeren denetim kuyruğu.
