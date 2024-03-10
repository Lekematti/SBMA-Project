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
    private var started = false

    fun start(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager?.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
        started = true
        Log.d("StepCounter", "Start: $totalSteps")
    }
    fun getStepCount(): Int {
        return totalSteps - previousTotalSteps
    }
    fun pause() {
        sensorManager?.unregisterListener(this)
    }
    fun resume() {
        sensorManager?.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)

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
            if (started) {
                previousTotalSteps = event.values[0].toInt()
                started = false
            }
            totalSteps = event.values[0].toInt()
            Log.d("StepCounter", "Current: $totalSteps")
        }
    }
}