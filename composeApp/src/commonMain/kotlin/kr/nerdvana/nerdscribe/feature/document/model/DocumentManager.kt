package kr.nerdvana.nerdscribe.feature.document.model

import kr.nerdvana.nerdscribe.core.model.DocumentId
import kr.nerdvana.nerdscribe.core.model.DocumentState

/**
 * 열린 문서(탭) 컬렉션을 관리하는 불변 데이터 모델.
 * 상태 변경은 항상 copy()를 통해 새 인스턴스를 반환한다.
 *
 * @param tabs       열린 탭 목록 (순서 유지)
 * @param activeId   현재 활성 탭의 DocumentId
 */
data class DocumentManager(
    val tabs: List<DocumentTab>  = listOf(DocumentTab()),
    val activeId: DocumentId     = tabs.first().id
) {
    val activeTab: DocumentTab?
        get() = tabs.find { it.id == activeId }

    val activeState: DocumentState?
        get() = activeTab?.state

    /**
     * 새 탭을 추가하고 해당 탭을 활성화한다.
     */
    fun addTab(tab: DocumentTab = DocumentTab()): DocumentManager =
        copy(tabs = tabs + tab, activeId = tab.id)

    /**
     * 지정 탭을 닫는다. 마지막 탭이면 빈 탭을 생성한다.
     * 활성 탭을 닫으면 인접 탭으로 전환한다.
     */
    fun closeTab(id: DocumentId): DocumentManager {
        val index     = tabs.indexOfFirst { it.id == id }
        if (index < 0) return this

        val remaining = tabs.filterNot { it.id == id }
        if (remaining.isEmpty()) {
            val newTab = DocumentTab()
            return copy(tabs = listOf(newTab), activeId = newTab.id)
        }

        val newActiveId = if (activeId == id) {
            val newIndex = if (index >= remaining.size) remaining.size - 1 else index
            remaining[newIndex].id
        } else {
            activeId
        }
        return copy(tabs = remaining, activeId = newActiveId)
    }

    /**
     * 활성 탭을 변경한다.
     */
    fun switchTo(id: DocumentId): DocumentManager =
        if (tabs.any { it.id == id }) copy(activeId = id) else this

    /**
     * 활성 탭의 DocumentState를 업데이트한다.
     */
    fun updateActiveState(transform: (DocumentState) -> DocumentState): DocumentManager {
        val updatedTabs = tabs.map { tab ->
            if (tab.id == activeId) tab.copy(state = transform(tab.state))
            else tab
        }
        return copy(tabs = updatedTabs)
    }

    /**
     * 특정 탭의 DocumentState를 업데이트한다.
     */
    fun updateTab(id: DocumentId, transform: (DocumentTab) -> DocumentTab): DocumentManager {
        val updatedTabs = tabs.map { tab ->
            if (tab.id == id) transform(tab)
            else tab
        }
        return copy(tabs = updatedTabs)
    }

    /**
     * filePath로 이미 열린 탭을 찾는다.
     */
    fun findByPath(filePath: String): DocumentTab? =
        tabs.find { it.filePath == filePath }
}
