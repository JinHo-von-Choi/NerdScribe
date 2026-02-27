package kr.nerdvana.nerdscribe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

/**
 * 에디터/프리뷰 수평 스플릿 패널.
 * 1dp 시각적 라인 + 6dp 드래그 영역 + 호버 시 accent 표시.
 */
@Composable
fun SplitPane(
    modifier: Modifier       = Modifier,
    initialSplitRatio: Float = 0.5f,
    minRatio: Float           = 0.2f,
    maxRatio: Float           = 0.8f,
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    var splitRatio by remember { mutableStateOf(initialSplitRatio) }
    var totalWidth by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { totalWidth = it.width }
    ) {
        // Left pane
        Box(
            modifier = Modifier
                .weight(splitRatio)
                .fillMaxHeight()
        ) {
            leftContent()
        }

        // Draggable divider
        SplitDivider(
            onDrag = { deltaX ->
                if (totalWidth > 0) {
                    val delta = deltaX / totalWidth
                    splitRatio = (splitRatio + delta).coerceIn(minRatio, maxRatio)
                }
            }
        )

        // Right pane
        Box(
            modifier = Modifier
                .weight(1f - splitRatio)
                .fillMaxHeight()
        ) {
            rightContent()
        }
    }
}

@Composable
private fun SplitDivider(onDrag: (Float) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val lineColor = if (isHovered) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = Modifier
            .width(6.dp)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(lineColor)
        )
    }
}
