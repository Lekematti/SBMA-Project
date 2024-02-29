package com.example.sbma_project.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sbma_project.APIHelper.FitApiHelper
import com.example.sbma_project.SettingsActionListener
import com.example.sbma_project.repository.TimerViewModel
import com.example.sbma_project.uiComponents.RunCard
import com.example.sbma_project.viewmodels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Polyline

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Home(
    locationPermissionState: String, currentPosition: LatLng? = null,
    cameraState: CameraPositionState? = null,
    pathPoints: List<LatLng>? = null,
    settingsActionListener: SettingsActionListener,
    isConnected: Boolean,
    locationViewModel: LocationViewModel,
    timerViewModel: TimerViewModel,
    fitApiHelper: FitApiHelper, // Pass FitApiHelper as a parameter
) {
    var isFirstTime by remember { mutableStateOf(true) } // Track if it's the first time
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isConnected) {
            if (locationPermissionState == "loading") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else if (locationPermissionState == "success") {
                if (isFirstTime) { // If it's the first time, zoom to 15f
                    LaunchedEffect(key1 = currentPosition) {
                        if (currentPosition != null) {
                            cameraState?.centerOnLocation(currentPosition, zoom = 15f)
                            isFirstTime = false // Set the flag to false after first zoom
                        }
                    }
                }
                //when marker is needed (for later)
                val marker = currentPosition?.let { LatLng(it.latitude, currentPosition.longitude) }

                if (cameraState != null) {
                    Column {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f),
                            cameraPositionState = cameraState,
                            properties = MapProperties(
                                isMyLocationEnabled = true,
                                mapType = MapType.HYBRID,
                                isTrafficEnabled = true
                            )
                        ) {
                            if (pathPoints != null) {
                                DrawPolyline(pathPoints)
                            }
                        }
                        RunCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .padding(10.dp),
                            locationViewModel = locationViewModel,
                            timerViewModel = timerViewModel,
                            pathPoints = pathPoints,
                            fitApiHelper = fitApiHelper // Pass FitApiHelper instance
                            )
                    }
                }
            } else {
                //grant permission screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("We need permissions to use this app")
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            settingsActionListener.openAppSettings()
                        }
                    ) {
                        Text("Open Settings")
                    }
                }
            }
        } else {
            NoInternetScreen()
        }


    }
}

@Composable
fun DrawPolyline(points: List<LatLng>) {
    if (points.isNotEmpty()) {
        Polyline(points = points, width = 15f, color = Color.Blue)
    }
}

private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng,
    zoom: Float = 15f
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        zoom
    ),
)