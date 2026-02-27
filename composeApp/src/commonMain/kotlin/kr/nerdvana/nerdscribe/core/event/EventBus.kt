package kr.nerdvana.nerdscribe.core.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 앱 전역 이벤트.
 * Feature 간 느슨한 결합을 위한 이벤트 타입 정의.
 */
sealed interface AppEvent {
    /** 특정 줄로 에디터 스크롤 이동 요청 */
    data class NavigateToLine(val lineNumber: Int) : AppEvent

    /** 문서 저장 완료 알림 */
    data class DocumentSaved(val filePath: String) : AppEvent

    /** 자동 저장 트리거 */
    data object AutoSaveTick : AppEvent

    /** 상태 바 메시지 표시 */
    data class StatusMessage(val message: String) : AppEvent
}

/**
 * SharedFlow 기반 이벤트 버스.
 * 이벤트를 발행(emit)하고 구독(collect)할 수 있다.
 *
 * extraBufferCapacity=64로 설정하여
 * collector가 느려도 이벤트가 유실되지 않도록 한다.
 */
class EventBus {
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    suspend fun emit(event: AppEvent) {
        _events.emit(event)
    }

    fun tryEmit(event: AppEvent): Boolean {
        return _events.tryEmit(event)
    }
}
