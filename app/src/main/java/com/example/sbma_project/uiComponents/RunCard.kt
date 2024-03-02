package com.example.sbma_project.uiComponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sbma_project.APIHelper.FitApiHelper
import com.example.sbma_project.R
import com.example.sbma_project.repository.TimerViewModel
//import com.example.sbma_project.viewmodels.DistanceViewModel
import com.example.sbma_project.viewmodels.LocationViewModel
import com.example.sbma_project.viewmodels.RunningState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RunCard(
    modifier: Modifier,
    locationViewModel: LocationViewModel,
    timerViewModel: TimerViewModel,
    //distanceViewModel: DistanceViewModel,
    pathPoints: List<LatLng>?,
    fitApiHelper: FitApiHelper, // Pass FitApiHelper as a parameter
) {
    val time by locationViewModel.time.collectAsState()
    val stopButtonEnabled by locationViewModel.stopButtonEnabled.collectAsState()

    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //First Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // First Row First Column
                CardTime(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    locationViewModel = locationViewModel
                )

                // Divider
                CustomDivider(vertical = true)

                // First Row Second Column
                CardSpeed(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

            }

            CustomDivider(vertical = false)

            //Second Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
                //Second Row First Column
                CardDistance(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    pathPoints = pathPoints
                )

                // Divider
                CustomDivider(vertical = true)

                //Second Row Second Column
                CardHeartBeat(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    fitApiHelper = fitApiHelper // Pass FitApiHelper instance
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Equal weight for each row
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Start/stop button
                Button(
                    onClick = {

                        when (locationViewModel.runningState) {
                            RunningState.Running -> locationViewModel.pauseRun()
                            RunningState.Paused -> locationViewModel.resumeRun()
                            RunningState.Stopped -> locationViewModel.startRun()
                            RunningState.Paused -> locationViewModel.pauseDistance()
                            RunningState.Paused -> locationViewModel.resumeDistance()
                        }
                    }) {
                    Icon(
                        painter = if (locationViewModel.runningState == RunningState.Running) painterResource(
                            R.drawable.pause
                        ) else painterResource(R.drawable.play_arrow),
                        contentDescription = if (locationViewModel.runningState == RunningState.Running) "pause button" else "play button"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                //End button
                Button(
                    onClick = {
                        if (pathPoints != null) {
                            timerViewModel.createTimer(time, pathPoints)
                        }
                        locationViewModel.resetTime()
                        locationViewModel.finishRun()
                        locationViewModel.resetDistance()
                    },
                    enabled = stopButtonEnabled
                ) {
                    Icon(
                        painter = painterResource(R.drawable.stop),
                        contentDescription = "Finish button"
                    )
                }
            }
        }
        LaunchedEffect(locationViewModel.runningState == RunningState.Running) {
            while (locationViewModel.runningState == RunningState.Running) {
                delay(1000)
                locationViewModel.updateTime(time + 1)
            }
        }
    }
}

@Composable
fun CustomDivider(vertical: Boolean) {

    Divider(
        color = Color.Gray,
        modifier =
        if (vertical) Modifier
            .fillMaxHeight(0.8f)
            .width(1.dp) else
            Modifier
                .fillMaxWidth(1f)
                .height(1.dp)
    )
}


