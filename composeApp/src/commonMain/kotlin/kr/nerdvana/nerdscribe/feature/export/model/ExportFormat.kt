package kr.nerdvana.nerdscribe.feature.export.model

/**
 * 내보내기 포맷.
 */
enum class ExportFormat(val extension: String, val displayName: String) {
    HTML("html", "HTML"),
    PDF("pdf", "PDF")
}
