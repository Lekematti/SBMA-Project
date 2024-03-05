package com.example.sbma_project.views


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.sbma_project.database.Timer
import com.example.sbma_project.repository.TimerViewModel
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.SimpleDateFormat
import java.util.Date
import org.w3c.dom.Text




@Composable
fun History(timerViewModel: TimerViewModel) {
    val timersState = remember { mutableStateOf<List<Timer>>(emptyList()) }
    var showDetailView by remember { mutableStateOf(false) }
    var selectedHistoryData by remember { mutableStateOf("") }


    LaunchedEffect(key1 = timerViewModel.timers) {
        timerViewModel.timers.observeForever { timers ->
            timersState.value = timers
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
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Graph placeholder", modifier = Modifier.align(Alignment.TopCenter))
                }
            }
            // run stats items
            LazyColumn {
                items(timersState.value) { timer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable {
                                selectedHistoryData = "This has history data in it for item $timer"
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
                                    text = "ID: ${timer.id}",
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { timerViewModel.deleteTimerById(timer.id) }) {
                                    DeleteIcon()
                                }
                            }


                            Spacer(modifier = Modifier.height(8.dp))


                            Text(text = "Duration: ${timer.durationInMillis}s")
                            Text(text = "route LatLng size: ${timer.routePath?.size}")
                            Text(text = "Created at: ${formatDate(timer.createdAt)}")
                        }
                    }
                }
            }


        } else {
            // Detailed view
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
    }
}




private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(date)
}


@Composable
fun DeleteIcon() {
    Icon(
        imageVector = Icons.Filled.Delete,
        contentDescription = "Delete",
        tint = Color.Red
    )
}



