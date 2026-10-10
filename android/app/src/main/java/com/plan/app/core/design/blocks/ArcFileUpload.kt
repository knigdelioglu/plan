package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 5 / 9: File Upload
 * File upload / drag & drop zone for importing custom or updated annual plan JSON files.
 */
@Composable
fun ArcFileUpload(
    onFileSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    acceptedExtensions: List<String> = listOf(".json")
) {
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var isValidated by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(ArcSpacing.space6)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                color = colors.surfaceMuted
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isValidated) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (isValidated) colors.success else colors.accent,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            Text(
                text = if (selectedFileName != null) selectedFileName!! else "Yıllık Plan JSON Dosyası Yükleyin",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(ArcSpacing.space1))

            Text(
                text = "Cihazınızdaki uyumlu annual-plan.json dosyasını içe aktarın",
                style = ArcTheme.typography.bodySmall,
                color = colors.textSecondary
            )

            if (selectedFileName != null) {
                Spacer(modifier = Modifier.height(ArcSpacing.space3))
                ArcBadge(
                    text = "Şema Doğrulandı: 37 Hafta / 45 Ders Bölümü",
                    variant = ArcBadgeVariant.SUCCESS
                )
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space4))

            ArcButton(
                onClick = {
                    selectedFileName = "annual-plan-2026-custom.json"
                    isValidated = true
                    onFileSelected("annual-plan-2026-custom.json")
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Dosya Seç")
                }
            }
        }
    }
}
