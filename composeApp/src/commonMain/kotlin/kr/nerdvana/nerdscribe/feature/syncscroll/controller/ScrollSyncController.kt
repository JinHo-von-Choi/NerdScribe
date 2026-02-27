package kr.nerdvana.nerdscribe.feature.syncscroll.controller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.nerdvana.nerdscribe.feature.syncscroll.model.ScrollSyncState

/**
 * 양방향 스크롤 동기화 컨트롤러.
 *
 * 에디터 또는 프리뷰에서 스크롤이 발생하면,
 * source 태깅으로 피드백 루프를 방지하면서 상대편에 비율을 전파한다.
 */
class ScrollSyncController {

    private val _state = MutableStateFlow(ScrollSyncState())
    val state: StateFlow<ScrollSyncState> = _state.asStateFlow()

    /**
     * 에디터 스크롤 변경 시 호출.
     *
     * @param scrollOffset 현재 스크롤 오프셋
     * @param maxOffset    최대 스크롤 오프셋
     */
    fun onEditorScroll(scrollOffset: Int, maxOffset: Int) {
        if (maxOffset <= 0) return
        val ratio = scrollOffset.toFloat() / maxOffset
        _state.value = ScrollSyncState(ratio = ratio, source = ScrollSyncState.Source.EDITOR)
    }

    /**
     * 프리뷰 스크롤 변경 시 호출.
     *
     * @param scrollOffset 현재 스크롤 오프셋
     * @param maxOffset    최대 스크롤 오프셋
     */
    fun onPreviewScroll(scrollOffset: Int, maxOffset: Int) {
        if (maxOffset <= 0) return
        val ratio = scrollOffset.toFloat() / maxOffset
        _state.value = ScrollSyncState(ratio = ratio, source = ScrollSyncState.Source.PREVIEW)
    }

    /**
     * 주어진 비율에 해당하는 스크롤 오프셋을 계산한다.
     */
    fun calculateOffset(ratio: Float, maxOffset: Int): Int {
        return (ratio * maxOffset).toInt().coerceIn(0, maxOffset)
    }
}
