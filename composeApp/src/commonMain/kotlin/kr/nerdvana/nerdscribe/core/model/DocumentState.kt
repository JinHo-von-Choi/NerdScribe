package kr.nerdvana.nerdscribe.core.model

/**
 * 단일 문서의 전체 상태.
 * isDirty는 content와 savedContent 비교로 파생된다.
 */
data class DocumentState(
    val id: DocumentId       = DocumentId(),
    val content: String      = "",
    val savedContent: String = "",
    val filePath: String?    = null,
    val fileName: String     = "제목 없음.md"
) {
    val isDirty: Boolean get() = content != savedContent
}
