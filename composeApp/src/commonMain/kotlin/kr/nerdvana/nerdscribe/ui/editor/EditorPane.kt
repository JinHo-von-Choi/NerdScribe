package kr.nerdvana.nerdscribe.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 마크다운 에디터 패널.
 * TextFieldValue 기반으로 동작하여 선택 범위, 커서 위치를 추적한다.
 *
 * @param value         현재 TextFieldValue 상태
 * @param onValueChange 텍스트/선택 변경 콜백
 * @param modifier      외부 Modifier
 */
@Composable
fun EditorPane(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
}

/**
 * String 기반 하위 호환 오버로드.
 * 내부적으로 TextFieldValue로 변환하여 동작한다.
 */
@Composable
fun EditorPane(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        BasicTextField(
            value         = content,
            onValueChange = onContentChange,
            modifier      = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
}
