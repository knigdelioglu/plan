# Plan — Öğretmenler İçin Yıllık Plan Arşivi

Bu depo, öğretmenlerin **yıllık ders planlarını JSON biçiminde**, öğretim yılı, ders, sınıf ve okul türüne göre düzenli ve yeniden kullanılabilir şekilde saklamak için oluşturulmuştur.

Amaç; yıllık planları uygulamalar, dijital ders araçları ve öğretmen iş akışları tarafından okunabilir kılarken **kaynak belgedeki içerik, sıra ve izlenebilirliği korumaktır**. Depo bir yıllık plan oluşturma uygulaması değil, sürümlenebilir veri arşividir.

## Klasör düzeni

```text
plans/
└── 2026-2027/
    └── turk-dili-ve-edebiyati/
        ├── 9/
        │   ├── anadolu-lisesi/
        │   ├── fen-lisesi/
        │   └── sosyal-bilimler-lisesi/
        ├── 10/
        │   ├── anadolu-lisesi/
        │   ├── fen-lisesi/
        │   └── sosyal-bilimler-lisesi/
        ├── 11/
        │   ├── anadolu-lisesi/
        │   ├── fen-lisesi/
        │   │   ├── annual-plan.json
        │   │   ├── annual-plan.schema.json
        │   │   ├── validation-report.md
        │   │   └── review-queue.json
        │   └── sosyal-bilimler-lisesi/
        └── 12/
            ├── anadolu-lisesi/
            ├── fen-lisesi/
            └── sosyal-bilimler-lisesi/
```

**Dizin kuralı:** `plans/<YYYY-YYYY>/<ders-kodu>/<sinif>/<okul-turu>/`

- **Öğretim yılı:** Başlangıç ve bitiş yılı; ör. `2026-2027`.
- **Ders:** Türkçe karakter içermeyen, küçük harfli, kısa çizgili ad; ör. `turk-dili-ve-edebiyati`.
- **Sınıf:** Sayısal düzey; ör. `9`, `10`, `11`, `12`.
- **Okul türü:** Planın geçerli olduğu tür; `anadolu-lisesi`, `fen-lisesi`, `sosyal-bilimler-lisesi`. Birbirinden farklı planlar **ayrı klasörlerde** tutulur.
- Aynı düzey için farklı sürümler gerektiğinde planların üzerine yazmak yerine ayrı ve açıkça tanımlanmış varyantlar kullanılmalıdır.

Şimdilik **2026-2027 Türk Dili ve Edebiyatı** dersi için 9–12. sınıfların her birinde **Anadolu Lisesi, Fen Lisesi ve Sosyal Bilimler Lisesi** klasörleri açılmıştır (toplam 12 okul türü/sınıf kombinasyonu). **11. sınıf Fen Lisesi** için plan ve doğrulama dosyaları vardır. **11. sınıf Anadolu Lisesi klasörüne de dosyalar eklenmiştir; ancak içerikleri şu anda Fen Lisesi kaynağından üretildiği için doğrulanmış Anadolu Lisesi planı değildir.** Diğer klasörlerdeki `.gitkeep` dosyaları dizinleri Git'te korumak içindir; **yıllık plan verisi içermez**.

## Mevcut plan

**[11. sınıf — Fen Lisesi — 2026–2027](plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi/annual-plan.json)**

Kaynak: 2026–2027 11. sınıf Fen Lisesi Türk Dili ve Edebiyatı çerçeve yıllık planının `11. SINIF` çalışma sayfası.

| Özellik | Değer |
| --- | --- |
| Öğretim haftası | 37 |
| Haftalık ders saati | 5 |
| Yıllık toplam | 185 saat |
| Ders bölümü (`lessons`) | 45 |
| Ana tema | 4 |
| Akademik tatil aralığı | 3 |

Bu planın yanında [JSON şeması](plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi/annual-plan.schema.json), [aktarım ve doğrulama raporu](plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi/validation-report.md) ve [özel durum / inceleme kayıtları](plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi/review-queue.json) bulunur.

### JSON veri modeli

`annual-plan.json` aşağıdaki ana alanlardan oluşur:

| Alan | İçerik |
| --- | --- |
| `metadata` | Öğretim yılı, sınıf, ders, okul türü, kaynak dosya/çalışma sayfası, saat bilgileri |
| `general_explanations` | Farklılaştırma ve okul temelli planlama genel açıklamaları |
| `academic_calendar_breaks` | Ara tatil ve yarıyıl tatili blokları |
| `themes` | Tema bilgileri, ölçme-değerlendirme ve programlar arası bileşenler |
| `weeks` | Hafta tarihleri, ders saati, tema, özel günler, pedagojik bağlam ve ders bölümleri |

**Hafta ile ders bölümü farklı kavramlardır.** Bir haftada birden çok ders bölümü bulunabilir: örneğin `3+2` saatlik dağılım, `weeks[].lessons` içinde iki kayıtla temsil edilir. Fiziksel Excel satırları tek başına hafta sınırı olarak kabul edilmez. `source_metadata`, `*_raw` ve hücre aralığı bilgileri kaynakla karşılaştırma için korunur.

**Şema kapsamı:** Şu an yayımlanan `annual-plan.schema.json`, 11. sınıf örneğine özgü doğrulama kurallarını (ör. 37 hafta ve 5 saat) içerir. Diğer sınıflara veya derslere **evrensel şema** olarak uygulanmamalıdır.

## Doğrulama

Python ve [jsonschema](https://pypi.org/project/jsonschema/) kuruluysa mevcut planı kontrol etmek için:

```bash
python -m pip install jsonschema
python - <<'PY'
import json
from pathlib import Path
from jsonschema import Draft202012Validator, FormatChecker

folder = Path("plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi")
data = json.loads((folder / "annual-plan.json").read_text(encoding="utf-8"))
schema = json.loads((folder / "annual-plan.schema.json").read_text(encoding="utf-8"))
Draft202012Validator(schema, format_checker=FormatChecker()).validate(data)

assert data["metadata"]["grade_level"] == 11
assert data["metadata"]["academic_year"] == "2026-2027"
assert len(data["weeks"]) == 37
assert sum(week["total_hours"] for week in data["weeks"]) == 185
assert all(sum(lesson["hours"] for lesson in week["lessons"]) == week["total_hours"] for week in data["weeks"])
print("Şema ve ders saati kontrolleri başarılı.")
PY
```

**Önemli:** Şemadan geçmek, kaynak belgenin pedagojik ve takvimsel açıdan kusursuz olduğu anlamına gelmez. Örnek dosyanın `review-queue.json` kaydında kaynakta korunmuş takvim çelişkisi ve kaynaktan çıkarımla belirlenen saat gibi durumlar da belgelenmiştir. Bu ayrımlar otomatik olarak silinmemeli veya kaynağa danışmadan değiştirilmemelidir.

### Özel günlerin planlanması ve kaynak doğruluğu

**Takvimdeki anma tarihi ile okuldaki uygulama haftası ayrıdır.** Örneğin 15 Temmuz Demokrasi ve Millî Birlik Günü anması, yaz tatili nedeniyle yıllık planda okulun açılış haftasına yerleştirilebilir. Bu, tarih farkı nedeniyle otomatik olarak `calendar_anomaly` sayılmamalıdır. `date_text`/`commemoration_date` ile `scheduled_week` ayrı korunmalı, kesin etkinlik günü kaynakta yoksa uydurulmamalıdır. `is_calendar_aligned=false` literal tarih karşılaştırmasını, `has_calendar_discrepancy=false` ise planlı ve geçerli hafta yerleşimini gösterebilir. Diğer özel günlerde de tarih farkının sebebi (tatil, hafta sonu, planlama kararı) incelenmeden hata veya otomatik düzeltme kararı verilmemelidir.

**Okul türü klasör adına göre değil özgün belgeye göre belirlenir.** Şu an `11/anadolu-lisesi/` altındaki JSON'un kaynağı Fen Lisesidir ve veride açık uyarı bulunur. Bu dosya Anadolu Lisesi kaynağıyla değiştirilip ayrıca doğrulanmadan Anadolu planı olarak kullanılmamalıdır.

Daha sonraki JSON aktarımları için ajana yönelik kalıcı ilkeler kökteki [AGENTS.md](AGENTS.md) belgesindedir.

## Yeni plan ekleme ilkeleri

1. Planı doğru öğretim yılı / ders / sınıf / okul türü klasörüne yerleştirin. Başka sınıfın veya okul türünün verisini kopyalayıp yalnızca `metadata` değiştirerek yeni plan üretmeyin.
2. `annual-plan.json` içinde kaynak kimliğini ve özgün metinleri koruyun. Birleştirilmiş hücreler, bölünmüş ders saatleri ve tatil aralıklarını semantik açıdan doğru modelleyin.
3. Planı uygun JSON Schema ile doğrulayın; hafta, tarih ve ders saati toplamlarını denetleyin. Belirsizlikleri ve kaynakla ilgili uyuşmazlıkları ayrı inceleme kayıtlarında açıkça tutun.
4. Mümkünse planla birlikte şemasını, doğrulama raporunu ve inceleme kayıtlarını ekleyin. İleride farklı müfredat şemaları yan yana bulunabilir.

## Kapsam ve sorumluluk

Bu arşiv, kaynak belgelerin **dijital aktarımı ve düzenlenmesi** amacıyla hazırlanmıştır; resmî MEB hizmeti veya resmî onay anlamına gelmez. Yayımlanan örnek bir **çerçeve/taslak yıllık plan** aktarımıdır. Kullanım öncesinde güncel öğretim programı, eğitim takvimi ve zümre kararlarıyla uyumluluk kontrol edilmelidir.
