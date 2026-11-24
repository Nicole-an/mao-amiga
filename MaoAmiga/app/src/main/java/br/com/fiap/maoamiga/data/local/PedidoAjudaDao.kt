package br.com.fiap.maoamiga.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.fiap.maoamiga.data.model.StatusPedido
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoAjudaDao {
    @Query("SELECT * FROM pedidos_ajuda ORDER BY favorito DESC, criadoEm DESC")
    fun observarTodos(): Flow<List<PedidoAjudaEntity>>

    @Query("SELECT * FROM pedidos_ajuda WHERE id = :id")
    fun observarPorId(id: Long): Flow<PedidoAjudaEntity?>

    @Query("SELECT * FROM pedidos_ajuda WHERE id = :id")
    suspend fun buscarPorId(id: Long): PedidoAjudaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(pedido: PedidoAjudaEntity): Long

    @Update
    suspend fun atualizar(pedido: PedidoAjudaEntity)

    @Delete
    suspend fun excluir(pedido: PedidoAjudaEntity)

    @Query("UPDATE pedidos_ajuda SET status = :status WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: StatusPedido)

    @Query("UPDATE pedidos_ajuda SET favorito = NOT favorito WHERE id = :id")
    suspend fun alternarFavorito(id: Long)
}
