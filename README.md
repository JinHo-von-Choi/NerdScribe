# NerdScribe

스플릿 뷰 방식의 마크다운 에디터입니다. 왼쪽에서 마크다운을 작성하고 오른쪽에서 실시간 미리보기를 확인할 수 있습니다.

## 주요 기능

- **실시간 미리보기**: 마크다운 작성 시 오른쪽 패널에서 즉시 렌더링 결과 확인
- **스플릿 뷰**: 드래그로 편집기/미리보기 패널 크기 조절 가능
- **파일 작업**: 마크다운(.md) 및 텍스트(.txt) 파일 열기/저장
- **코드 문법 하이라이팅**: 코드 블록 자동 하이라이팅

## 키보드 단축키

| 기능 | macOS | Windows/Linux |
|------|-------|---------------|
| 새 문서 | Cmd+N | Ctrl+N |
| 열기 | Cmd+O | Ctrl+O |
| 저장 | Cmd+S | Ctrl+S |
| 다른 이름으로 저장 | Cmd+Shift+S | Ctrl+Shift+S |
| 종료 | Cmd+Q | Ctrl+Q |

## 실행 방법

### Desktop (JVM)

```shell
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

## 기술 스택

- **Kotlin Multiplatform** + **Compose Multiplatform**
- **multiplatform-markdown-renderer**: 마크다운 렌더링 및 코드 하이라이팅

## 프로젝트 구조

```
composeApp/src/
├── commonMain/kotlin/kr/nerdvana/nerdscribe/
│   ├── App.kt                      # 앱 진입점
│   ├── model/
│   │   └── EditorState.kt          # 에디터 상태 모델
│   ├── viewmodel/
│   │   └── EditorViewModel.kt      # 상태 관리
│   ├── ui/
│   │   ├── MainScreen.kt           # 메인 화면
│   │   ├── editor/
│   │   │   └── EditorPane.kt       # 마크다운 편집기
│   │   ├── preview/
│   │   │   └── PreviewPane.kt      # 미리보기 패널
│   │   └── components/
│   │       └── SplitPane.kt        # 스플릿 컨테이너
│   └── util/
│       └── FileOperations.kt       # 파일 작업 (expect)
│
└── jvmMain/kotlin/kr/nerdvana/nerdscribe/
    ├── main.kt                     # Desktop 진입점
    └── util/
        └── FileOperations.jvm.kt   # 파일 작업 (actual)
```

## 라이선스

MIT License
