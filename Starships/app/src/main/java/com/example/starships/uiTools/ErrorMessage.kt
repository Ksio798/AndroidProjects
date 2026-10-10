package com.example.starships.uiTools

import retrofit2.HttpException
import java.io.IOException

fun Exception.toUserMessage(): String {
    return when (this) {
        is HttpException -> {
            when (code()) {
                404 -> "Данные не найдены."
                429 -> "Слишком много запросов. Попробуйте позже."
                in 500..599 -> "Ошибка сервера. Попробуйте позже."
                else -> "Ошибка HTTP ${code()}. Попробуйте ещё раз."
            }
        }

        is IOException ->
            "Не удалось подключиться к серверу. Проверьте интернет."

        else ->
            "Не удалось загрузить данные. Попробуйте ещё раз."
    }
}