# Haftalık Plan — React Native + Expo

Bu klasör `knigdelioglu/plan` deposundaki **yeni mobil uygulama çalışma alanıdır**. Uygulama **React Native + Expo + TypeScript** ile sıfırdan yazılacaktır.

Kaynak yıllık planlar `../plans/**` içindedir. Mobil uygulama bu verileri **salt okunur** kullanır; `plans/**` altında JSON, şema veya doğrulama kayıtlarını değiştirmez.

## Hedef işlevler

- Ders, sınıf düzeyi, okul türü ve öğretim yılına göre haftalık planları gösterme
- Bir öğretmenin birden fazla şubesini tanımlama; her şubenin gerçek ilerlemesini ayrı saklama
- 3+2 gibi ders bölümlerinde ayrı işlenen saat takibi; kaldığım yer ve kısa ders notları
- Android/iOS için yerel arayüz, çevrimdışı kullanım ve güvenli yerel kayıt
- Veri kaynağı kimliği ve doğrulama durumuna göre planları güvenilir biçimde yayımlama

## Planlanan klasörler

```text
mobile/
├── AGENTS.md             # Bu alanın yazma sınırları
├── README.md
├── app/                 # Expo Router ekranları (kurulumla)
├── assets/              # Uygulama görselleri
├── src/
│   ├── features/
│   │   ├── catalog/      # Yıllık plan seçimi
│   │   ├── classes/      # Şubeler
│   │   ├── weeks/        # Haftalık plan
│   │   └── progress/     # Şube ilerlemesi
│   ├── lib/
│   │   ├── data/         # Planlar için salt okunur uyarlayıcı
│   │   └── storage/      # Öğretmen kişisel verileri
│   └── theme/            # React Native tasarım değişkenleri
├── scripts/              # Plan kataloğu üretimi (yalnızca okuma)
└── tests/                # Uygulama testleri
```

**Durum:** Bu aşamada eski web uygulaması kaldırıldı, yalnızca yeni mobil proje için alan açıldı. Henüz Expo projesi oluşturulmuş veya bağımlılık kurulmuş değildir.

Kapsam: [Mobil AGENTS.md](AGENTS.md), [Depo scope kuralları](../SCOPES.md).
