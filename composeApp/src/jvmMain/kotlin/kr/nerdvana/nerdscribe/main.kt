package kr.nerdvana.nerdscribe

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import kr.nerdvana.nerdscribe.util.openFileDialog
import kr.nerdvana.nerdscribe.util.saveFile
import kr.nerdvana.nerdscribe.util.saveFileDialog
import kr.nerdvana.nerdscribe.viewmodel.EditorViewModel

fun main() = application {
    val viewModel = remember { EditorViewModel() }
    val state by viewModel.state.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val scope = rememberCoroutineScope()

    val windowTitle = buildString {
        append("NerdScribe - ")
        append(state.fileName)
        if (state.isDirty) {
            append(" *")
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = windowTitle,
    ) {
        MenuBar {
            Menu("파일") {
                Item(
                    "새 문서",
                    shortcut = KeyShortcut(Key.N, meta = true),
                    onClick = {
                        viewModel.newDocument()
                    }
                )
                Item(
                    "열기...",
                    shortcut = KeyShortcut(Key.O, meta = true),
                    onClick = {
                        scope.launch {
                            val result = openFileDialog()
                            if (result != null) {
                                viewModel.openFile(result.path, result.content)
                            }
                        }
                    }
                )
                Separator()
                Item(
                    "저장",
                    shortcut = KeyShortcut(Key.S, meta = true),
                    onClick = {
                        scope.launch {
                            val filePath = state.filePath
                            if (filePath != null) {
                                if (saveFile(filePath, state.content)) {
                                    viewModel.markAsSaved()
                                }
                            } else {
                                val savedPath = saveFileDialog(state.content, state.fileName)
                                if (savedPath != null) {
                                    viewModel.saveFile(savedPath)
                                }
                            }
                        }
                    }
                )
                Item(
                    "다른 이름으로 저장...",
                    shortcut = KeyShortcut(Key.S, meta = true, shift = true),
                    onClick = {
                        scope.launch {
                            val savedPath = saveFileDialog(state.content, state.fileName)
                            if (savedPath != null) {
                                viewModel.saveFile(savedPath)
                            }
                        }
                    }
                )
                Separator()
                Item(
                    "종료",
                    shortcut = KeyShortcut(Key.Q, meta = true),
                    onClick = ::exitApplication
                )
            }
            Menu("보기") {
                CheckboxItem(
                    "다크 모드",
                    checked = isDarkTheme,
                    shortcut = KeyShortcut(Key.D, meta = true, shift = true),
                    onCheckedChange = { viewModel.setDarkTheme(it) }
                )
            }
        }

        App(viewModel)
    }
}
