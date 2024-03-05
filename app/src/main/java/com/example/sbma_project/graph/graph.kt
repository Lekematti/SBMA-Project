package com.example.sbma_project.graph

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.*
import com.github.mikephil.charting.charts.LineChart




@Composable
fun RunHistoryScreen() {
    val runs = remember { mutableStateListOf<Run>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            RunsGraph(runs)
        }
    }
}

@Composable
fun RunsGraph(runs: List<Run>) {
    if (runs.isEmpty()) {
        Text("No runs in history yet")
    } else {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            data = getLineData(runs)
        ) {
            setupLineChart(this)
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

fun getLineData(runs: List<Run>): LineData {
    val values = runs.flatMapIndexed { index, run ->
        listOf(Entry(index.toFloat(), run.avgSpeed))
    }

    val dataSet = LineDataSet(values, "Average Speed vs. Run Index")
    dataSet.color = Color.BLUE
    dataSet.valueTextColor = Color.RED

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
    lineChart.setBackgroundColor(Color.WHITE)

    // Customize the chart legend
    val legend: Legend = lineChart.legend
    legend.isEnabled = true
    legend.textSize = 12f

    // Customize the chart description
    val description = Description()
    description.text = "Average Speed vs. Run Index"
    lineChart.description = description
}

@Composable
fun AddRunButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        content = {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Run")
        },
        modifier = Modifier
            .padding(16.dp)
            .size(56.dp)
    )
}

@Composable
fun RunItem(run: Run) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Run ${run.index}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Distance: ${run.distance} km")
        Text("Time: ${run.time} minutes")
        Text("Average Speed: ${run.avgSpeed} km/h")
    }
}

data class Run(
    val index: Int,
    val distance: Float,
    val time: Int,
    val avgSpeed: Float
)

@Preview(showBackground = true)
@Composable
fun RunsGraphPreview() {
    FeatherAndroidTasksTheme {
        RunHistoryScreen()
    }
}