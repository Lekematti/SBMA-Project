package com.example.sbma_project.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sbma_project.database.Timer
import com.example.sbma_project.repository.TimerViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun RunHistoryGraph(timerViewModel: TimerViewModel) {
    val timersState = remember { mutableStateOf<List<Timer>>(emptyList()) }

    LaunchedEffect(key1 = timerViewModel.timers) {
        timerViewModel.timers.observeForever { timers ->
            timersState.value = timers
        }
    }

    RunsGraph(timersState.value)
}
@Composable
fun RunsGraph(timers: List<Timer>) {
    if (timers.isEmpty()) {
        Text("No runs in history yet")
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AndroidView(
                factory = { context ->
                    LineChart(context)
                },
                modifier = Modifier
                    .fillMaxSize()
            ) { lineChart ->
                setupLineChart(lineChart)
                lineChart.data = getLineData(timers)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display each run's details
            LazyColumn {
                items(timers) { timer ->
                    RunItem(timer)
                    Divider()
                }
            }
        }
    }
}
@Composable
fun RunItem(timer: Timer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Run ${timer.id}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Date: ${timer.createdAt}")
        Text("Distance: ${timer.distance} km")
        Text("Time: ${timer.durationInMillis} minutes")
        Text("Average Speed: ${timer.avgSpeed} km/h")
    }
}

fun getLineData(timers: List<Timer>): LineData {
    val values = timers.flatMapIndexed { index, run ->
        listOf(run.avgSpeed?.let { Entry(index.toFloat(), it.toFloat()) })
    }

    val dataSet = LineDataSet(values, "Average Speed vs. Run Index")
    dataSet.color = Color.Blue.toArgb()
    dataSet.valueTextColor = Color.Red.toArgb()
    return LineData(dataSet)
}

fun setupLineChart(lineChart: LineChart) {
    // Customize the chart appearance
    lineChart.setDrawGridBackground(false)
    lineChart.description.isEnabled = false
    lineChart.setTouchEnabled(true)
    lineChart.isDragEnabled = true
    lineChart.setScaleEnabled(true)
    lineChart.setPinchZoom(true)
    lineChart.setBackgroundColor(Color.White.toArgb())

    // Customize the chart legend
    val legend: Legend = lineChart.legend
    legend.isEnabled = true
    legend.textSize = 12f

    // Customize the chart description
    val description = Description()
    description.text = "Average Speed vs. Run Index"
    lineChart.description = description
}