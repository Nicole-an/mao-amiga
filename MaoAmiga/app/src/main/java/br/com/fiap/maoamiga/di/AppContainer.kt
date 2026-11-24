package br.com.fiap.maoamiga.di

import android.content.Context
import br.com.fiap.maoamiga.data.local.MaoAmigaDatabase
import br.com.fiap.maoamiga.data.repository.PedidoAjudaRepository
import br.com.fiap.maoamiga.location.LocationHelper

class AppContainer(private val context: Context) {
    val repository by lazy {
        PedidoAjudaRepository(MaoAmigaDatabase.getInstance(context).pedidoAjudaDao())
    }
    val locationHelper by lazy { LocationHelper(context) }
}
