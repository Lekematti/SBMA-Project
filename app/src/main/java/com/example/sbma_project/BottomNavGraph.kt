package com.example.sbma_project

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sbma_project.APIHelper.FitApiHelper
import com.example.sbma_project.repository.TimerViewModel
import com.example.sbma_project.viewmodels.DistanceViewModel
import com.example.sbma_project.viewmodels.LocationViewModel
import com.example.sbma_project.views.History
import com.example.sbma_project.views.Home
import com.example.sbma_project.views.Info
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    locationPermissionState:String,
    currentPosition: LatLng? = null,
    cameraState: CameraPositionState? = null,
    pathPoints: List<LatLng>? = null,
    settingsActionListener: SettingsActionListener,
    isConnected: Boolean,
    locationViewModel: LocationViewModel,
    timerViewModel: TimerViewModel,
    fitApiHelper: FitApiHelper, // Pass FitApiHelper as a parameter
    distanceViewModel: DistanceViewModel,

    ) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route,
    ) {
        composable(route = BottomBarScreen.Home.route) {
            Home(
                locationPermissionState = locationPermissionState,
                currentPosition= currentPosition, cameraState = cameraState,
                pathPoints = pathPoints,
                settingsActionListener = settingsActionListener,
                isConnected = isConnected,
                locationViewModel = locationViewModel,
                timerViewModel = timerViewModel,
                fitApiHelper = fitApiHelper, // Pass FitApiHelper instance
                distanceViewModel = distanceViewModel
            )
        }
        composable(route = BottomBarScreen.History.route) {
            History(
                timerViewModel = timerViewModel,
                )
        }
        composable(route = BottomBarScreen.Info.route) {
            Info()
        }
    }
}