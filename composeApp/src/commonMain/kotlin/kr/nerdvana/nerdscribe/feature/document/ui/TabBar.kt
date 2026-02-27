package kr.nerdvana.nerdscribe.feature.document.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.nerdvana.nerdscribe.core.model.DocumentId
import kr.nerdvana.nerdscribe.feature.document.model.DocumentTab

/**
 * 수평 탭 스트립.
 * 열린 문서들을 탭으로 표시하며, 활성 탭 하이라이트와 하단 accent border, 닫기 버튼을 제공한다.
 */
@Composable
fun TabBar(
    tabs: List<DocumentTab>,
    activeId: DocumentId,
    onTabClick: (DocumentId) -> Unit,
    onTabClose: (DocumentId) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .horizontalScroll(rememberScrollState())
            .drawBehind {
                // 하단 경계선
                drawLine(
                    color       = borderColor,
                    start       = Offset(0f, size.height - 1f),
                    end         = Offset(size.width, size.height - 1f),
                    strokeWidth = 1f
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            TabItem(
                tab      = tab,
                isActive = tab.id == activeId,
                onClick  = { onTabClick(tab.id) },
                onClose  = { onTabClose(tab.id) }
            )
        }
    }
}

/**
 * 단일 탭 컴포넌트.
 * 활성 탭: 밝은 배경 + 하단 accent border.
 * 비활성 탭: 어두운 배경, 호버 시 밝아짐.
 */
@Composable
private fun TabItem(
    tab: DocumentTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = when {
        isActive -> MaterialTheme.colorScheme.surface
        isHovered -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val textColor = if (isActive) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val accentColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .height(36.dp)
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .drawBehind {
                if (isActive) {
                    // 활성 탭 하단 accent border (2dp)
                    drawLine(
                        color       = accentColor,
                        start       = Offset(0f, size.height - 2f),
                        end         = Offset(size.width, size.height - 2f),
                        strokeWidth = 2f
                    )
                }
            }
            .padding(horizontal = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val displayName = buildString {
            append(tab.fileName)
        }

        // dirty 표시: 파일명 앞에 점 표시
        if (tab.isDirty) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
        }

        Text(
            text     = displayName,
            color    = textColor,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // 닫기 버튼 (호버 시만 표시하거나 항상 표시)
        val closeAlpha = if (isActive || isHovered) 0.6f else 0.3f
        Text(
            text     = "\u00D7",
            color    = textColor.copy(alpha = closeAlpha),
            fontSize = 14.sp,
            modifier = Modifier
                .clickable(onClick = onClose)
                .padding(2.dp)
        )
    }
}
