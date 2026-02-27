package kr.nerdvana.nerdscribe.core.settings

import kr.nerdvana.nerdscribe.core.model.AppSettings

/**
 * 설정 영속화를 위한 expect 인터페이스.
 * 각 플랫폼에서 actual 구현을 제공한다.
 */
expect class SettingsRepository() {
    fun load(): AppSettings
    fun save(settings: AppSettings)
}
