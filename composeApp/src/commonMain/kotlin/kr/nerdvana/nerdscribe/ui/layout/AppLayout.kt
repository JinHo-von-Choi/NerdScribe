package kr.nerdvana.nerdscribe.ui.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kr.nerdvana.nerdscribe.ui.components.ResizablePanel

/**
 * 앱 메인 레이아웃.
 * 상단 toolbar + 3단(파일트리|에디터영역|아웃라인) + 하단 StatusBar 구조.
 *
 * @param showFileTree   좌측 파일 트리 표시 여부
 * @param showOutline    우측 아웃라인 표시 여부
 * @param toolbar        상단 툴바 컨텐츠
 * @param fileTree       좌측 파일 트리 컨텐츠
 * @param editor         중앙 에디터 영역 컨텐츠 (탭바 + 에디터/프리뷰 스플릿)
 * @param outline        우측 아웃라인 컨텐츠
 * @param statusBar      하단 상태 바 컨텐츠
 */
@Composable
fun AppLayout(
    showFileTree: Boolean             = true,
    showOutline: Boolean              = true,
    toolbar: @Composable () -> Unit   = {},
    fileTree: @Composable () -> Unit  = {},
    editor: @Composable () -> Unit,
    outline: @Composable () -> Unit   = {},
    statusBar: @Composable () -> Unit = {},
    modifier: Modifier                = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        toolbar()

        ResizablePanel(
            modifier     = Modifier.weight(1f).fillMaxWidth(),
            showLeft     = showFileTree,
            showRight    = showOutline,
            leftWidth    = 0.2f,
            rightWidth   = 0.18f,
            leftContent  = fileTree,
            centerContent = editor,
            rightContent = outline
        )

        statusBar()
    }
}
