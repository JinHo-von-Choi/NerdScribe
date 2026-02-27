package kr.nerdvana.nerdscribe.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.nerdvana.nerdscribe.core.command.CommandExecutor
import kr.nerdvana.nerdscribe.core.command.CommandId

/**
 * 에디터 서식 툴바.
 * Bold, Italic, Heading, Link, Image, Code, Quote, List, Table, HR, Strikethrough 버튼을 제공.
 * 각 버튼은 CommandExecutor를 통해 해당 커맨드를 실행한다.
 */
@Composable
fun EditorToolbar(
    commandExecutor: CommandExecutor,
    canUndo: Boolean            = false,
    canRedo: Boolean            = false,
    syncScrollEnabled: Boolean  = true,
    onToggleSyncScroll: () -> Unit = {},
    modifier: Modifier          = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.surface)
            .horizontalScroll(rememberScrollState())
            .drawBehind {
                // 하단 경계선
                drawLine(
                    color       = borderColor,
                    start       = Offset(0f, size.height - 1f),
                    end         = Offset(size.width, size.height - 1f),
                    strokeWidth = 1f
                )
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Undo / Redo
        ToolbarButton("\u21A9", enabled = canUndo) { commandExecutor.execute(CommandId.UNDO) }
        ToolbarButton("\u21AA", enabled = canRedo) { commandExecutor.execute(CommandId.REDO) }
        ToolbarSeparator()

        // 서식
        ToolbarButton("B", fontWeight = FontWeight.Bold) { commandExecutor.execute(CommandId.FORMAT_BOLD) }
        ToolbarButton("I", fontWeight = FontWeight.Normal) { commandExecutor.execute(CommandId.FORMAT_ITALIC) }
        ToolbarButton("S\u0336") { commandExecutor.execute(CommandId.FORMAT_STRIKETHROUGH) }
        ToolbarSeparator()

        // 헤딩
        ToolbarButton("H1", fontWeight = FontWeight.SemiBold) { commandExecutor.execute(CommandId.FORMAT_HEADING1) }
        ToolbarButton("H2", fontWeight = FontWeight.SemiBold) { commandExecutor.execute(CommandId.FORMAT_HEADING2) }
        ToolbarButton("H3") { commandExecutor.execute(CommandId.FORMAT_HEADING3) }
        ToolbarSeparator()

        // 링크 / 이미지
        ToolbarButton("\uD83D\uDD17") { commandExecutor.execute(CommandId.FORMAT_LINK) }
        ToolbarButton("\uD83D\uDDBC") { commandExecutor.execute(CommandId.FORMAT_IMAGE) }
        ToolbarSeparator()

        // 코드
        ToolbarButton("</>") { commandExecutor.execute(CommandId.FORMAT_CODE) }
        ToolbarButton("{ }") { commandExecutor.execute(CommandId.FORMAT_CODE_BLOCK) }
        ToolbarSeparator()

        // 블록쿼트 / 리스트
        ToolbarButton("\u275E") { commandExecutor.execute(CommandId.FORMAT_QUOTE) }
        ToolbarButton("\u2022") { commandExecutor.execute(CommandId.FORMAT_UNORDERED_LIST) }
        ToolbarButton("1.") { commandExecutor.execute(CommandId.FORMAT_ORDERED_LIST) }
        ToolbarSeparator()

        // 테이블 / 수평선
        ToolbarButton("\u2637") { commandExecutor.execute(CommandId.FORMAT_TABLE) }
        ToolbarButton("\u2500") { commandExecutor.execute(CommandId.FORMAT_HORIZONTAL_RULE) }
        ToolbarSeparator()

        // 동기 스크롤 토글
        ToolbarToggleButton(
            text      = "\u21C5",
            isActive  = syncScrollEnabled,
            onClick   = onToggleSyncScroll
        )
    }
}

@Composable
private fun ToolbarButton(
    text: String,
    enabled: Boolean       = true,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = when {
        !enabled -> MaterialTheme.colorScheme.surface
        isHovered -> MaterialTheme.colorScheme.surfaceContainerHighest
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .hoverable(interactionSource)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontSize   = 13.sp,
            fontWeight = fontWeight,
            color      = textColor
        )
    }
}

@Composable
private fun ToolbarToggleButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = when {
        isActive  -> MaterialTheme.colorScheme.primaryContainer
        isHovered -> MaterialTheme.colorScheme.surfaceContainerHighest
        else      -> MaterialTheme.colorScheme.surface
    }

    val textColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = text,
            fontSize = 13.sp,
            color    = textColor
        )
    }
}

@Composable
private fun ToolbarSeparator() {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .width(1.dp)
            .height(18.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
