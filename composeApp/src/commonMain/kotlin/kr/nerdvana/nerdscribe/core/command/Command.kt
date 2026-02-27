package kr.nerdvana.nerdscribe.core.command

/**
 * 사용자 액션을 추상화하는 Command 인터페이스.
 * 메뉴, 툴바, 단축키 모두 동일한 Command를 실행한다.
 */
fun interface Command {
    suspend fun execute()
}

/**
 * 앱 전체 커맨드 식별자.
 * 메뉴/툴바/단축키에서 동일한 커맨드를 참조할 때 사용.
 */
enum class CommandId {
    // 파일 관련
    NEW_DOCUMENT,
    OPEN_FILE,
    SAVE,
    SAVE_AS,
    OPEN_FOLDER,

    // 편집 관련
    UNDO,
    REDO,
    FIND,
    REPLACE,
    FORMAT_BOLD,
    FORMAT_ITALIC,
    FORMAT_HEADING1,
    FORMAT_HEADING2,
    FORMAT_HEADING3,
    FORMAT_HEADING4,
    FORMAT_HEADING5,
    FORMAT_HEADING6,
    FORMAT_LINK,
    FORMAT_IMAGE,
    FORMAT_CODE,
    FORMAT_CODE_BLOCK,
    FORMAT_QUOTE,
    FORMAT_UNORDERED_LIST,
    FORMAT_ORDERED_LIST,
    FORMAT_TABLE,
    FORMAT_HORIZONTAL_RULE,
    FORMAT_STRIKETHROUGH,

    // 내보내기
    EXPORT_HTML,
    EXPORT_PDF,

    // 보기
    TOGGLE_DARK_MODE,
    TOGGLE_FILE_TREE,
    TOGGLE_OUTLINE,
    TOGGLE_SYNC_SCROLL,
}
