package com.weatherapp.utils

/*
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*

@Composable
fun rememberLocationPermissionState(): LocationPermissionState {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    return remember {
        LocationPermissionState(
            permissionState = permissionState,
            context = context
        )
    }
}

class LocationPermissionState(
    private val permissionState: PermissionState,
    private val context: Context
) {
    val hasPermission: Boolean
        @Composable get() = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val shouldShowRationale: Boolean
        @Composable get() = permissionState.status.shouldShowRationale

    fun launchPermissionRequest() {
        permissionState.launchPermissionRequest()
    }
}

@Composable
fun PermissionAwareContent(
    permissionState: LocationPermissionState,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit,
    onPermissionRationale: @Composable () -> Unit
) {
    if (permissionState.hasPermission) {
        onPermissionGranted()
    } else {
        if (permissionState.shouldShowRationale) {
            onPermissionRationale()
        } else {
            onPermissionDenied()
        }
    }
}

 */