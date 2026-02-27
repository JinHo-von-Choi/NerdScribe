package kr.nerdvana.nerdscribe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import kr.nerdvana.nerdscribe.ui.MainScreen
import kr.nerdvana.nerdscribe.ui.theme.NerdScribeTheme

/**
 * 앱 루트 Composable.
 * AppDependencies를 받아 테마, 단축키, 메인 화면을 구성한다.
 */
@Composable
fun App(deps: AppDependencies) {
    val settings by deps.settingsViewModel.settings.collectAsState()

    NerdScribeTheme(darkTheme = settings.isDarkTheme) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { deps.shortcutManager.handleKeyEvent(it) },
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(deps = deps)
        }
    }
}
