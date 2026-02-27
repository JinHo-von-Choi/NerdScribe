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
 * 수직 분할선으로 리사이즈 가능한 3단 레이아웃.
 * 1dp 시각적 라인 + 8dp 드래그 영역 + 호버 시 accent 색상 표시.
 */
@Composable
fun ResizablePanel(
    modifier: Modifier       = Modifier,
    leftWidth: Float          = 0.2f,
    rightWidth: Float         = 0.18f,
    minWidth: Float           = 0.1f,
    showLeft: Boolean         = true,
    showRight: Boolean        = true,
    leftContent: @Composable () -> Unit,
    centerContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    var leftRatio  by remember { mutableStateOf(leftWidth) }
    var rightRatio by remember { mutableStateOf(rightWidth) }
    var totalWidth by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { totalWidth = it.width }
    ) {
        if (showLeft) {
            Box(modifier = Modifier.weight(leftRatio).fillMaxHeight()) {
                leftContent()
            }
            PanelDivider(
                onDrag = { deltaX ->
                    if (totalWidth > 0) {
                        val delta  = deltaX / totalWidth
                        val maxLeft = 1f - (if (showRight) rightRatio else 0f) - minWidth
                        leftRatio = (leftRatio + delta).coerceIn(minWidth, maxLeft)
                    }
                }
            )
        }

        val centerWeight = 1f -
            (if (showLeft) leftRatio else 0f) -
            (if (showRight) rightRatio else 0f)

        Box(modifier = Modifier.weight(centerWeight.coerceAtLeast(minWidth)).fillMaxHeight()) {
            centerContent()
        }

        if (showRight) {
            PanelDivider(
                onDrag = { deltaX ->
                    if (totalWidth > 0) {
                        val delta   = -deltaX / totalWidth
                        val maxRight = 1f - (if (showLeft) leftRatio else 0f) - minWidth
                        rightRatio = (rightRatio + delta).coerceIn(minWidth, maxRight)
                    }
                }
            )
            Box(modifier = Modifier.weight(rightRatio).fillMaxHeight()) {
                rightContent()
            }
        }
    }
}

/**
 * 패널 분할선.
 * 시각적으로 1dp, 드래그 영역은 6dp.
 * 호버 시 accent 색상으로 변경.
 */
@Composable
private fun PanelDivider(onDrag: (Float) -> Unit) {
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
        // 1dp 시각적 라인 (가운데 정렬)
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(lineColor)
        )
    }
}
