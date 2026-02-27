package kr.nerdvana.nerdscribe.feature.filetree.model

/**
 * 워크스페이스 상태.
 *
 * @param rootPath   루트 디렉토리 경로 (null이면 폴더 미선택)
 * @param rootNodes  최상위 파일/디렉토리 노드 목록
 */
data class Workspace(
    val rootPath: String?       = null,
    val rootNodes: List<FileNode> = emptyList()
)
