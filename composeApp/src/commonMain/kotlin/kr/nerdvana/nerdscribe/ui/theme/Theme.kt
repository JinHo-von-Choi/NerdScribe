package kr.nerdvana.nerdscribe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary                = Color(0xFF1975D2),
    onPrimary              = Color.White,
    primaryContainer       = Color(0xFFD4E8FC),
    onPrimaryContainer     = Color(0xFF0B3D7A),
    secondary              = Color(0xFF526070),
    onSecondary            = Color.White,
    secondaryContainer     = Color(0xFFD5E3F7),
    onSecondaryContainer   = Color(0xFF0E1D2B),
    background             = Color(0xFFFAFBFC),
    onBackground           = Color(0xFF1F2937),
    surface                = Color(0xFFFFFFFF),
    onSurface              = Color(0xFF1F2937),
    surfaceVariant         = Color(0xFFF3F4F6),
    onSurfaceVariant       = Color(0xFF4B5563),
    surfaceContainerHighest = Color(0xFFE5E7EB),
    surfaceContainer       = Color(0xFFEEF0F2),
    surfaceDim             = Color(0xFFF9FAFB),
    outline                = Color(0xFFD1D5DB),
    outlineVariant         = Color(0xFFE5E7EB),
    error                  = Color(0xFFDC2626),
    onError                = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary                = Color(0xFF7CB8F2),
    onPrimary              = Color(0xFF0A3060),
    primaryContainer       = Color(0xFF1A4A80),
    onPrimaryContainer     = Color(0xFFD4E8FC),
    secondary              = Color(0xFF9BB8D4),
    onSecondary            = Color(0xFF1C2E40),
    secondaryContainer     = Color(0xFF334558),
    onSecondaryContainer   = Color(0xFFD5E3F7),
    background             = Color(0xFF0F1117),
    onBackground           = Color(0xFFD4D6DB),
    surface                = Color(0xFF181A20),
    onSurface              = Color(0xFFD4D6DB),
    surfaceVariant         = Color(0xFF1E2028),
    onSurfaceVariant       = Color(0xFF9CA3AF),
    surfaceContainerHighest = Color(0xFF2A2D36),
    surfaceContainer       = Color(0xFF232630),
    surfaceDim             = Color(0xFF141620),
    outline                = Color(0xFF4B5563),
    outlineVariant         = Color(0xFF2E3440),
    error                  = Color(0xFFF87171),
    onError                = Color(0xFF450A0A)
)

@Composable
fun NerdScribeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content
    )
}
