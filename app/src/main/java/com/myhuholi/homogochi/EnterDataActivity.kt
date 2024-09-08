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
import com.myhuholi.homogochi.dto.StepRecord
import com.myhuholi.homogochi.dto.UserInfoRequest
import com.myhuholi.homogochi.dto.UserInfoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

import java.util.UUID

class EnterDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_enter_data)

        val dataPress = findViewById<Button>(R.id.data_press)
        dataPress.setOnClickListener {
            dataPressClickListener()
        }
    }

    private fun dataPressClickListener() {
        val db = DbHelper(this, null)
        val userId = UUID.randomUUID()
        val name = findViewById<EditText>(R.id.name)
        val age = findViewById<EditText>(R.id.age)
        val height = findViewById<EditText>(R.id.height)
        val weight = findViewById<EditText>(R.id.weight)
        val sex = findViewById<RadioGroup>(R.id.sex)
        val activity = findViewById<RadioGroup>(R.id.activity)
        val user = createUserInfoRequest(userId, name, age, height, weight, sex, activity)
        val request = createRequest(user)
        OkHttpClient().newCall(request).enqueue(handlerRequest(db))
        db.addUser(user)
        val intent = Intent(this, ChoiceActivity::class.java)
        startActivity(intent)
    }

    private fun handlerRequest(db: DbHelper) = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // Обработка ошибки
            CoroutineScope(Dispatchers.Main).launch {
                e.printStackTrace()
                println("Request failed: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            // Обработка ответа
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    val responseBody = try {
                        response.body?.string()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        TODO("Not yet implemented")
                    }
                    var userInfoResponse =
                        Gson().fromJson(responseBody, UserInfoResponse::class.java)
                    db.addStep(
                        StepRecord(
                            1,
                            1,
                            0,
                            0,
                            userInfoResponse.stepsCount
                        )
                    )
                } else {
                    println("Request failed with code: ${response.code}")
                }
            }
        }
    }

    private fun createRequest(user: UserInfoRequest): Request {
        val request = Request.Builder()
            .url("http://192.168.192.40:8181/register")
            .post(
                Gson().toJson(
                    user
                ).toRequestBody("application/json".toMediaType())
            )  // Устанавливаем POST-запрос
            .build()
        return request
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