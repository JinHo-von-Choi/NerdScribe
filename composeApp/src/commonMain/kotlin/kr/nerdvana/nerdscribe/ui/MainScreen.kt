package kr.nerdvana.nerdscribe.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kr.nerdvana.nerdscribe.model.EditorState
import kr.nerdvana.nerdscribe.ui.components.SplitPane
import kr.nerdvana.nerdscribe.ui.editor.EditorPane
import kr.nerdvana.nerdscribe.ui.preview.PreviewPane

@Composable
fun MainScreen(
    state: EditorState,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SplitPane(
        modifier = modifier.fillMaxSize(),
        initialSplitRatio = 0.5f,
        leftContent = {
            EditorPane(
                content = state.content,
                onContentChange = onContentChange
            )
        },
        rightContent = {
            PreviewPane(
                content = state.content
            )
        }
    )
}
