package kr.nerdvana.nerdscribe.feature.autosave.model

/**
 * 자동 저장 설정.
 *
 * @param enabled    자동 저장 활성화 여부
 * @param intervalMs 자동 저장 주기 (밀리초)
 */
data class AutoSaveConfig(
    val enabled: Boolean    = true,
    val intervalMs: Long    = 30_000L
)
