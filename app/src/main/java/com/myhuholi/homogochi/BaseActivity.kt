package com.myhuholi.homogochi

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.properties.Delegates

class BaseActivity : AppCompatActivity(), StepCounter.StepListener {

    private lateinit var stepCounter: StepCounter
    private val dbHelper = DbHelper(this, null)
//    private var currentCountStepSession = dbHelper.getStepsByUserId(1).currentCountStep
//    private var generalCountStep = dbHelper.getStepsByUserId(1).generalCountStep
//    private var recomentedCountStep = dbHelper.getStepsByUserId(1).recomentedCountStep

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_base)
        val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
        progressBar.setProgress(100, true)
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
        val progressBar = findViewById<ProgressBar>(R.id.hungry_progress)
        val percentage = try {
            val progressBarLive = findViewById<ProgressBar>(R.id.live_progress)
            var currentCountStepSession = dbHelper.getStepsByUserId(1)?.currentCountStep ?: 0
            var generalCountStep = dbHelper.getStepsByUserId(1)?.generalCountStep ?: 0
            var recomentedCountStep = dbHelper.getStepsByUserId(1)?.recomentedCountStep ?: 10_000
            if (recomentedCountStep > 0) {
                (stepCount.toFloat() / recomentedCountStep * 100).toInt()
            } else 0
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
        progressBar.setProgress(percentage, true)
    }
}