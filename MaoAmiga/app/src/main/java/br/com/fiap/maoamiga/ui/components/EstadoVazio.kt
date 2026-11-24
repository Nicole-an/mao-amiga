package br.com.fiap.maoamiga.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun EstadoVazio(
    icone: ImageVector,
    mensagem: String,
    modifier: Modifier = Modifier,
    textoAcao: String? = null,
    onAcao: (() -> Unit)? = null,
    textoAcaoSecundaria: String? = null,
    onAcaoSecundaria: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = mensagem,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        if (textoAcao != null && onAcao != null) {
            Button(onClick = onAcao) { Text(textoAcao) }
        }
        if (textoAcaoSecundaria != null && onAcaoSecundaria != null) {
            TextButton(onClick = onAcaoSecundaria) { Text(textoAcaoSecundaria) }
        }
    }
}

/** Exibe em Snackbar a mensagem recebida de outra tela (via SavedStateHandle) e a consome. */
@Composable
fun MensagemResultadoEffect(
    mensagem: String?,
    snackbarHostState: SnackbarHostState,
    onConsumida: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val consumir by rememberUpdatedState(onConsumida)
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            consumir()
            scope.launch { snackbarHostState.showSnackbar(mensagem) }
        }
    }
}
