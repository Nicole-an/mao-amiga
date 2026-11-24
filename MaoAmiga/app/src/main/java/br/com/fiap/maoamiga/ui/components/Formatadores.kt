package br.com.fiap.maoamiga.ui.components

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoData: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

fun formatarData(millis: Long): String =
    formatoData.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

fun formatarTelefone(digitos: String): String = when (digitos.length) {
    11 -> "(${digitos.substring(0, 2)}) ${digitos.substring(2, 7)}-${digitos.substring(7)}"
    10 -> "(${digitos.substring(0, 2)}) ${digitos.substring(2, 6)}-${digitos.substring(6)}"
    else -> digitos
}

fun formatarCoordenadas(latitude: Double, longitude: Double): String =
    String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
