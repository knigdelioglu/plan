# Haftalık Plan PWA

`knigdelioglu/plan` deposunun **yalnızca `pwa/**` altında geliştirilen** React uygulaması. Kaynak yıllık plan JSON'ları `../plans/**` altında tutulur ve uygulama bunları değiştirmez.

## Başlangıç

Node.js 22+ önerilir. Depo kökünden:

```bash
cd pwa
npm install
npm run dev
```

Dev sunucusu başlamadan önce `scripts/build-plan-catalog.mjs` yalnızca `../plans` verilerini **okuyup** `pwa/public/data/catalog.json` üretir. Yayımlanabilir olan planlar kaynak okul türü / sınıf / yıl ile eşleşen ve hafta-ders saati temel kontrollerinden geçen kayıtlardır. Çelişkili kaynaklar uygulamaya alınmaz.

```bash
npm run test
npm run typecheck
npm run build
npm run preview
```

GitHub Pages gibi bir alt yola dağıtım için örnek: `BASE_PATH=/plan/ npm run build`. Dağıtım iş akışı henüz kurulmadı.

## Kabuğun mevcut işlevleri

- Responsive yan gezinme ve mobil alt gezinme: **Haftam / Sınıflarım / Planlar**
- Arc Library MIT kaynak bileşenleri, ortak foundation.css, hareket ve koyu/açık tema
- Kaynak JSON'lardan okunan plan kataloğu; haftalar, ders bölümleri, kazanımlar ve tatiller
- Plan ve hafta seçimi; gerçek veriden ders bölümü ayrıntısı; okul türü uyuşmazlığını dışlama
- Manifest + hizmet çalışanı hazırlığıyla çevrimdışı önbellekleme
- Şube ekleme/silme, başlangıç haftası belirleme, şube bazlı ayrı ilerleme kayıtları
- Her ders bölümü için 0–planlanan saat aralığında kayıt; 3+2 bölünmüş haftalarda bağımsız takip
- Kısa ders notları ve kaldığı yere gitme
- Dexie + IndexedDB ile cihazda kalıcı veri; veri kaydı tamamlanmadan başarı mesajı verilmez
- **Henüz bulut senkronizasyonu veya yedekleme/geri yükleme yoktur.** Tarayıcı verilerini silmek kişisel kayıtları kaybettirebilir.

## Arc Library

MIT lisanslı kaynaklar [kuratlielia/arc-library](https://github.com/kuratlielia/arc-library) deposundan alınmıştır; lisans `src/components/arc/LICENSE` dosyasındadır. İlk alınan bileşenler: Button, Badge, Tabs, Segmented Control, Progress. Diğerleri ihtiyaç oldukça `components.json` içindeki `@uiarc` registry'sinden eklenebilir.

## Mimari

```text
src/app/                      uygulama kabuğu ve ekran yönlendirmesi
src/components/arc/           özgün Arc kaynak bileşenleri ve foundation
src/features/weeks/           haftalık içerik
src/features/catalog/         kaynak plan kataloğu
src/features/classes/         şube oluşturma, silme ve ilerleme özeti
src/features/progress/        ders bazlı saat, not ve ilerleme hesaplaması
src/lib/storage/teacher-db.ts Dexie + IndexedDB kayıt katmanı
src/lib/motion-tokens.ts      Arc hareket değişkenleri
scripts/build-plan-catalog.mjs  salt okunur veri dönüştürme
public/data/catalog.json      üretilmiş PWA katalog verisi
```

Bu çalışma alanında yalnızca `pwa/**` yazılabilir. Ayrıntılı sınırlar [AGENTS.md](AGENTS.md) ve [../SCOPES.md](../SCOPES.md) içindedir.
