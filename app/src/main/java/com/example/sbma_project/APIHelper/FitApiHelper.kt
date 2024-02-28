package com.example.sbma_project.APIHelper

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit
import com.google.android.gms.fitness.FitnessOptions.builder
import com.google.android.gms.fitness.*


class FitApiHelper(private val activity: Activity) {

    private val fitnessOptions: FitnessOptions by lazy {
        builder()
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(fitnessOptions.scope)
            .build()
        GoogleSignIn.getClient(activity, options)
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            // Signed in successfully, you can now access Google Fit API
            // Call a function to request and retrieve heart rate data
        } catch (e: ApiException) {
            // Handle sign-in failure (e.g., user denied access)
        }
    }

    fun readHeartRateData() {
        val end = System.currentTimeMillis()
        val start = end - 86400000 // 1 day in milliseconds

        val response = GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Fitness.getHistoryClient(activity, it)
                .readData(
                    DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .setTimeRange(start, end, TimeUnit.MILLISECONDS)
                        .build()
                )
        }
        // Handle the response to get heart rate data
        response?.addOnSuccessListener {
            // Use dataReadResult to access heart rate data
            // dataReadResult.getDataSets() //will contain the heart rate values
        }
    }

    companion object {
        const val REQUEST_CODE_SIGN_IN = 123
    }
}
