package com.example.sbma_project.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sbma_project.R
import com.example.sbma_project.repository.RunViewModel

@Composable
fun DetailedHistoryView(runViewModel: RunViewModel, runId: Long, onBack: () -> Unit) {
    val runDetails = runViewModel.getRunById(runId).observeAsState()

    // Render detailed view
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Back button
        Text(
            text = "Back",
            modifier = Modifier
                .clickable { onBack() }
                .padding(top = 16.dp)
                .background(color = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        runDetails.value?.let { run ->
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "${formatDate(run.createdAt)}",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                )
                Card {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.timer_24px),
                                title = "Duration",
                                description = "${run.durationInMillis}"
                            )
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.route_24px),
                                title = "Distance",
                                description = "${run.routePath?.size}"
                            )
                        }
                        Row {
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.footprint_24px),
                                title = "Steps",
                                description = "${run.durationInMillis}"
                            )
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.speed_24px),
                                title = "Average Speed",
                                description = "${run.avgSpeed}"
                            )
                        }
                        Row {
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.ecg_heart_24px),
                                title = "Heart Beats",
                                description = "${run.durationInMillis}"
                            )
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.steps_24px),
                                title = "Average Pace",
                                description = "${run.speedTimestamps?.size}"
                            )
                        }
                        Row {
                            run.rating?.let { "${ratingToEmoji(it)}" }?.let {
                                DetailCardItem(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(0.5f),
                                    icon = painterResource(id = R.drawable.relax_24px),
                                    title = "Post Run Feeling",
                                    description = it
                                )
                            }
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(0.5f),
                                icon = painterResource(id = R.drawable.steps_24px),
                                title = "Average Pace",
                                description = "${run.speedList?.size}"
                            )
                        }
                        run.notes?.let {
                            DetailCardItem(
                                modifier = Modifier
                                    .padding(16.dp),
                                icon = painterResource(id = R.drawable.description_24px),
                                title = "Notes",
                                description = it
                            )
                        }
                    }
                }

            }
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
                .padding(end = 8.dp)
                .size(32.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}



