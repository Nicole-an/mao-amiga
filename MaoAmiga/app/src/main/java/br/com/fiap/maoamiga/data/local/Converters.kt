package br.com.fiap.maoamiga.data.local

import androidx.room.TypeConverter
import br.com.fiap.maoamiga.data.model.Categoria
import br.com.fiap.maoamiga.data.model.StatusPedido

class Converters {
    @TypeConverter
    fun categoriaParaString(categoria: Categoria): String = categoria.name

    @TypeConverter
    fun stringParaCategoria(valor: String): Categoria = Categoria.valueOf(valor)

    @TypeConverter
    fun statusParaString(status: StatusPedido): String = status.name

    @TypeConverter
    fun stringParaStatus(valor: String): StatusPedido = StatusPedido.valueOf(valor)
}
