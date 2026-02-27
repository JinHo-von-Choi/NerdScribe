package kr.nerdvana.nerdscribe

import kotlinx.coroutines.CoroutineScope
import kr.nerdvana.nerdscribe.core.command.CommandExecutor
import kr.nerdvana.nerdscribe.core.command.CommandId
import kr.nerdvana.nerdscribe.core.command.KeyboardShortcutManager
import kr.nerdvana.nerdscribe.core.event.AppEvent
import kr.nerdvana.nerdscribe.core.event.EventBus
import kr.nerdvana.nerdscribe.core.settings.SettingsRepository
import kr.nerdvana.nerdscribe.core.settings.SettingsViewModel
import kr.nerdvana.nerdscribe.feature.autosave.model.AutoSaveConfig
import kr.nerdvana.nerdscribe.feature.autosave.service.AutoSaveService
import kr.nerdvana.nerdscribe.feature.document.viewmodel.DocumentViewModel
import kr.nerdvana.nerdscribe.feature.editor.viewmodel.EditorViewModel
import kr.nerdvana.nerdscribe.feature.filetree.viewmodel.FileTreeViewModel
import kr.nerdvana.nerdscribe.feature.findreplace.viewmodel.FindReplaceViewModel
import kr.nerdvana.nerdscribe.feature.outline.viewmodel.OutlineViewModel
import kr.nerdvana.nerdscribe.feature.syncscroll.controller.ScrollSyncController
import kr.nerdvana.nerdscribe.feature.tableeditor.viewmodel.TableEditorViewModel
import kr.nerdvana.nerdscribe.util.saveFile

/**
 * 수동 DI 컨테이너.
 * 앱 전역 의존성을 조립하고 Feature ViewModel에 제공한다.
 *
 * @param scope 앱 수준 CoroutineScope
 */
class AppDependencies(val scope: CoroutineScope) {
    val eventBus            = EventBus()
    val commandExecutor     = CommandExecutor(scope)
    val shortcutManager     = KeyboardShortcutManager(commandExecutor)
    val settingsRepository  = SettingsRepository()
    val settingsViewModel   = SettingsViewModel(settingsRepository)
    val documentViewModel   = DocumentViewModel()
    val editorViewModel     = EditorViewModel(
        onContentChanged = { documentViewModel.updateContent(it) }
    )
    val findReplaceViewModel = FindReplaceViewModel(
        getContent = { documentViewModel.manager.value.activeState?.content ?: "" },
        setContent = { editorViewModel.insertAtCursor(it) },
        navigateTo = { /* 커서 이동은 Phase 5에서 후속 처리 */ }
    )
    val fileTreeViewModel = FileTreeViewModel(
        scope      = scope,
        onFileOpen = { path, content -> documentViewModel.openFile(path, content) }
    )
    val scrollSyncController = ScrollSyncController()
    val outlineViewModel     = OutlineViewModel()
    val tableEditorViewModel = TableEditorViewModel()
    val autoSaveService      = AutoSaveService(
        scope      = scope,
        getDirtyTabs = {
            documentViewModel.getDirtyTabsWithPaths().map { tab ->
                tab.id to (tab.filePath!! to tab.state.content)
            }
        },
        saveTab    = { path, content -> saveFile(path, content) },
        markSaved  = { id -> documentViewModel.markTabSaved(id) }
    )

    /**
     * 기본 커맨드를 등록한다.
     * 플랫폼별 진입점에서 파일 다이얼로그 등을 추가 등록할 수 있다.
     */
    fun registerCoreCommands() {
        commandExecutor.register(CommandId.NEW_DOCUMENT) {
            documentViewModel.newDocument()
        }
        commandExecutor.register(CommandId.UNDO) {
            editorViewModel.undo()
        }
        commandExecutor.register(CommandId.REDO) {
            editorViewModel.redo()
        }
        commandExecutor.register(CommandId.FIND) {
            findReplaceViewModel.show(withReplace = false)
        }
        commandExecutor.register(CommandId.REPLACE) {
            findReplaceViewModel.show(withReplace = true)
        }
        commandExecutor.register(CommandId.TOGGLE_DARK_MODE) {
            settingsViewModel.toggleDarkTheme()
        }
        commandExecutor.register(CommandId.TOGGLE_FILE_TREE) {
            settingsViewModel.toggleFileTree()
        }
        commandExecutor.register(CommandId.TOGGLE_OUTLINE) {
            settingsViewModel.toggleOutline()
        }
        commandExecutor.register(CommandId.TOGGLE_SYNC_SCROLL) {
            settingsViewModel.toggleSyncScroll()
        }
        commandExecutor.register(CommandId.FORMAT_TABLE) {
            tableEditorViewModel.show()
        }

        // 서식 커맨드
        commandExecutor.register(CommandId.FORMAT_BOLD) {
            editorViewModel.wrapSelection("**", "**", "bold")
        }
        commandExecutor.register(CommandId.FORMAT_ITALIC) {
            editorViewModel.wrapSelection("*", "*", "italic")
        }
        commandExecutor.register(CommandId.FORMAT_STRIKETHROUGH) {
            editorViewModel.wrapSelection("~~", "~~", "strikethrough")
        }
        commandExecutor.register(CommandId.FORMAT_CODE) {
            editorViewModel.wrapSelection("`", "`", "code")
        }
        commandExecutor.register(CommandId.FORMAT_CODE_BLOCK) {
            editorViewModel.wrapSelection("```\n", "\n```", "code")
        }
        commandExecutor.register(CommandId.FORMAT_LINK) {
            editorViewModel.wrapSelection("[", "](url)", "link text")
        }
        commandExecutor.register(CommandId.FORMAT_IMAGE) {
            editorViewModel.wrapSelection("![", "](url)", "alt text")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING1) {
            editorViewModel.insertLinePrefix("# ")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING2) {
            editorViewModel.insertLinePrefix("## ")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING3) {
            editorViewModel.insertLinePrefix("### ")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING4) {
            editorViewModel.insertLinePrefix("#### ")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING5) {
            editorViewModel.insertLinePrefix("##### ")
        }
        commandExecutor.register(CommandId.FORMAT_HEADING6) {
            editorViewModel.insertLinePrefix("###### ")
        }
        commandExecutor.register(CommandId.FORMAT_QUOTE) {
            editorViewModel.insertLinePrefix("> ")
        }
        commandExecutor.register(CommandId.FORMAT_UNORDERED_LIST) {
            editorViewModel.insertLinePrefix("- ")
        }
        commandExecutor.register(CommandId.FORMAT_ORDERED_LIST) {
            editorViewModel.insertLinePrefix("1. ")
        }
        commandExecutor.register(CommandId.FORMAT_HORIZONTAL_RULE) {
            editorViewModel.insertAtCursor("\n---\n")
        }

        // 자동 저장 시작
        val settings = settingsViewModel.settings.value
        autoSaveService.start(
            AutoSaveConfig(
                enabled    = settings.autoSaveEnabled,
                intervalMs = settings.autoSaveIntervalMs
            )
        )
    }
}
