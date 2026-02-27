package kr.nerdvana.nerdscribe.feature.editor.model

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * TextFieldValue 스냅샷 기반 Undo/Redo 히스토리.
 *
 * undo 스택에 변경 이전 상태를, redo 스택에 되돌린 상태를 보관한다.
 * 새 변경이 발생하면 redo 스택은 초기화된다.
 *
 * @param maxDepth 최대 히스토리 깊이
 */
class UndoRedoHistory(private val maxDepth: Int = 100) {

    private val undoStack = ArrayDeque<TextFieldValue>()
    private val redoStack = ArrayDeque<TextFieldValue>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    /**
     * 현재 상태를 undo 스택에 푸시한다.
     * 새 변경이므로 redo 스택은 초기화된다.
     *
     * @param current 변경 직전의 TextFieldValue
     */
    fun pushState(current: TextFieldValue) {
        undoStack.addLast(current)
        if (undoStack.size > maxDepth) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    /**
     * Undo 실행. 현재 상태를 redo에 넣고 이전 상태를 반환한다.
     *
     * @param current 현재 TextFieldValue
     * @return 이전 상태, 또는 undo 불가 시 null
     */
    fun undo(current: TextFieldValue): TextFieldValue? {
        if (!canUndo) return null
        redoStack.addLast(current)
        return undoStack.removeLast()
    }

    /**
     * Redo 실행. 현재 상태를 undo에 넣고 다음 상태를 반환한다.
     *
     * @param current 현재 TextFieldValue
     * @return 다음 상태, 또는 redo 불가 시 null
     */
    fun redo(current: TextFieldValue): TextFieldValue? {
        if (!canRedo) return null
        undoStack.addLast(current)
        return redoStack.removeLast()
    }

    /** 히스토리 초기화 */
    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
