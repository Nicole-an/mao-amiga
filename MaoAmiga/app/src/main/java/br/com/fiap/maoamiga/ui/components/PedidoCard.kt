package br.com.fiap.maoamiga.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.maoamiga.data.local.PedidoAjudaEntity
import br.com.fiap.maoamiga.ui.theme.CorFavorito

@Composable
fun PedidoCard(
    pedido: PedidoAjudaEntity,
    onClick: () -> Unit,
    onFavoritar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pedido.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFavoritar) {
                    Icon(
                        imageVector = if (pedido.favorito) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = if (pedido.favorito) "Remover dos favoritos" else "Marcar como favorito",
                        tint = if (pedido.favorito) CorFavorito else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "${pedido.categoria.rotulo} · ${pedido.bairro}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(pedido.status)
                Text(text = formatarData(pedido.criadoEm), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
