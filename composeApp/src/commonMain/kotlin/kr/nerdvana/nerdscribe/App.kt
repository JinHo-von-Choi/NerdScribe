package kr.nerdvana.nerdscribe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kr.nerdvana.nerdscribe.ui.MainScreen
import kr.nerdvana.nerdscribe.ui.theme.NerdScribeTheme
import kr.nerdvana.nerdscribe.viewmodel.EditorViewModel

@Composable
fun App(viewModel: EditorViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    NerdScribeTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val state by viewModel.state.collectAsState()
            MainScreen(
                state = state,
                onContentChange = { viewModel.updateContent(it) }
            )
        }
    }
}
