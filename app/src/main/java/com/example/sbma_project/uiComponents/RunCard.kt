package com.example.sbma_project.uiComponents

import android.content.Intent
import android.os.Build
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
import com.example.sbma_project.R
import com.example.sbma_project.calculators.DistanceCalculator
import com.example.sbma_project.calculators.calculateAverageSpeed
import com.example.sbma_project.services.RunningService
import com.example.sbma_project.utils.emojiToRating
import com.example.sbma_project.viewmodels.LocationViewModel
import com.example.sbma_project.viewmodels.RunViewModel
import com.example.sbma_project.viewmodels.RunningState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RunCard(
    modifier: Modifier,
    locationViewModel: LocationViewModel,
    runViewModel: RunViewModel,
    pathPoints: List<LatLng>?,
) {
    val speedList by locationViewModel.speedList.collectAsState()
    val speedTimeStampsList by locationViewModel.speedTimestamps.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    val time by locationViewModel.time.collectAsState()
    val stopButtonEnabled by locationViewModel.stopButtonEnabled.collectAsState()
    var enteredText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var avgSpeed: Float?
    val userHeight = 0.0


    fun resetStateAndHideDialog() {
        selectedEmoji = ""
        enteredText = ""
        showDialog = false
        locationViewModel.resetTime()
        locationViewModel.resetPathPoints()
        locationViewModel.resetDistance()
    }

    fun Float.round(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
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
                CardSteps(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // colors for start and pause
            var buttonColor by remember { mutableStateOf(Color.Green) }
            val mainButtonColors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = MaterialTheme.colorScheme.surface
            )

            //Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Equal weight for each row
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Start/pause button
                Button(colors = mainButtonColors,
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
                            RunningState.Running -> locationViewModel.resumeDistance()
                        }
                    }

                ) {
                    Icon(
                        painter = if (locationViewModel.runningState == RunningState.Running) painterResource(
                            R.drawable.pause
                        ) else painterResource(R.drawable.play_arrow),
                        contentDescription = if (locationViewModel.runningState == RunningState.Running) "pause button" else "play button"
                    )
                }
                // colors change for start and pause
                buttonColor = when (locationViewModel.runningState) {
                    RunningState.Running -> Color.Yellow
                    else -> Color.Green
                }

                Spacer(modifier = Modifier.width(16.dp))

                // color for stop button
                val stopButtonColor = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = MaterialTheme.colorScheme.surface
                )
                //End button
                Button(
                    colors = stopButtonColor,
                    onClick = {
                        Intent(context, RunningService::class.java).also {
                            it.action = RunningService.Actions.STOP.toString()
                            context.startService(it)
                        }
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
                            style = MaterialTheme.typography.titleSmall,
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
                                Text(
                                    text = "How do you feel after running?",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                                Text(
                                    text = "Write private notes here.",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                                    val totalDistance = pathPoints.let {
                                        DistanceCalculator.calculateTotalDistance(
                                            it
                                        )
                                    }
                                    val totalTimeInSeconds = locationViewModel.time.value
                                    avgSpeed =
                                        calculateAverageSpeed(totalDistance, totalTimeInSeconds)
                                    val roundedAvgSpeed = String.format(Locale.US, "%.2f", avgSpeed)
                                    runViewModel.createRun(
                                        startTime = time,
                                        routePath = pathPoints,
                                        speedList = speedList,
                                        rating = rating,
                                        notes = enteredText,
                                        speedTimestamps = speedTimeStampsList,
                                        avgSpeed = roundedAvgSpeed.toFloat(),
                                        //userHeight = userHeight
                                    )
                                }
                                resetStateAndHideDialog()
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Save", style = MaterialTheme.typography.titleSmall)
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
                            Text("Discard", style = MaterialTheme.typography.titleSmall)
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




