package br.com.fiap.maoamiga.ui.screens.detalhe

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.maoamiga.R
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.ui.components.EstadoVazio
import br.com.fiap.maoamiga.ui.components.MensagemResultadoEffect
import br.com.fiap.maoamiga.ui.components.SeletorStatus
import br.com.fiap.maoamiga.ui.components.StatusChip
import br.com.fiap.maoamiga.ui.components.formatarCoordenadas
import br.com.fiap.maoamiga.ui.components.formatarData
import br.com.fiap.maoamiga.ui.components.formatarTelefone
import br.com.fiap.maoamiga.ui.theme.CorFavorito
import br.com.fiap.maoamiga.viewmodel.AppViewModelProvider
import br.com.fiap.maoamiga.viewmodel.DetalheViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhePedidoScreen(
    mensagem: String?,
    onMensagemConsumida: () -> Unit,
    onVoltar: () -> Unit,
    onEditar: (Long) -> Unit,
    onExcluido: () -> Unit,
    viewModel: DetalheViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val excluido by viewModel.excluido.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var confirmarExclusao by rememberSaveable { mutableStateOf(false) }
    val pedido = ui.pedido

    MensagemResultadoEffect(mensagem, snackbarHostState, onMensagemConsumida)
    LaunchedEffect(excluido) {
        if (excluido) onExcluido()
    }

    fun avisar(texto: String) {
        scope.launch { snackbarHostState.showSnackbar(texto) }
    }

    fun alterarStatus(atual: PedidoAjudaEntity, novo: StatusPedido) {
        if (atual.status == novo) return
        viewModel.atualizarStatus(novo)
        avisar("Status alterado para ${novo.rotulo}")
    }

    fun abrirMapa(lat: Double, lng: Double) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, "geo:$lat,$lng?q=$lat,$lng".toUri()))
        } catch (e: ActivityNotFoundException) {
            avisar("Nenhum aplicativo de mapas encontrado no dispositivo.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pedido?.titulo ?: stringResource(R.string.titulo_detalhe),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar))
                    }
                },
                actions = {
                    if (pedido != null) {
                        IconButton(onClick = { onEditar(pedido.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar pedido")
                        }
                        IconButton(onClick = {
                            viewModel.alternarFavorito()
                            avisar(if (pedido.favorito) "Removido dos favoritos." else "Marcado como favorito.")
                        }) {
                            Icon(
                                imageVector = if (pedido.favorito) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = if (pedido.favorito) "Remover dos favoritos" else "Marcar como favorito",
                                tint = if (pedido.favorito) CorFavorito else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { confirmarExclusao = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir pedido")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when {
            ui.carregando -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            pedido == null -> if (!excluido) {
                EstadoVazio(
                    icone = Icons.Filled.SearchOff,
                    mensagem = stringResource(R.string.pedido_nao_encontrado),
                    modifier = Modifier.padding(innerPadding),
                    textoAcao = stringResource(R.string.voltar),
                    onAcao = onVoltar
                )
            } else {
                Box(Modifier.fillMaxSize())
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusChip(pedido.status)
                            if (pedido.favorito) {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Filled.Star, contentDescription = null, tint = CorFavorito)
                                Text("Favorito", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Text(pedido.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(pedido.descricao, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        LinhaInfo(Icons.Filled.Category, "Categoria", pedido.categoria.rotulo)
                        HorizontalDivider()
                        LinhaInfo(Icons.Filled.Person, "Solicitante", pedido.solicitante)
                        HorizontalDivider()
                        LinhaInfo(Icons.Filled.Phone, "Contato", formatarTelefone(pedido.contato))
                        HorizontalDivider()
                        LinhaInfo(Icons.Filled.Place, "Bairro", pedido.bairro)
                        HorizontalDivider()
                        LinhaInfo(Icons.Filled.Event, "Criado em", formatarData(pedido.criadoEm))
                    }
                }

                val lat = pedido.latitude
                val lng = pedido.longitude
                if (lat != null && lng != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Localização", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(formatarCoordenadas(lat, lng), style = MaterialTheme.typography.bodyLarge)
                            OutlinedButton(onClick = { abrirMapa(lat, lng) }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Filled.Map, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.acao_abrir_mapa))
                            }
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Alterar status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        SeletorStatus(selecionado = pedido.status, onSelecionar = { alterarStatus(pedido, it) })
                        if (pedido.status != StatusPedido.ATENDIDO) {
                            Button(
                                onClick = { alterarStatus(pedido, StatusPedido.ATENDIDO) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.acao_marcar_atendido), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text(stringResource(R.string.dialogo_excluir_titulo)) },
            text = { Text(stringResource(R.string.dialogo_excluir_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    viewModel.excluir()
                }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun LinhaInfo(icone: ImageVector, rotulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(rotulo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
