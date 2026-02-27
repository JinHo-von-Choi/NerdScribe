package kr.nerdvana.nerdscribe.feature.editor.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.nerdvana.nerdscribe.feature.editor.model.UndoRedoHistory

/**
 * 에디터 뷰모델.
 * TextFieldValue 기반 편집 상태와 Undo/Redo 히스토리를 관리한다.
 *
 * DocumentViewModel의 content(String)과 동기화:
 * - 외부에서 content가 변경되면 resetContent()로 반영
 * - 내부 편집 시 onValueChange() -> onContentChanged 콜백으로 전파
 */
class EditorViewModel(
    private val onContentChanged: (String) -> Unit
) {
    private val history = UndoRedoHistory()

    private val _textFieldValue = MutableStateFlow(TextFieldValue())
    val textFieldValue: StateFlow<TextFieldValue> = _textFieldValue.asStateFlow()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    /**
     * 에디터 내부에서 텍스트가 변경될 때 호출.
     * 텍스트가 실제로 변경된 경우에만 undo 스택에 이전 상태를 기록한다.
     */
    fun onValueChange(newValue: TextFieldValue) {
        val oldValue = _textFieldValue.value
        if (oldValue.text != newValue.text) {
            history.pushState(oldValue)
            updateCanFlags()
        }
        _textFieldValue.value = newValue
        onContentChanged(newValue.text)
    }

    /**
     * 외부에서 문서 콘텐츠가 변경되었을 때 (탭 전환 등) 에디터 상태를 리셋.
     * 히스토리도 초기화된다.
     */
    fun resetContent(content: String) {
        history.clear()
        _textFieldValue.value = TextFieldValue(
            text      = content,
            selection = TextRange(content.length)
        )
        updateCanFlags()
    }

    /** Undo 실행 */
    fun undo() {
        val restored = history.undo(_textFieldValue.value) ?: return
        _textFieldValue.value = restored
        onContentChanged(restored.text)
        updateCanFlags()
    }

    /** Redo 실행 */
    fun redo() {
        val restored = history.redo(_textFieldValue.value) ?: return
        _textFieldValue.value = restored
        onContentChanged(restored.text)
        updateCanFlags()
    }

    /**
     * 현재 선택 범위에 마크다운 문법을 래핑/삽입한다.
     *
     * @param prefix 선택 앞에 삽입할 접두사 (예: "**")
     * @param suffix 선택 뒤에 삽입할 접미사 (예: "**")
     * @param defaultText 선택이 없을 때 삽입할 기본 텍스트
     */
    fun wrapSelection(prefix: String, suffix: String, defaultText: String = "") {
        val current   = _textFieldValue.value
        val text      = current.text
        val selection = current.selection

        history.pushState(current)

        val selectedText = if (selection.collapsed) defaultText
                           else text.substring(selection.min, selection.max)

        val newText = buildString {
            append(text.substring(0, selection.min))
            append(prefix)
            append(selectedText)
            append(suffix)
            append(text.substring(selection.max))
        }

        val cursorPos = selection.min + prefix.length + selectedText.length
        _textFieldValue.value = TextFieldValue(
            text      = newText,
            selection = TextRange(cursorPos)
        )
        onContentChanged(newText)
        updateCanFlags()
    }

    /**
     * 현재 줄의 시작에 접두사를 삽입한다 (헤딩, 리스트 등).
     *
     * @param prefix 줄 시작에 삽입할 문자열 (예: "# ", "- ")
     */
    fun insertLinePrefix(prefix: String) {
        val current = _textFieldValue.value
        val text    = current.text
        val cursor  = current.selection.min

        history.pushState(current)

        val lineStart = text.lastIndexOf('\n', cursor - 1) + 1

        val newText = buildString {
            append(text.substring(0, lineStart))
            append(prefix)
            append(text.substring(lineStart))
        }

        _textFieldValue.value = TextFieldValue(
            text      = newText,
            selection = TextRange(cursor + prefix.length)
        )
        onContentChanged(newText)
        updateCanFlags()
    }

    /**
     * 커서 위치에 텍스트를 삽입한다.
     */
    fun insertAtCursor(textToInsert: String) {
        val current = _textFieldValue.value
        val text    = current.text

        history.pushState(current)

        val newText = buildString {
            append(text.substring(0, current.selection.min))
            append(textToInsert)
            append(text.substring(current.selection.max))
        }

        val newCursor = current.selection.min + textToInsert.length
        _textFieldValue.value = TextFieldValue(
            text      = newText,
            selection = TextRange(newCursor)
        )
        onContentChanged(newText)
        updateCanFlags()
    }

    private fun updateCanFlags() {
        _canUndo.value = history.canUndo
        _canRedo.value = history.canRedo
    }
}
