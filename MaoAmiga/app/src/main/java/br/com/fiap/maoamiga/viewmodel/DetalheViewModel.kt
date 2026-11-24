package br.com.fiap.maoamiga.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.data.repository.PedidoAjudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalheUiState(
    val pedido: PedidoAjudaEntity? = null,
    val carregando: Boolean = true
)

class DetalheViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PedidoAjudaRepository
) : ViewModel() {

    private val pedidoId: Long = savedStateHandle.get<Long>("id") ?: -1L

    val uiState: StateFlow<DetalheUiState> = repository.observarPorId(pedidoId)
        .map { DetalheUiState(pedido = it, carregando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalheUiState())

    private val _excluido = MutableStateFlow(false)
    val excluido: StateFlow<Boolean> = _excluido.asStateFlow()

    fun alternarFavorito() {
        viewModelScope.launch { repository.alternarFavorito(pedidoId) }
    }

    fun atualizarStatus(status: StatusPedido) {
        viewModelScope.launch { repository.atualizarStatus(pedidoId, status) }
    }

    fun excluir() {
        val pedido = uiState.value.pedido ?: return
        viewModelScope.launch {
            repository.excluir(pedido)
            _excluido.value = true
        }
    }
}
