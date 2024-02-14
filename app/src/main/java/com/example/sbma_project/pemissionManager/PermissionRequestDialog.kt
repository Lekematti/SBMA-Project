package com.example.sbma_project.pemissionManager

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun PermissionRequestDialog(
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit,
    onOkClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        title = {
            Text(
                text = "Permission Required",
            )
        },
        text = {
            Text(
                text = "To continue using this Application, you must allow location access.",
            )
        },
        modifier = modifier,
        confirmButton = {
            Button(
                onClick = onOkClick,
            ) {
                Text(
                    text = "Grant Permission",
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissClick,
                ) {
                Text(
                    text = "Close",
                )
            }
        },
    )
}
