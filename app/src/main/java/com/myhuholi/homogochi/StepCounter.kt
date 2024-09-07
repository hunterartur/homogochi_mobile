package com.myhuholi.homogochi

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepCounter(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var isSensorPresent: Boolean = stepCounterSensor != null
        private set

    private var stepCount = 0

    private var listener: StepListener? = null

    fun startCounting() {
        if (isSensorPresent) {
            sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopCounting() {
        if (isSensorPresent) {
            sensorManager.unregisterListener(this)
        }
    }

    fun setStepListener(listener: StepListener) {
        this.listener = listener
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            stepCount = event.values[0].toInt()
            Log.d("StepCounter", "Steps: $stepCount")
            listener?.onStepCountUpdated(stepCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Не используется в данном примере
    }

    interface StepListener {
        fun onStepCountUpdated(stepCount: Int)
    }
}
