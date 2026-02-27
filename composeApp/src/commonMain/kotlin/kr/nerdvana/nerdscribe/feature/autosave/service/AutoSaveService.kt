package kr.nerdvana.nerdscribe.feature.autosave.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.nerdvana.nerdscribe.core.model.DocumentId
import kr.nerdvana.nerdscribe.feature.autosave.model.AutoSaveConfig

/**
 * 코루틴 기반 주기적 자동 저장 서비스.
 *
 * filePath가 있는 dirty 문서만 자동 저장 대상이다.
 * 신규 문서(filePath=null)는 자동 저장하지 않는다.
 *
 * @param scope         코루틴 스코프
 * @param getDirtyTabs  dirty + filePath 있는 탭 목록 조회 함수
 * @param saveTab       특정 탭을 저장하는 함수 (filePath, content)
 * @param markSaved     저장 완료 후 탭의 dirty 플래그를 초기화하는 함수
 */
class AutoSaveService(
    private val scope: CoroutineScope,
    private val getDirtyTabs: () -> List<Pair<DocumentId, Pair<String, String>>>,
    private val saveTab: suspend (String, String) -> Boolean,
    private val markSaved: (DocumentId) -> Unit
) {
    private var job: Job? = null

    private val _lastSaveMessage = MutableStateFlow("")
    val lastSaveMessage: StateFlow<String> = _lastSaveMessage.asStateFlow()

    /**
     * 자동 저장을 시작한다.
     */
    fun start(config: AutoSaveConfig) {
        stop()
        if (!config.enabled) return

        job = scope.launch {
            while (isActive) {
                delay(config.intervalMs)
                performAutoSave()
            }
        }
    }

    /**
     * 자동 저장을 중지한다.
     */
    fun stop() {
        job?.cancel()
        job = null
    }

    private suspend fun performAutoSave() {
        val dirtyTabs = getDirtyTabs()
        if (dirtyTabs.isEmpty()) return

        var savedCount = 0
        for ((id, pathAndContent) in dirtyTabs) {
            val (path, content) = pathAndContent
            if (saveTab(path, content)) {
                markSaved(id)
                savedCount++
            }
        }

        if (savedCount > 0) {
            _lastSaveMessage.value = "자동 저장됨 (${savedCount}개 파일)"
        }
    }
}
