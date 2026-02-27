package kr.nerdvana.nerdscribe.core.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 커맨드 레지스트리 및 실행기.
 * CommandId에 대응하는 Command를 등록하고, 이름으로 실행한다.
 *
 * @param scope 커맨드 실행에 사용할 CoroutineScope
 */
class CommandExecutor(private val scope: CoroutineScope) {
    private val commands = mutableMapOf<CommandId, Command>()

    /**
     * 커맨드를 레지스트리에 등록한다.
     *
     * @param id      커맨드 식별자
     * @param command 실행할 커맨드
     */
    fun register(id: CommandId, command: Command) {
        commands[id] = command
    }

    /**
     * 등록된 커맨드를 비동기 실행한다.
     * 미등록 커맨드는 무시한다.
     *
     * @param id 실행할 커맨드 식별자
     */
    fun execute(id: CommandId) {
        val command = commands[id] ?: return
        scope.launch { command.execute() }
    }

    /**
     * 특정 커맨드가 등록되어 있는지 확인한다.
     */
    fun isRegistered(id: CommandId): Boolean = commands.containsKey(id)
}
