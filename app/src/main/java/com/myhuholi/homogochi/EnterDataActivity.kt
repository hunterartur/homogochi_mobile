package com.myhuholi.homogochi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.myhuholi.homogochi.dto.Active
import com.myhuholi.homogochi.dto.Sex
import com.myhuholi.homogochi.dto.UserInfoRequest
import com.myhuholi.homogochi.dto.UserInfoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.UUID

class EnterDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_enter_data)

        val dataPress = findViewById<Button>(R.id.data_press)
        dataPress.setOnClickListener {

            val userId = UUID.randomUUID()
            val name = findViewById<EditText>(R.id.name)
            val age = findViewById<EditText>(R.id.age)
            val height = findViewById<EditText>(R.id.height)
            val weight = findViewById<EditText>(R.id.weight)
            val sex = findViewById<RadioGroup>(R.id.sex)
            val activity = findViewById<RadioGroup>(R.id.activity)
            val user = createUserInfoRequest(userId, name, age, height, weight, sex, activity)

//            var userInfoResponse: UserInfoResponse
//            val requestBody = RequestBody.create(
//                "application/json; charset=utf-8".toMediaType(),
//                Gson().toJson(
//                    user
//                )
//            )
//
//            val request = Request.Builder()
//                .url("http://127.0.0.1:8181/register")
//                .post(requestBody)  // Устанавливаем POST-запрос
//                .build()
//
//            // Запуск запроса с помощью enqueue
//            OkHttpClient().newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    // Обработка ошибки
//                    CoroutineScope(Dispatchers.Main).launch {
//                        e.printStackTrace()
//                        println("Request failed: ${e.message}")
//                    }
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    // Обработка ответа
//                    CoroutineScope(Dispatchers.Main).launch {
//                        if (response.isSuccessful) {
//                            val responseBody = response.body?.string()
//                            userInfoResponse = Gson().fromJson(responseBody, UserInfoResponse::class.java)
//                            println("Response: $responseBody")
//                        } else {
//                            println("Request failed with code: ${response.code}")
//                        }
//                    }
//                }
//            })
            val db = DbHelper(this, null)
            db.addUser(user)
            val intent = Intent(this, ChoiceActivity::class.java)
            startActivity(intent)

        }
    }

    private fun createUserInfoRequest(
        userId: UUID,
        name: EditText,
        age: EditText,
        height: EditText,
        weight: EditText,
        sex: RadioGroup,
        activity: RadioGroup
    ) = UserInfoRequest(
        userId,
        name.text.toString(),
        age.text.toString().toInt(),
        height.text.toString().toInt(),
        weight.text.toString().toInt(),
        Sex.fromValue(
            findViewById<RadioButton>(sex.checkedRadioButtonId).text.toString()
        ) ?: Sex.MALE,
        Active.fromValue(
            findViewById<RadioButton>(activity.checkedRadioButtonId).text.toString()
        ) ?: Active.LOW
    )
}