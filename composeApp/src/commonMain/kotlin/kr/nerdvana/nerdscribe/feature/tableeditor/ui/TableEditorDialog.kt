package kr.nerdvana.nerdscribe.feature.tableeditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.nerdvana.nerdscribe.feature.tableeditor.viewmodel.TableEditorViewModel

/**
 * 테이블 편집 모달 다이얼로그.
 * 그리드 형태로 셀을 편집하고, 행/열 추가/삭제, 정렬 설정을 제공한다.
 *
 * @param viewModel 테이블 편집 ViewModel
 * @param onInsert  마크다운 테이블 삽입 콜백
 * @param onDismiss 다이얼로그 닫기 콜백
 */
@Composable
fun TableEditorDialog(
    viewModel: TableEditorViewModel,
    onInsert: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val table     by viewModel.table.collectAsState()
    val isVisible by viewModel.isVisible.collectAsState()

    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("테이블 편집") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // 그리드
                table.rows.forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        row.forEachIndexed { colIndex, cell ->
                            BasicTextField(
                                value         = cell,
                                onValueChange = { viewModel.updateCell(rowIndex, colIndex, it) },
                                modifier      = Modifier
                                    .width(100.dp)
                                    .height(32.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline)
                                    .padding(4.dp),
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color    = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine  = true,
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }

                // 행/열 추가/삭제 버튼
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = { viewModel.addRow() }) { Text("+행") }
                    TextButton(onClick = { viewModel.addColumn() }) { Text("+열") }
                    if (table.rowCount > 1) {
                        TextButton(onClick = { viewModel.removeRow(table.rowCount - 1) }) { Text("-행") }
                    }
                    if (table.columnCount > 1) {
                        TextButton(onClick = { viewModel.removeColumn(table.columnCount - 1) }) { Text("-열") }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onInsert(viewModel.toMarkdown())
                viewModel.hide()
            }) {
                Text("삽입")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.hide()
                onDismiss()
            }) {
                Text("취소")
            }
        }
    )
}
