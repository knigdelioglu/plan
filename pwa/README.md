# Haftalık Plan PWA — Çalışma Alanı

Bu klasör `knigdelioglu/plan` deposunun **uygulama alanıdır**. Yıllık planların asıl veri kaynağı `../plans/` altındadır; burada değiştirilmez.

## Hedef

Öğretmenlerin seçtikleri öğretim yılı/ders/okul türü/sınıf için haftalık planı görüntülediği, her şubede işlenen ders saatlerini ve kaldığı yeri ayrı takip edebildiği, çevrimdışı çalışabilen PWA.

## Öngörülen teknoloji

- React 19 + TypeScript + Vite
- Arc Library tasarım temeli ve kaynak bileşenleri (`src/components/arc/`)
- IndexedDB tabanlı kişisel şube ve ilerleme kayıtları
- Service worker ile PWA çevrimdışı desteği

## Klasörler

```text
pwa/
├── AGENTS.md                 # PWA ajan kuralları; plans/ salt okunur
├── README.md
├── public/
│   └── data/                 # Derleme sırasında üretilen, PWA'ya özgü salt okunur plan kataloğu
├── scripts/                  # Arşivden okuyan dönüşüm / doğrulama betikleri
├── src/
│   ├── app/                  # Uygulama kabuğu, yönlendirme ve sağlayıcılar
│   ├── components/
│   │   └── arc/              # Kurulan/uyarlanan Arc bileşenlerinin kaynakları
│   ├── features/
│   │   ├── catalog/          # Yıl / ders / sınıf / okul türü seçimi
│   │   ├── classes/          # Öğretmenin şubeleri
│   │   ├── weeks/            # Haftalık plan görünümü
│   │   └── progress/         # Şube bazlı ilerleme
│   ├── lib/
│   │   ├── data/             # Plan okuma ve uyarlama
│   │   └── storage/          # Yerel öğretmen verisi
│   ├── styles/               # Arc Foundation ve uygulama teması
│   └── types/                # Uygulama tipleri
└── tests/                    # PWA testleri
```

**Durum:** Bu aşamada yalnızca klasör iskeleti ve çalışma kapsamları hazırlanmıştır. Henüz npm paketi, çalışan PWA, derleme veya dağıtım kurulumu yoktur.

## Kapsam kuralları

PWA geliştirirken `plans/**` salt okunurdur. Veri arşivi üzerinde çalışırken de `pwa/**` değiştirilemez. İlgili kurallar [PWA AGENTS.md](AGENTS.md) ve [kökteki SCOPES.md](../SCOPES.md) içindedir.

11. sınıf Anadolu Lisesi klasöründeki JSON'un kaynağı Fen Lisesidir; doğrulanmış Anadolu planı gibi gösterilemez.
