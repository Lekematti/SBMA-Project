package com.example.sbma_project

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sbma_project.extension.hasLocationPermission
import com.example.sbma_project.internetConnection.ConnectionStatus
import com.example.sbma_project.internetConnection.currentConnectivityStatus
import com.example.sbma_project.internetConnection.observeConnectivityAsFLow
import com.example.sbma_project.ui.theme.SBMAProjectTheme
import com.example.sbma_project.viewmodels.LocationViewModel
import com.example.sbma_project.viewmodels.PermissionEvent
import com.example.sbma_project.viewmodels.RunViewModel
import com.example.sbma_project.viewmodels.RunningState
import com.example.sbma_project.viewmodels.ViewState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), SettingsActionListener {
   /* @Composable
    fun HeightInputDialog(
        showDialog: Boolean,
        onHeightEntered: (Double) -> Unit
    ) {
        var heightText by remember { mutableStateOf(TextFieldValue()) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Enter Your Height") },
                text = {
                    TextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text("Height (in meters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val height = heightText.text.toDoubleOrNull() ?: 0.0
                        onHeightEntered(height)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {}) {
                        Text("Cancel")
                    }
                }
            )
        }
    }*/
    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationViewModel: LocationViewModel by viewModels()
        val runViewModel: RunViewModel by viewModels()

        setContent {
            val pathPoints by locationViewModel.pathPoints.collectAsState()
            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                )
            )
            val viewState by locationViewModel.viewState.collectAsState()
            val isConnected = checkConnectivityStatus()
/*
            var userHeight by remember { mutableDoubleStateOf(0.0) }
            var dialogState by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                // Check if user height is already in the database
                val storedHeight = runViewModel.getUserHeight()
                // If yes, use the stored user height
                userHeight = storedHeight
                dialogState = false
            }
            HeightInputDialog(
                showDialog = dialogState,  // Use dialogState here
                onHeightEntered = { height ->
                    userHeight = height
                    runViewModel.setUserHeight(userHeight)
                    dialogState = false
                    Log.d("Height", "User height: $userHeight")
                }
            )
            runViewModel.onSaveHeight = { success ->
                if (success) {
                    Toast.makeText(this, "Height saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save height", Toast.LENGTH_SHORT).show()
                }
            }*/

            SBMAProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(!hasLocationPermission()) {
                        permissionState.launchMultiplePermissionRequest()
                    }
                    when {
                        permissionState.allPermissionsGranted -> {
                            locationViewModel.handle(PermissionEvent.Granted)
                        }
                        permissionState.shouldShowRationale -> {
                            RationaleAlert(onDismiss = {}) {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        }
                        else -> {
                            locationViewModel.handle(PermissionEvent.Revoked)
                        }
                    }
                    with(viewState) {
                        when (this) {
                            ViewState.Loading -> {
                                MainScreen(
                                    "loading",
                                    settingsActionListener = this@MainActivity,
                                    isConnected = isConnected,
                                    locationViewModel = locationViewModel,
                                    runViewModel = runViewModel,

                                )
                            }
                            ViewState.RevokedPermissions -> {
                                MainScreen(
                                    "revoked",
                                    settingsActionListener = this@MainActivity,
                                    isConnected = isConnected,
                                    locationViewModel = locationViewModel,
                                    runViewModel = runViewModel,

                                )
                            }
                            is ViewState.Success -> {
                                MainScreen(
                                    "success",
                                    LatLng(
                                        this.location?.latitude ?: 0.0,
                                        this.location?.longitude ?: 0.0
                                    ),
                                    rememberCameraPositionState(),
                                    pathPoints = if (locationViewModel.runningState == RunningState.Running) pathPoints else emptyList(),
                                    settingsActionListener = this@MainActivity,
                                    isConnected = isConnected,
                                    locationViewModel = locationViewModel,
                                    runViewModel = runViewModel,

                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @Composable
    private fun checkConnectivityStatus(): Boolean {
        val connection by connectivityStatus()
        return connection != ConnectionStatus.Unavailable
    }

    @Composable
    private fun connectivityStatus(): State<ConnectionStatus> {
        val mContext = LocalContext.current
        return produceState(initialValue = mContext.currentConnectivityStatus) {
            mContext.observeConnectivityAsFLow().collect {
                value = it
            }
        }
    }
}

@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "We need location permissions to use this app",
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SBMAProjectTheme {
        // Replace with your preview content
        Text("Preview Content")
    }
}

interface SettingsActionListener {
    fun openAppSettings()
}