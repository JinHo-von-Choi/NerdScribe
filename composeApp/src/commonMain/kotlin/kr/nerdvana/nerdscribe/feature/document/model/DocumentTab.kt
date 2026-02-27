package kr.nerdvana.nerdscribe.feature.document.model

import kr.nerdvana.nerdscribe.core.model.DocumentId
import kr.nerdvana.nerdscribe.core.model.DocumentState

/**
 * 탭 하나의 전체 상태.
 * 문서 상태와 함께 스크롤 위치, 커서 위치 등 탭 고유 상태를 보관한다.
 *
 * @param state          문서 상태
 * @param scrollPosition 에디터 스크롤 위치 (복원용)
 * @param cursorPosition 커서 위치 (복원용)
 */
data class DocumentTab(
    val state: DocumentState   = DocumentState(),
    val scrollPosition: Int    = 0,
    val cursorPosition: Int    = 0
) {
    val id: DocumentId get()       = state.id
    val fileName: String get()     = state.fileName
    val isDirty: Boolean get()     = state.isDirty
    val filePath: String? get()    = state.filePath
}
