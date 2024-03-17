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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.sbma_project.R
import com.example.sbma_project.SettingsActionListener
import com.example.sbma_project.database.Run
import com.example.sbma_project.graph.RunHistoryGraph
import com.example.sbma_project.repository.RunViewModel
import com.example.sbma_project.uiComponents.DateRangeSelector
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    val showDialog = remember { mutableStateOf(false) }
    val showSortDialog = remember { mutableStateOf(false) }
    val runIdToDelete = remember { mutableLongStateOf(-1L) }

    // Remember the last selected sorting option
    var selectedSortOption by remember { mutableStateOf(sortingOptions.first()) }
    val showInfo = remember { mutableStateOf(false) }

    val showDateRangeSelector =
        remember { mutableStateOf(false) } // State for showing DateRangeSelector

    var selectedStartDate by remember { mutableStateOf<Date?>(null) }
    var selectedEndDate by remember { mutableStateOf<Date?>(null) }

    // Function to filter runs based on selected date range
    fun filterRunsByDate(startDate: Date?, endDate: Date?) {
        selectedStartDate = startDate
        selectedEndDate = endDate

        // Adjusting the time components of the selected dates
        val adjustedStartDate = startDate?.let { adjustTimeToStartOfDay(it) }
        val adjustedEndDate = endDate?.let { adjustTimeToEndOfDay(it) }

        // filtering based on adjusted dates
        val filteredRuns = if (adjustedStartDate != null && adjustedEndDate != null) {
            runViewModel.runs.value?.filter { run ->
                run.createdAt in adjustedStartDate..adjustedEndDate
            }
        } else {
            runViewModel.runs.value
        }
        if (filteredRuns != null) {
            runsState.value = filteredRuns
        }
    }

    fun clearDateRange() {
        selectedStartDate = null
        selectedEndDate = null
        runsState.value = runViewModel.runs.value ?: emptyList()
    }

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
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                //Graphs
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(300.dp)
                        .width(400.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        RunHistoryGraph(runViewModel = runViewModel)
                    }
                }
                Text(
                    text = "Click here for more info",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp)
                        .clickable {
                            showInfo.value = true
                        }
                )
            }
            if (showInfo.value) {
                AlertDialog(
                    onDismissRequest = { showInfo.value = false },
                    title = { Text("Graph Info", style = MaterialTheme.typography.titleSmall) },
                    text = {
                        Text(
                            "This graph shows the progression of your runs. The lower your time and the higher your distance the better.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showInfo.value = false }) {
                            Text("Close", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                )
            }

            // DateRangeSelector
            if (showDateRangeSelector.value) {
                DateRangeSelector(
                    onDismiss = { showDateRangeSelector.value = false },
                    onDateRangeSelected = { startDate, endDate ->
                        filterRunsByDate(
                            startDate,
                            endDate
                        )
                        showDateRangeSelector.value = false
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedEndDate != null && selectedStartDate != null) {
                    // Row for the selected date range and close button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //selected date range
                        Text(
                            text = buildString {
                                selectedStartDate?.let { startDate ->
                                    selectedEndDate?.let { endDate ->
                                        append(formatDateRange(startDate))
                                        append(" - ")
                                        append(formatDateRange(endDate))
                                    }
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        // reset date range button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                        ) {
                            IconButton(
                                onClick = { clearDateRange() },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Clear Date Range",
                                )
                            }
                        }
                    }
                } else {
                    // Text Button to show DateRangeSelector
                    TextButton(
                        onClick = { showDateRangeSelector.value = true }
                    ) {
                        Text("Select Dates", style = MaterialTheme.typography.titleSmall)
                    }
                }
                // Button to show the sort dialog
                Button(
                    onClick = { showSortDialog.value = true },
                    modifier = Modifier.padding(start = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Sort By", style = MaterialTheme.typography.titleSmall)

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.sort_24px),
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
                                    style = MaterialTheme.typography.titleSmall,
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
                                    DeleteIcon(Modifier.size(48.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Duration: ${runs.durationInMillis}s",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Distance: ${runs.distance}m",
                                style = MaterialTheme.typography.bodySmall
                            )
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
            }
        }

        // delete dialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Confirm Deletion", style = MaterialTheme.typography.titleSmall) },
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
                            showDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Confirm", style = MaterialTheme.typography.titleSmall)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog.value = false },
                        colors = ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.titleSmall)
                    }
                }
            )
        }
        // Sorting dialog
        if (showSortDialog.value) {
            AlertDialog(
                onDismissRequest = { showSortDialog.value = false },
                title = { Text("Sort By", style = MaterialTheme.typography.titleSmall) },
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
                                    showSortDialog.value = false // Close the dialog after sorting
                                },
                                colors = ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(option, style = MaterialTheme.typography.bodySmall)
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

fun formatDateRange(date: Date): String {
    val formatter = SimpleDateFormat("MM/dd/yy", Locale.US)
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

fun adjustTimeToStartOfDay(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

fun adjustTimeToEndOfDay(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.time
}



