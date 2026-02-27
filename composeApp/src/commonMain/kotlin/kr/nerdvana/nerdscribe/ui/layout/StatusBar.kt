package kr.nerdvana.nerdscribe.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 하단 상태 바.
 * 줄/열 위치, 단어 수, 인코딩, 자동 저장 상태를 표시한다.
 *
 * @param lineNumber      현재 커서 줄 번호
 * @param columnNumber    현재 커서 열 번호
 * @param wordCount       문서 내 단어 수
 * @param encoding        파일 인코딩 (기본 UTF-8)
 * @param statusMessage   추가 상태 메시지 (예: "자동 저장됨")
 */
@Composable
fun StatusBar(
    lineNumber: Int       = 1,
    columnNumber: Int     = 1,
    wordCount: Int        = 0,
    encoding: String      = "UTF-8",
    statusMessage: String = "",
    modifier: Modifier    = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatusText("Ln $lineNumber, Col $columnNumber")
            StatusText("${wordCount}단어")
            StatusText(encoding)
        }
        if (statusMessage.isNotEmpty()) {
            StatusText(statusMessage)
        }
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text     = text,
        style    = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color    = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
