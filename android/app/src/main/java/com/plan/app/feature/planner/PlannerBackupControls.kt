package com.plan.app.feature.planner

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import java.io.Reader
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.theme.ArcTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** SAF backup is user-initiated: we never need storage permissions or cloud access. */
@Composable
internal fun PlannerBackupControls(
    onExport: () -> String,
    onImport: suspend (String) -> Unit,
    allowExport: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var pendingRestore by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) scope.launch {
            status = runCatching {
                withContext(Dispatchers.IO) {
                    val data = onExport().toByteArray(Charsets.UTF_8)
                    context.contentResolver.openOutputStream(uri)?.use { it.write(data) }
                        ?: error("Dosya açılamadı")
                }
                "Yedek kaydedildi."
            }.getOrElse { "Yedek alınamadı: " + (it.message ?: "Dosya hatası") }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)
                        ?.use { readLimitedBackup(it) } ?: error("Yedek okunamadı")
                }
            }.onSuccess { pendingRestore = it }
                .onFailure { status = "Yedek okunamadı: " + (it.message ?: "Dosya hatası") }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Kişisel veri yedeği", style = ArcTheme.typography.titleMedium, color = ArcTheme.colors.foreground)
        Text(
            "Sınıfları, tamamlanan dersleri, öğretmen notlarını ve tercihleri JSON olarak yedekleyin. " +
                "APK değiştirmeden önce yedek alın.",
            style = ArcTheme.typography.bodyMedium,
            color = ArcTheme.colors.textSecondary
        )
        ArcButton(
            onClick = { exportLauncher.launch("Plan-ogretmen-yedegi.json") },
            enabled = allowExport,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Yedeği dışa aktar") }
        ArcButton(
            onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
            variant = ArcButtonVariant.OUTLINE,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Yedekten geri yükle") }
        if (status != null) {
            Text(status.orEmpty(), style = ArcTheme.typography.bodySmall, color = ArcTheme.colors.foreground)
        }
    }

    if (pendingRestore != null) {
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = { Text("Yedeği geri yükle?") },
            text = { Text("Mevcut sınıflar, notlar, ilerleme ve tercihler bu yedekle değiştirilecek. " +
                "İşlemden önce mevcut verilerinizi dışa aktarmanız önerilir.") },
            confirmButton = {
                TextButton(onClick = {
                    val backup = pendingRestore ?: return@TextButton
                    pendingRestore = null
                    scope.launch {
                        status = runCatching { onImport(backup); "Yedek geri yüklendi." }
                            .getOrElse { "Geri yükleme başarısız: " + (it.message ?: "Geçersiz veri") }
                    }
                }) { Text("Geri yükle") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) { Text("İptal") }
            }
        )
    }
}

/** Reject unexpectedly large or hostile documents without buffering all their bytes. */
internal fun readLimitedBackup(reader: Reader, maxCharacters: Int = 10 * 1024 * 1024): String {
    require(maxCharacters > 0)
    val buffer = CharArray(8192)
    val output = StringBuilder()
    while (true) {
        val count = reader.read(buffer)
        if (count < 0) break
        check(output.length + count <= maxCharacters) { "Yedek dosyası boyut sınırını aşıyor." }
        output.append(buffer, 0, count)
    }
    return output.toString()
}

@Composable
internal fun PlannerRecoveryScreen(
    message: String?,
    onImportBackup: suspend (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Kişisel veri kurtarma gerekiyor", style = ArcTheme.typography.titleLarge,
                color = ArcTheme.colors.danger)
            Text(message ?: "Kişisel veri okunamadı. Eski kayıtların üzerine yazılmıyor.",
                style = ArcTheme.typography.bodyMedium,
                color = ArcTheme.colors.foreground)
            Text("Veriler güvenlik için salt okunur korumaya alındı. Geçerli bir yedek seçerek devam edin. " +
                "Mevcut bozuk dosya silinmedi.", style = ArcTheme.typography.bodyMedium,
                color = ArcTheme.colors.textSecondary)
            PlannerBackupControls(
                onExport = { error("Kurtarma tamamlanmadan dışa aktarma kullanılamaz.") },
                onImport = onImportBackup,
                allowExport = false
            )
        }
    }
}
