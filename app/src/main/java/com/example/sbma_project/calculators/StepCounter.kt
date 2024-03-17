package com.example.sbma_project.calculators

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object StepCounter : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null
    private var previousTotalSteps = 0
    private var totalSteps = 0
    private var savedSteps = 0
    private var isInitialized = false

    fun start(context: Context) {
        if (!isInitialized) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            if (stepCountSensor != null) {
                sensorManager?.registerListener(
                    this,
                    stepCountSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                previousTotalSteps = 0
                isInitialized = true
                Log.d("StepCounter", "Start: $totalSteps")
            } else {
                Log.d("StepCounter", "Step counter sensor not available")
            }
        }
    }

    fun resetSavedSteps() {
        savedSteps = 0
        previousTotalSteps = 0
        totalSteps = 0
    }

    fun getStepCount(): Int {
        return totalSteps - previousTotalSteps
    }

    fun saveSteps(): Int {
        val stepsSinceLastSave = getStepCount()
        savedSteps += stepsSinceLastSave
        previousTotalSteps = totalSteps
        return savedSteps
    }

    fun pause() {
        sensorManager?.unregisterListener(this)
    }

    fun resume() {
        sensorManager?.registerListener(
            this,
            stepCountSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        previousTotalSteps = totalSteps
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle changes in sensor accuracy here if needed
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (isInitialized) {
                previousTotalSteps = event.values[0].toInt()
                isInitialized = false
            }
            totalSteps = event.values[0].toInt()
            Log.d("StepCounter", "Current: $totalSteps")
        }
    }
}

