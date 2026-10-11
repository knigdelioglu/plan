# Arc Library — Jetpack Compose Uyarlama ve Kapsam Matrisi

Bu belge, `arc-library` (Elia Kuratli, MIT Lisansı) kaynaklı 70 potansiyel bileşenin, 2 tasarım paketinin ve 9 bloğun **kaynak/envanter haritasıdır**. Bir satırın burada bulunması bileşenin Arc ile davranış eşdeğerliğini, uygulamada kullanıldığını veya cihazda test edildiğini kanıtlamaz.

**Envanter tarihi: 11 Ekim 2026.** Referans [Arc Library GitHub ana dalındaki README](https://github.com/kuratlielia/arc-library#readme) 108 bileşen ve 22 blok bildiriyor. Bu yerel Compose uyarlama matrisi 70 bileşen ve 9 blok kapsıyor; sayım farkı 38 bileşen ve 13 bloktur. Bu yalnızca sayısal kapsam farkıdır, isim isim birebir eşleme veya davranış eşdeğerliği iddiası değildir. Arc upstream React 19/Motion web kodudur; buradaki Kotlin/Compose dosyalarının varlığı native davranışın eşdeğer olduğunu göstermez.

`Uygulamadaki Kullanım Alanı` sütunu amaçlanan kullanım bağlamını anlatır; gerçekleşen kullanım, davranış uyumu ve test kanıtı için [P2_AUDIT.md](P2_AUDIT.md) içindeki tarihli durum tablosuna bakın.

**Gerçek P2 kapsam ve durum belgesi:** [P2_AUDIT.md](P2_AUDIT.md). Kişisel öğretmen uygulaması yalnızca **Haftalık, Aylık, Temalar, İlerleme** sekmelerini kullanır. Gereksiz denetim, karşılaştırma, JSON içe aktarma, yardım ve imza ekranları ürünün parçası değildir. Kod karşılıkları ortak kütüphanede bulunabilir; üründe kullanılacakmış gibi yorumlanmamalıdır.

---

## 1. Temel Altyapı Paketleri (2 Paket)

| Paket | Dosya Konumu | İçerik & Özellikler | Durum |
| --- | --- | --- | --- |
| **`arc-foundation`** | [`ArcTheme.kt`](app/src/main/java/com/plan/app/core/design/theme/ArcTheme.kt) | OKLCH renk paleti (Açık ve Koyu tema), Yüzey tonları (`surface`, `surfaceRaised`, `surfaceMuted`), Kenar çizgileri, Tipografi, 4dp boşluk sistemi (`ArcSpacing`), Yuvarlatma yarıçapları (`ArcRadius`). | Doğrulandı & Test Edildi |
| **`arc-motion-tokens`** | [`ArcMotionTokens.kt`](app/src/main/java/com/plan/app/core/design/motion/ArcMotionTokens.kt) | Süreler (`Instant`: 120ms, `Fast`: 160ms, `Standard`: 240ms, `Considered`: 480ms), Easing eğrileri (`Enter`, `Exit`, `Standard`, `InOut`), Yaylar (`Snappy`, `Smooth`, `Morph`, `Responsive`, `Gentle`), Kademeli gecikmeler (`Stagger`). | Doğrulandı & Test Edildi |

---

## 2. A. Çekirdek Bileşenler (30 Bileşen)

| # | Bileşen | Compose Karşılığı | Dosya Konumu | Uygulamadaki Kullanım Alanı |
|---|---|---|---|---|
| 1 | `Calendar` | `ArcCalendar` | `core/ArcCalendar.kt` | Aylık takvim, hafta seçimleri, etkinlik ve tatil işaretleme noktaları |
| 2 | `Tabs` | `ArcTabs` | `core/ArcNavigation.kt` | Pedagojik bağlam sekmeleri (Değerlendirme, SDB, Değerler, Farklılaştırma) |
| 3 | `Drawer` | `ArcDrawer` | `core/ArcNavigation.kt` | Yan gezinme çekmecesi ve bildirim paneli |
| 4 | `Resizable Panels` | `ArcResizablePanels` | `core/ArcNavigation.kt` | Tablet ve Samsung DeX için sürüklenebilir çok bölmeli sütun düzeni |
| 5 | `Scroll Area` | `ArcScrollArea` | `core/ArcNavigation.kt` | Pürüzsüz kaydırma alanı ve dokunmatik/fare desteği |
| 6 | `Bottom Sheet` | `ArcBottomSheet` | `core/ArcNavigation.kt` | Mobil ve dar pencerelerde ders detay alt çekmecesi |
| 7 | `Card` | `ArcCard` | `core/ArcCards.kt` | Temel içerik ve panel kartları |
| 8 | `Expandable Card` | `ArcExpandableCard` | `core/ArcCards.kt` | Genişletilebilir pedagojik açıklamalar ve kazanım kartları |
| 9 | `Accordion` | `ArcAccordion` | `core/ArcCards.kt` | Ders detay katmanları için kütüphane bileşeni; SSS ekranı kaldırıldı |
| 10 | `Badge` | `ArcBadge` | `core/ArcIndicators.kt` | Hafta numaraları, kazanım kodları (TDE2.1), durum etiketleri |
| 11 | `Tooltip` | `ArcTooltip` | `core/ArcIndicators.kt` | Butonlar ve kısayollar için fare/uzun dokunuş ipuçları |
| 12 | `Popover` | `ArcPopover` | `core/ArcIndicators.kt` | Hızlı işlem ve detay penceresi |
| 13 | `Segmented Control` | `ArcSegmentedControl` | `core/ArcControls.kt` | Dört görünüm: Haftalık, Aylık, Temalar, İlerleme; 48dp hedef ve yatay kaydırma |
| 14 | `Select` | `ArcSelect` | `core/ArcControls.kt` | Okul türü ve sınıf seçim açılır menüleri |
| 15 | `Combobox` | `ArcCombobox` | `core/ArcControls.kt` | Aranabilir ve filtrelenebilir seçim alanı |
| 16 | `Filter Toolbar` | `ArcFilterToolbar` | `core/ArcControls.kt` | Çok ölçütlü plan filtreleme araç çubuğu |
| 17 | `Chip Group` | `ArcChipGroup` | `core/ArcControls.kt` | Tema, ay ve özel gün filtre çipleri |
| 18 | `Search Field` | `ArcSearchField` | `core/ArcControls.kt` | Planda ve kazanımlarda anlık arama kutusu |
| 19 | `Radio Cards` | `ArcRadioCards` | `core/ArcCards.kt` | Okul türü ve plan sürümü tekli seçim kartları |
| 20 | `Button` | `ArcButton` | `core/ArcButton.kt` | Birincil, ikincil, hafif, çerçeveli ve tehlike eylem butonları |
| 21 | `Action Button` | `ArcActionButton` | `core/ArcButton.kt` | Kompakt ikon butonları |
| 22 | `Button Group` | `ArcButtonGroup` | `core/ArcButton.kt` | Bağlantılı buton kümesi |
| 23 | `Checkbox` | `ArcCheckbox` | `core/ArcControls.kt` | Ders bölümü tamamlandı işaretleme kutusu |
| 24 | `Dropdown Menu` | `ArcDropdownMenu` | `core/ArcControls.kt` | Bağlamsal menüler |
| 25 | `Dialog` | `ArcDialog` | `core/ArcControls.kt` | Yeni sınıf oluşturma ve onay modalleri |
| 26 | `Alert` | `ArcAlert` | `core/ArcControls.kt` | 15 Temmuz açılış haftası ve tatil bilgilendirme panoları |
| 27 | `Toast` | `ArcToast` | `core/ArcControls.kt` | İşlem geri bildirim bildirimleri |
| 28 | `Progress` | `ArcProgress` | `core/ArcIndicators.kt` | Çizgisel ve dairesel ilerleme çubukları |
| 29 | `Skeleton` | `ArcSkeleton` | `core/ArcIndicators.kt` | Shimmer yükleme yer tutucuları |
| 30 | `Empty State` | `ArcEmptyState` | `core/ArcIndicators.kt` | Arama sonucu ve henüz eklenmemiş sınıflar için boş durumlar |

---

## 3. B. Gelişmiş Öğretmen İş Akışı (25 Bileşen)

| # | Bileşen | Compose Karşılığı | Dosya Konumu | Uygulamadaki Kullanım Alanı |
|---|---|---|---|---|
| 31 | `Tree View` | `ArcTreeView` | `advanced/ArcWorkflowAdvanced.kt` | Tema -> Hafta -> Ders -> Kazanım hiyerarşik ağacı |
| 32 | `Date Picker` | `ArcDatePicker` | `advanced/ArcPickerAdvanced.kt` | Takvim üzerinden gün ve ders tarihi seçici |
| 33 | `Date Range Picker` | `ArcDateRangePicker` | `advanced/ArcPickerAdvanced.kt` | Dönem ve ara tatil aralığı seçici |
| 34 | `Morph Select` | `ArcMorphSelect` | `advanced/ArcFormAdvanced.kt` | Butondan açılır listeye yumuşak dönüşen seçici |
| 35 | `Switch` | `ArcSwitch` | `advanced/ArcFormAdvanced.kt` | Yaylı parmak animasyonlu iki durumlu anahtar |
| 36 | `Multi Select` | `ArcMultiSelect` | `advanced/ArcFormAdvanced.kt` | Çoklu tema ve kazanım seçici |
| 37 | `Radio Group` | `ArcRadioGroup` | `advanced/ArcFormAdvanced.kt` | Radyo buton grubu |
| 38 | `Input` | `ArcInput` | `advanced/ArcFormAdvanced.kt` | Etiketli ve hata durumlu metin alanı |
| 39 | `Textarea` | `ArcTextarea` | `advanced/ArcFormAdvanced.kt` | Çok satırlı öğretmen not alanı |
| 40 | `Inline Edit` | `ArcInlineEdit` | `advanced/ArcFormAdvanced.kt` | Yerinde tıklayarak düzenlenen metin |
| 41 | `Context Menu` | `ArcContextMenu` | `advanced/ArcActionsAdvanced.kt` | Sağ tık ve uzun basma bağlam menüsü |
| 42 | `Copy Button` | `ArcCopyButton` | `advanced/ArcActionsAdvanced.kt` | Tek tıkla kazanım/not kopyalama ve tik geri bildirimi |
| 43 | `Theme Switch` | `ArcThemeSwitch` | `advanced/ArcActionsAdvanced.kt` | Güneş/ay morflu açık-koyu tema geçiş düğmesi |
| 44 | `Swipe Actions` | `ArcSwipeActions` | `advanced/ArcActionsAdvanced.kt` | Hafta listesinde sağa/sola kaydırarak ders tamamlama |
| 45 | `Expanding Search` | `ArcExpandingSearch` | `advanced/ArcActionsAdvanced.kt` | İkondan arama çubuğuna genişleyen arama alanı |
| 46 | `Metric Card` | `ArcMetricCard` | `advanced/ArcWorkflowAdvanced.kt` | Ders saati, hafta ve ilerleme metrik kartları |
| 47 | `Carousel` | `ArcCarousel` | `advanced/ArcWorkflowAdvanced.kt` | Aylık ve tematik haftalar arasında kaydırmalı kart gösterimi |
| 48 | `Activity Heatmap` | `ArcActivityHeatmap` | `advanced/ArcWorkflowAdvanced.kt` | İlerleme ekranında gerçek sınıf kayıtlarıyla adaptif (5/10 sütun) ısı haritası |
| 49 | `Timeline` | `ArcTimeline` | `advanced/ArcWorkflowAdvanced.kt` | Akademik yıl ve tatil kilometre taşları zaman çizelgesi |
| 50 | `Confirm Morph` | `ArcConfirmMorph` | `advanced/ArcActionsAdvanced.kt` | Sil/Temizle işleminde onay butonuna dönüşen arayüz |
| 51 | `Hold to Confirm` | `ArcHoldToConfirm` | `advanced/ArcActionsAdvanced.kt` | İlerlemeyi sıfırlama veya kritik işlemlerde basılı tutma halkası |
| 52 | `Toast Stack` | `ArcToastStack` | `advanced/ArcWorkflowAdvanced.kt` | Üst üste yığılan bildirim kuyruğu |
| 53 | `Stepper` | `ArcStepper` | `advanced/ArcWorkflowAdvanced.kt` | Sınıf kurulum ve dönem ilerleme adımları |
| 54 | `Split Button` | `ArcSplitButton` | `advanced/ArcActionsAdvanced.kt` | Ana eylem + açılır alt seçenek butonları |
| 55 | `Floating Button Group` | `ArcFloatingButtonGroup` | `advanced/ArcActionsAdvanced.kt` | Tablet çalışma alanında yüzen hızlı araç çubuğu |

---

## 4. C. Genişletilmiş Özellikler (15 Bileşen)

| # | Bileşen | Compose Karşılığı | Dosya Konumu | Uygulamadaki Kullanım Alanı |
|---|---|---|---|---|
| 56 | `Expanding Button Group` | `ArcExpandingButtonGroup` | `extended/ArcExtendedActions.kt` | Açılan yüzen işlem butonu (Not, Kalem, Arama) |
| 57 | `Card Stack` | `ArcCardStack` | `extended/ArcExtendedActions.kt` | Haftalık dersleri kart destesi gibi inceleme |
| 58 | `JSON Viewer` | `ArcJsonViewer` | `extended/ArcDataViews.kt` | Uygulama dışı: JSON denetim ekranı kaldırıldı, ortak bileşen kaynakta mevcut |
| 59 | `Pagination` | `ArcPagination` | `extended/ArcExtendedActions.kt` | Sayfalama ve haftalar arasında hızlı geçiş |
| 60 | `Animated Counter` | `ArcAnimatedCounter` | `extended/ArcAnimationText.kt` | Saat ve tamamlanma yüzdelerinde yuvarlanan sayaç animasyonu |
| 61 | `Text Morph` | `ArcTextMorph` | `extended/ArcAnimationText.kt` | Başlıklar arasında yumuşak metin dönüşümü |
| 62 | `Text Reveal` | `ArcTextReveal` | `extended/ArcAnimationText.kt` | Başlıklarda kelime kelime beliren akıcı animasyon |
| 63 | `Sortable Data Table` | `ArcSortableDataTable` | `extended/ArcDataViews.kt` | Sıralanabilir kazanım ve inceleme kuyruğu tablosu |
| 64 | `Line Chart` | `ArcLineChart` | `extended/ArcCharts.kt` | 37 haftalık kümülatif ders saati ilerleme eğrisi |
| 65 | `Donut Chart` | `ArcDonutChart` | `extended/ArcCharts.kt` | 4 temanın 185 saatlik oransal pasta/halka dağılımı |
| 66 | `Usage Meter` | `ArcUsageMeter` | `extended/ArcCharts.kt` | Yalnızca kaynakta bulunan doğrulanmış/varsayımsal saatleri ayrı gösterir |
| 67 | `Signature Pad` | `ArcSignaturePad` | `extended/ArcTeacherTools.kt` | Uygulama dışı: kişisel öğretmen ekranından kaldırıldı; eski çizim verileri silinmedi |
| 68 | `Rich Text Editor` | `ArcRichTextEditor` | `extended/ArcTeacherTools.kt` | Kalın, italik, madde imli zengin öğretmen notları editörü |
| 69 | `Bar Chart` | `ArcBarChart` | `extended/ArcCharts.kt` | Tema başına düşen saatlerin sütun grafiği |
| 70 | `User Menu` | `ArcUserMenu` | `extended/ArcExtendedActions.kt` | Sınıf değiştirici, çalışma alanı ve tercihler menüsü |

---

## 5. Hazır Arayüz Blokları (9 Blok)

| # | Blok Adı | Compose Karşılığı | Dosya Konumu | Uygulamadaki Kullanım Alanı |
|---|---|---|---|---|
| 1 | `Page Header` | `ArcPageHeader` | `blocks/ArcPageHeader.kt` | Üst başlık, kırıntı bilgisi, hafta rozeti ve işlem araçları |
| 2 | `Command Palette` | `ArcCommandPalette` | `blocks/ArcCommandPalette.kt` | `Cmd+K` / `Ctrl+K` fiziksel klavye kısayoluyla anında haftaya/temaya atlama |
| 3 | `Empty States` | `ArcEmptyStatesBlock` | `blocks/ArcEmptyStatesBlock.kt` | Henüz planı olmayan sınıflar (9, 10, 12. sınıf) ve boş arama ekranları |
| 4 | `Notification Center` | `ArcNotificationCenter` | `blocks/ArcNotificationCenter.kt` | Ara tatiller, 15 Temmuz anması ve plan doğrulama bildirimleri çekmecesi |
| 5 | `File Upload` | `ArcFileUpload` | `blocks/ArcFileUpload.kt` | Uygulama dışı: JSON içe aktarma ekranı kaldırıldı |
| 6 | `Changelog Feed` | `ArcChangelogFeed` | `blocks/ArcChangelogFeed.kt` | Uygulama dışı: sürüm geçmişi ekranı kaldırıldı |
| 7 | `Comparison Table` | `ArcComparisonTable` | `blocks/ArcComparisonTable.kt` | Uygulama dışı: karşılaştırma sekmesi kaldırıldı |
| 8 | `Stats Band` | `ArcStatsBand` | `blocks/ArcStatsBand.kt` | Seçili kaynak plandan türetilen hafta, ders bölümü, saat ve ilerleme göstergeleri |
| 9 | `FAQ Section` | `ArcFaqSection` | `blocks/ArcFaqSection.kt` | Uygulama dışı: SSS sekmesi kaldırıldı |
