package com.example.quiz.data.local

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Classe para ensinar o Room a converter tipos de dados complexos.
class Converters {

    // Converte uma lista de Strings para um JSON (String) para ser guardado.
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    // Converte um JSON (String) de volta para uma lista de Strings.
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    // Converte um Timestamp do Firebase para um Long (milissegundos) para ser guardado.
    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp): Long {
        return timestamp.toDate().time
    }

    // Converte um Long (milissegundos) de volta para um Timestamp do Firebase.
    @TypeConverter
    fun toTimestamp(time: Long): Timestamp {
        return Timestamp(java.util.Date(time))
    }
}
