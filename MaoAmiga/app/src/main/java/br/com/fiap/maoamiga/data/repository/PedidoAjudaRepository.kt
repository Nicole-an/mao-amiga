package br.com.fiap.maoamiga.data.repository

import br.com.fiap.maoamiga.data.local.PedidoAjudaDao
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.data.model.StatusPedido
import kotlinx.coroutines.flow.Flow

class PedidoAjudaRepository(private val dao: PedidoAjudaDao) {

    fun observarTodos(): Flow<List<PedidoAjudaEntity>> = dao.observarTodos()

    fun observarPorId(id: Long): Flow<PedidoAjudaEntity?> = dao.observarPorId(id)

    suspend fun buscarPorId(id: Long): PedidoAjudaEntity? = dao.buscarPorId(id)

    suspend fun inserir(pedido: PedidoAjudaEntity): Long = dao.inserir(pedido)

    suspend fun atualizar(pedido: PedidoAjudaEntity) = dao.atualizar(pedido)

    suspend fun excluir(pedido: PedidoAjudaEntity) = dao.excluir(pedido)

    suspend fun atualizarStatus(id: Long, status: StatusPedido) = dao.atualizarStatus(id, status)

    suspend fun alternarFavorito(id: Long) = dao.alternarFavorito(id)
}
