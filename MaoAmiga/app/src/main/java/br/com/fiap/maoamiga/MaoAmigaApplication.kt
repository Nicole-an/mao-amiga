package br.com.fiap.maoamiga

import android.app.Application
import br.com.fiap.maoamiga.di.AppContainer

class MaoAmigaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
