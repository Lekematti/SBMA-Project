package com.example.sbma_project.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbma_project.R
import com.example.sbma_project.viewmodels.RunViewModel


@Composable
fun EditHeightDialog(
    showDialog: Boolean,
    onHeightEntered: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var newHeightText by remember { mutableStateOf(TextFieldValue()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Your Height") },
            text = {
                TextField(
                    value = newHeightText,
                    onValueChange = { newHeightText = it },
                    label = { Text("New Height (in meters)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val newHeight = newHeightText.text.toDoubleOrNull() ?: 0.0
                    onHeightEntered(newHeight)
                    onDismiss()
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
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

Row {

}
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

//        Column {
//            Text(
//                text = "User Height: ${runViewModel.getUserHeight()}",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 18.dp),
//                textAlign = TextAlign.Center
//            )
//
//            Button(
//                onClick = {
//                    runViewModel.setShowHeightDialog(true)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text("Edit Height")
//            }
//
//            EditHeightDialog(
//                showDialog = runViewModel.showHeightDialog,
//                onHeightEntered = { newHeight ->
//                    runViewModel.setUserHeight(newHeight)
//                },
//                onDismiss = { runViewModel.setShowHeightDialog(false) }
//            )
//        }
    }
}