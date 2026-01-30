package kr.nerdvana.nerdscribe.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.nerdvana.nerdscribe.model.EditorState

class EditorViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun updateContent(newContent: String) {
        _state.update { currentState ->
            currentState.copy(
                content = newContent,
                isDirty = newContent != currentState.content || currentState.isDirty
            )
        }
    }

    fun openFile(filePath: String, content: String) {
        val fileName = filePath.substringAfterLast("/").substringAfterLast("\\")
        _state.value = EditorState(
            content = content,
            filePath = filePath,
            fileName = fileName,
            isDirty = false
        )
    }

    fun saveFile(filePath: String? = null) {
        val path = filePath ?: _state.value.filePath
        if (path != null) {
            val fileName = path.substringAfterLast("/").substringAfterLast("\\")
            _state.update { currentState ->
                currentState.copy(
                    filePath = path,
                    fileName = fileName,
                    isDirty = false
                )
            }
        }
    }

    fun newDocument() {
        _state.value = EditorState()
    }

    fun markAsSaved() {
        _state.update { it.copy(isDirty = false) }
    }
}
