package com.plan.app.core.design.blocks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.components.core.ArcEmptyState
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 3 / 9: Empty States
 * Purpose-built empty state screens.
 */
object ArcEmptyStatesBlock {

    @Composable
    fun PlanNotAvailable(
        gradeLevel: Int,
        courseName: String,
        onSelectAvailablePlan: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        ArcCard(modifier = modifier.fillMaxWidth()) {
            ArcEmptyState(
                title = "$gradeLevel. Sınıf Planı Arşivde Bulunmuyor",
                description = "Bu deponun kaynak arşivinde şu an için 11. Sınıf Türk Dili ve Edebiyatı planları yer almaktadır. Diğer sınıf seviyeleri kaynak belgeler doğrulandıkça sisteme dahil edilecektir.",
                icon = {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = ArcTheme.colors.textSecondary
                    )
                },
                action = {
                    ArcButton(onClick = onSelectAvailablePlan) {
                        Text("11. Sınıf Planına Geç")
                    }
                }
            )
        }
    }

    @Composable
    fun SearchResultsEmpty(
        query: String,
        onClearQuery: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        ArcCard(modifier = modifier.fillMaxWidth()) {
            ArcEmptyState(
                title = "'$query' ile eşleşen sonuç bulunamadı",
                description = "Farklı bir anahtar kelime, kazanım kodu (örn: TDE2.1) veya tema adı deneyebilirsiniz.",
                icon = {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = ArcTheme.colors.textSecondary
                    )
                },
                action = {
                    ArcButton(onClick = onClearQuery) {
                        Text("Aramayı Temizle")
                    }
                }
            )
        }
    }

    @Composable
    fun NotesEmpty(
        weekNumber: Int,
        onAddNote: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        ArcCard(modifier = modifier.fillMaxWidth()) {
            ArcEmptyState(
                title = "$weekNumber. Hafta İçin Henüz Not Eklenmemiş",
                description = "Bu hafta için kişisel pedagojik açıklamalar, ders notları veya kalemle çizilmiş el yazısı ekleyebilirsiniz.",
                icon = {
                    Icon(
                        imageVector = Icons.Default.NoteAdd,
                        contentDescription = null,
                        tint = ArcTheme.colors.textSecondary
                    )
                },
                action = {
                    ArcButton(onClick = onAddNote) {
                        Text("Not Ekle")
                    }
                }
            )
        }
    }
}
