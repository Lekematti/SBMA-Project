package com.example.sbma_project.utils

import androidx.compose.runtime.MutableState

fun showDeleteConfirmationDialog(
    runId: Long,
    runIdToDelete: MutableState<Long>,
    showDialog: MutableState<Boolean>
) {
    runIdToDelete.value = runId
    showDialog.value = true
}