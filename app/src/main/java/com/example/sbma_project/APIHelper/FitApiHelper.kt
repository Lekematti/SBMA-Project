package com.example.sbma_project.APIHelper

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class FitApiHelper(private val activity: Activity) {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .build()

    private val account: GoogleSignInAccount by lazy {
        GoogleSignIn.getAccountForExtension(activity, fitnessOptions)
    }

    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1 // You need to define this

    init {
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            accessGoogleFit()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> accessGoogleFit()
                    else -> {
                        // Result wasn't from Google Fit
                    }
                }
            }
            else -> {
                // Permission not granted
                Log.d("Permission", "No permission")
            }
        }
    }

    suspend fun readHeartRateData(): Float? {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val responseTask = GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Fitness.getHistoryClient(activity, it)
                .readData(
                    DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_HEART_RATE_BPM)
                        .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .build()
                )
        }

        return try {
            responseTask?.addOnSuccessListener { response ->
                // Use response data here
                val dataSet = response.getDataSet(DataType.TYPE_HEART_RATE_BPM)
                val dataPoint = dataSet.dataPoints.lastOrNull()
                dataPoint?.getValue(Field.FIELD_BPM)?.asFloat()
            }?.addOnFailureListener { e ->
                Log.e(TAG, "Error reading heart rate data", e)
                null
            }
            null // Return null as the result is handled asynchronously
        } catch (e: Exception) {
            Log.e(TAG, "Error reading heart rate data", e)
            null
        }
    }


    private fun accessGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        Fitness.getHistoryClient(activity, account)
            .readData(readRequest)
            .addOnSuccessListener {
                // Use response data here
                Log.i(TAG, "OnSuccess()")
            }
            .addOnFailureListener { e -> Log.d(TAG, "OnFailure()", e) }
    }

    companion object {
        const val TAG = "FitApiHelper"
    }
}
