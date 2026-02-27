package kr.nerdvana.nerdscribe.feature.findreplace.model

/**
 * 검색/바꾸기 상태.
 *
 * @param query           검색어
 * @param replacement     바꾸기 텍스트
 * @param isRegex         정규식 모드 여부
 * @param isCaseSensitive 대소문자 구분 여부
 * @param isVisible       검색 바 표시 여부
 * @param showReplace     바꾸기 필드 표시 여부
 * @param matches         매칭된 위치 목록 (startIndex)
 * @param currentMatch    현재 활성 매치 인덱스
 */
data class FindReplaceState(
    val query: String           = "",
    val replacement: String     = "",
    val isRegex: Boolean        = false,
    val isCaseSensitive: Boolean = false,
    val isVisible: Boolean      = false,
    val showReplace: Boolean    = false,
    val matches: List<IntRange> = emptyList(),
    val currentMatch: Int       = -1
) {
    val matchCount: Int get() = matches.size
    val hasMatches: Boolean get() = matches.isNotEmpty()
}
