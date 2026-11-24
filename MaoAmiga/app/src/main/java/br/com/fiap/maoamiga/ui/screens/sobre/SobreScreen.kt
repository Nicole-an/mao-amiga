package br.com.fiap.maoamiga.ui.screens.sobre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.maoamiga.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SobreScreen(onVoltar: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_sobre)) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.voltar))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Secao(
                Icons.Filled.Warning, "Problema",
                "Em bairros e comunidades, pedidos de ajuda (alimentos, roupas, remédios, transporte, apoio a idosos, " +
                    "pequenos reparos) circulam de forma dispersa em grupos de mensagem, se perdem e não têm status. " +
                    "Ninguém sabe o que já foi atendido, o que gera duplicidade de esforços e pessoas sem atendimento."
            )
            Secao(
                Icons.Filled.People, "Público beneficiado",
                "Moradores em situação de vulnerabilidade (idosos, famílias de baixa renda, pessoas com deficiência), " +
                    "voluntários e líderes/associações de bairro."
            )
            Secao(
                Icons.Filled.Lightbulb, "Proposta",
                "Aplicativo simples e offline para registrar, acompanhar e organizar pedidos de ajuda da comunidade, " +
                    "com status (Aberto → Em andamento → Atendido), favoritos para priorização, edição, exclusão e " +
                    "localização opcional do pedido."
            )
            Secao(
                Icons.Filled.Public, "Objetivos de Desenvolvimento Sustentável",
                "ODS 11 – Cidades e Comunidades Sustentáveis: fortalece a participação e a gestão comunitária " +
                    "inclusiva (meta 11.3), organizando a ajuda mútua no bairro.\n\n" +
                    "ODS 10 – Redução das Desigualdades: dá visibilidade e prioridade a pessoas vulneráveis, " +
                    "promovendo sua inclusão na rede de apoio.\n\n" +
                    "ODS 1 – Erradicação da Pobreza: facilita o acesso a recursos básicos (alimentos, roupas, remédios) " +
                    "por meio da rede solidária local."
            )
            Secao(
                Icons.Filled.Code, "Tecnologias",
                "Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · " +
                    "Room (KSP) · Coroutines/Flow · Google Play Services Location · Arquitetura MVVM"
            )
            Secao(
                Icons.Filled.School, "Aviso",
                "Protótipo acadêmico desenvolvido para a Global Solution FIAP 2026 (CitizenTech). " +
                    "Os dados ficam armazenados apenas no dispositivo; não há integração com serviços públicos nem servidores externos."
            )
            Secao(Icons.Filled.Groups, "Equipe", "Nicole Alves Nogueira — RM 555182")
        }
    }
}

@Composable
private fun Secao(icone: ImageVector, titulo: String, texto: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
