package br.com.fiap.maoamiga.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LaranjaCalor,
    onPrimary = Color.White,
    primaryContainer = LaranjaClaro,
    onPrimaryContainer = LaranjaProfundo,
    secondary = VerdeEsperanca,
    onSecondary = Color.White,
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = VerdeProfundo,
    background = FundoClaro,
    onBackground = TextoEscuro,
    surface = FundoClaro,
    onSurface = TextoEscuro,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoVariante,
    surfaceContainerLow = SuperficieClara,
    surfaceContainerHighest = SuperficieVariante
)

private val DarkColors = darkColorScheme(
    primary = LaranjaNoturno,
    onPrimary = LaranjaNoturnoTexto,
    primaryContainer = LaranjaNoturnoContainer,
    onPrimaryContainer = LaranjaClaro,
    secondary = VerdeNoturno,
    onSecondary = VerdeNoturnoTexto,
    secondaryContainer = VerdeNoturnoContainer,
    onSecondaryContainer = VerdeClaro,
    background = FundoEscuro,
    onBackground = TextoClaro,
    surface = FundoEscuro,
    onSurface = TextoClaro,
    surfaceContainerLow = SuperficieEscura
)

@Composable
fun MaoAmigaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
