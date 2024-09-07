package com.myhuholi.homogochi

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FirstActivity : AppCompatActivity() {
    private val PREFS_NAME = "MyPrefsFile"
    private val KEY_FIRST_LAUNCH = "first_launch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Проверяем, был ли это первый запуск
        val settings = getSharedPreferences(PREFS_NAME, 0)
        val firstLaunch = settings.getBoolean(KEY_FIRST_LAUNCH, true)

        if (firstLaunch) {
            // Устанавливаем флаг, что приложение больше не запускается впервые
            val editor = settings.edit()
            editor.putBoolean(KEY_FIRST_LAUNCH, false)
            editor.apply()

            // Показать активность приветствия или инструкций
            // (это можно сделать здесь или через таймер или анимацию)
            showIntro()
        } else {
            // Переход к основной активности
            goToMainActivity()
        }
    }

    private fun showIntro() {
        // Реализуйте логику отображения активности приветствия
        // После завершения, переключитесь на основную активность
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
//        finish() // Закрыть IntroActivity, чтобы пользователь не мог вернуться к ней
    }

    private fun goToMainActivity() {
        val intent = Intent(this, BaseActivity::class.java)
        startActivity(intent)
//        finish() // Закрыть IntroActivity
    }
}