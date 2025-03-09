package com.example.palabradeldiarae

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


private val TAG: String = MainActivity::class.java.getName()
private const val QUEUE_REQUEST_ID = "11bc9742-6835-440e-bdd6-552c0d2f1df4"

class MainActivity: AppCompatActivity() {

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
            WofdRae::class.java,
            24, TimeUnit.HOURS
        ).build()
        Log.d(TAG, "PeriodicWorkRequest created")

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(QUEUE_REQUEST_ID,
            androidx.work.ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicWorkRequest
        )
        Log.d(TAG, "PeriodicWorkRequest enqueued")
        showToast("${getString(R.string.app_name)} configurado para notificar a las" +
                "${DateFormat.getTimeInstance().format(Date())}")
    }

    val activityResultLauncher =
        registerForActivityResult(RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted")
                enqueuePeriodicWork()
            } else {
                Log.d(TAG, "Notification permission not granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity created")

        Log.d(TAG, "Checking for permissions")
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                enqueuePeriodicWork()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                permissionRationale(
                    onDismissRequest = { finish() },
                    onConfirmation = { },
                    dialogTitle = "Permiso de notificación",
                    dialogText = "Test",
                    icon = android.R.drawable.ic_dialog_info
                )
            }
            else -> {
                activityResultLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
        finish()
    }

}

@Composable
fun permissionRationale(
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
