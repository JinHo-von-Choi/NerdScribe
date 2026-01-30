package kr.nerdvana.nerdscribe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

@Composable
fun SplitPane(
    modifier: Modifier = Modifier,
    initialSplitRatio: Float = 0.5f,
    minRatio: Float = 0.2f,
    maxRatio: Float = 0.8f,
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    var splitRatio by remember { mutableStateOf(initialSplitRatio) }
    var totalWidth by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                totalWidth = size.width
            }
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
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant)
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (totalWidth > 0) {
                            val delta = dragAmount.x / totalWidth
                            splitRatio = (splitRatio + delta).coerceIn(minRatio, maxRatio)
                        }
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
