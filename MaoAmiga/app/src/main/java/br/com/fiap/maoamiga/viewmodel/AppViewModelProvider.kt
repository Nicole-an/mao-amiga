package br.com.fiap.maoamiga.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.maoamiga.MaoAmigaApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PedidosViewModel(app().container.repository)
        }
        initializer {
            FormularioViewModel(
                createSavedStateHandle(),
                app().container.repository,
                app().container.locationHelper
            )
        }
        initializer {
            DetalheViewModel(createSavedStateHandle(), app().container.repository)
        }
    }
}

private fun CreationExtras.app(): MaoAmigaApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MaoAmigaApplication
