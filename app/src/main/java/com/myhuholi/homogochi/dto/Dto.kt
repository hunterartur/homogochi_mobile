package com.myhuholi.homogochi.dto

import java.util.UUID

data class UserInfoRequest(
    val userId: UUID,
    val name: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val sex: Sex,
    val activity: Active
)

data class UserInfoResponse(
    val recommendedStepCount: Int
)

enum class Sex(val value: String) {
    MALE("Мужской"),
    FEMALE("Женский");
    companion object {
        // Метод для получения элемента по значению
        fun fromValue(value: String): Sex? {
            return entries.find { it.value == value }
        }
    }
}

enum class Active(val value: String) {
    LOW("Низкий"),
    NORMAL("Средний"),
    HIGHT("Высокий");
    companion object {
        // Метод для получения элемента по значению
        fun fromValue(value: String): Active? {
            return entries.find { it.value == value }
        }
    }
}

data class StepRecord(
    val id: Int,
    val userId: Int,
    val currentProgressBar: Int,
    val  generalCountStep: Int,
    val recommendedCountStep: Int
)