package kr.nerdvana.nerdscribe.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kr.nerdvana.nerdscribe.AppDependencies
import kr.nerdvana.nerdscribe.feature.document.ui.TabBar
import kr.nerdvana.nerdscribe.feature.editor.ui.EditorToolbar
import kr.nerdvana.nerdscribe.feature.filetree.ui.FileTreePanel
import kr.nerdvana.nerdscribe.feature.findreplace.ui.FindReplaceBar
import kr.nerdvana.nerdscribe.feature.outline.ui.OutlinePanel
import kr.nerdvana.nerdscribe.feature.tableeditor.ui.TableEditorDialog
import kr.nerdvana.nerdscribe.ui.components.SplitPane
import kr.nerdvana.nerdscribe.ui.editor.EditorPane
import kr.nerdvana.nerdscribe.ui.layout.AppLayout
import kr.nerdvana.nerdscribe.ui.layout.StatusBar
import kr.nerdvana.nerdscribe.ui.preview.PreviewPane

/**
 * 메인 화면.
 * AppLayout 기반 3단 레이아웃 + 탭바 + 에디터/프리뷰 스플릿.
 * 검색/바꾸기 바, 아웃라인, 파일 트리, 테이블 편집 다이얼로그를 통합한다.
 */
@Composable
fun MainScreen(
    deps: AppDependencies,
    modifier: Modifier = Modifier
) {
    val manager        by deps.documentViewModel.manager.collectAsState()
    val settings       by deps.settingsViewModel.settings.collectAsState()
    val textFieldValue by deps.editorViewModel.textFieldValue.collectAsState()
    val canUndo        by deps.editorViewModel.canUndo.collectAsState()
    val canRedo        by deps.editorViewModel.canRedo.collectAsState()
    val findState      by deps.findReplaceViewModel.state.collectAsState()
    val workspace      by deps.fileTreeViewModel.workspace.collectAsState()
    val headings       by deps.outlineViewModel.headings.collectAsState()
    val autoSaveMsg    by deps.autoSaveService.lastSaveMessage.collectAsState()

    val activeState = manager.activeState
    val content     = activeState?.content ?: ""

    /** 탭 전환 시 에디터 내용을 동기화 */
    val lastActiveId = remember { mutableStateOf(manager.activeId) }
    LaunchedEffect(manager.activeId) {
        if (manager.activeId != lastActiveId.value) {
            lastActiveId.value = manager.activeId
            deps.editorViewModel.resetContent(manager.activeState?.content ?: "")
        }
    }

    /** 에디터 초기화: 최초 로드 시 콘텐츠 동기화 */
    LaunchedEffect(Unit) {
        deps.editorViewModel.resetContent(content)
    }

    /** 아웃라인 업데이트 (콘텐츠 변경 시) */
    LaunchedEffect(content) {
        deps.outlineViewModel.updateContent(content)
    }

    val wordCount = if (content.isBlank()) 0
                    else content.split(Regex("\\s+")).size

    /** 커서 위치에서 줄/열 번호 계산 */
    val cursorOffset     = textFieldValue.selection.min
    val safeOffset       = cursorOffset.coerceAtMost(content.length)
    val textBeforeCursor = content.substring(0, safeOffset)
    val lineNumber       = textBeforeCursor.count { it == '\n' } + 1
    val columnNumber     = safeOffset - textBeforeCursor.lastIndexOf('\n')

    /** 상태 바 메시지: 자동 저장 > dirty 표시 */
    val statusMessage = when {
        autoSaveMsg.isNotEmpty() -> autoSaveMsg
        activeState?.isDirty == true -> "수정됨"
        else -> ""
    }

    Box(modifier = modifier.fillMaxSize()) {
        AppLayout(
            showFileTree = settings.showFileTree,
            showOutline  = settings.showOutline,
            toolbar      = {
                EditorToolbar(
                    commandExecutor    = deps.commandExecutor,
                    canUndo            = canUndo,
                    canRedo            = canRedo,
                    syncScrollEnabled  = settings.syncScrollEnabled,
                    onToggleSyncScroll = { deps.settingsViewModel.toggleSyncScroll() }
                )
            },
            fileTree = {
                FileTreePanel(
                    workspace   = workspace,
                    onNodeClick = { deps.fileTreeViewModel.onFileClick(it) }
                )
            },
            editor = {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabBar(
                        tabs       = manager.tabs,
                        activeId   = manager.activeId,
                        onTabClick = { deps.documentViewModel.switchTab(it) },
                        onTabClose = { deps.documentViewModel.closeTab(it) }
                    )

                    FindReplaceBar(
                        state               = findState,
                        onQueryChange       = { deps.findReplaceViewModel.updateQuery(it) },
                        onReplacementChange = { deps.findReplaceViewModel.updateReplacement(it) },
                        onNext              = { deps.findReplaceViewModel.nextMatch() },
                        onPrevious          = { deps.findReplaceViewModel.previousMatch() },
                        onReplace           = { deps.findReplaceViewModel.replaceCurrent() },
                        onReplaceAll        = { deps.findReplaceViewModel.replaceAll() },
                        onToggleRegex       = { deps.findReplaceViewModel.toggleRegex() },
                        onToggleCaseSensitive = { deps.findReplaceViewModel.toggleCaseSensitive() },
                        onToggleReplace     = { deps.findReplaceViewModel.toggleReplace() },
                        onClose             = { deps.findReplaceViewModel.hide() }
                    )

                    SplitPane(
                        modifier          = Modifier.weight(1f),
                        initialSplitRatio = 0.5f,
                        leftContent = {
                            EditorPane(
                                value         = textFieldValue,
                                onValueChange = { deps.editorViewModel.onValueChange(it) }
                            )
                        },
                        rightContent = {
                            PreviewPane(content = content)
                        }
                    )
                }
            },
            outline = {
                OutlinePanel(
                    headings       = headings,
                    onHeadingClick = { lineNum ->
                        deps.eventBus.tryEmit(
                            kr.nerdvana.nerdscribe.core.event.AppEvent.NavigateToLine(lineNum)
                        )
                    }
                )
            },
            statusBar = {
                StatusBar(
                    lineNumber    = lineNumber,
                    columnNumber  = columnNumber,
                    wordCount     = wordCount,
                    statusMessage = statusMessage
                )
            }
        )

        /** 테이블 편집 다이얼로그 */
        TableEditorDialog(
            viewModel = deps.tableEditorViewModel,
            onInsert  = { markdown -> deps.editorViewModel.insertAtCursor(markdown) },
            onDismiss = { deps.tableEditorViewModel.hide() }
        )
    }
}
