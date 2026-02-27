package kr.nerdvana.nerdscribe.feature.export.service

/**
 * 마크다운 -> HTML 변환 내보내기.
 * CSS가 내장된 독립 HTML 파일을 생성한다.
 */
object HtmlExporter {

    /**
     * 마크다운 콘텐츠를 HTML 문자열로 변환한다.
     * 간단한 마크다운 -> HTML 변환기.
     *
     * @param markdown 원본 마크다운 텍스트
     * @param title    HTML 문서 제목
     * @return 완전한 HTML 문서 문자열
     */
    fun export(markdown: String, title: String = "NerdScribe Export"): String {
        val bodyHtml = convertMarkdownToHtml(markdown)

        return """
            |<!DOCTYPE html>
            |<html lang="ko">
            |<head>
            |<meta charset="UTF-8">
            |<meta name="viewport" content="width=device-width, initial-scale=1.0">
            |<title>$title</title>
            |<style>
            |body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 800px; margin: 0 auto; padding: 40px 20px; line-height: 1.6; color: #333; }
            |h1, h2, h3, h4, h5, h6 { margin-top: 1.5em; margin-bottom: 0.5em; }
            |h1 { font-size: 2em; border-bottom: 2px solid #eee; padding-bottom: 0.3em; }
            |h2 { font-size: 1.5em; border-bottom: 1px solid #eee; padding-bottom: 0.3em; }
            |code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
            |pre { background: #f4f4f4; padding: 16px; border-radius: 6px; overflow-x: auto; }
            |pre code { background: none; padding: 0; }
            |blockquote { border-left: 4px solid #ddd; margin-left: 0; padding-left: 16px; color: #666; }
            |table { border-collapse: collapse; width: 100%; }
            |th, td { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
            |th { background: #f4f4f4; }
            |hr { border: none; border-top: 1px solid #eee; margin: 2em 0; }
            |a { color: #1976D2; }
            |img { max-width: 100%; }
            |</style>
            |</head>
            |<body>
            |$bodyHtml
            |</body>
            |</html>
        """.trimMargin()
    }

    /**
     * 간단한 마크다운 -> HTML 변환.
     * 기본적인 블록/인라인 요소를 처리한다.
     */
    private fun convertMarkdownToHtml(markdown: String): String {
        val lines    = markdown.split("\n")
        val html     = StringBuilder()
        var inCodeBlock = false
        var inList      = false
        var listType    = ""

        for (line in lines) {
            // 코드 블록
            if (line.trimStart().startsWith("```")) {
                if (inCodeBlock) {
                    html.appendLine("</code></pre>")
                    inCodeBlock = false
                } else {
                    html.appendLine("<pre><code>")
                    inCodeBlock = true
                }
                continue
            }
            if (inCodeBlock) {
                html.appendLine(escapeHtml(line))
                continue
            }

            // 리스트 닫기
            if (inList && !line.trimStart().matches(Regex("^[-*+]\\s.*|^\\d+\\.\\s.*"))) {
                html.appendLine("</$listType>")
                inList = false
            }

            // 빈 줄
            if (line.isBlank()) {
                if (inList) {
                    html.appendLine("</$listType>")
                    inList = false
                }
                continue
            }

            // 헤딩
            val headingMatch = Regex("^(#{1,6})\\s+(.+)$").matchEntire(line)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val text  = processInline(headingMatch.groupValues[2])
                html.appendLine("<h$level>$text</h$level>")
                continue
            }

            // 수평선
            if (line.trim().matches(Regex("^(-{3,}|\\*{3,}|_{3,})$"))) {
                html.appendLine("<hr>")
                continue
            }

            // 블록쿼트
            if (line.trimStart().startsWith("> ")) {
                val text = processInline(line.trimStart().removePrefix("> "))
                html.appendLine("<blockquote><p>$text</p></blockquote>")
                continue
            }

            // 비순서 리스트
            val ulMatch = Regex("^[-*+]\\s+(.+)$").matchEntire(line.trimStart())
            if (ulMatch != null) {
                if (!inList || listType != "ul") {
                    if (inList) html.appendLine("</$listType>")
                    html.appendLine("<ul>")
                    inList   = true
                    listType = "ul"
                }
                html.appendLine("<li>${processInline(ulMatch.groupValues[1])}</li>")
                continue
            }

            // 순서 리스트
            val olMatch = Regex("^\\d+\\.\\s+(.+)$").matchEntire(line.trimStart())
            if (olMatch != null) {
                if (!inList || listType != "ol") {
                    if (inList) html.appendLine("</$listType>")
                    html.appendLine("<ol>")
                    inList   = true
                    listType = "ol"
                }
                html.appendLine("<li>${processInline(olMatch.groupValues[1])}</li>")
                continue
            }

            // 일반 단락
            html.appendLine("<p>${processInline(line)}</p>")
        }

        if (inCodeBlock) html.appendLine("</code></pre>")
        if (inList) html.appendLine("</$listType>")

        return html.toString()
    }

    private fun processInline(text: String): String {
        var result = escapeHtml(text)
        // 볼드
        result = Regex("\\*\\*(.+?)\\*\\*").replace(result) { "<strong>${it.groupValues[1]}</strong>" }
        // 이탤릭
        result = Regex("\\*(.+?)\\*").replace(result) { "<em>${it.groupValues[1]}</em>" }
        // 취소선
        result = Regex("~~(.+?)~~").replace(result) { "<del>${it.groupValues[1]}</del>" }
        // 인라인 코드
        result = Regex("`(.+?)`").replace(result) { "<code>${it.groupValues[1]}</code>" }
        // 이미지
        result = Regex("!\\[(.+?)\\]\\((.+?)\\)").replace(result) { "<img src=\"${it.groupValues[2]}\" alt=\"${it.groupValues[1]}\">" }
        // 링크
        result = Regex("\\[(.+?)\\]\\((.+?)\\)").replace(result) { "<a href=\"${it.groupValues[2]}\">${it.groupValues[1]}</a>" }
        return result
    }

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}
