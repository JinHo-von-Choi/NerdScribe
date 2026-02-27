package kr.nerdvana.nerdscribe.feature.filetree.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.nerdvana.nerdscribe.feature.filetree.model.FileNode
import kr.nerdvana.nerdscribe.feature.filetree.model.Workspace
import kr.nerdvana.nerdscribe.util.listDirectory

/**
 * 파일 트리 ViewModel.
 * 디렉토리 스캔, 확장/축소, 파일 클릭 이벤트를 관리한다.
 *
 * @param scope      코루틴 스코프
 * @param onFileOpen 파일 클릭 시 호출되는 콜백 (filePath, content)
 */
class FileTreeViewModel(
    private val scope: CoroutineScope,
    private val onFileOpen: (String, String) -> Unit
) {
    private val _workspace = MutableStateFlow(Workspace())
    val workspace: StateFlow<Workspace> = _workspace.asStateFlow()

    /**
     * 폴더를 열어 워크스페이스를 초기화한다.
     */
    fun openFolder(rootPath: String) {
        scope.launch {
            val nodes = scanDirectory(rootPath)
            _workspace.value = Workspace(rootPath = rootPath, rootNodes = nodes)
        }
    }

    /**
     * 디렉토리 노드를 확장/축소 토글한다.
     */
    fun toggleExpand(nodePath: String) {
        scope.launch {
            _workspace.update { ws ->
                ws.copy(rootNodes = toggleNode(ws.rootNodes, nodePath))
            }
        }
    }

    /**
     * 파일을 클릭했을 때 호출. .md 파일이면 내용을 읽어 탭으로 연다.
     */
    fun onFileClick(node: FileNode) {
        if (node.isDirectory) {
            toggleExpand(node.path)
        } else {
            scope.launch {
                try {
                    val content = kr.nerdvana.nerdscribe.util.readFileContent(node.path)
                    onFileOpen(node.path, content)
                } catch (_: Exception) {
                    // 파일 읽기 실패 시 무시
                }
            }
        }
    }

    private suspend fun scanDirectory(path: String): List<FileNode> {
        return try {
            val entries = listDirectory(path)
            entries
                .sortedWith(compareByDescending<FileNode> { it.isDirectory }.thenBy { it.name.lowercase() })
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun toggleNode(nodes: List<FileNode>, targetPath: String): List<FileNode> {
        return nodes.map { node ->
            if (node.path == targetPath && node.isDirectory) {
                if (node.isExpanded) {
                    node.copy(isExpanded = false)
                } else {
                    val children = scanDirectory(node.path)
                    node.copy(isExpanded = true, children = children)
                }
            } else if (node.isDirectory && node.isExpanded) {
                node.copy(children = toggleNode(node.children, targetPath))
            } else {
                node
            }
        }
    }
}
