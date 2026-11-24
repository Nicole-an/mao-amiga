package br.com.fiap.maoamiga.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.data.model.Categoria
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.data.repository.PedidoAjudaRepository
import br.com.fiap.maoamiga.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val titulo: String = "",
    val descricao: String = "",
    val categoria: Categoria = Categoria.OUTROS,
    val solicitante: String = "",
    val contato: String = "",
    val bairro: String = "",
    val status: StatusPedido = StatusPedido.ABERTO,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val erroTitulo: String? = null,
    val erroDescricao: String? = null,
    val erroSolicitante: String? = null,
    val erroContato: String? = null,
    val erroBairro: String? = null,
    val edicao: Boolean = false,
    val carregando: Boolean = false,
    val naoEncontrado: Boolean = false,
    val salvando: Boolean = false,
    val buscandoLocalizacao: Boolean = false,
    val salvo: Boolean = false,
    val mensagem: String? = null
) {
    val temErros: Boolean
        get() = listOf(erroTitulo, erroDescricao, erroSolicitante, erroContato, erroBairro).any { it != null }
}

class FormularioViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PedidoAjudaRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val pedidoId: Long = savedStateHandle.get<Long>("id") ?: -1L

    private val _uiState = MutableStateFlow(
        FormularioUiState(edicao = pedidoId > 0, carregando = pedidoId > 0)
    )
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    init {
        if (pedidoId > 0) carregarPedido()
    }

    private fun carregarPedido() {
        viewModelScope.launch {
            val pedido = repository.buscarPorId(pedidoId)
            _uiState.update { atual ->
                if (pedido == null) {
                    atual.copy(carregando = false, naoEncontrado = true)
                } else {
                    atual.copy(
                        titulo = pedido.titulo,
                        descricao = pedido.descricao,
                        categoria = pedido.categoria,
                        solicitante = pedido.solicitante,
                        contato = pedido.contato,
                        bairro = pedido.bairro,
                        status = pedido.status,
                        latitude = pedido.latitude,
                        longitude = pedido.longitude,
                        carregando = false
                    )
                }
            }
        }
    }

    fun onTituloChange(valor: String) {
        val texto = valor.take(TITULO_MAX)
        _uiState.update { it.copy(titulo = texto, erroTitulo = it.erroTitulo?.let { validarTitulo(texto) }) }
    }

    fun onDescricaoChange(valor: String) {
        val texto = valor.take(DESCRICAO_MAX)
        _uiState.update { it.copy(descricao = texto, erroDescricao = it.erroDescricao?.let { validarDescricao(texto) }) }
    }

    fun onSolicitanteChange(valor: String) {
        val texto = valor.take(NOME_MAX)
        _uiState.update { it.copy(solicitante = texto, erroSolicitante = it.erroSolicitante?.let { validarSolicitante(texto) }) }
    }

    fun onContatoChange(valor: String) {
        val texto = valor.take(CONTATO_MAX)
        _uiState.update { it.copy(contato = texto, erroContato = it.erroContato?.let { validarContato(texto) }) }
    }

    fun onBairroChange(valor: String) {
        val texto = valor.take(NOME_MAX)
        _uiState.update { it.copy(bairro = texto, erroBairro = it.erroBairro?.let { validarBairro(texto) }) }
    }

    fun onCategoriaChange(categoria: Categoria) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onStatusChange(status: StatusPedido) {
        _uiState.update { it.copy(status = status) }
    }

    fun capturarLocalizacao() {
        _uiState.update { it.copy(buscandoLocalizacao = true) }
        locationHelper.obterLocalizacaoAtual(
            onSucesso = { lat, lng ->
                _uiState.update {
                    it.copy(latitude = lat, longitude = lng, buscandoLocalizacao = false, mensagem = "Localização capturada.")
                }
            },
            onErro = {
                _uiState.update { it.copy(buscandoLocalizacao = false, mensagem = MSG_SEM_LOCALIZACAO) }
            }
        )
    }

    fun permissaoNegada() {
        _uiState.update { it.copy(buscandoLocalizacao = false, mensagem = MSG_SEM_LOCALIZACAO) }
    }

    fun removerLocalizacao() {
        _uiState.update { it.copy(latitude = null, longitude = null) }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }

    fun salvar() {
        val atual = _uiState.value
        if (atual.salvando || atual.carregando) return

        val validado = atual.copy(
            erroTitulo = validarTitulo(atual.titulo),
            erroDescricao = validarDescricao(atual.descricao),
            erroSolicitante = validarSolicitante(atual.solicitante),
            erroContato = validarContato(atual.contato),
            erroBairro = validarBairro(atual.bairro)
        )
        if (validado.temErros) {
            _uiState.value = validado.copy(mensagem = "Corrija os campos destacados.")
            return
        }

        _uiState.value = validado.copy(salvando = true)
        viewModelScope.launch {
            val titulo = validado.titulo.trim()
            val descricao = validado.descricao.trim()
            val solicitante = validado.solicitante.trim()
            val contato = validado.contato.filter(Char::isDigit)
            val bairro = validado.bairro.trim()

            if (pedidoId > 0) {
                val existente = repository.buscarPorId(pedidoId)
                if (existente == null) {
                    _uiState.update { it.copy(salvando = false, naoEncontrado = true) }
                    return@launch
                }
                repository.atualizar(
                    existente.copy(
                        titulo = titulo,
                        descricao = descricao,
                        categoria = validado.categoria,
                        solicitante = solicitante,
                        contato = contato,
                        bairro = bairro,
                        status = validado.status,
                        latitude = validado.latitude,
                        longitude = validado.longitude
                    )
                )
            } else {
                repository.inserir(
                    PedidoAjudaEntity(
                        titulo = titulo,
                        descricao = descricao,
                        categoria = validado.categoria,
                        solicitante = solicitante,
                        contato = contato,
                        bairro = bairro,
                        latitude = validado.latitude,
                        longitude = validado.longitude
                    )
                )
            }
            _uiState.update { it.copy(salvando = false, salvo = true) }
        }
    }

    companion object {
        const val TITULO_MAX = 60
        const val DESCRICAO_MAX = 500
        private const val NOME_MAX = 80
        private const val CONTATO_MAX = 20
        private const val MSG_SEM_LOCALIZACAO =
            "Não foi possível obter a localização. O pedido pode ser salvo sem ela."

        fun validarTitulo(valor: String): String? = when {
            valor.trim().length < 3 -> "Informe um título com ao menos 3 caracteres."
            valor.trim().length > TITULO_MAX -> "O título deve ter no máximo $TITULO_MAX caracteres."
            else -> null
        }

        fun validarDescricao(valor: String): String? = when {
            valor.trim().length < 10 -> "Descreva o pedido com ao menos 10 caracteres."
            valor.trim().length > DESCRICAO_MAX -> "A descrição deve ter no máximo $DESCRICAO_MAX caracteres."
            else -> null
        }

        fun validarSolicitante(valor: String): String? =
            if (valor.trim().length < 2) "Informe o nome de quem precisa de ajuda." else null

        fun validarContato(valor: String): String? =
            if (valor.count(Char::isDigit) !in 10..11) "Telefone deve ter 10 ou 11 dígitos." else null

        fun validarBairro(valor: String): String? =
            if (valor.trim().length < 2) "Informe o bairro com ao menos 2 caracteres." else null
    }
}
