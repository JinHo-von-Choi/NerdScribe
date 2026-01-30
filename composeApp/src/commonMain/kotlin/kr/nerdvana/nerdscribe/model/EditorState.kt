package kr.nerdvana.nerdscribe.model

data class EditorState(
    val content: String = """
# 안녕하세요! 👋

**NerdScribe**에 오신 것을 환영합니다.

이것은 실시간 미리보기를 지원하는 마크다운 에디터입니다.
왼쪽에서 마크다운을 작성하면 오른쪽에서 실시간으로 결과를 확인할 수 있습니다.

## 시작하기

지금 바로 마크다운을 작성해보세요!

### 예제

**굵게**, *기울임*, ~~취소선~~

- 목록 항목 1
- 목록 항목 2
- 목록 항목 3

```kotlin
fun main() {
    println("Hello, NerdScribe!")
}
```

> 인용문도 사용할 수 있습니다.

즐거운 작성 되세요! 🚀
    """.trimIndent(),
    val filePath: String? = null,
    val fileName: String = "제목 없음.md",
    val isDirty: Boolean = false
)
