package br.com.fiap.maoamiga.ui.screens.formulario

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.maoamiga.R
import br.com.fiap.maoamiga.data.model.Categoria
import br.com.fiap.maoamiga.ui.components.EstadoVazio
import br.com.fiap.maoamiga.ui.components.SeletorStatus
import br.com.fiap.maoamiga.ui.components.formatarCoordenadas
import br.com.fiap.maoamiga.viewmodel.AppViewModelProvider
import br.com.fiap.maoamiga.viewmodel.FormularioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormularioPedidoScreen(
    onVoltar: () -> Unit,
    onSalvo: () -> Unit,
    viewModel: FormularioViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val permissaoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) viewModel.capturarLocalizacao() else viewModel.permissaoNegada()
    }

    LaunchedEffect(ui.mensagem) {
        ui.mensagem?.let { texto ->
            viewModel.mensagemExibida()
            scope.launch { snackbarHostState.showSnackbar(texto) }
        }
    }
    LaunchedEffect(ui.salvo) {
        if (ui.salvo) onSalvo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (ui.edicao) R.string.titulo_editar_pedido else R.string.titulo_novo_pedido))
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar))
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

            ui.naoEncontrado -> EstadoVazio(
                icone = Icons.Filled.SearchOff,
                mensagem = stringResource(R.string.pedido_nao_encontrado),
                modifier = Modifier.padding(innerPadding),
                textoAcao = stringResource(R.string.voltar),
                onAcao = onVoltar
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Campos com * são obrigatórios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = ui.titulo,
                    onValueChange = viewModel::onTituloChange,
                    label = { Text("Título *") },
                    isError = ui.erroTitulo != null,
                    supportingText = {
                        Row(Modifier.fillMaxWidth()) {
                            Text(ui.erroTitulo.orEmpty(), modifier = Modifier.weight(1f))
                            Text("${ui.titulo.length}/${FormularioViewModel.TITULO_MAX}")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ui.descricao,
                    onValueChange = viewModel::onDescricaoChange,
                    label = { Text("Descrição *") },
                    isError = ui.erroDescricao != null,
                    supportingText = {
                        Row(Modifier.fillMaxWidth()) {
                            Text(ui.erroDescricao.orEmpty(), modifier = Modifier.weight(1f))
                            Text("${ui.descricao.length}/${FormularioViewModel.DESCRICAO_MAX}")
                        }
                    },
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )

                SeletorCategoria(selecionada = ui.categoria, onSelecionar = viewModel::onCategoriaChange)

                OutlinedTextField(
                    value = ui.solicitante,
                    onValueChange = viewModel::onSolicitanteChange,
                    label = { Text("Solicitante (quem precisa) *") },
                    isError = ui.erroSolicitante != null,
                    supportingText = { ui.erroSolicitante?.let { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ui.contato,
                    onValueChange = viewModel::onContatoChange,
                    label = { Text("Telefone de contato (com DDD) *") },
                    placeholder = { Text("11987654321") },
                    isError = ui.erroContato != null,
                    supportingText = { ui.erroContato?.let { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ui.bairro,
                    onValueChange = viewModel::onBairroChange,
                    label = { Text("Bairro *") },
                    isError = ui.erroBairro != null,
                    supportingText = { ui.erroBairro?.let { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (ui.edicao) {
                    Text("Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    SeletorStatus(selecionado = ui.status, onSelecionar = viewModel::onStatusChange)
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Localização (opcional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        val lat = ui.latitude
                        val lng = ui.longitude
                        if (lat != null && lng != null) {
                            Text(
                                "Localização capturada: ${formatarCoordenadas(lat, lng)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = viewModel::removerLocalizacao) {
                                Icon(Icons.Filled.LocationOff, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.acao_remover_localizacao))
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                permissaoLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                )
                            },
                            enabled = !ui.buscandoLocalizacao,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            if (ui.buscandoLocalizacao) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Obtendo localização...")
                            } else {
                                Icon(Icons.Filled.MyLocation, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.acao_usar_localizacao))
                            }
                        }
                    }
                }

                Button(
                    onClick = viewModel::salvar,
                    enabled = !ui.salvando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    if (ui.salvando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.acao_salvar), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorCategoria(selecionada: Categoria, onSelecionar: (Categoria) -> Unit) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = selecionada.rotulo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            Categoria.entries.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.rotulo) },
                    onClick = {
                        onSelecionar(categoria)
                        expandido = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
