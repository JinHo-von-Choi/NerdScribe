package kr.nerdvana.nerdscribe.feature.outline.model

/**
 * 아웃라인 헤딩 노드.
 *
 * @param level      헤딩 레벨 (1~6)
 * @param text       헤딩 텍스트
 * @param lineNumber 문서 내 줄 번호 (0-based)
 * @param children   하위 헤딩 노드
 */
data class HeadingNode(
    val level: Int,
    val text: String,
    val lineNumber: Int,
    val children: List<HeadingNode> = emptyList()
)
