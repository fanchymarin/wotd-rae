package com.example.palabradeldiarae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.palabradeldiarae.ui.theme.PalabraDelDiaRAETheme
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
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
import java.util.Calendar

private val TAG: String = MainActivity::class.java.getName()

class MainActivity : ComponentActivity() {

    private fun showToast(str: String, length: Int = LENGTH_SHORT) {
        val toast = Toast.makeText(this, str, length)
        toast.show()
    }

    private fun setAlarm() {
        try {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(this, NotificationService::class.java).let { intent ->
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }
            Log.d(TAG, "Setting alarm")

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            Log.d(TAG, "Alarm set for ${calendar.time}")

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
            Log.d(TAG, "Alarm set")
            showToast(
                "${getString(R.string.app_name)} configurado correctamente"
            )
            showToast(
                "Abre la aplicación de nuevo para eliminar la notificación"
            , LENGTH_LONG)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting alarm: ${e.message}")
        }
        finish()
    }

    private fun installOrUninstall() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmUp = (PendingIntent.getBroadcast(this, 0,
            Intent(this, NotificationService::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE) != null)

        Log.d(TAG, "Checking if alarm exists")
        if (!alarmUp) {
            Log.d(TAG, "Alarm does not exist")
            setAlarm()
        }
        else {
            Log.d(TAG, "Alarm exists")
            val alarmIntent = Intent(this, NotificationService::class.java).let { intent ->
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }
            alarmManager.cancel(alarmIntent)
            alarmIntent.cancel()
            Log.d(TAG, "Alarm cancelled")
            showToast(
                "${getString(R.string.app_name)} desinstalado correctamente"
            )
            finish()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted")
                setAlarm()
            } else {
                Log.d(TAG, "Notification permission not granted")
                showToast(
                    "La aplicación necesita permisos para funcionar correctamente"
                )
            }
            finish()
        }

    private fun requestPermission() {
        Log.d(TAG, "Requesting notification permission")
        activityResultLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Starting permission acceptance flow")
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
