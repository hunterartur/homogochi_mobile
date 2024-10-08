package com.myhuholi.homogochi

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BaseActivity : AppCompatActivity(), StepCounter.StepListener {

    private lateinit var stepCounter: StepCounter

    private val dbHelper = DbHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_base)
        val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
        progressBar.setProgress(dbHelper.getStepsByUserId(1)?.currentProgressBar ?: 0, true)
        stepCounter = StepCounter(this)
        stepCounter.setStepListener(this)
        // Проверка наличия датчика и установка текстового сообщения
        if (!stepCounter.isSensorPresent) {
            Toast.makeText(this, "Step Counter Sensor is not available!", Toast.LENGTH_LONG).show()
        }
        // Проверка и запрос разрешений
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                1001
            )
        }
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleWithFixedDelay(calculateHungry(), 0, 5, TimeUnit.SECONDS)

    }

    private fun calculateHungry(): () -> Unit = {
        val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
        progressBar.setProgress(progressBar.progress - 5, true)
        calculateProgressBar(progressBar.progress)
        dbHelper.updateProgressCount(1, progressBar.progress)
    }

    override fun onResume() {
        super.onResume()
        stepCounter.startCounting()
    }

    override fun onPause() {
        super.onPause()
        stepCounter.stopCounting()
    }

    override fun onStepCountUpdated(stepCount: Int) {
        // Обновление TextView с количеством шагов
        // если ночь на дворе, то хомячок спит
        if (LocalDateTime.now().hour in 9..22) {
            val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
            val percentage: Int
            var stepsCount =
                dbHelper.getStepsByUserId(1)?.stepsCount ?: 10_000
            if (stepsCount > 0) {
                percentage =
                    progressBar.progress + (stepCount.toFloat() / stepsCount * 100).toInt()
            } else percentage = 0
            calculateProgressBar(percentage)
            progressBar.setProgress(percentage, true)
            dbHelper.updateProgressCount(1, progressBar.progress)
        } else {
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.well_fed)
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("##c0c0c0"))
        }
    }

    private fun calculateProgressBar(percentage: Int) {
        if (percentage > 70) {
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("#606e79"))
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.normal)
        } else if (percentage <= 70 && percentage > 30) {
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("#5c5b60"))
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.hungry)
        } else {
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("#9e9e9e"))
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.dying)
        }
    }
}