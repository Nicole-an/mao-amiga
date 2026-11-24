package br.com.fiap.maoamiga.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fiap.maoamiga.data.model.Categoria
import br.com.fiap.maoamiga.data.model.StatusPedido

@Entity(tableName = "pedidos_ajuda")
data class PedidoAjudaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String,
    val categoria: Categoria = Categoria.OUTROS,
    val solicitante: String,
    val contato: String,
    val bairro: String,
    val status: StatusPedido = StatusPedido.ABERTO,
    val favorito: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
