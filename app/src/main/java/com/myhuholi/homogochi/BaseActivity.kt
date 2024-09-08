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
        scheduler.scheduleWithFixedDelay({
            val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
            progressBar.progress += 5
            if (progressBar.progress > 70) {
                val layout = findViewById<LinearLayout>(R.id.active_base)
                layout.setBackgroundColor(Color.parseColor("#9e9e9e"))
                findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.dying)
            } else if (progressBar.progress <= 70 && progressBar.progress > 30) {
                val layout = findViewById<LinearLayout>(R.id.active_base)
                layout.setBackgroundColor(Color.parseColor("#5c5b60"))
                findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.hungry)
            } else {
                val layout = findViewById<LinearLayout>(R.id.active_base)
                layout.setBackgroundColor(Color.parseColor("#606e79"))
                findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.normal)
            }
//            dbHelper.updateProgressCount(1, progressBar.progress)
        }, 0, 5, TimeUnit.SECONDS)

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
//        if (LocalDateTime.now().hour in 9..22) {
        val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
        val percentage = try {
            val progressBarLive = findViewById<ProgressBar>(R.id.live_progress)
            var currentCountStepSession = dbHelper.getStepsByUserId(1)?.currentProgressBar ?: 0
            var generalCountStep = dbHelper.getStepsByUserId(1)?.generalCountStep ?: 0
            var recomentedCountStep =
                dbHelper.getStepsByUserId(1)?.recommendedCountStep ?: 10_000
            if (recomentedCountStep > 0) {
                progressBar.progress - (stepCount.toFloat() / recomentedCountStep * 100).toInt()
            } else 0
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
        if (percentage > 70) {
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("#9e9e9e"))
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.dying)
        } else if (percentage <= 70 && percentage > 30) {
            val layout = findViewById<LinearLayout>(R.id.active_base)
            layout.setBackgroundColor(Color.parseColor("#5c5b60"))
            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.hungry)
        } else {
            try {
                val layout = findViewById<LinearLayout>(R.id.active_base)
                layout.setBackgroundColor(Color.parseColor("#606e79"))
                findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.normal)
            } catch (e: Exception) {
                TODO("Not yet implemented")
            }
        }
        progressBar.setProgress(percentage, true)
//        dbHelper.updateProgressCount(1, progressBar.progress)
//        } else {
//            findViewById<ImageView>(R.id.base_image).setImageResource(R.drawable.well_fed)
//        val layout = findViewById<LinearLayout>(R.id.active_base)
//        layout.setBackgroundColor(Color.parseColor("##c0c0c0"))
//        }
    }
}