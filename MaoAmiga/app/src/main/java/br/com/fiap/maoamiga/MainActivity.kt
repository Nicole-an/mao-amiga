package br.com.fiap.maoamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import br.com.fiap.maoamiga.ui.navigation.MaoAmigaNavHost
import br.com.fiap.maoamiga.ui.theme.MaoAmigaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaoAmigaTheme {
                MaoAmigaApp()
            }
        }
    }
}

@Composable
fun MaoAmigaApp() {
    MaoAmigaNavHost()
}
