# PWA Alanı — Scope: pwa

Bu dosya `pwa/**` kapsamındaki bütün uygulama dosyaları için geçerlidir.

**Yazma izni:** Yalnızca `pwa/**`. PWA görevi sırasında `plans/**` **salt okunur kaynaktır**: yıllık plan JSON, şema, doğrulama raporu, inceleme kuyruğu ve klasörlerine dokunma, taşıma veya otomatik biçimlendirme uygulama.

## Amaç ve teknoloji

Öğretmenlere haftalık yıllık planı gösteren, şube bazında bağımsız ilerleme ve ders saati kaydı tutan, çevrimdışı çalışabilen PWA.
Hedef mimari React + TypeScript + Vite, Arc Library (ücretsiz MIT bileşenleri), IndexedDB. Uygulama henüz oluşturulmadı; bu klasörde şu an yalnızca hazırlık iskeleti bulunur.

- Tasarım temeli: Arc Foundation, semantik tema değişkenleri ve motion tokens. Arc bileşenleri `src/components/arc/**` altında, uygulamaya özgü bileşenler `src/components/**` veya `src/features/**` altında tutulur. Azaltılmış hareket tercihini destekle.
- Kaynak yıllık planlar GitHub arşivinde `../plans/**` altında kalır. Okuma/dönüştürme gerekiyorsa betik ve türetilmiş kataloglar **yalnızca `pwa/**`** içinde oluşturulur; `../plans/**` alanına yazılmaz.
- Okul türü ve plan kaynağı doğrulanmadan planı kullanılabilir ilan etme. Mevcut 11. sınıf Anadolu Lisesi klasöründeki planın kaynak kimliği Fen Lisesidir; yanlış etiketle yayımlama.
- Öğretmenin kişisel şube, ilerleme ve not kayıtlarını ortak plan JSON'larına ekleme; bunları uygulamanın kişisel veri katmanında ayrı tut.
- Haftayı ve ders bölümünü karıştırma: bir haftada `3+2` gibi birden çok ders bölümü olabilir; tatil haftaları da kaynaktan ayrı okunur.
- PWA'yı düzeltmek için veri arşivini değiştirmek yerine uyarlayıcı/normalizasyon kodunu PWA alanında düzelt. Gerçek veri hatasını tespit edersen yalnızca raporla; veri düzeltmesi için ayrı `data` kapsamlı görev gerekir.
- Test, stil, bağımlılık, dağıtım ve otomasyon dosyalarını mümkün olduğunca `pwa/**` altında tut. Kök veya `.github/**` dosyalarını kullanıcı açıkça kapsam olarak vermedikçe değiştirme.
- Teslim öncesinde `git diff --name-only` ile `plans/**` dosyalarının PWA görevi sırasında değişmediğini doğrula.
