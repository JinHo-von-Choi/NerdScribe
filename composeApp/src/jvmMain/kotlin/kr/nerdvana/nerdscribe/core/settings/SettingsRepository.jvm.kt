package kr.nerdvana.nerdscribe.core.settings

import kr.nerdvana.nerdscribe.core.model.AppSettings
import java.util.prefs.Preferences

/**
 * JVM 플랫폼 설정 저장소.
 * java.util.prefs.Preferences를 사용하여 설정을 영속화한다.
 */
actual class SettingsRepository actual constructor() {

    private val prefs = Preferences.userNodeForPackage(SettingsRepository::class.java)

    actual fun load(): AppSettings = AppSettings(
        isDarkTheme        = prefs.getBoolean(KEY_DARK_THEME, false),
        autoSaveEnabled    = prefs.getBoolean(KEY_AUTO_SAVE, true),
        autoSaveIntervalMs = prefs.getLong(KEY_AUTO_SAVE_INTERVAL, 30_000L),
        syncScrollEnabled  = prefs.getBoolean(KEY_SYNC_SCROLL, true),
        fontSize           = prefs.getInt(KEY_FONT_SIZE, 14),
        showOutline        = prefs.getBoolean(KEY_SHOW_OUTLINE, true),
        showFileTree       = prefs.getBoolean(KEY_SHOW_FILE_TREE, true)
    )

    actual fun save(settings: AppSettings) {
        prefs.putBoolean(KEY_DARK_THEME, settings.isDarkTheme)
        prefs.putBoolean(KEY_AUTO_SAVE, settings.autoSaveEnabled)
        prefs.putLong(KEY_AUTO_SAVE_INTERVAL, settings.autoSaveIntervalMs)
        prefs.putBoolean(KEY_SYNC_SCROLL, settings.syncScrollEnabled)
        prefs.putInt(KEY_FONT_SIZE, settings.fontSize)
        prefs.putBoolean(KEY_SHOW_OUTLINE, settings.showOutline)
        prefs.putBoolean(KEY_SHOW_FILE_TREE, settings.showFileTree)
        prefs.flush()
    }

    private companion object {
        const val KEY_DARK_THEME        = "dark_theme"
        const val KEY_AUTO_SAVE         = "auto_save"
        const val KEY_AUTO_SAVE_INTERVAL = "auto_save_interval"
        const val KEY_SYNC_SCROLL       = "sync_scroll"
        const val KEY_FONT_SIZE         = "font_size"
        const val KEY_SHOW_OUTLINE      = "show_outline"
        const val KEY_SHOW_FILE_TREE    = "show_file_tree"
    }
}
