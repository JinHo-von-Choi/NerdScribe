package kr.nerdvana.nerdscribe.feature.outline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.nerdvana.nerdscribe.feature.outline.model.HeadingNode

/**
 * 우측 아웃라인 사이드바 패널.
 * 상단에 "OUTLINE" 헤더, 아래에 헤딩 트리를 표시한다.
 * 클릭 시 해당 줄로 이동한다.
 */
@Composable
fun OutlinePanel(
    headings: List<HeadingNode>,
    onHeadingClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // 섹션 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text          = "OUTLINE",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
        }

        // 헤딩 트리
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            if (headings.isEmpty()) {
                Text(
                    text     = "헤딩 없음",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                headings.forEach { node ->
                    HeadingItem(node = node, depth = 0, onClick = onHeadingClick)
                }
            }
        }
    }
}

@Composable
private fun HeadingItem(
    node: HeadingNode,
    depth: Int,
    onClick: (Int) -> Unit
) {
    val indent         = (depth * 12 + 8).dp
    val fontWeight     = if (node.level <= 2) FontWeight.SemiBold else FontWeight.Normal
    val fontSize       = when (node.level) {
        1    -> 13.sp
        2    -> 12.sp
        else -> 11.sp
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = if (isHovered) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // 레벨 표시 prefix
    val levelPrefix = when (node.level) {
        1    -> "H1  "
        2    -> "H2  "
        3    -> "H3  "
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable { onClick(node.lineNumber) }
            .padding(start = indent, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (levelPrefix.isNotEmpty()) {
            Text(
                text     = levelPrefix,
                fontSize = 9.sp,
                color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text       = node.text,
            fontSize   = fontSize,
            fontWeight = fontWeight,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }

    node.children.forEach { child ->
        HeadingItem(node = child, depth = depth + 1, onClick = onClick)
    }
}
