package com.example.sbma_project.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sbma_project.R
import com.example.sbma_project.SettingsActionListener
import com.example.sbma_project.repository.RunViewModel
import com.example.sbma_project.uiComponents.EmojiButton
import com.example.sbma_project.uiComponents.emojiToRating
import com.example.sbma_project.uiComponents.formatTime
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.util.Date

@Composable
fun DetailedHistoryView(
    settingsActionListener: SettingsActionListener,
    locationPermissionState: String,
    runViewModel: RunViewModel,
    runId: Long,
    onBack: () -> Unit
) {
    var isFirstTime by remember { mutableStateOf(true) }
    val cameraState =
        remember { CameraPositionState(CameraPosition(LatLng(0.0, 0.0), 20f, 0f, 0f)) }

    val runDetails = runViewModel.getRunById(runId).observeAsState()

    // State to track whether the dialog is open
    val showDialog = remember { mutableStateOf(false) }
    val showNoteDialog = remember { mutableStateOf(false) }
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }

    val runIdToDelete = remember { mutableLongStateOf(-1L) }


    // state to track notes and emojis
    var selectedEmoji by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    // State to track selected rating
    val selectedRating = remember { mutableIntStateOf(0) }

    // Function to show dialog for updating rating
    fun showRatingDialog() {
        showDialog.value = true
        // Initialize emoji  with the existing emoji if available
        runDetails.value?.rating?.let { rating ->
            selectedEmoji = ratingToEmoji(rating)
        }
    }


    // Function to show note editing dialog
    fun showNoteEditDialog() {
        showNoteDialog.value = true
        // Initialize note text with the existing note if available
        runDetails.value?.notes?.let { note ->
            noteText = note
        }
    }

    // Function to handle rating selection
    fun onRatingSelected(rating: Int) {
        val currentTimestamp = Date()
        selectedRating.intValue = rating
        runViewModel.updateModifiedDate(runId, currentTimestamp)
        runViewModel.updateRunRating(runId, rating)
        showDialog.value = false
    }

    // Function to handle saving edited note
    fun onSaveNote() {
        val currentTimestamp = Date() // Get current date and time
        runViewModel.updateModifiedDate(runId, currentTimestamp)
        runViewModel.updateRunNotes(runId, noteText)
        showNoteDialog.value = false
    }

    // Function to handle cancelling note editing
    fun onCancelNoteEdit() {
        // Reset noteText to the existing note if available
        runDetails.value?.notes?.let { note ->
            noteText = note
        }
        showNoteDialog.value = false
    }

    // Render detailed view
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        runDetails.value?.let { run ->
            val initialPosition = run.routePath?.firstOrNull()?.let {
                LatLng(it.latitude, it.longitude)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(modifier = Modifier.wrapContentSize()) {
                        Button(
                            onClick = { onBack() },
                            modifier = Modifier.padding(top = 6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back_ios_24px),
                                    contentDescription = "Back Arrow",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Back", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)) {
                        Card(
                            modifier = Modifier
                                .clickable {
                                    showDeleteConfirmationDialog(
                                        run.id,
                                        runIdToDelete,
                                        showDeleteConfirmationDialog
                                    )
                                }
                                .size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                DeleteIcon(Modifier.size(32.dp))
                            }
                        }
                    }


                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(run.createdAt),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .weight(1f)
                    )
                }

                //This holds map, duration, distance, steps, avg speed, step length, avg pace, rating, note
                Card(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Text(
                                text = "* Tap to edit",
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Card(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxHeight(0.5f)
                                .fillMaxWidth()
                        ) {

                            if (locationPermissionState == "loading") {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (locationPermissionState == "success") {
                                if (!run.routePath.isNullOrEmpty()) {
                                    initialPosition?.let {
                                        if (isFirstTime) { // If it's the first time, set camera position
                                            LaunchedEffect(key1 = run.routePath.first()) {
                                                cameraState.centerOnLocation(
                                                    run.routePath.first(),
                                                    zoom = 20f
                                                )
                                                isFirstTime = false
                                            }
                                        }
                                        GoogleMap(
                                            cameraPositionState = cameraState,
                                            properties = MapProperties(
                                                isMyLocationEnabled = true,
                                                mapType = MapType.HYBRID,
                                                isTrafficEnabled = true
                                            ),
                                        ) {
                                            // Draw polyline
                                            DrawPolyline(run.routePath)

                                            // marker for start point
                                            if (run.routePath.isNotEmpty()) {
                                                Marker(
                                                    state = MarkerState(
                                                        LatLng(
                                                            run.routePath.first().latitude,
                                                            run.routePath.first().longitude
                                                        )
                                                    ),
                                                    title = "Start",
                                                    icon = BitmapDescriptorFactory.defaultMarker(
                                                        BitmapDescriptorFactory.HUE_CYAN
                                                    )
                                                )
                                            }
                                            // marker for finish point
                                            if (run.routePath.isNotEmpty()) {
                                                Marker(
                                                    state = MarkerState(
                                                        LatLng(
                                                            run.routePath.last().latitude,
                                                            run.routePath.last().longitude
                                                        )
                                                    ),
                                                    title = "Finish",
                                                    icon = BitmapDescriptorFactory.defaultMarker(
                                                        BitmapDescriptorFactory.HUE_GREEN
                                                    )
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxSize()
                                    ) {
                                        Text(
                                            text = "No routes were detected.",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.align(Alignment.Center),
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
                                    Text(
                                        "We need permissions to view the routes on the map",
                                        style = MaterialTheme.typography.titleSmall,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            settingsActionListener.openAppSettings()
                                        }
                                    ) {
                                        Text(
                                            "Open Settings",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                            }
                        }
                        //card to make this section scrollable
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.timer_24px),
                                    title = "Duration",
                                    description = formatTime(run.durationInMillis)
                                )
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.route_24px),
                                    title = "Distance",
                                    description = "${run.distance}m"
                                )
                            }
                            Row {
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.footprint_24px),
                                    title = "Steps",
                                    description = "${run.steps}"
                                )
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.speed_24px),
                                    title = "Average Speed",
                                    description = "${run.avgSpeed}km/hr"
                                )
                            }
                            Row {
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.footprint_24px),
                                    title = "Step Length",
                                    description = "${run.stepLength}cm"
                                )
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.steps_24px),
                                    title = "Average Pace",
                                    description = "${run.speedTimestamps?.size}"
                                )
                            }
                            Row {
                                run.rating?.let { rating ->
                                    DetailCardItem(
                                        modifier = Modifier
                                            .clickable { showRatingDialog() }
                                            .padding(10.dp)
                                            .weight(0.5f),
                                        icon = painterResource(id = R.drawable.relax_24px),
                                        title = "Feeling *",
                                        description = ratingToEmoji(rating)
                                    )
                                } ?: DetailCardItem(
                                    modifier = Modifier
                                        .clickable { showRatingDialog() }
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.relax_24px),
                                    title = "Feeling *",
                                    description = ""
                                )
                            }
                            Row {
                                run.notes?.let {
                                    DetailCardItem(
                                        modifier = Modifier
                                            .clickable { showNoteEditDialog() }
                                            .padding(10.dp)
                                            .weight(1f),
                                        icon = painterResource(id = R.drawable.description_24px),
                                        title = "Notes *",
                                        description = it
                                    )
                                }
                            }
                            //update rating dialog
                            if (showDialog.value) {
                                Dialog(onDismissRequest = { showDialog.value = false }) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .height(200.dp)
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Update Post Run Rating",
                                                style = MaterialTheme.typography.titleSmall,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                listOf("😞", "😐", "😊", "😃", "😄").forEach { emoji ->
                                                    EmojiButton(
                                                        emoji = emoji,
                                                        isSelected = selectedEmoji == emoji,
                                                        onClick = {
                                                            onRatingSelected(
                                                                emojiToRating(
                                                                    emoji
                                                                ).value
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Note editing dialog
                            if (showNoteDialog.value) {
                                Dialog(onDismissRequest = { showNoteDialog.value = false }) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Edit Note",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            // Text field to edit note
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .verticalScroll(rememberScrollState())
                                            ) {
                                                BasicTextField(
                                                    value = noteText,
                                                    onValueChange = { noteText = it },
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp),
                                                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                                                    maxLines = Int.MAX_VALUE
                                                )
                                            }
                                            // Buttons
                                            Row(
                                                horizontalArrangement = Arrangement.End,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Button(
                                                    onClick = { onCancelNoteEdit() },
                                                    modifier = Modifier.padding(end = 8.dp),
                                                    colors = if (noteText != run.notes) ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors(
                                                        MaterialTheme.colorScheme.primary
                                                    ),
                                                ) {
                                                    Text(
                                                        text = "Cancel",
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                                Button(
                                                    onClick = { onSaveNote() },
                                                    enabled = noteText != run.notes,
                                                ) {
                                                    Text(
                                                        text = "Update Note",
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //delete confirmation dialog
                            if (showDeleteConfirmationDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDeleteConfirmationDialog.value = false
                                    },
                                    title = {
                                        Text(
                                            "Confirm Deletion",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    },
                                    text = {
                                        Text(
                                            "Are you sure you want to delete this run?",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                runViewModel.deleteRunById(runIdToDelete.longValue)
                                                showDeleteConfirmationDialog.value = false
                                                onBack()
                                            },
                                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                            contentPadding = PaddingValues(16.dp)
                                        ) {
                                            Text(
                                                "Confirm",
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = {
                                                showDeleteConfirmationDialog.value = false
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(),
                                            contentPadding = PaddingValues(16.dp)
                                        ) {
                                            Text(
                                                "Cancel",
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    }
                                )
                            }
                            run.modifiedAt?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    Text(
                                        text = "last modified: ${formatDate(it)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DetailCardItem(modifier: Modifier, icon: Painter, title: String, description: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = title,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(22.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
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