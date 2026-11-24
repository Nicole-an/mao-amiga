package br.com.fiap.maoamiga.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.fiap.maoamiga.R
import br.com.fiap.maoamiga.ui.screens.detalhe.DetalhePedidoScreen
import br.com.fiap.maoamiga.ui.screens.formulario.FormularioPedidoScreen
import br.com.fiap.maoamiga.ui.screens.home.HomeScreen
import br.com.fiap.maoamiga.ui.screens.lista.ListaPedidosScreen
import br.com.fiap.maoamiga.ui.screens.sobre.SobreScreen

@Composable
fun MaoAmigaNavHost(navController: NavHostController = rememberNavController()) {
    val msgSalvo = stringResource(R.string.msg_pedido_salvo)
    val msgExcluido = stringResource(R.string.msg_pedido_excluido)

    NavHost(navController = navController, startDestination = Rotas.HOME) {

        composable(Rotas.HOME) { entry ->
            val mensagem by entry.mensagemResultado()
            HomeScreen(
                mensagem = mensagem,
                onMensagemConsumida = { entry.limparMensagem() },
                onNovoPedido = { navController.navigate(Rotas.formulario()) { launchSingleTop = true } },
                onVerPedidos = { navController.navigate(Rotas.LISTA) { launchSingleTop = true } },
                onSobre = { navController.navigate(Rotas.SOBRE) { launchSingleTop = true } }
            )
        }

        composable(Rotas.LISTA) { entry ->
            val mensagem by entry.mensagemResultado()
            ListaPedidosScreen(
                mensagem = mensagem,
                onMensagemConsumida = { entry.limparMensagem() },
                onVoltar = { navController.voltarDe(entry) },
                onNovoPedido = { navController.navigate(Rotas.formulario()) { launchSingleTop = true } },
                onAbrirPedido = { id -> navController.navigate(Rotas.detalhe(id)) { launchSingleTop = true } }
            )
        }

        composable(
            route = Rotas.FORMULARIO,
            arguments = listOf(navArgument(Rotas.ARG_ID) {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { entry ->
            FormularioPedidoScreen(
                onVoltar = { navController.voltarDe(entry) },
                onSalvo = { navController.voltarDe(entry, mensagem = msgSalvo) }
            )
        }

        composable(
            route = Rotas.DETALHE,
            arguments = listOf(navArgument(Rotas.ARG_ID) { type = NavType.LongType })
        ) { entry ->
            val mensagem by entry.mensagemResultado()
            DetalhePedidoScreen(
                mensagem = mensagem,
                onMensagemConsumida = { entry.limparMensagem() },
                onVoltar = { navController.voltarDe(entry) },
                onEditar = { id -> navController.navigate(Rotas.formulario(id)) { launchSingleTop = true } },
                onExcluido = { navController.voltarDe(entry, mensagem = msgExcluido) }
            )
        }

        composable(Rotas.SOBRE) { entry ->
            SobreScreen(onVoltar = { navController.voltarDe(entry) })
        }
    }
}

@Composable
private fun NavBackStackEntry.mensagemResultado() =
    savedStateHandle.getStateFlow<String?>(Rotas.CHAVE_MENSAGEM, null).collectAsStateWithLifecycle()

private fun NavBackStackEntry.limparMensagem() {
    savedStateHandle.set<String?>(Rotas.CHAVE_MENSAGEM, null)
}

/** Volta apenas se a tela ainda está ativa, evitando pops duplos por cliques rápidos. */
private fun NavHostController.voltarDe(entry: NavBackStackEntry, mensagem: String? = null) {
    if (entry.lifecycle.currentState != Lifecycle.State.RESUMED) return
    if (mensagem != null) {
        previousBackStackEntry?.savedStateHandle?.set(Rotas.CHAVE_MENSAGEM, mensagem)
    }
    popBackStack()
}
