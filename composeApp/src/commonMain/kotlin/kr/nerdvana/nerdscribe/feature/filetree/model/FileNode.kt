package kr.nerdvana.nerdscribe.feature.filetree.model

/**
 * 파일 트리의 단일 노드.
 *
 * @param name         파일/디렉토리 이름
 * @param path         전체 경로
 * @param isDirectory  디렉토리 여부
 * @param children     하위 노드 (디렉토리인 경우)
 * @param isExpanded   디렉토리 확장 여부
 */
data class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean = false,
    val children: List<FileNode> = emptyList(),
    val isExpanded: Boolean = false
)
