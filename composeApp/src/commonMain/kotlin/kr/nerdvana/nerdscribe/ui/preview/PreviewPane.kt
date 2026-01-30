package kr.nerdvana.nerdscribe.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownExtendedSpans

@Composable
fun PreviewPane(
    content: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Markdown(
            content = content,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            colors = markdownColor(
                text = MaterialTheme.colorScheme.onSurface,
                codeText = MaterialTheme.colorScheme.onSurfaceVariant,
                codeBackground = MaterialTheme.colorScheme.surfaceContainerHighest,
                dividerColor = MaterialTheme.colorScheme.outlineVariant
            ),
            typography = markdownTypography()
        )
    }
}
