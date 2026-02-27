package kr.nerdvana.nerdscribe.feature.export.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.nerdvana.nerdscribe.feature.export.model.ExportFormat

/**
 * 내보내기 포맷 선택 다이얼로그.
 *
 * @param onExport  선택된 포맷으로 내보내기 실행 콜백
 * @param onDismiss 다이얼로그 닫기 콜백
 */
@Composable
fun ExportDialog(
    onExport: (ExportFormat) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf(ExportFormat.HTML) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("내보내기") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExportFormat.entries.forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFormat = format }
                            .padding(8.dp)
                    ) {
                        val prefix = if (selectedFormat == format) "[x] " else "[ ] "
                        Text(
                            text  = prefix + format.displayName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onExport(selectedFormat) }) {
                Text("내보내기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
