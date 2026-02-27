package kr.nerdvana.nerdscribe.feature.outline.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.nerdvana.nerdscribe.feature.outline.model.HeadingNode

/**
 * 아웃라인 ViewModel.
 * 마크다운 콘텐츠에서 헤딩을 파싱하여 트리 구조로 관리한다.
 */
class OutlineViewModel {

    private val _headings = MutableStateFlow<List<HeadingNode>>(emptyList())
    val headings: StateFlow<List<HeadingNode>> = _headings.asStateFlow()

    /**
     * 마크다운 콘텐츠를 파싱하여 헤딩 트리를 갱신한다.
     *
     * @param content 현재 마크다운 텍스트
     */
    fun updateContent(content: String) {
        val flatHeadings = parseHeadings(content)
        _headings.value  = buildTree(flatHeadings)
    }

    /**
     * 정규식으로 헤딩을 추출한다.
     */
    private fun parseHeadings(content: String): List<HeadingNode> {
        val regex = Regex("^(#{1,6})\\s+(.+)$", RegexOption.MULTILINE)
        return regex.findAll(content).map { match ->
            val level      = match.groupValues[1].length
            val text       = match.groupValues[2].trim()
            val lineNumber = content.substring(0, match.range.first).count { it == '\n' }
            HeadingNode(level = level, text = text, lineNumber = lineNumber)
        }.toList()
    }

    /**
     * 플랫 헤딩 목록을 중첩 트리로 변환한다.
     * H1 > H2 > H3 형태의 계층 구조를 생성한다.
     */
    private fun buildTree(headings: List<HeadingNode>): List<HeadingNode> {
        if (headings.isEmpty()) return emptyList()

        val result = mutableListOf<HeadingNode>()
        val stack  = ArrayDeque<MutableHeadingNode>()

        for (heading in headings) {
            val mutable = MutableHeadingNode(heading)

            while (stack.isNotEmpty() && stack.last().node.level >= heading.level) {
                stack.removeLast()
            }

            if (stack.isNotEmpty()) {
                stack.last().children.add(mutable)
            } else {
                result.add(mutable.toImmutable())
                stack.addLast(mutable)
                continue
            }
            stack.addLast(mutable)
        }

        return rebuildImmutable(result, headings)
    }

    /**
     * 스택 기반으로 재귀적으로 불변 트리를 구성한다.
     */
    private fun rebuildImmutable(
        topLevel: List<HeadingNode>,
        allHeadings: List<HeadingNode>
    ): List<HeadingNode> {
        // 단순 재귀 구조: 각 헤딩 뒤에 자신보다 레벨이 큰 헤딩들을 자식으로 수집
        val result = mutableListOf<HeadingNode>()
        var i = 0

        while (i < allHeadings.size) {
            val current  = allHeadings[i]
            val children = mutableListOf<HeadingNode>()
            var j = i + 1

            while (j < allHeadings.size && allHeadings[j].level > current.level) {
                j++
            }

            val subList = allHeadings.subList(i + 1, j)
            val childTree = rebuildImmutable(emptyList(), subList)

            result.add(current.copy(children = childTree))
            i = j
        }

        return result
    }

    private class MutableHeadingNode(val node: HeadingNode) {
        val children = mutableListOf<MutableHeadingNode>()
        fun toImmutable(): HeadingNode = node.copy(
            children = children.map { it.toImmutable() }
        )
    }
}
