package com.example.sbma_project.views


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbma_project.R
import com.example.sbma_project.SettingsActionListener
import com.example.sbma_project.database.Run
import com.example.sbma_project.graph.RunHistoryGraph
import com.example.sbma_project.repository.RunViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun History(
    settingsActionListener: SettingsActionListener,
    runViewModel: RunViewModel,
    locationPermissionState: String,
) {
    // Define sorting options
    val sortingOptions = listOf(
        "Created Date Ascending",
        "Created Date Descending",
        "Modified Date Ascending",
        "Modified Date Descending"
    )

    val runsState = remember { mutableStateOf<List<Run>>(emptyList()) }
    var showDetailView by remember { mutableStateOf(false) }
    var selectedHistoryId by remember { mutableLongStateOf(-1L) }
    val selectedHistoryData by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val showSortDialog = remember { mutableStateOf(false) }
    val runIdToDelete = remember { mutableLongStateOf(-1L) }

    // Remember the last selected sorting option
    var selectedSortOption by remember { mutableStateOf(sortingOptions.first()) }
    val showInfo = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = runViewModel.runs) {
        runViewModel.runs.observeForever { runs ->
            runsState.value = runs
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!showDetailView) {
            //Graphs
            Card(
                modifier = Modifier
                    .height(300.dp)
                    .width(400.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    RunHistoryGraph(runViewModel = runViewModel)
                }
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showSortDialog.value = true }, // Show the sort dialog
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Sort By")

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.sort_24px),
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = "Click here for more info",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            showInfo.value = true
                        }
                )
                if (showInfo.value) {
                    AlertDialog(
                        onDismissRequest = { showInfo.value = false },
                        title = { Text("Graph Info") },
                        text = { Text("This graph shows the progression of your runs. The lower your time and the higher your distance the better.") },
                        confirmButton = {
                            Button(onClick = { showInfo.value = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
                // run stats items
                LazyColumn {
                    items(runsState.value) { runs ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    selectedHistoryId = runs.id // Store the ID only
                                    showDetailView = true
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = formatDate(runs.createdAt),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Card(
                                        modifier = Modifier
                                            .clickable {
                                                showDeleteConfirmationDialog(
                                                    runs.id,
                                                    runIdToDelete,
                                                    showDialog
                                                )
                                            }
                                            .align(Alignment.CenterVertically)
                                            .size(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                    ) {
                                        IconButton(onClick = {
                                            showDeleteConfirmationDialog(
                                                runs.id,
                                                runIdToDelete,
                                                showDialog
                                            )
                                        }
                                        ) {
                                            DeleteIcon(Modifier.size(32.dp))
                                        }
                                    }

                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = "Duration: ${runs.durationInMillis}s")
                                Text(text = "route LatLng size: ${runs.routePath?.size}")
                            }
                        }
                    }
                }
            }
        } else {
            // Detailed view
            selectedHistoryId.takeIf { it != -1L }?.let { id ->
                DetailedHistoryView(
                    settingsActionListener = settingsActionListener,
                    runViewModel = runViewModel,
                    runId = id,
                    locationPermissionState = locationPermissionState,
                ) {
                    showDetailView = false // Close the detailed view
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Graph placeholder for $selectedHistoryData")
                    }

                    Text(
                        text = "Detailed View for $selectedHistoryData",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "More detailed information here...",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Back",
                        modifier = Modifier
                            .clickable { showDetailView = false }
                            .padding(top = 16.dp)
                            .background(color = Color.LightGray)
                    )
                }
            }

            // delete dialog
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete this run?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                runViewModel.deleteRunById(runIdToDelete.longValue)
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog.value = false },
                            colors = ButtonDefaults.outlinedButtonColors(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showSortDialog.value) {
                // Sorting dialog
                AlertDialog(
                    onDismissRequest = { showSortDialog.value = false },
                    title = { Text("Sort By") },
                    confirmButton = {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                        ) {
                            sortingOptions.forEachIndexed { index, option ->
                                Button(
                                    onClick = {
                                        selectedSortOption = option
                                        // sorting based on the selected option
                                        when (option) {
                                            "Created Date Ascending" -> {
                                                runsState.value =
                                                    runsState.value.sortedBy { it.createdAt }
                                            }

                                            "Created Date Descending" -> {
                                                runsState.value =
                                                    runsState.value.sortedByDescending { it.createdAt }
                                            }

                                            "Modified Date Ascending" -> {
                                                runsState.value =
                                                    runsState.value.sortedBy { it.modifiedAt }
                                            }

                                            "Modified Date Descending" -> {
                                                runsState.value =
                                                    runsState.value.sortedByDescending { it.modifiedAt }
                                            }
                                        }
                                        showSortDialog.value =
                                            false // Close the dialog after sorting
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    Text(option)
                                }
                                //divider after each option except the last one
                                if (index < sortingOptions.size - 1) {
                                    Divider(
                                        modifier = Modifier
                                            .height(2.dp)
                                            .fillMaxWidth(0.9f)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}


    fun showDeleteConfirmationDialog(
        runId: Long,
        runIdToDelete: MutableState<Long>,
        showDialog: MutableState<Boolean>
    ) {
        runIdToDelete.value = runId
        showDialog.value = true
    }


    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("MMMM d, yyyy 'at' HH:mm:ss", Locale.US)
        return formatter.format(date)
    }


    @Composable
    fun DeleteIcon(modifier: Modifier) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = Color.Red,
            modifier = modifier
        )
    }

    fun ratingToEmoji(rating: Int): String {
        return when (rating) {
            1 -> "😞"
            2 -> "😐"
            3 -> "😊"
            4 -> "😃"
            5 -> "😄"
            else -> throw IllegalArgumentException("Invalid rating value")
        }
    }




