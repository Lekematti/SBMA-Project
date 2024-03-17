package com.example.sbma_project

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sbma_project.viewmodels.LocationViewModel
import com.example.sbma_project.viewmodels.RunViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    locationPermissionState:String,
    currentPosition: LatLng? = null,
    cameraState: CameraPositionState? = null,
    pathPoints: List<LatLng>? = null,
    settingsActionListener: SettingsActionListener,
    isConnected: Boolean,
    locationViewModel: LocationViewModel,
    runViewModel: RunViewModel,
    ) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            BottomNavGraph(
                navController = navController,
                locationPermissionState = locationPermissionState,
                currentPosition = currentPosition,
                cameraState = cameraState,
                pathPoints = pathPoints,
                settingsActionListener = settingsActionListener,
                isConnected = isConnected,
                locationViewModel = locationViewModel,
                runViewModel = runViewModel,
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController){
    val views = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.History,
        BottomBarScreen.Info,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        views.forEach { view ->
            AddItem(view = view, currentDestination = currentDestination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItem(
    view: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    NavigationBarItem(
        label = {
            Text(text = view.title)
        },
        icon = {
            Icon(imageVector = view.icon, contentDescription = "Navigation icon")
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == view.route
        } == true,
        onClick = {
            navController.navigate(view.route)
        }
    )
}