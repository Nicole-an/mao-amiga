package br.com.fiap.maoamiga.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(private val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    private fun temPermissao(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun obterLocalizacaoAtual(onSucesso: (Double, Double) -> Unit, onErro: () -> Unit) {
        if (!temPermissao()) {
            onErro()
            return
        }
        try {
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { local ->
                    if (local != null) onSucesso(local.latitude, local.longitude) else onErro()
                }
                .addOnFailureListener { onErro() }
        } catch (e: SecurityException) {
            onErro()
        }
    }
}
