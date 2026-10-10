# 11. Sınıf Türk Dili ve Edebiyatı Yıllık Planı Veri Doğrulama ve Aktarım Raporu

**Belge:** 2026-2027 EĞİTİM ÖĞRETİM YILI 11. SINIF FEN LİSESİ TÜRK DİLİ VE EDEBİYATI DERSİ ÇERÇEVE YILLIK PLANI  
**Kaynak Dosya:** `2026-2027 FEN LİSESİ 9,10,11, 12. SINIF TASLAK YILLIK PLAN son.xlsx`  
**Çalışma Sayfası:** `11. SINIF`  
**Tarih:** 2026-10-10  
**Durum:** Eksiksiz Aktarıldı — JSON Şeması Doğrulandı (%100 Başarılı)

---

## 1. Yönetici Özeti ve Kapsam

Bu çalışma, Millî Eğitim Bakanlığı Türkiye Yüzyılı Maarif Modeli kapsamında hazırlanan **11. Sınıf Fen Lisesi Türk Dili ve Edebiyatı Dersi Çerçeve Yıllık Planı**'nın Excel formatından yüksek doğruluklu, hafta merkezli ve ders bölümlerini destekleyen dijital JSON formatına aktarımını ve doğrulamasını belgelemektedir.

Aktarım süreci salt bir programatik hücre okuma işlemi olarak ele alınmamış; **Computer Use** araçları kullanılarak elektronik tablo uygulamasındaki (ONLYOFFICE) görsel hiyerarşi, birleştirilmiş hücreler, çok satırlı haftalar ve takvimsel yerleşim incelenerek doğrulanmıştır.

### Temel Doğrulama Metrikleri

| Metrik | Beklenen Değer | Aktarılan Değer | Doğrulama Durumu |
| :--- | :---: | :---: | :---: |
| **Toplam Öğretim Haftası** | 37 Hafta | 37 Hafta | Doğrulandı (Eksiksiz) |
| **Haftalık Ders Saati** | 5 Saat | 5 Saat | Doğrulandı (Tüm haftalar 5 saat) |
| **Toplam Yıllık Ders Saati** | 185 Saat | 185 Saat | Doğrulandı ($37 \times 5 = 185$) |
| **Toplam Ders Bölümü (Lessons)** | 45 Bölüm | 45 Bölüm | Doğrulandı (8 hafta çift bölümlü) |
| **Müfredat Temaları** | 4 Tema + 1 Yıl Sonu | 4 Tema + 1 Yıl Sonu | Doğrulandı (Her ana tema 45 saat) |
| **Akademik Tatil Blokları** | 3 Tatil | 3 Tatil | Doğrulandı (1. ve 2. Ara Tatil + Yarıyıl) |
| **Belirli Gün ve Haftalar** | 11 Kayıt | 11 Kayıt | Doğrulandı (Tüm özel günler eşleşti) |
| **Birleştirilmiş Hücre Aralıkları** | 49 Aralık | 49 Aralık | Doğrulandı (Tümü çözümlendi) |
| **JSON Schema Doğrulaması** | Draft 2020-12 | Geçerli (0 Hata) | Otomatik Test Edildi |

---

## 2. Computer Use ile Elektronik Tablo Görsel İncelemesi

Electronic spreadsheet ortamında (ONLYOFFICE) gerçekleştirilen görsel kontrollerde tablonun fiziksel yerleşimi ile semantik anlamı arasındaki şu kritik ilişkiler teyit edilmiştir:

1. **Hafta ve Satır Hiyerarşisi:**
   - Tablonun ilk 3 satırı üst başlık ve sütun hiyerarşisine ayrılmıştır (1. satır çerçeve başlığı, 2. satır üst tema/pedagoji grupları, 3. satır sütun adları).
   - Öğretim haftaları 4. satırdan başlayıp 47. satırda tamamlanmaktadır. 48-53 arasındaki satırlar tamamen boş ızgara satırlarıdır.

2. **İki Fiziksel Satıra Yayılan Haftaların Tespiti (9, 18, 27 ve 36. Haftalar):**
   - Görsel incelemede her temanın son haftasının B sütununda iki satır boyunca dikey olarak birleştirildiği (ör. B12:B13, B23:B24, B35:B36, B45:B46) görülmüştür.
   - İlk satırda 3 saatlik "Yazma" dersi yer almakta; alt satırda ise C sütununda 2 saat ve D'den L sütununa kadar uzanan yeşil renkli **`OKUL TEMELLİ PLANLAMA*`** bandı yer almaktadır.
   - Bu durum görsel olarak doğrulanmış ve bu satırların iki farklı hafta değil, aynı haftanın 3+2 saatlik iki ayrı ders bölümü (`lessons`) olduğu modele doğru şekilde yansıtılmıştır.

3. **Hücre İçi "3+2" Saat Dağılımı Olan Haftalar (7, 16, 25 ve 34. Haftalar):**
   - Tek bir fiziksel satırda yer almasına rağmen C sütununda metin olarak `"3+2"` yazan haftalar incelenmiştir.
   - Bu haftalarda E (Konu), F (Öğrenme Çıktıları) ve G (Süreç Bileşenleri) sütunlarının üst bölümünde 3 saatlik **Dinleme/İzleme**, alt bölümünde ise 2 saatlik **Yazma** içeriklerinin yer aldığı hem formül çubuğu hem de hücre metni üzerinden görsel olarak kanıtlanmıştır.

4. **Yatay ve Dikey Renkli Tatil Bantları (14, 25 ve 30. Satırlar):**
   - 14. satır: `1. DÖNEM ARA TATİLİ: 16 - 20 Kasım 2026` (A14:L14 birleştirilmiş mavi bant)
   - 25. satır: `YARIYIL TATİLİ: 25 Ocak - 5 Şubat 2027` (A25:L25 birleştirilmiş mavi bant)
   - 30. satır: `2. DÖNEM ARA TATİLİ: 8-12 Mart 2027` (A30:L30 birleştirilmiş mavi bant)
   - Bu satırların bağımsız birer tatil kesintisi olduğu, alt ve üstlerindeki haftaların ders bütünlüğünü bölmediği doğrulanmıştır.

5. **37. Hafta Yıl Sonu Sosyal Etkinlik Bandı (47. Satır):**
   - 37. Hafta satırında C'den L sütununa kadar birleştirilmiş sarı renkli **`SOSYAL ETKİNLİK`** hücresi yer almaktadır. Haftalık süresi müfredat gereği 5 saattir.

---

## 3. Hafta Hafta Ders Saatleri ve Akış Doğrulama Tablosu

Aşağıdaki tablo, aktarılan 37 öğretim haftasının ders saati dengesini, tema dağılımını ve kaynak hücre satırlarını göstermektedir:

| Hafta | Tarih Aralığı | Ay | Satır | Tema | Ders 1 (Saat - Alan) | Ders 2 (Saat - Alan) | Toplam |
| :---: | :--- | :--- | :---: | :--- | :--- | :--- | :---: |
| **1** | 14-18 Eylül | EYLÜL | 4 | 1.TEMA | 5s OKUMA | - | **5** |
| **2** | 21-25 Eylül | EYLÜL | 5 | 1.TEMA | 5s OKUMA | - | **5** |
| **3** | 28 Eylül-2 Ekim | EYLÜL | 6 | 1.TEMA | 5s OKUMA | - | **5** |
| **4** | 5-9 Ekim | EKİM | 7 | 1.TEMA | 5s KONUŞMA | - | **5** |
| **5** | 12-16 Ekim | EKİM | 8 | 1.TEMA | 5s KONUŞMA | - | **5** |
| **6** | 19-23 Ekim | EKİM | 9 | 1.TEMA | 5s DİNLEME/İZLEME | - | **5** |
| **7** | 26-30 Ekim | EKİM | 10 | 1.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** |
| **8** | 2-6 Kasım | KASIM | 11 | 1.TEMA | 5s YAZMA | - | **5** |
| **9** | 9-13 Kasım | KASIM | 12, 13 | 1.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** |
| **10** | 23-27 Kasım | KASIM | 15 | 2.TEMA | 5s OKUMA | - | **5** |
| **11** | 30 Kasım-4 Aralık | ARALIK | 16 | 2.TEMA | 5s OKUMA | - | **5** |
| **12** | 7-11 Aralık | ARALIK | 17 | 2.TEMA | 5s OKUMA | - | **5** |
| **13** | 14-18 Aralık | ARALIK | 18 | 2.TEMA | 5s KONUŞMA | - | **5** |
| **14** | 21-25 Aralık | ARALIK | 19 | 2.TEMA | 5s KONUŞMA | - | **5** |
| **15** | 28 Aralık -1Ocak | ARALIK | 20 | 2.TEMA | 5s DİNLEME/İZLEME | - | **5** |
| **16** | 4-8 Ocak | OCAK | 21 | 2.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** |
| **17** | 11-15 Ocak | OCAK | 22 | 2.TEMA | 5s YAZMA | - | **5** |
| **18** | 18-22 Ocak | OCAK | 23, 24 | 2.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** |
| **19** | 8-12 Şubat | ŞUBAT | 26 | 3.TEMA | 5s OKUMA | - | **5** |
| **20** | 15-19 Şubat | ŞUBAT | 27 | 3.TEMA | 5s OKUMA | - | **5** |
| **21** | 22-26 Şubat | ŞUBAT | 28 | 3.TEMA | 5s OKUMA | - | **5** |
| **22** | 1-5 Mart | MART | 29 | 3.TEMA | 5s KONUŞMA | - | **5** |
| **23** | 15-19 Mart | MART | 31 | 3.TEMA | 5s KONUŞMA | - | **5** |
| **24** | 22-26 Mart | MART | 32 | 3.TEMA | 5s DİNLEME/İZLEME | - | **5** |
| **25** | 29 Mart-2 Nisan | MART | 33 | 3.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** |
| **26** | 5-9 Nisan | NİSAN | 34 | 3.TEMA | 5s YAZMA | - | **5** |
| **27** | 12-16 Nisan | NİSAN | 35, 36 | 3.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** |
| **28** | 19-23 Nisan | NİSAN | 37 | 4.TEMA | 5s OKUMA | - | **5** |
| **29** | 26-30 Nisan | NİSAN | 38 | 4.TEMA | 5s OKUMA | - | **5** |
| **30** | 3-7 Mayıs | MAYIS | 39 | 4.TEMA | 5s OKUMA | - | **5** |
| **31** | 10-14 Mayıs | MAYIS | 40 | 4.TEMA | 5s KONUŞMA | - | **5** |
| **32** | 17-21 Mayıs | MAYIS | 41 | 4.TEMA | 5s KONUŞMA | - | **5** |
| **33** | 24-28 Mayıs | MAYIS | 42 | 4.TEMA | 5s DİNLEME/İZLEME | - | **5** |
| **34** | 31 Mayıs-4 Haziran | HAZİRAN | 43 | 4.TEMA | 3s DİNLEME/İZLEME | 2s YAZMA | **5** |
| **35** | 7-11 Haziran | HAZİRAN | 44 | 4.TEMA | 5s YAZMA | - | **5** |
| **36** | 14-18 Haziran | HAZİRAN | 45, 46 | 4.TEMA | 3s YAZMA | 2s OKUL TEMELLİ PLANLAMA | **5** |
| **37** | 21-25 Haziran | HAZİRAN | 47 | YIL SONU | 5s SOSYAL ETKİNLİK | - | **5** |

**Yıllık Toplam Ders Saati:** **185 Saat** (Doğrulandı)

---

## 4. Birleştirilmiş Hücreler (Merged Cells) Denetimi

Excel dosyasında bulunan **49 adet birleştirilmiş hücre aralığı** ve bunların JSON modelindeki temsili aşağıda listelenmiştir:

| No | Hücre Aralığı | Satır Aralığı | Sütun Aralığı | Kaynak Başlık / Değer Özeti | JSON Temsili |
| :---: | :---: | :---: | :---: | :--- | :--- |
| 1 | `A1:M1` | 1 - 1 | Col 1 - 13 | 2026-2027 EĞİTİM ÖĞRETİM YILI 11. SINIF FEN L | Başarıyla Eşleştirildi |
| 2 | `A2:C2` | 2 - 2 | Col 1 - 3 | SÜRE | Başarıyla Eşleştirildi |
| 3 | `D2:E2` | 2 - 2 | Col 4 - 5 | ÜNİTE/TEMA - İÇERİK ÇERÇEVESİ | Başarıyla Eşleştirildi |
| 4 | `F2:G2` | 2 - 2 | Col 6 - 7 | ÖĞRENME ÇIKTILARI VE SÜREÇ BİLEŞENLERİ | Başarıyla Eşleştirildi |
| 5 | `I2:K2` | 2 - 2 | Col 9 - 11 | PROGRAMLAR ARASI BİLEŞENLER | Başarıyla Eşleştirildi |
| 6 | `A4:A6` | 4 - 6 | Col 1 - 1 | EYLÜL | Başarıyla Eşleştirildi |
| 7 | `H4:H12` | 4 - 12 | Col 8 - 8 | Metin Tahlili (Anlama) Okuma ve Dinleme/İzlem | Başarıyla Eşleştirildi |
| 8 | `I4:I12` | 4 - 12 | Col 9 - 9 | SDB1.2. Kendini Düzenleme (Öz Düzenleme) SDB2 | Başarıyla Eşleştirildi |
| 9 | `J4:J12` | 4 - 12 | Col 10 - 10 | D4. Dostluk D14. Saygı D16. Sorumluluk D19. V | Başarıyla Eşleştirildi |
| 10 | `K4:K12` | 4 - 12 | Col 11 - 11 | OB1. Bilgi Okuryazarlığı OB5. Kültür Okuryaza | Başarıyla Eşleştirildi |
| 11 | `M4:M47` | 4 - 47 | Col 13 - 13 | Türkiye Yüzyılı Maarif Modeli Öğretim Program | Başarıyla Eşleştirildi |
| 12 | `N4:N47` | 4 - 47 | Col 14 - 14 | Okul temelli planlama; zümre öğretmenler kur | Başarıyla Eşleştirildi |
| 13 | `A7:A10` | 7 - 10 | Col 1 - 1 | EKİM | Başarıyla Eşleştirildi |
| 14 | `A11:A13` | 11 - 13 | Col 1 - 1 | KASIM | Başarıyla Eşleştirildi |
| 15 | `B12:B13` | 12 - 13 | Col 2 - 2 | 9. Hafta: 9-13 Kasım | Başarıyla Eşleştirildi |
| 16 | `D13:L13` | 13 - 13 | Col 4 - 12 | OKUL TEMELLİ PLANLAMA* | Başarıyla Eşleştirildi |
| 17 | `A14:L14` | 14 - 14 | Col 1 - 12 | 1. DÖNEM ARA TATİLİ: 16 - 20 Kasım 2026 | Başarıyla Eşleştirildi |
| 18 | `H15:H23` | 15 - 23 | Col 8 - 8 | Metin Tahlili (Anlama) Okuma ve Dinleme/İzlem | Başarıyla Eşleştirildi |
| 19 | `I15:I23` | 15 - 23 | Col 9 - 9 | SDB1.2. Kendini Düzenleme (Öz Düzenleme) SDB1 | Başarıyla Eşleştirildi |
| 20 | `J15:J23` | 15 - 23 | Col 10 - 10 | D1. Adalet D7. Estetik D11. Özgürlük D14. Say | Başarıyla Eşleştirildi |
| 21 | `K15:K23` | 15 - 23 | Col 11 - 11 | OB1. Bilgi Okuryazarlığı OB2. Dijital Okuryaz | Başarıyla Eşleştirildi |
| 22 | `A16:A20` | 16 - 20 | Col 1 - 1 | ARALIK | Başarıyla Eşleştirildi |
| 23 | `A21:A23` | 21 - 23 | Col 1 - 1 | OCAK | Başarıyla Eşleştirildi |
| 24 | `B23:B24` | 23 - 24 | Col 2 - 2 | 18. Hafta: 18-22 Ocak | Başarıyla Eşleştirildi |
| 25 | `D24:L24` | 24 - 24 | Col 4 - 12 | OKUL TEMELLİ PLANLAMA* | Başarıyla Eşleştirildi |
| 26 | `A25:L25` | 25 - 25 | Col 1 - 12 | YARIYIL TATİLİ: 25 Ocak - 5 Şubat 2027 | Başarıyla Eşleştirildi |
| 27 | `A26:A28` | 26 - 28 | Col 1 - 1 | ŞUBAT | Başarıyla Eşleştirildi |
| 28 | `H26:H29` | 26 - 29 | Col 8 - 8 | Metin Tahlili (Anlama) Okuma ve Dinleme/İzlem | Başarıyla Eşleştirildi |
| 29 | `I26:I29` | 26 - 29 | Col 9 - 9 | SDB1.2. Kendini Düzenleme (Öz Düzenleme) SDB1 | Başarıyla Eşleştirildi |
| 30 | `J26:J29` | 26 - 29 | Col 10 - 10 | D4. Dostluk D5. Duyarlılık D7. Estetik D14. S | Başarıyla Eşleştirildi |
| 31 | `K26:K29` | 26 - 29 | Col 11 - 11 | OB2. Dijital Okuryazarlık OB4. Görsel Okuryaz | Başarıyla Eşleştirildi |
| 32 | `A30:L30` | 30 - 30 | Col 1 - 12 | 2. DÖNEM ARA TATİLİ: 8-12 Mart 2027 | Başarıyla Eşleştirildi |
| 33 | `A31:A33` | 31 - 33 | Col 1 - 1 | MART | Başarıyla Eşleştirildi |
| 34 | `H31:H35` | 31 - 35 | Col 8 - 8 | Metin Tahlili (Anlama) Okuma ve Dinleme/İzlem | Başarıyla Eşleştirildi |
| 35 | `I31:I35` | 31 - 35 | Col 9 - 9 | SDB1.2. Kendini Düzenleme (Öz Düzenleme) SDB1 | Başarıyla Eşleştirildi |
| 36 | `J31:J35` | 31 - 35 | Col 10 - 10 | D4. Dostluk D5. Duyarlılık D7. Estetik D14. S | Başarıyla Eşleştirildi |
| 37 | `K31:K35` | 31 - 35 | Col 11 - 11 | OB2. Dijital Okuryazarlık OB4. Görsel Okuryaz | Başarıyla Eşleştirildi |
| 38 | `A34:A38` | 34 - 38 | Col 1 - 1 | NİSAN | Başarıyla Eşleştirildi |
| 39 | `B35:B36` | 35 - 36 | Col 2 - 2 | 27. Hafta: 12-16 Nisan | Başarıyla Eşleştirildi |
| 40 | `D36:L36` | 36 - 36 | Col 4 - 12 | OKUL TEMELLİ PLANLAMA* | Başarıyla Eşleştirildi |
| 41 | `H37:H45` | 37 - 45 | Col 8 - 8 | Metin Tahlili (Anlama) Okuma ve Dinleme/İzlem | Başarıyla Eşleştirildi |
| 42 | `I37:I45` | 37 - 45 | Col 9 - 9 | SDB1.1. Kendini Tanıma (Öz Farkındalık) SDB1. | Başarıyla Eşleştirildi |
| 43 | `J37:J45` | 37 - 45 | Col 10 - 10 | D1. Adalet D3. Çalışkanlık D6. Dürüstlük D14. | Başarıyla Eşleştirildi |
| 44 | `K37:K45` | 37 - 45 | Col 11 - 11 | OB2. Dijital Okuryazarlık OB5. Kültür Okuryaz | Başarıyla Eşleştirildi |
| 45 | `A39:A42` | 39 - 42 | Col 1 - 1 | MAYIS | Başarıyla Eşleştirildi |
| 46 | `A43:A47` | 43 - 47 | Col 1 - 1 | HAZİRAN | Başarıyla Eşleştirildi |
| 47 | `B45:B46` | 45 - 46 | Col 2 - 2 | 36. Hafta: 14-18 Haziran | Başarıyla Eşleştirildi |
| 48 | `D46:L46` | 46 - 46 | Col 4 - 12 | OKUL TEMELLİ PLANLAMA* | Başarıyla Eşleştirildi |
| 49 | `C47:L47` | 47 - 47 | Col 3 - 12 | SOSYAL ETKİNLİK | Başarıyla Eşleştirildi |


---

## 5. İnceleme Kuyruğu (Review Queue) ve Kaynak Tutarsızlıkları

Kaynak Excel tablosunda tespit edilen ve pedagojik ya da takvimsel bütünlük açısından özel inceleme gerektiren hususlar `review-queue.json` dosyasına işlenmiştir. Özeti aşağıdadır:

1. **RQ-001 (Hücre İçi "3+2" Saat Dağılımı):**
   - **Haftalar:** 7 (R10), 16 (R21), 25 (R33), 34 (R43).
   - **Durum:** C sütununda ders saati `"3+2"` olarak belirtilmiştir.
   - **Çözüm:** Modelde `lessons` dizisi altında 3 saat Dinleme/İzleme ve 2 saat Yazma olarak iki ayrı ders kaydı oluşturulmuştur.

2. **RQ-002 (İki Fiziksel Satıra Yayılan Haftalar):**
   - **Haftalar:** 9 (R12-R13), 18 (R23-R24), 27 (R35-R36), 36 (R45-R46).
   - **Durum:** B sütunundaki hafta hücresi iki satır boyunca birleştirilmiş, 2. satırda yeşil zeminli `OKUL TEMELLİ PLANLAMA*` yer almıştır.
   - **Çözüm:** İki satır tek bir üst hafta nesnesinde toplanmış; Lesson 1 (3 saat Yazma) ve Lesson 2 (2 saat Okul Temelli Planlama) olarak modellenmiştir.

3. **RQ-003 (37. Hafta Saat Hücresinde Metin Başlığı):**
   - **Hafta:** 37 (R47).
   - **Durum:** C47:L47 birleştirilmiş hücresinde sayısal ders saati yerine doğrudan `SOSYAL ETKİNLİK` yazmaktadır.
   - **Çözüm:** MEB haftalık ders dağılım standardı olan 5 saat temel alınmış; durum metadata notlarında açıklanmıştır.

4. **RQ-004 (22. Hafta Belirli Günler Takvimsel Çelişkisi):**
   - **Hafta:** 22 (R29, L29 hücresi).
   - **Durum:** Hücrede `İstiklâl Marşı'nın Kabulü ve Mehmet Akif Ersoy'u Anma Günü (12 Mart)` yazmaktadır. 22. Haftanın tarih aralığı ise 1-5 Mart 2027'dir. 12 Mart tarihi bir sonraki hafta olan 2. Dönem Ara Tatili'ne (8-12 Mart) denk gelmektedir.
   - **Çözüm:** Kaynak veriye sadakat ilkesi gereği anma günü değiştirilmemiş, 22. haftadaki özgün yerinde tutulmuş ve takvimsel durum raporlanmıştır.

5. **RQ-005 (23. Hafta Belirli Günler Boşluk ve Çift Etkinlik Düzensizliği):**
   - **Hafta:** 23 (R31, L31 hücresi).
   - **Durum:** Hücrede hem Türk Dünyası ve Toplulukları Haftası (16-22 Mart) hem Şehitler Haftası (18 Mart) aşırı satır başı (`\n`) boşluklarıyla yer almaktadır.
   - **Çözüm:** Özgün metin tam olarak korunmuş; yapılandırılmış alanda iki etkinlik net olarak ayrıştırılmıştır.

6. **RQ-006 (Farklılaştırma ve Okul Temelli Planlama Sütunlarının Tablo Boyunca Birleştirilmesi):**
   - **Aralık:** M4:M47 ve N4:N47.
   - **Durum:** Bu sütunlar 37 hafta boyunca tek bir hücre olarak birleştirilmiştir.
   - **Çözüm:** Genel açıklamalar tekil olarak `general_explanations` içinde saklanmış, her haftanın `pedagogical_context` alanından metadata referansı verilmiştir.

7. **RQ-007 (Pedagojik Bileşenlerin Tema Düzeyinde Birleştirilmesi):**
   - **Sütunlar:** H, I, J, K (Ölçme, SDB, Değerler, Okuryazarlık).
   - **Durum:** Tema blokları boyunca birleştirilmiştir (3. temada ara tatil nedeniyle iki blok halindedir).
   - **Çözüm:** Temalar düzeyinde ortak nesneler tanımlanmış, her haftaya kaynak aralık referansı eklenmiştir.

8. **RQ-008 (1. Tema SDB Becerilerinde Noktalama ve Boşluk Düzensizliği):**
   - **Hücre:** I4.
   - **Durum:** `SDB3.2 Esneklik` ifadesinde nokta eksiktir ve satır sonu yerine boşluklarla ayrılmıştır.
   - **Çözüm:** Özgün metin korunmuş, ayrıştırılan kod listesinde `SDB3.2` standart biçimde sunulmuştur.

9. **RQ-009 (Fen Lisesi ile Anadolu Lisesi Çalışma Sayfası Başlık Farkı):**
   - **Hücre:** A1.
   - **Durum:** Fen Lisesi dosyasında A1 hücresinde `FEN LİSESİ` ibaresi yer almakta, sekme adı `11. SINIF` olarak geçmektedir.
   - **Çözüm:** Görev kapsamı doğrultusunda doğrudan Fen Lisesi çalışma sayfası işlenmiştir.

---

## 6. Veri Bütünlüğü ve Metin Koruma Kontrolleri

- **Kazanım Kodları:** TDE1.1'den TDE4.4'e kadar tüm öğrenme çıktı kodları özgün biçimleriyle korunmuştur.
- **Süreç Bileşenleri:** Alt bileşenler (`a) TDE...`, `b) TDE...`) ve madde imleri (`• ...`) hiyerarşik yapıya aktarılmış, özgün metin dizesi (`process_components_raw`) eksiksiz saklanmıştır.
- **Pedagojik Metinler:** Ölçme ve değerlendirme açıklamaları, farklılaştırma metni ve okul temelli planlama ilkeleri tek bir karakter dahi eksiltilmeden veya özetlenmeden UTF-8 standardında aktarılmıştır.
- **JSON Schema Testi:** `annual-plan.schema.json` şeması hazırlanmış ve `jsonschema.validate` fonksiyonu ile test edilerek 0 hata ile geçerlilik almıştır.

---

## 7. Teslim Edilen Dosyalar

`output/grade-11/` dizini altında üretilen dosyalar:

1. `annual-plan.json` — 11. Sınıf Fen Lisesi eksiksiz, hafta merkezli, ders bölümlerini destekleyen yıllık plan verisi.
2. `annual-plan.schema.json` — JSON Schema Draft 2020-12 standardına uygun resmi veri şeması.
3. `validation-report.md` — Bu kapsamlı doğrulama ve aktarım raporu.
4. `review-queue.json` — Manuel inceleme ve özel durum kayıtlarını içeren yapılandırılmış kuyruk dosyası.
