package br.com.fiap.maoamiga.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.maoamiga.R
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.ui.components.MensagemResultadoEffect
import br.com.fiap.maoamiga.ui.components.cor
import br.com.fiap.maoamiga.viewmodel.AppViewModelProvider
import br.com.fiap.maoamiga.viewmodel.PedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mensagem: String?,
    onMensagemConsumida: () -> Unit,
    onNovoPedido: () -> Unit,
    onVerPedidos: () -> Unit,
    onSobre: () -> Unit,
    viewModel: PedidosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val contadores by viewModel.contadores.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    MensagemResultadoEffect(mensagem, snackbarHostState, onMensagemConsumida)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CabecalhoMarca()

            Text(
                text = stringResource(R.string.home_resumo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContadorCard("Abertos", contadores.abertos, StatusPedido.ABERTO.cor(), Modifier.weight(1f))
                ContadorCard("Em andamento", contadores.emAndamento, StatusPedido.EM_ANDAMENTO.cor(), Modifier.weight(1f))
                ContadorCard("Atendidos", contadores.atendidos, StatusPedido.ATENDIDO.cor(), Modifier.weight(1f))
            }

            BotaoHome(stringResource(R.string.acao_novo_pedido), Icons.Filled.Add, onNovoPedido, Estilo.PRINCIPAL)
            BotaoHome(stringResource(R.string.acao_ver_pedidos), Icons.AutoMirrored.Filled.List, onVerPedidos, Estilo.TONAL)
            BotaoHome(stringResource(R.string.acao_sobre), Icons.Filled.Info, onSobre, Estilo.CONTORNO)

            SeloOds()
        }
    }
}

@Composable
private fun CabecalhoMarca() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.VolunteerActivism,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = stringResource(R.string.slogan), style = MaterialTheme.typography.bodyLarge)
                }
            }
            Text(text = stringResource(R.string.home_descricao), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ContadorCard(rotulo: String, valor: Int, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.semantics(mergeDescendants = true) {},
        colors = CardDefaults.cardColors(containerColor = cor, contentColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = rotulo, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

private enum class Estilo { PRINCIPAL, TONAL, CONTORNO }

@Composable
private fun BotaoHome(texto: String, icone: ImageVector, onClick: () -> Unit, estilo: Estilo) {
    val modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 56.dp)
    val conteudo: @Composable () -> Unit = {
        Icon(icone, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
    when (estilo) {
        Estilo.PRINCIPAL -> Button(onClick = onClick, modifier = modifier) { conteudo() }
        Estilo.TONAL -> FilledTonalButton(onClick = onClick, modifier = modifier) { conteudo() }
        Estilo.CONTORNO -> OutlinedButton(onClick = onClick, modifier = modifier) { conteudo() }
    }
}

@Composable
private fun SeloOds() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Public, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "ODS 11 · ODS 10 · ODS 1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cidades e comunidades sustentáveis, redução das desigualdades e erradicação da pobreza.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
