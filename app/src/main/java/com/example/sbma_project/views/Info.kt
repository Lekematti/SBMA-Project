package com.example.sbma_project.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbma_project.R
import com.example.sbma_project.repository.RunViewModel

@Composable
fun Info(runViewModel: RunViewModel) {
    val appDescription = """
        RunAlyzer is an application meant to give important data for runners or walkers who want to see information about their runs/walks after or during them.
    """.trimIndent()

    val developerNames = """
                        Developers:                      
○ Leo Koskimäki
○ Atte Kilpeläinen
○ Petrus Kajastie
○ Bibek Shrestha
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.runalyzer),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = appDescription,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
        )

        Text(
            text = developerNames, textAlign = TextAlign.Center, fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .padding(bottom = 18.dp)
        )
    }
}