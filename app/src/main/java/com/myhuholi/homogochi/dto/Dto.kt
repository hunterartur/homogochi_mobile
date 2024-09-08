package com.myhuholi.homogochi.dto

import java.util.UUID

data class UserInfoRequest(
    val uuid: UUID,
    val name: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val sexSysName: Sex,
    val activitySysName: Active
)

data class UserInfoResponse(
    val uuid: UUID,
    val name: String,
    val age: Int,
    val sexSysName: Sex,
    val sexDescription: String,
    val height: Int,
    val weight: Int,
    val activityRate: Int,
    val activityRateDescription: String,
    val stateSysName: String,
    val stateBrief: String,
    val stateDescription: String,
    val stepsCount: Int,
    val pictureBytes: String
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
    val stepsCount: Int
)