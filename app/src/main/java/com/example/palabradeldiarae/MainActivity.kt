package com.example.palabradeldiarae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.palabradeldiarae.ui.theme.PalabraDelDiaRAETheme
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit


private val TAG: String = MainActivity::class.java.getName()
private const val WORK_ID = "11bc9742-6835-440e-bdd6-552c0d2f1df4"

class MainActivity : ComponentActivity() {

    private val activityResultLauncher =
        registerForActivityResult(RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted")
                enqueuePeriodicWork()
            } else {
                Log.d(TAG, "Notification permission not granted")
                showToast("La aplicación necesita permisos para funcionar correctamente")
            }
            finish()
        }

    private fun showToast(str: String) {
        val toast = Toast.makeText(this,
            str,
            LENGTH_SHORT
        )
        toast.show()
    }

    private fun enqueuePeriodicWork() {
        Log.d(TAG, "Enqueuing periodic work request")
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            Notification::class.java,
            24, TimeUnit.HOURS
        ).build()
        Log.d(TAG, "PeriodicWorkRequest created")

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            WORK_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        Log.d(TAG, "PeriodicWorkRequest enqueued")
        showToast(
            "${getString(R.string.app_name)} configurado para notificar a las " +
                    "${DateFormat.getTimeInstance().format(Date())}"
        )
        showToast("Abre la aplicación de nuevo para eliminar la notificación")
        finish()
    }

    private fun requestPermission() {
        Log.d(TAG, "Requesting notification permission")
        activityResultLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun installOrUninstall() {
        val workManager = WorkManager.getInstance(this)
        val workInfoList = workManager.getWorkInfosForUniqueWork(WORK_ID).get()

        Log.d(TAG, "Checking if work request exists")
        if (workInfoList.isEmpty() || workInfoList[0].state == androidx.work.WorkInfo.State.CANCELLED)
            enqueuePeriodicWork()
        else {
            workManager.cancelUniqueWork(WORK_ID)
            Log.d(TAG,
                "PeriodicWorkRequest cancelled"
            )
            showToast(
                "${getString(R.string.app_name)} desinstalado correctamente"
            )
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Checking for permissions")
        when {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Notification permission was previously granted")
                installOrUninstall()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                Log.d(TAG, "Notification permission was previously denied")
                Log.d(TAG, "Showing permission rationale")
                enableEdgeToEdge()
                setContent {
                    PalabraDelDiaRAETheme {
                        PermissionRationale(
                            onDismissRequest = { finish() },
                            onConfirmation = {
                                requestPermission()
                            },
                            dialogTitle =
                                "Permiso de notificación",
                            dialogText =
                                "${getString(R.string.app_name)} " +
                                "necesita el permiso de notificación para funcionar correctamente.",
                            icon = Icons.Default.Info
                        )
                    }
                }
            }

            else -> requestPermission()
        }
    }
}

@Composable
fun PermissionRationale(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
