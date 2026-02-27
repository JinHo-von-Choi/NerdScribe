# NerdScribe

스플릿 뷰 방식의 마크다운 에디터. Kotlin Multiplatform + Compose Multiplatform 기반으로, 왼쪽에서 마크다운을 작성하고 오른쪽에서 실시간 미리보기를 확인할 수 있다.

## 주요 기능

- **탭 기반 다중 문서**: 여러 문서를 탭으로 동시에 열고 전환
- **실시간 미리보기**: 마크다운 작성 시 오른쪽 패널에서 즉시 렌더링 결과 확인
- **스플릿 뷰**: 드래그로 편집기/미리보기 패널 크기 조절 가능
- **서식 툴바**: Bold, Italic, Heading, Link, Image, Code, Quote, List, Table, HR 버튼
- **Undo/Redo**: TextFieldValue 스냅샷 기반 실행 취소/다시 실행
- **검색/바꾸기**: 정규식, 대소문자 구분 토글, 매치 탐색, 전체 바꾸기
- **파일 트리 사이드바**: 폴더를 열어 디렉토리 구조 탐색, 파일 클릭 시 탭으로 열기
- **아웃라인 패널**: 헤딩 기반 문서 구조 트리, 클릭 시 해당 위치로 이동
- **마크다운 문법 하이라이팅**: 헤딩, 볼드, 이탤릭, 코드, 링크, 블록쿼트 스타일링
- **동기 스크롤**: 에디터/프리뷰 간 스크롤 위치 동기화 (토글 가능)
- **자동 저장**: 30초 주기로 dirty 문서 자동 저장 (파일 경로가 있는 문서만)
- **HTML/PDF 내보내기**: CSS 내장 HTML 또는 PDF로 문서 내보내기
- **이미지 붙여넣기**: 클립보드 이미지를 파일로 저장하고 마크다운 링크 삽입
- **테이블 편집 UI**: 모달 그리드 편집기로 마크다운 테이블 생성/편집
- **다크 모드**: 시스템 테마 또는 수동 전환
- **상태 바**: 줄/열 번호, 단어 수, 인코딩, 자동 저장 상태 표시

## 키보드 단축키

| 기능 | macOS | Windows/Linux |
|------|-------|---------------|
| 새 문서 | Cmd+N | Ctrl+N |
| 열기 | Cmd+O | Ctrl+O |
| 폴더 열기 | Cmd+Shift+O | Ctrl+Shift+O |
| 저장 | Cmd+S | Ctrl+S |
| 다른 이름으로 저장 | Cmd+Shift+S | Ctrl+Shift+S |
| 실행 취소 | Cmd+Z | Ctrl+Z |
| 다시 실행 | Cmd+Shift+Z | Ctrl+Shift+Z |
| 찾기 | Cmd+F | Ctrl+F |
| 바꾸기 | Cmd+H | Ctrl+H |
| 볼드 | Cmd+B | Ctrl+B |
| 이탤릭 | Cmd+I | Ctrl+I |
| 다크 모드 토글 | Cmd+Shift+D | Ctrl+Shift+D |
| 종료 | Cmd+Q | Ctrl+Q |

## 실행 방법

### Desktop (JVM)

```shell
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

JDK 21 이상 필요. JDK 25에서는 Gradle 호환성 문제가 있으므로 `JAVA_HOME`을 JDK 21로 지정할 것.

```shell
export JAVA_HOME=/path/to/jdk-21
./gradlew :composeApp:run
```

## 기술 스택

- **Kotlin Multiplatform** + **Compose Multiplatform**
- **multiplatform-markdown-renderer**: 마크다운 렌더링 및 코드 하이라이팅
- **openhtmltopdf** (선택): PDF 내보내기 (미설치 시 HTML 폴백)

## 아키텍처

Feature-Modular 패키지 구조. AppDependencies가 수동 DI 컨테이너 역할을 하며, CommandExecutor + EventBus로 feature 간 느슨한 결합을 유지한다.

```
App.kt -> AppLayout(FileTree | TabBar+Toolbar+FindReplace+SplitPane(Editor,Preview) | Outline) + StatusBar
AppDependencies(EventBus, CommandExecutor, SettingsVM, DocumentVM, EditorVM, FeatureVMs...)
```

## 프로젝트 구조

```
composeApp/src/
├── commonMain/kotlin/kr/nerdvana/nerdscribe/
│   ├── App.kt                          # 앱 루트 Composable
│   ├── AppDependencies.kt              # 수동 DI 컨테이너
│   ├── core/
│   │   ├── command/                    # Command, CommandExecutor, KeyboardShortcutManager
│   │   ├── event/                      # EventBus, AppEvent
│   │   ├── model/                      # DocumentId, DocumentState, AppSettings
│   │   ├── settings/                   # SettingsRepository(expect), SettingsViewModel
│   │   └── util/                       # ClipboardOperations(expect)
│   ├── feature/
│   │   ├── document/                   # 탭 관리: DocumentTab, DocumentManager, DocumentViewModel, TabBar
│   │   ├── editor/                     # 편집기: EditorViewModel, EditorToolbar, UndoRedoHistory, SyntaxHighlighter
│   │   ├── findreplace/                # 검색/바꾸기: FindReplaceState, FindReplaceViewModel, FindReplaceBar
│   │   ├── filetree/                   # 파일 트리: FileNode, Workspace, FileTreeViewModel, FileTreePanel
│   │   ├── syncscroll/                 # 동기 스크롤: ScrollSyncState, ScrollSyncController
│   │   ├── autosave/                   # 자동 저장: AutoSaveConfig, AutoSaveService
│   │   ├── export/                     # 내보내기: HtmlExporter, PdfExporter(expect), ExportDialog
│   │   ├── imagepaste/                 # 이미지 붙여넣기: ImagePasteHandler
│   │   ├── tableeditor/               # 테이블 편집: TableModel, TableEditorViewModel, TableEditorDialog
│   │   └── outline/                    # 아웃라인: HeadingNode, OutlineViewModel, OutlinePanel
│   ├── ui/
│   │   ├── MainScreen.kt              # 메인 화면 (전체 통합)
│   │   ├── layout/                     # AppLayout, StatusBar
│   │   ├── editor/                     # EditorPane
│   │   ├── preview/                    # PreviewPane
│   │   ├── components/                 # SplitPane, ResizablePanel
│   │   └── theme/                      # NerdScribeTheme
│   ├── util/
│   │   └── FileOperations.kt          # 파일 작업 (expect)
│   ├── model/
│   │   └── EditorState.kt             # 레거시 호환
│   └── viewmodel/
│       └── EditorViewModel.kt         # 레거시 호환
│
└── jvmMain/kotlin/kr/nerdvana/nerdscribe/
    ├── main.kt                         # Desktop 진입점 + MenuBar
    ├── core/
    │   ├── settings/                   # SettingsRepository.jvm (java.util.prefs)
    │   └── util/                       # ClipboardOperations.jvm (AWT)
    ├── feature/
    │   └── export/service/             # PdfExporter.jvm (openhtmltopdf)
    └── util/
        └── FileOperations.jvm.kt      # 파일 작업 (JFileChooser)
```

## 라이선스

MIT License
