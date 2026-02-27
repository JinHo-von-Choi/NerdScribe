package kr.nerdvana.nerdscribe.model

data class EditorState(
    val content: String      = "",
    val savedContent: String = "",
    val filePath: String?    = null,
    val fileName: String     = "제목 없음.md"
) {
    val isDirty: Boolean get() = content != savedContent
}
