package kr.nerdvana.nerdscribe.core.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.nerdvana.nerdscribe.core.model.AppSettings

/**
 * 앱 설정 상태 관리.
 * SettingsRepository를 통해 설정을 로드/저장하며,
 * StateFlow로 UI에 설정 변경을 전파한다.
 */
class SettingsViewModel(private val repository: SettingsRepository) {

    private val _settings = MutableStateFlow(repository.load())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        _settings.update { current ->
            val updated = transform(current)
            repository.save(updated)
            updated
        }
    }

    fun toggleDarkTheme() {
        updateSettings { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun toggleAutoSave() {
        updateSettings { it.copy(autoSaveEnabled = !it.autoSaveEnabled) }
    }

    fun toggleSyncScroll() {
        updateSettings { it.copy(syncScrollEnabled = !it.syncScrollEnabled) }
    }

    fun toggleOutline() {
        updateSettings { it.copy(showOutline = !it.showOutline) }
    }

    fun toggleFileTree() {
        updateSettings { it.copy(showFileTree = !it.showFileTree) }
    }
}
