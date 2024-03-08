package com.example.sbma_project.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbma_project.R


@Composable
fun Info() {
    val appDescription = """
        RunAlyzer is an application meant to give important data for runners or walkers who want to see information about their runs/walks after or during them.
    """.trimIndent()

    val developerNames = """
                         Developers:
                         
                         - Leo Koskimäki
                         
                         - Atte Kilpeläinen
                         
                         - Petrus Kajastie
                         
                         - Bibek Shrestha
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
                .padding(bottom = 16.dp)
        )

        Text(
            text = developerNames,textAlign = TextAlign.Center, fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}