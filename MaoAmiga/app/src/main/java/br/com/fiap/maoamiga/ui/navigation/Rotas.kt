package br.com.fiap.maoamiga.ui.navigation

object Rotas {
    const val HOME = "home"
    const val LISTA = "lista"
    const val FORMULARIO = "formulario?id={id}"
    const val DETALHE = "detalhe/{id}"
    const val SOBRE = "sobre"

    const val ARG_ID = "id"
    const val CHAVE_MENSAGEM = "mensagem"

    fun formulario(id: Long = -1L) = "formulario?id=$id"
    fun detalhe(id: Long) = "detalhe/$id"
}
