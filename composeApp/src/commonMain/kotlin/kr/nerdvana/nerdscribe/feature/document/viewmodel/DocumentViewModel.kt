package kr.nerdvana.nerdscribe.feature.document.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.nerdvana.nerdscribe.core.model.DocumentId
import kr.nerdvana.nerdscribe.core.model.DocumentState
import kr.nerdvana.nerdscribe.feature.document.model.DocumentManager
import kr.nerdvana.nerdscribe.feature.document.model.DocumentTab

/**
 * 탭 기반 다중 문서 관리 ViewModel.
 * DocumentManager의 불변 상태를 StateFlow로 노출한다.
 */
class DocumentViewModel {

    private val _manager = MutableStateFlow(DocumentManager())
    val manager: StateFlow<DocumentManager> = _manager.asStateFlow()

    /** 새 빈 탭 생성 */
    fun newDocument() {
        _manager.update { it.addTab() }
    }

    /** 파일을 열어 새 탭으로 추가. 이미 열린 파일이면 해당 탭으로 전환. */
    fun openFile(filePath: String, content: String) {
        _manager.update { mgr ->
            val existing = mgr.findByPath(filePath)
            if (existing != null) {
                mgr.switchTo(existing.id)
            } else {
                val fileName = filePath.substringAfterLast("/").substringAfterLast("\\")
                val tab = DocumentTab(
                    state = DocumentState(
                        content      = content,
                        savedContent = content,
                        filePath     = filePath,
                        fileName     = fileName
                    )
                )
                mgr.addTab(tab)
            }
        }
    }

    /** 활성 탭의 콘텐츠 업데이트 */
    fun updateContent(newContent: String) {
        _manager.update { mgr ->
            mgr.updateActiveState { it.copy(content = newContent) }
        }
    }

    /** 활성 탭 전환 */
    fun switchTab(id: DocumentId) {
        _manager.update { it.switchTo(id) }
    }

    /** 탭 닫기 */
    fun closeTab(id: DocumentId) {
        _manager.update { it.closeTab(id) }
    }

    /** 저장 완료 처리. filePath가 null이면 활성 탭의 기존 경로 사용. */
    fun markSaved(filePath: String? = null) {
        _manager.update { mgr ->
            mgr.updateActiveState { state ->
                val path = filePath ?: state.filePath
                val fileName = path?.substringAfterLast("/")?.substringAfterLast("\\") ?: state.fileName
                state.copy(
                    savedContent = state.content,
                    filePath     = path,
                    fileName     = fileName
                )
            }
        }
    }

    /** 특정 탭의 저장 완료 처리 */
    fun markTabSaved(id: DocumentId) {
        _manager.update { mgr ->
            mgr.updateTab(id) { tab ->
                tab.copy(state = tab.state.copy(savedContent = tab.state.content))
            }
        }
    }

    /** dirty 탭 목록 반환 (자동 저장 등에서 사용) */
    fun getDirtyTabsWithPaths(): List<DocumentTab> {
        return _manager.value.tabs.filter { it.isDirty && it.filePath != null }
    }
}
