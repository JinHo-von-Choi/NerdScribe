package kr.nerdvana.nerdscribe.feature.editor.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * 마크다운 문법 하이라이팅.
 * 일반 텍스트를 AnnotatedString으로 변환하여 구문별 스타일을 적용한다.
 *
 * @param headingColor     헤딩 색상
 * @param boldColor        볼드 텍스트 색상
 * @param codeColor        코드 배경/텍스트 색상
 * @param linkColor        링크 색상
 * @param quoteColor       블록쿼트 색상
 * @param textColor        기본 텍스트 색상
 */
class SyntaxHighlighter(
    private val headingColor: Color = Color(0xFF1976D2),
    private val boldColor: Color    = Color.Unspecified,
    private val codeColor: Color    = Color(0xFF6A8759),
    private val linkColor: Color    = Color(0xFF6897BB),
    private val quoteColor: Color   = Color(0xFF808080),
    private val textColor: Color    = Color.Unspecified
) {
    /**
     * 마크다운 텍스트를 구문 하이라이팅된 AnnotatedString으로 변환한다.
     */
    fun highlight(text: String): AnnotatedString = buildAnnotatedString {
        val lines = text.split("\n")

        var inCodeBlock = false

        for ((lineIndex, line) in lines.withIndex()) {
            if (lineIndex > 0) append("\n")

            // 코드 블록 토글
            if (line.trimStart().startsWith("```")) {
                inCodeBlock = !inCodeBlock
                withStyle(SpanStyle(color = codeColor, fontFamily = FontFamily.Monospace)) {
                    append(line)
                }
                continue
            }

            if (inCodeBlock) {
                withStyle(SpanStyle(color = codeColor, fontFamily = FontFamily.Monospace)) {
                    append(line)
                }
                continue
            }

            // 헤딩
            val headingMatch = HEADING_REGEX.matchEntire(line)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val weight = when {
                    level <= 2 -> FontWeight.Bold
                    level <= 4 -> FontWeight.SemiBold
                    else       -> FontWeight.Medium
                }
                withStyle(SpanStyle(color = headingColor, fontWeight = weight)) {
                    append(line)
                }
                continue
            }

            // 블록쿼트
            if (line.trimStart().startsWith("> ")) {
                withStyle(SpanStyle(color = quoteColor, fontStyle = FontStyle.Italic)) {
                    append(line)
                }
                continue
            }

            // 인라인 하이라이팅
            highlightInline(this, line)
        }
    }

    private fun highlightInline(builder: AnnotatedString.Builder, line: String) {
        var i = 0
        while (i < line.length) {
            // 인라인 코드
            if (line[i] == '`') {
                val end = line.indexOf('`', i + 1)
                if (end > i) {
                    builder.withStyle(SpanStyle(color = codeColor, fontFamily = FontFamily.Monospace)) {
                        append(line.substring(i, end + 1))
                    }
                    i = end + 1
                    continue
                }
            }

            // 볼드 (**)
            if (i + 1 < line.length && line[i] == '*' && line[i + 1] == '*') {
                val end = line.indexOf("**", i + 2)
                if (end > i) {
                    builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                        append(line.substring(i, end + 2))
                    }
                    i = end + 2
                    continue
                }
            }

            // 이탤릭 (*)
            if (line[i] == '*' && (i + 1 >= line.length || line[i + 1] != '*')) {
                val end = line.indexOf('*', i + 1)
                if (end > i) {
                    builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(line.substring(i, end + 1))
                    }
                    i = end + 1
                    continue
                }
            }

            // 링크 [text](url)
            if (line[i] == '[') {
                val closeBracket = line.indexOf(']', i + 1)
                if (closeBracket > i && closeBracket + 1 < line.length && line[closeBracket + 1] == '(') {
                    val closeParen = line.indexOf(')', closeBracket + 2)
                    if (closeParen > closeBracket) {
                        builder.withStyle(SpanStyle(color = linkColor)) {
                            append(line.substring(i, closeParen + 1))
                        }
                        i = closeParen + 1
                        continue
                    }
                }
            }

            builder.append(line[i])
            i++
        }
    }

    private companion object {
        val HEADING_REGEX = Regex("^(#{1,6})\\s+(.+)$")
    }
}
