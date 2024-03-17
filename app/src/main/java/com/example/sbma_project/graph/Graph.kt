package com.example.sbma_project.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sbma_project.database.Run
import com.example.sbma_project.viewmodels.RunViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun RunHistoryGraph(runViewModel: RunViewModel) {
    val runsState = remember { mutableStateOf<List<Run>>(emptyList()) }
    val displayMode = remember { mutableStateOf(DisplayMode.DISTANCE) }

    val options = listOf(DisplayMode.DISTANCE.title, DisplayMode.TIME.title)

    LaunchedEffect(key1 = runViewModel.runs) {
        runViewModel.runs.observeForever { runs ->
            runsState.value = runs
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SegmentedControl(
                items = options,
                defaultSelectedItemIndex = displayMode.value.ordinal,
                onItemSelection = { index ->
                    displayMode.value = when (index) {
                        DisplayMode.DISTANCE.ordinal -> DisplayMode.DISTANCE
                        DisplayMode.TIME.ordinal -> DisplayMode.TIME
                        else -> DisplayMode.DISTANCE
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            RunsGraph(runsState.value, displayMode.value.title)
        }
    }
}


@Composable
fun RunsGraph(runs: List<Run>, displayMode: String) {
    val displayModeState = rememberUpdatedState(displayMode)

    if (runs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No runs in history yet")
        }
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
                    .fillMaxSize(),
                update = { lineChart ->
                    setupLineChart(lineChart, displayModeState.value)
                    lineChart.data = getLineData(runs, displayModeState.value)
                    lineChart.invalidate()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun getLineData(runs: List<Run>, displayMode: String): LineData {
    val sortedRuns = runs.sortedBy { it.createdAt }

    val values = sortedRuns.mapIndexed { index, run ->
        when (displayMode) {
            "distance" -> run.distance?.let {
                Entry(index.toFloat(), it.toFloat(), run.id)
            }

            "time" -> Entry(index.toFloat(), run.durationInMillis.toFloat(), run.id)
            else -> null
        }
    }.filterNotNull()
    val dataSet = LineDataSet(values, "Runs")
    dataSet.color = Color.Blue.toArgb()
    dataSet.valueTextColor = Color.Green.toArgb() // Id color

    // Customize the appearance of data points
    dataSet.setDrawCircles(true)
    dataSet.circleRadius = 5f
    dataSet.setCircleColor(Color.Blue.toArgb())

    // Set value formatter to display run id
    dataSet.valueFormatter = object : ValueFormatter() {
        override fun getPointLabel(entry: Entry): String {
            return entry.data.toString()
        }
    }
    return LineData(dataSet)
}

fun setupLineChart(lineChart: LineChart, displayMode: String) {

    // Customize the chart appearance
    lineChart.setDrawGridBackground(false)
    lineChart.description.isEnabled = false
    lineChart.setTouchEnabled(true)
    lineChart.isDragEnabled = true
    lineChart.setScaleEnabled(true)
    lineChart.setPinchZoom(true)
    lineChart.setBackgroundColor(Color.Black.toArgb())

    // Enable x and y axis labels
    lineChart.xAxis.isEnabled = false
    lineChart.axisLeft.isEnabled = true
    lineChart.axisRight.isEnabled = false

    // Set minimum and maximum values for y-axis (distance)
    lineChart.axisLeft.axisMinimum = 0f

    // Set minimum and maximum values for x-axis (time)
    lineChart.xAxis.axisMinimum = 0f

    // Set y axis value formatter to display distance in meters or time in seconds
    lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return when (displayMode) {
                "distance" -> value.roundToInt().toString() + "m"
                "time" -> value.roundToInt().toString() + "s"
                else -> super.getAxisLabel(value, axis)
            }
        }
    }
    // Set color of x and y axis labels to blue
    lineChart.xAxis.textColor = Color.Cyan.toArgb()
    lineChart.axisLeft.textColor = Color.Cyan.toArgb()
    lineChart.axisRight.textColor = Color.Cyan.toArgb()

    // Customize legend
    val legend: Legend = lineChart.legend
    legend.isEnabled = true
    legend.textSize = 12f
    legend.textColor = Color.Cyan.toArgb()
}

enum class DisplayMode(val title: String) {
    DISTANCE("distance"),
    TIME("time")
}