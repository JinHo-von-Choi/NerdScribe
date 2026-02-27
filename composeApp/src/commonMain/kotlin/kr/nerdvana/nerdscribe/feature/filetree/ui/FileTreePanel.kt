package kr.nerdvana.nerdscribe.feature.filetree.ui

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
import kr.nerdvana.nerdscribe.feature.filetree.model.FileNode
import kr.nerdvana.nerdscribe.feature.filetree.model.Workspace

/**
 * 파일 트리 사이드바 패널.
 * 상단에 "EXPLORER" 헤더, 아래에 파일 트리를 표시한다.
 */
@Composable
fun FileTreePanel(
    workspace: Workspace,
    onNodeClick: (FileNode) -> Unit,
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
                text       = "EXPLORER",
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.8.sp
            )
        }

        // 파일 트리
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (workspace.rootPath == null) {
                Text(
                    text     = "폴더를 열어주세요",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                workspace.rootNodes.forEach { node ->
                    FileTreeItem(node = node, depth = 0, onNodeClick = onNodeClick)
                }
            }
        }
    }
}

@Composable
private fun FileTreeItem(
    node: FileNode,
    depth: Int,
    onNodeClick: (FileNode) -> Unit
) {
    val indent         = (depth * 16 + 8).dp
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val bgColor = if (isHovered) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable { onNodeClick(node) }
            .padding(start = indent, top = 3.dp, bottom = 3.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val prefix = when {
            node.isDirectory && node.isExpanded -> "\u25BE "
            node.isDirectory                    -> "\u25B8 "
            else                                -> "  "
        }

        val icon = when {
            node.isDirectory -> "\uD83D\uDCC1 "
            node.name.endsWith(".md") -> "\uD83D\uDCC4 "
            else -> "\uD83D\uDCC4 "
        }

        Text(
            text     = prefix + node.name,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (node.isDirectory && node.isExpanded) {
        node.children.forEach { child ->
            FileTreeItem(node = child, depth = depth + 1, onNodeClick = onNodeClick)
        }
    }
}
