package com.weatherapp.data.local.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toDoubleList(value: String?): List<Double>? {
        return value?.let {
            val type = object : TypeToken<List<Double>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String?): Long? {
        return value?.toLongOrNull()
    }
}