package kr.nerdvana.nerdscribe.feature.findreplace.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.nerdvana.nerdscribe.feature.findreplace.model.FindReplaceState

/**
 * 검색/바꾸기 오버레이 바.
 * 에디터 상단에 표시되며, 검색어 입력, 정규식/대소문자 토글, 이전/다음 이동, 바꾸기 기능을 제공한다.
 */
@Composable
fun FindReplaceBar(
    state: FindReplaceState,
    onQueryChange: (String) -> Unit,
    onReplacementChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onReplace: () -> Unit,
    onReplaceAll: () -> Unit,
    onToggleRegex: () -> Unit,
    onToggleCaseSensitive: () -> Unit,
    onToggleReplace: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isVisible) return

    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .drawBehind {
                drawLine(
                    color       = borderColor,
                    start       = Offset(0f, size.height - 1f),
                    end         = Offset(size.width, size.height - 1f),
                    strokeWidth = 1f
                )
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 검색어 입력
            SearchField(
                value         = state.query,
                onValueChange = onQueryChange,
                placeholder   = "검색...",
                modifier      = Modifier.weight(1f)
            )

            // 매치 카운트
            if (state.query.isNotEmpty()) {
                Text(
                    text     = "${state.currentMatch + 1}/${state.matchCount}",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 토글 버튼
            ToggleChip("Aa", state.isCaseSensitive, onToggleCaseSensitive)
            ToggleChip(".*", state.isRegex, onToggleRegex)

            // 이동 버튼
            ActionButton("\u25B2", onPrevious)
            ActionButton("\u25BC", onNext)

            // 바꾸기 토글
            ActionButton("\u21C4", onToggleReplace)

            // 닫기
            ActionButton("\u00D7", onClose)
        }

        // 바꾸기 행
        if (state.showReplace) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SearchField(
                    value         = state.replacement,
                    onValueChange = onReplacementChange,
                    placeholder   = "바꾸기...",
                    modifier      = Modifier.weight(1f)
                )
                ActionButton("Replace", onReplace)
                ActionButton("All", onReplaceAll)
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)

    Box(
        modifier = modifier
            .height(28.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text     = placeholder,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            textStyle     = TextStyle(
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurface
            ),
            singleLine  = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = if (isHovered) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = text,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ToggleChip(text: String, active: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = when {
        active    -> MaterialTheme.colorScheme.primaryContainer
        isHovered -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
        else      -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val textColor = if (active) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = text,
            fontSize = 12.sp,
            color    = textColor
        )
    }
}
