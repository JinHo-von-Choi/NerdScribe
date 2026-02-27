package kr.nerdvana.nerdscribe.core.command

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType

/**
 * 키보드 단축키 -> CommandId 매핑 관리자.
 *
 * macOS에서는 Meta(Cmd), 그 외에서는 Ctrl을 주 modifier로 사용한다.
 * onKeyEvent에서 이 매니저를 호출하여 단축키에 해당하는 커맨드를 실행한다.
 */
class KeyboardShortcutManager(private val executor: CommandExecutor) {

    /**
     * 단축키 바인딩 정의.
     * Triple(key, shift, commandId) 형태.
     * meta/ctrl은 모든 바인딩에 공통 적용.
     */
    private data class Binding(
        val key: Key,
        val shift: Boolean = false,
        val commandId: CommandId
    )

    private val bindings = listOf(
        // 파일
        Binding(Key.N, shift = false, CommandId.NEW_DOCUMENT),
        Binding(Key.O, shift = false, CommandId.OPEN_FILE),
        Binding(Key.S, shift = false, CommandId.SAVE),
        Binding(Key.S, shift = true,  CommandId.SAVE_AS),

        // 편집
        Binding(Key.Z, shift = false, CommandId.UNDO),
        Binding(Key.Z, shift = true,  CommandId.REDO),
        Binding(Key.F, shift = false, CommandId.FIND),
        Binding(Key.H, shift = false, CommandId.REPLACE),

        // 서식
        Binding(Key.B, shift = false, CommandId.FORMAT_BOLD),
        Binding(Key.I, shift = false, CommandId.FORMAT_ITALIC),

        // 보기
        Binding(Key.D, shift = true,  CommandId.TOGGLE_DARK_MODE),
    )

    /**
     * KeyEvent를 처리하여 매칭되는 커맨드를 실행한다.
     *
     * @param event 키보드 이벤트
     * @return 이벤트가 소비되었으면 true
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val hasCmdOrCtrl = event.isMetaPressed || event.isCtrlPressed

        if (!hasCmdOrCtrl) return false

        for (binding in bindings) {
            if (event.key == binding.key && event.isShiftPressed == binding.shift) {
                executor.execute(binding.commandId)
                return true
            }
        }
        return false
    }
}
