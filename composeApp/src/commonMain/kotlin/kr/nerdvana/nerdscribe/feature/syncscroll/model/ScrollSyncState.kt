package kr.nerdvana.nerdscribe.feature.syncscroll.model

/**
 * 동기 스크롤 상태.
 *
 * @param ratio   현재 스크롤 비율 (0.0~1.0)
 * @param source  마지막 스크롤을 발생시킨 소스 (피드백 루프 방지)
 */
data class ScrollSyncState(
    val ratio: Float     = 0f,
    val source: Source   = Source.NONE
) {
    enum class Source { NONE, EDITOR, PREVIEW }
}
