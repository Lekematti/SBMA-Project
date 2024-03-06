package com.example.sbma_project.graph

import android.util.Log
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
import com.example.sbma_project.database.Run
import com.example.sbma_project.repository.RunViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun RunHistoryGraph(runViewModel: RunViewModel) {
    val runsState = remember { mutableStateOf<List<Run>>(emptyList()) }

    LaunchedEffect(key1 = runViewModel.runs) {
        runViewModel.runs.observeForever { runs ->
            runsState.value = runs
        }
    }
    RunsGraph(runsState.value)
}
@Composable
fun RunsGraph(runs: List<Run>) {
    if (runs.isEmpty()) {
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
                lineChart.data = getLineData(runs)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display each run's details
            LazyColumn {
                items(runs) { run ->
                    RunItem(run)
                    Divider()
                }
            }
        }
    }
}
@Composable
fun RunItem(run: Run) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Run ${run.id}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Date: ${run.createdAt}")
        Text("Distance: ${run.distance} m")
        Text("Time: ${run.durationInMillis} seconds")
        Text("Speed: ${run.speed} km/h")
    }
}

fun getLineData(runs: List<Run>): LineData {
    var cumulativeDistance = 0f
    var cumulativeTime = 0f

    val values = runs.mapNotNull { run ->
        run.distance?.let {
            cumulativeDistance += it
            cumulativeTime += run.durationInMillis / 1000f // convert milliseconds to seconds
            Log.d("Stats", "Run ${run.id} - Distance: $it, Cumulative Distance: $cumulativeDistance, Time: ${run.durationInMillis / 1000} seconds, Cumulative Time: $cumulativeTime seconds")
            Entry(cumulativeDistance, cumulativeTime)
        }
    }

    val dataSet = LineDataSet(values, "Cumulative Time vs. Cumulative Distance")
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