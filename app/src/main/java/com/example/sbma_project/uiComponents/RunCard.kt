package com.example.sbma_project.uiComponents

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbma_project.APIHelper.FitApiHelper
import com.example.sbma_project.R
import com.example.sbma_project.distance.DistanceCalculator
import com.example.sbma_project.repository.TimerViewModel
import com.example.sbma_project.services.RunningService
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
    var showDialog by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    val time by locationViewModel.time.collectAsState()
    val stopButtonEnabled by locationViewModel.stopButtonEnabled.collectAsState()
    var enteredText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Log.d("RunCardPoints","$pathPoints")

 /*   val averageSpeed = locationViewModel.averageSpeed // Retrieve the average speed from LocationViewModel

    LaunchedEffect(locationViewModel.runningState) {
        if (locationViewModel.runningState == RunningState.Stopped) {
            // Display toast with average speed
            Toast.makeText(
                context,
                "Average Speed: $averageSpeed km/hr",
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/


    fun resetStateAndHideDialog() {
        selectedEmoji = ""
        enteredText = ""
        showDialog = false
        locationViewModel.resetTime()
        locationViewModel.resetPathPoints()
        locationViewModel.resetDistance()

    }

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
                        .fillMaxHeight(),
                    locationViewModel = locationViewModel,
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
                // Start/pause button
                Button(
                    onClick = {
                        Intent(context, RunningService::class.java).also {
                            it.action = RunningService.Actions.START.toString()
                            context.startService(it)
                        }
                        when (locationViewModel.runningState) {
                            RunningState.Running -> locationViewModel.pauseRun()
                            RunningState.Paused -> locationViewModel.resumeRun()
                            RunningState.Stopped -> locationViewModel.startRun()
                            RunningState.Paused -> locationViewModel.pauseDistance()
                            RunningState.Paused -> locationViewModel.resumeDistance()
                            else -> {}
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
                        Intent(context, RunningService::class.java).also {
                            it.action = RunningService.Actions.STOP.toString()
                            context.startService(it)
                        }
                        // Retrieve total distance and total time from LocationViewModel
                        val totalDistance = pathPoints?.let {
                            DistanceCalculator.calculateTotalDistance(
                                it
                            )
                        }
                        val totalTimeInSeconds = locationViewModel.time.value

                        val averageSpeed = calculateAverageSpeed(totalDistance ?: 0.0, totalTimeInSeconds)

                        Toast.makeText(
                            context,
                            "Average Speed: $averageSpeed km/hr",
                            Toast.LENGTH_SHORT
                        ).show()

                        locationViewModel.finishRun()
                        showDialog = true
                    },
                    enabled = stopButtonEnabled
                ) {
                    Icon(
                        painter = painterResource(R.drawable.stop),
                        contentDescription = "Finish button"
                    )
                }
            }

            // Modal Dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Hide the modal when dismissed
                        resetStateAndHideDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),

                    title = {
                        Text(
                            "Save your run.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = "How do you feel after running?")
                                Spacer(modifier = Modifier.height(4.dp))
                                // Row of emojis
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EmojiButton("😞", isSelected = selectedEmoji == "😞") {
                                        selectedEmoji = "😞"
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    EmojiButton("😐", isSelected = selectedEmoji == "😐") {
                                        selectedEmoji = "😐"
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    EmojiButton("😊", isSelected = selectedEmoji == "😊") {
                                        selectedEmoji = "😊"
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    EmojiButton("😃", isSelected = selectedEmoji == "😃") {
                                        selectedEmoji = "😃"
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    EmojiButton("😄", isSelected = selectedEmoji == "😄") {
                                        selectedEmoji = "😄"
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Text field for notes
                                Text(text = "Write private notes here.")
                                Spacer(modifier = Modifier.height(4.dp))

                                TextField(
                                    value = enteredText,
                                    onValueChange = { enteredText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4
                                )
                            }

                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val rating = if (selectedEmoji.isEmpty()) null else emojiToRating(
                                    selectedEmoji
                                ).value
                                if (pathPoints != null) {
                                    timerViewModel.createTimer(
                                        time,
                                        pathPoints,
                                        rating,
                                        enteredText
                                    )
                                }
                                resetStateAndHideDialog()
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                resetStateAndHideDialog()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Discard")
                        }
                    }
                )
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

@Composable
fun EmojiButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(if (isSelected) Color.Gray else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 28.sp, modifier = Modifier.align(Alignment.Center))
    }
}

sealed class Rating(val value: Int) {
    data object VeryBad : Rating(1)
    data object Bad : Rating(2)
    data object Neutral : Rating(3)
    data object Good : Rating(4)
    data object VeryGood : Rating(5)
}

fun emojiToRating(emoji: String): Rating {
    return when (emoji) {
        "😞" -> Rating.VeryBad
        "😐" -> Rating.Bad
        "😊" -> Rating.Neutral
        "😃" -> Rating.Good
        "😄" -> Rating.VeryGood
        else -> throw IllegalArgumentException("Invalid emoji")
    }

}

// Modify the calculateAverageSpeed function to convert total distance to kilometers
fun calculateAverageSpeed(totalDistanceInMeters: Double, totalTimeInSeconds: Long): Float {
    val totalDistanceInKm = totalDistanceInMeters / 1000 // Convert total distance to kilometers
    val totalTimeInHours = totalTimeInSeconds / 3600f // Convert total time to hours
    return if (totalTimeInHours > 0 && totalDistanceInKm > 0) {
        (totalDistanceInKm / totalTimeInHours).toFloat() // Calculate average speed
    } else {
        0f // Return 0 if either time or distance is 0
    }
}




