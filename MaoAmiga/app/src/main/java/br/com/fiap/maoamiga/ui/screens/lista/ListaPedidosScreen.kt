package br.com.fiap.maoamiga.ui.screens.lista

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.maoamiga.R
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.ui.components.EstadoVazio
import br.com.fiap.maoamiga.ui.components.MensagemResultadoEffect
import br.com.fiap.maoamiga.ui.components.PedidoCard
import br.com.fiap.maoamiga.viewmodel.AppViewModelProvider
import br.com.fiap.maoamiga.viewmodel.PedidosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPedidosScreen(
    mensagem: String?,
    onMensagemConsumida: () -> Unit,
    onVoltar: () -> Unit,
    onNovoPedido: () -> Unit,
    onAbrirPedido: (Long) -> Unit,
    viewModel: PedidosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    MensagemResultadoEffect(mensagem, snackbarHostState, onMensagemConsumida)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_lista)) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNovoPedido) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.acao_novo_pedido))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.totalGeral > 0) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.filtro == null,
                        onClick = { viewModel.selecionarFiltro(null) },
                        label = { Text("Todos") }
                    )
                    StatusPedido.entries.forEach { status ->
                        FilterChip(
                            selected = uiState.filtro == status,
                            onClick = { viewModel.selecionarFiltro(status) },
                            label = { Text(status.rotulo) }
                        )
                    }
                }
            }

            when {
                uiState.carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                uiState.totalGeral == 0 -> EstadoVazio(
                    icone = Icons.Filled.Inbox,
                    mensagem = stringResource(R.string.lista_vazia),
                    textoAcao = stringResource(R.string.acao_cadastrar_primeiro),
                    onAcao = onNovoPedido,
                    textoAcaoSecundaria = stringResource(R.string.acao_carregar_exemplos),
                    onAcaoSecundaria = {
                        viewModel.carregarExemplos()
                        scope.launch { snackbarHostState.showSnackbar("Pedidos de exemplo carregados.") }
                    }
                )

                uiState.pedidos.isEmpty() -> EstadoVazio(
                    icone = Icons.Filled.FilterAlt,
                    mensagem = stringResource(R.string.lista_filtro_vazio)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.pedidos, key = { it.id }) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            onClick = { onAbrirPedido(pedido.id) },
                            onFavoritar = {
                                viewModel.alternarFavorito(pedido.id)
                                val texto = if (pedido.favorito) "Removido dos favoritos." else "Marcado como favorito."
                                scope.launch { snackbarHostState.showSnackbar(texto) }
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
