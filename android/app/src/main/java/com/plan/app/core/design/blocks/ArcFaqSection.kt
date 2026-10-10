package com.plan.app.core.design.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plan.app.core.design.components.core.AccordionItem
import com.plan.app.core.design.components.core.ArcAccordion
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 9 / 9: FAQ Section
 * Pedagogical and architectural frequently asked questions section.
 */
@Composable
fun ArcFaqSection(
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    val faqItems = listOf(
        AccordionItem(
            id = "faq-15temmuz",
            title = "15 Temmuz Anma Günü Neden 1. Haftada Yer Alıyor?",
            content = {
                Text(
                    text = "15 Temmuz Demokrasi ve Millî Birlik Günü takvimde yaz tatiline denk gelmektedir. MEB yıllık çerçeve planlarında bu anma etkinliği, eğitim öğretim yılının ilk haftasında (14-18 Eylül) okul açılış anması olarak planlı biçimde uygulanır. Bu durum bir takvim hatası olmayıp planlı bir pedagojik uygulamadır.",
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        ),
        AccordionItem(
            id = "faq-3plus2",
            title = "3+2 Saatlik Ders Bölümleri Nasıl Dağıtılmıştır?",
            content = {
                Text(
                    text = "11. sınıf haftalık 5 saatlik Türk Dili ve Edebiyatı dersi bazı haftalarda 3 saat Okuma/Dinleme ve 2 saat Edebiyat Atölyesi (Yazma/Konuşma) şeklinde çoklu bölümlere ayrılır. Yıllık planda 8 haftada toplam 16 ders bölümü bu şekilde yapılandırılmış olup her bölümün kazanımları bağımsız olarak takip edilir.",
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        ),
        AccordionItem(
            id = "faq-hours",
            title = "180 Doğrulanmış ve 185 Varsayımsal Saat Farkı Nedir?",
            content = {
                Text(
                    text = "Orijinal Excel dosyasının 1–36. haftalarında haftalık 5 saat açıkça belirtilmiş ve 180 saat doğrulanmıştır. 37. haftada (Sosyal Etkinlik haftası) saat hücresi birleştirilmiş olduğundan sayısal değer yazmamaktadır; MEB standart haftalık çizelgesi uyarınca 5 saat varsayım olarak hesaplanmıştır.",
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        ),
        AccordionItem(
            id = "faq-classrooms",
            title = "Farklı Sınıfların İlerlemesi ve Notları Birbirine Karışır mı?",
            content = {
                Text(
                    text = "Hayır. Öğretmenin her sınıfı (örn: 11-A, 11-B, 11-Fen) bağımsız bir sınıf kimliğine (classroomId) sahiptir. Ders tamamlama işaretlemeleri, kişisel öğretmen notları ve kalemle çizilen el yazısı notlar cihaz üzerinde yerel veritabanında tamamen ayrı saklanır.",
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        ),
        AccordionItem(
            id = "faq-maarif",
            title = "Türkiye Yüzyılı Maarif Modeli Süreç Bileşenleri Nelerdir?",
            content = {
                Text(
                    text = "Yeni öğretim programında yer alan Sosyal Duygusal Öğrenme Becerileri (SDB), Değerler (D) ve Okuryazarlık Becerileri (OB) her hafta ve temaya entegre biçimde kayıpsız olarak planda görüntülenir.",
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        )
    )

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(ArcSpacing.space5)) {
            Text(
                text = "Yıllık Plan ve Maarif Modeli Rehberi (SSS)",
                style = ArcTheme.typography.titleLarge,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space1))
            Text(
                text = "Pedagojik bağlam, saat muhasebesi ve plan ilkeleri hakkında sık sorulan sorular",
                style = ArcTheme.typography.bodySmall,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(ArcSpacing.space4))

            ArcAccordion(items = faqItems, allowMultiple = true)
        }
    }
}
