package com.weatherapp.update

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed

class VVAppUpdateManager(
    private val activity: Activity,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>
) {
    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    private var isUpdateFlowStarted = false

    fun checkForUpdates() {
        isUpdateFlowStarted = false
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

            if (isUpdateAvailable && appUpdateInfo.isImmediateUpdateAllowed) {
                startImmediateUpdate(appUpdateInfo)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Log.e(
                "update/VVAppUpdateManager",
                "App Update Error, entry-point VVAppUpdateManager class\n" + e.message
            )
        }
    }

    fun resumeUpdateIfNeeded() {
        isUpdateFlowStarted = false
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdate(appUpdateInfo)
            }

        }.addOnFailureListener {
            it.printStackTrace()
            Log.e(
                "update/VVAppUpdateManager",
                "App Update Error, entry-point VVAppUpdateManager class\n" + it.message
            )
        }
    }

    private fun startImmediateUpdate(appUpdateInfo: AppUpdateInfo) {
        if (!isUpdateFlowStarted) return
        isUpdateFlowStarted = true
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            Log.e(
                "update/VVAppUpdateManager",
                "App Update Error, entry-point VVAppUpdateManager class\n" + e.message
            )
        }
    }
}