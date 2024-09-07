package com.myhuholi.homogochi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        val button = findViewById<Button>(R.id.button_ready)
        button.setOnClickListener {
            val intent = Intent(this, EnterDataActivity::class.java)
            startActivity(intent)
        }
    }
}
