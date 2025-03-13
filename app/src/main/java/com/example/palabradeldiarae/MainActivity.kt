package com.example.palabradeldiarae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.palabradeldiarae.ui.theme.PalabraDelDiaRAETheme
import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
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
import com.example.palabradeldiarae.SetAlarm.Companion.setAlarm

private val TAG: String = MainActivity::class.java.getName()

class MainActivity : ComponentActivity() {

    private fun showToast(str: String, length: Int = LENGTH_LONG) {
        val toast = Toast.makeText(this, str, length)
        toast.show()
    }

    private fun setAlarmAndBoot() {
        setAlarm(this, 0)
        val receiver = ComponentName(this, BootReceiver::class.java)
        Log.d(TAG, "Enabling boot receiver")
        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        showToast(
            "${getString(R.string.app_name)} configurado correctamente"
        )
    }

    private val activityResultLauncher =
        registerForActivityResult(RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted")
                setAlarmAndBoot()
            } else {
                Log.d(TAG, "Notification permission not granted")
                showToast(
                    "${getString(R.string.app_name)} no ha sido configurado"
                )
            }
            finish()
        }

    private fun requestPermission() {
        Log.d(TAG, "Requesting notification permission")
        try {
            activityResultLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting notification permission: ${e.message}")
        }
    }

    private fun showRationale() {
        Log.d(TAG, "Showing permission rationale")
        enableEdgeToEdge()
        setContent {
            PalabraDelDiaRAETheme {
                PermissionRationale(
                    onDismissRequest = { finish() },
                    onConfirmation = {
                        requestPermission()
                    },
                    dialogTitle =   "Permiso de notificación",
                    dialogText =    "${getString(R.string.app_name)} necesita el permiso " +
                                    "de notificación para funcionar correctamente",
                    icon = Icons.Default.Info
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Starting permission acceptance flow")
        when {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Notification permission was previously granted")
                setAlarmAndBoot()
                finish()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                Log.d(TAG, "Notification permission was previously denied")
                showRationale()
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
            Icon(icon, contentDescription = "Info icon")
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
                Text("Continuar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Salir")
            }
        }
    )
}
