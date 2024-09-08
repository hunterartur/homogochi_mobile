package com.myhuholi.homogochi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.myhuholi.homogochi.dto.Active
import com.myhuholi.homogochi.dto.Sex
import com.myhuholi.homogochi.dto.StepRecord
import com.myhuholi.homogochi.dto.UserInfoRequest
import java.util.UUID

class DbHelper(
    val context: Context, val factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, "homogochi", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val queryCreateUsers =
            "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, userId TEXT, name TEXT, age INT, height INT, weight INT, sex TEXT, activity TEXT)"
        val queryCreateSteps =
            "CREATE TABLE IF NOT EXISTS steps (id INT PRIMARY KEY, userId INT, currentProgressBar INT, recomentedCountStep INT, FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE)"
        db!!.execSQL(queryCreateUsers)
        db.execSQL(queryCreateSteps)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addUser(user: UserInfoRequest) {
        val values = ContentValues()
        values.put("id", 1)
        values.put("userId", user.userId.toString())
        values.put("name", user.name)
        values.put("age", user.age)
        values.put("height", user.height)
        values.put("weight", user.weight)
        values.put("sex", user.sex.name)
        values.put("activity", user.activity.name)
        val db = this.writableDatabase
        db.insert("users", null, values)
    }

    fun addStep(step: StepRecord) {
        val values = ContentValues()
        values.put("id", 1)
        values.put("userId", step.userId)
        values.put("generalCountStep", step.generalCountStep)
        values.put("recommendedCountStep", step.recommendedCountStep)
        values.put("currentProgressBar", step.currentProgressBar)
        val db = this.writableDatabase
        db.insert("steps", null, values)
    }

    fun getUserById(id: Int): UserInfoRequest? {
        val db = this.readableDatabase
        val cursor = db.query(
            "users",  // Имя таблицы
            null,  // Колонки, которые нужно вернуть (null - все)
            "id = ?",  // Условие WHERE
            arrayOf(id.toString()),  // Аргументы условия WHERE
            null,  // Группировка
            null,  // Наличие
            null  // Порядок сортировки
        )

        // Проверяем, есть ли результат
        return if (cursor.moveToFirst()) {
            // Получаем данные из курсора
            val userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val age = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
            val height = cursor.getInt(cursor.getColumnIndexOrThrow("height"))
            val weight = cursor.getInt(cursor.getColumnIndexOrThrow("weight"))
            val sex = Sex.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("sex")))
            val activity =
                Active.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("activity")))

            // Закрываем курсор и возвращаем объект UserInfoRequest
            cursor.close()
            UserInfoRequest(UUID.fromString(userId), name, age, height, weight, sex, activity)
        } else {
            // Если записи не найдены
            cursor.close()
            null
        }
    }

    fun getStepsByUserId(userId: Int): StepRecord? {
        val db = this.readableDatabase
        val cursor = db.query(
            "steps",  // Имя таблицы
            null,  // Выбрать все колонки
            "userId = ?",  // Условие WHERE
            arrayOf(userId.toString()),  // Аргумент для WHERE
            null,  // Группировка
            null,  // Наличие
            null  // Порядок сортировки
        )

        val stepsList = mutableListOf<StepRecord>()

        if (cursor.moveToFirst()) {
            do {
                // Извлечение данных из курсора
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val currentCountStep =
                    cursor.getInt(cursor.getColumnIndexOrThrow("currentCountStep"))
                val generalCountStep =
                    cursor.getInt(cursor.getColumnIndexOrThrow("generalCountStep"))
                val recomentedCountStep =
                    cursor.getInt(cursor.getColumnIndexOrThrow("recomentedCountStep"))

                // Добавление данных в список шагов
                stepsList.add(
                    StepRecord(
                        id,
                        userId,
                        currentCountStep,
                        generalCountStep,
                        recomentedCountStep
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return if (stepsList.size > 0) stepsList.get(0) else null
    }

    fun updateProgressCount(userId: Int, currentProgressBar: Int) {
        val db = this.readableDatabase
        val values = ContentValues()
        values.put("currentProgressBar", currentProgressBar)
        db.update("steps", values, "userId=", arrayOf(userId.toString()))
    }

}