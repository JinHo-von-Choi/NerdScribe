package kr.nerdvana.nerdscribe

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        val scope = rememberCoroutineScope()
        val deps  = remember {
            AppDependencies(scope).also { it.registerCoreCommands() }
        }
        App(deps)
    }
}
