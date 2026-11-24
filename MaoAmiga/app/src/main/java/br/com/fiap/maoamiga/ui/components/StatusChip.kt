package br.com.fiap.maoamiga.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.fiap.maoamiga.data.model.StatusPedido
import br.com.fiap.maoamiga.ui.theme.CorStatusAberto
import br.com.fiap.maoamiga.ui.theme.CorStatusAtendido
import br.com.fiap.maoamiga.ui.theme.CorStatusEmAndamento

fun StatusPedido.cor(): Color = when (this) {
    StatusPedido.ABERTO -> CorStatusAberto
    StatusPedido.EM_ANDAMENTO -> CorStatusEmAndamento
    StatusPedido.ATENDIDO -> CorStatusAtendido
}

fun StatusPedido.icone(): ImageVector = when (this) {
    StatusPedido.ABERTO -> Icons.Filled.PanTool
    StatusPedido.EM_ANDAMENTO -> Icons.Filled.HourglassTop
    StatusPedido.ATENDIDO -> Icons.Filled.CheckCircle
}

@Composable
fun StatusChip(status: StatusPedido, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = status.cor(),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(PaddingValues(horizontal = 12.dp, vertical = 6.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(status.icone(), contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text = status.rotulo, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorStatus(
    selecionado: StatusPedido,
    onSelecionar: (StatusPedido) -> Unit,
    modifier: Modifier = Modifier
) {
    val opcoes = StatusPedido.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        opcoes.forEachIndexed { indice, status ->
            SegmentedButton(
                selected = status == selecionado,
                onClick = { onSelecionar(status) },
                shape = SegmentedButtonDefaults.itemShape(index = indice, count = opcoes.size),
                icon = {}
            ) {
                Text(
                    text = status.rotulo,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
