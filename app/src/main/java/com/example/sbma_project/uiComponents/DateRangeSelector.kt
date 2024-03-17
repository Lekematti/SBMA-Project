package com.example.sbma_project.uiComponents


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDate: Date, endDate: Date) -> Unit,
    modifier: Modifier
) {
    val state = rememberDateRangePickerState()

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Close Button")
            }
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Date(state.selectedStartDateMillis ?: 0),
                        Date(state.selectedEndDateMillis ?: 0)
                    )
                    onDismiss()
                },
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Save")
            }
        }
        DateRangePicker(
            state = state,
            modifier = Modifier.fillMaxSize(),
            headline = { CustomDateRangePickerHeadline(state = state) } // Custom headline composable
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePickerHeadline(state: DateRangePickerState) {
    Text(
        text = buildString {
            if (state.selectedStartDateMillis != null && state.selectedEndDateMillis != null) {
                append(formatRange(state.selectedStartDateMillis))
                append(" - ")
                append(formatRange(state.selectedEndDateMillis))
            } else {
                append("Date Range")
            }
        },
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
    )
}


@Composable
fun formatRange(date: Long?): String {
    date ?: return "Select Date Range"
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(date))
}
