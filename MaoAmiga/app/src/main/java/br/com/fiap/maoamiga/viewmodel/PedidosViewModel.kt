package br.com.fiap.maoamiga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.data.model.Categoria
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.data.repository.PedidoAjudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ContadoresStatus(
    val abertos: Int = 0,
    val emAndamento: Int = 0,
    val atendidos: Int = 0
)

data class ListaUiState(
    val pedidos: List<PedidoAjudaEntity> = emptyList(),
    val filtro: StatusPedido? = null,
    val totalGeral: Int = 0,
    val carregando: Boolean = true
)

class PedidosViewModel(private val repository: PedidoAjudaRepository) : ViewModel() {

    private val filtro = MutableStateFlow<StatusPedido?>(null)
    private val todos = repository.observarTodos()
    private var carregandoExemplos = false

    val uiState: StateFlow<ListaUiState> = combine(todos, filtro) { lista, statusFiltro ->
        ListaUiState(
            pedidos = if (statusFiltro == null) lista else lista.filter { it.status == statusFiltro },
            filtro = statusFiltro,
            totalGeral = lista.size,
            carregando = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListaUiState())

    val contadores: StateFlow<ContadoresStatus> = todos.map { lista ->
        ContadoresStatus(
            abertos = lista.count { it.status == StatusPedido.ABERTO },
            emAndamento = lista.count { it.status == StatusPedido.EM_ANDAMENTO },
            atendidos = lista.count { it.status == StatusPedido.ATENDIDO }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContadoresStatus())

    fun selecionarFiltro(status: StatusPedido?) {
        filtro.value = status
    }

    fun alternarFavorito(id: Long) {
        viewModelScope.launch { repository.alternarFavorito(id) }
    }

    fun carregarExemplos() {
        if (carregandoExemplos || uiState.value.totalGeral > 0) return
        carregandoExemplos = true
        viewModelScope.launch {
            exemplos().forEach { repository.inserir(it) }
            carregandoExemplos = false
        }
    }

    private fun exemplos(): List<PedidoAjudaEntity> {
        val agora = System.currentTimeMillis()
        val hora = 60 * 60 * 1000L
        return listOf(
            PedidoAjudaEntity(
                titulo = "Cesta básica para família",
                descricao = "Família com três crianças precisa de alimentos não perecíveis para o mês.",
                categoria = Categoria.ALIMENTOS,
                solicitante = "Maria da Silva",
                contato = "11987654321",
                bairro = "Jardim Esperança",
                criadoEm = agora - 3 * hora
            ),
            PedidoAjudaEntity(
                titulo = "Carona para consulta médica",
                descricao = "Idoso precisa de transporte até o posto de saúde na quinta-feira às 9h.",
                categoria = Categoria.TRANSPORTE,
                solicitante = "José Pereira",
                contato = "1132654789",
                bairro = "Vila Nova",
                status = StatusPedido.EM_ANDAMENTO,
                criadoEm = agora - 2 * hora
            ),
            PedidoAjudaEntity(
                titulo = "Conserto de torneira",
                descricao = "Torneira da cozinha vazando há dias; moradora com mobilidade reduzida.",
                categoria = Categoria.REPAROS,
                solicitante = "Ana Souza",
                contato = "11912345678",
                bairro = "Centro",
                criadoEm = agora - hora
            )
        )
    }
}
