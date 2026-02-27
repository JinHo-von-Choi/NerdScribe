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
import kr.nerdvana.nerdscribe.core.command.CommandId
import kr.nerdvana.nerdscribe.feature.export.model.ExportFormat
import kr.nerdvana.nerdscribe.feature.export.service.HtmlExporter
import kr.nerdvana.nerdscribe.feature.export.service.PdfExporter
import kr.nerdvana.nerdscribe.util.openDirectoryDialog
import kr.nerdvana.nerdscribe.util.openFileDialog
import kr.nerdvana.nerdscribe.util.saveFile
import kr.nerdvana.nerdscribe.util.saveFileDialog

fun main() = application {
    val scope = rememberCoroutineScope()
    val deps  = remember {
        AppDependencies(scope).also { it.registerCoreCommands() }
    }

    /** JVM 전용 커맨드 등록 (파일 다이얼로그, 내보내기, 폴더 열기) */
    remember {
        deps.commandExecutor.register(CommandId.OPEN_FILE) {
            val result = openFileDialog()
            if (result != null) {
                deps.documentViewModel.openFile(result.path, result.content)
                deps.editorViewModel.resetContent(result.content)
            }
        }
        deps.commandExecutor.register(CommandId.SAVE) {
            val state = deps.documentViewModel.manager.value.activeState ?: return@register
            val path  = state.filePath
            if (path != null) {
                if (saveFile(path, state.content)) {
                    deps.documentViewModel.markSaved()
                }
            } else {
                val savedPath = saveFileDialog(state.content, state.fileName)
                if (savedPath != null) {
                    deps.documentViewModel.markSaved(savedPath)
                }
            }
        }
        deps.commandExecutor.register(CommandId.SAVE_AS) {
            val state = deps.documentViewModel.manager.value.activeState ?: return@register
            val savedPath = saveFileDialog(state.content, state.fileName)
            if (savedPath != null) {
                deps.documentViewModel.markSaved(savedPath)
            }
        }
        deps.commandExecutor.register(CommandId.OPEN_FOLDER) {
            val dirPath = openDirectoryDialog()
            if (dirPath != null) {
                deps.fileTreeViewModel.openFolder(dirPath)
            }
        }
        deps.commandExecutor.register(CommandId.EXPORT_HTML) {
            val state = deps.documentViewModel.manager.value.activeState ?: return@register
            val html  = HtmlExporter.export(state.content, state.fileName)
            val suggestedName = state.fileName.removeSuffix(".md") + ".html"
            val savedPath = saveFileDialog(html, suggestedName)
            if (savedPath != null) {
                deps.eventBus.tryEmit(
                    kr.nerdvana.nerdscribe.core.event.AppEvent.StatusMessage("HTML로 내보냄: $savedPath")
                )
            }
        }
        deps.commandExecutor.register(CommandId.EXPORT_PDF) {
            val state = deps.documentViewModel.manager.value.activeState ?: return@register
            val html  = HtmlExporter.export(state.content, state.fileName)
            val suggestedName = state.fileName.removeSuffix(".md") + ".pdf"
            val savedPath = saveFileDialog("", suggestedName)
            if (savedPath != null) {
                PdfExporter.export(html, savedPath)
                deps.eventBus.tryEmit(
                    kr.nerdvana.nerdscribe.core.event.AppEvent.StatusMessage("PDF로 내보냄: $savedPath")
                )
            }
        }
    }

    val manager  by deps.documentViewModel.manager.collectAsState()
    val settings by deps.settingsViewModel.settings.collectAsState()

    val activeState = manager.activeState
    val windowTitle = buildString {
        append("NerdScribe - ")
        append(activeState?.fileName ?: "제목 없음.md")
        if (activeState?.isDirty == true) append(" *")
    }

    Window(
        onCloseRequest = ::exitApplication,
        title          = windowTitle,
    ) {
        MenuBar {
            Menu("파일") {
                Item(
                    "새 문서",
                    shortcut = KeyShortcut(Key.N, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.NEW_DOCUMENT) }
                )
                Item(
                    "열기...",
                    shortcut = KeyShortcut(Key.O, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.OPEN_FILE) }
                )
                Item(
                    "폴더 열기...",
                    shortcut = KeyShortcut(Key.O, meta = true, shift = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.OPEN_FOLDER) }
                )
                Separator()
                Item(
                    "저장",
                    shortcut = KeyShortcut(Key.S, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.SAVE) }
                )
                Item(
                    "다른 이름으로 저장...",
                    shortcut = KeyShortcut(Key.S, meta = true, shift = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.SAVE_AS) }
                )
                Separator()
                Item(
                    "HTML로 내보내기...",
                    onClick = { deps.commandExecutor.execute(CommandId.EXPORT_HTML) }
                )
                Item(
                    "PDF로 내보내기...",
                    onClick = { deps.commandExecutor.execute(CommandId.EXPORT_PDF) }
                )
                Separator()
                Item(
                    "종료",
                    shortcut = KeyShortcut(Key.Q, meta = true),
                    onClick  = ::exitApplication
                )
            }
            Menu("편집") {
                Item(
                    "실행 취소",
                    shortcut = KeyShortcut(Key.Z, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.UNDO) }
                )
                Item(
                    "다시 실행",
                    shortcut = KeyShortcut(Key.Z, meta = true, shift = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.REDO) }
                )
                Separator()
                Item(
                    "찾기...",
                    shortcut = KeyShortcut(Key.F, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.FIND) }
                )
                Item(
                    "바꾸기...",
                    shortcut = KeyShortcut(Key.H, meta = true),
                    onClick  = { deps.commandExecutor.execute(CommandId.REPLACE) }
                )
            }
            Menu("보기") {
                CheckboxItem(
                    "다크 모드",
                    checked         = settings.isDarkTheme,
                    shortcut        = KeyShortcut(Key.D, meta = true, shift = true),
                    onCheckedChange = { deps.settingsViewModel.toggleDarkTheme() }
                )
                CheckboxItem(
                    "파일 트리",
                    checked         = settings.showFileTree,
                    onCheckedChange = { deps.settingsViewModel.toggleFileTree() }
                )
                CheckboxItem(
                    "아웃라인",
                    checked         = settings.showOutline,
                    onCheckedChange = { deps.settingsViewModel.toggleOutline() }
                )
                CheckboxItem(
                    "동기 스크롤",
                    checked         = settings.syncScrollEnabled,
                    onCheckedChange = { deps.settingsViewModel.toggleSyncScroll() }
                )
            }
        }

        App(deps)
    }
}
