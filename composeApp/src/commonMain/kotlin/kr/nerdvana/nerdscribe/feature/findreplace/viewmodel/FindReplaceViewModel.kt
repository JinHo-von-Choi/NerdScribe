package kr.nerdvana.nerdscribe.feature.findreplace.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.nerdvana.nerdscribe.feature.findreplace.model.FindReplaceState

/**
 * 검색/바꾸기 ViewModel.
 * 매치 탐색, 바꾸기, 전체 바꾸기 로직을 관리한다.
 *
 * @param getContent   현재 에디터 콘텐츠를 가져오는 함수
 * @param setContent   에디터 콘텐츠를 업데이트하는 함수
 * @param navigateTo   특정 오프셋으로 커서를 이동시키는 함수
 */
class FindReplaceViewModel(
    private val getContent: () -> String,
    private val setContent: (String) -> Unit,
    private val navigateTo: (Int) -> Unit
) {
    private val _state = MutableStateFlow(FindReplaceState())
    val state: StateFlow<FindReplaceState> = _state.asStateFlow()

    fun show(withReplace: Boolean = false) {
        _state.update { it.copy(isVisible = true, showReplace = withReplace) }
    }

    fun hide() {
        _state.update { it.copy(isVisible = false, matches = emptyList(), currentMatch = -1) }
    }

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
        performSearch()
    }

    fun updateReplacement(replacement: String) {
        _state.update { it.copy(replacement = replacement) }
    }

    fun toggleRegex() {
        _state.update { it.copy(isRegex = !it.isRegex) }
        performSearch()
    }

    fun toggleCaseSensitive() {
        _state.update { it.copy(isCaseSensitive = !it.isCaseSensitive) }
        performSearch()
    }

    fun toggleReplace() {
        _state.update { it.copy(showReplace = !it.showReplace) }
    }

    /** 다음 매치로 이동 */
    fun nextMatch() {
        val s = _state.value
        if (!s.hasMatches) return
        val next = if (s.currentMatch < s.matchCount - 1) s.currentMatch + 1 else 0
        _state.update { it.copy(currentMatch = next) }
        navigateTo(s.matches[next].first)
    }

    /** 이전 매치로 이동 */
    fun previousMatch() {
        val s = _state.value
        if (!s.hasMatches) return
        val prev = if (s.currentMatch > 0) s.currentMatch - 1 else s.matchCount - 1
        _state.update { it.copy(currentMatch = prev) }
        navigateTo(s.matches[prev].first)
    }

    /** 현재 매치를 바꾸기 */
    fun replaceCurrent() {
        val s = _state.value
        if (s.currentMatch < 0 || s.currentMatch >= s.matchCount) return

        val content = getContent()
        val range   = s.matches[s.currentMatch]
        val newContent = buildString {
            append(content.substring(0, range.first))
            append(s.replacement)
            append(content.substring(range.last + 1))
        }
        setContent(newContent)
        performSearch()
    }

    /** 전체 바꾸기 */
    fun replaceAll() {
        val s = _state.value
        if (!s.hasMatches) return

        val content = getContent()
        val newContent = if (s.isRegex) {
            try {
                val options = if (s.isCaseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE)
                Regex(s.query, options).replace(content, s.replacement)
            } catch (_: Exception) {
                content
            }
        } else {
            content.replace(s.query, s.replacement, ignoreCase = !s.isCaseSensitive)
        }
        setContent(newContent)
        performSearch()
    }

    private fun performSearch() {
        val s       = _state.value
        val content = getContent()
        if (s.query.isEmpty()) {
            _state.update { it.copy(matches = emptyList(), currentMatch = -1) }
            return
        }

        val matches = try {
            if (s.isRegex) {
                val options = if (s.isCaseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE)
                Regex(s.query, options).findAll(content).map { it.range }.toList()
            } else {
                val searchIn = if (s.isCaseSensitive) content else content.lowercase()
                val searchFor = if (s.isCaseSensitive) s.query else s.query.lowercase()
                buildList {
                    var index = searchIn.indexOf(searchFor)
                    while (index >= 0) {
                        add(index..index + searchFor.length - 1)
                        index = searchIn.indexOf(searchFor, index + 1)
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }

        val currentMatch = if (matches.isNotEmpty()) 0 else -1
        _state.update { it.copy(matches = matches, currentMatch = currentMatch) }
        if (matches.isNotEmpty()) {
            navigateTo(matches[0].first)
        }
    }
}
