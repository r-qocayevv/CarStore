package com.carstore.app.db

import androidx.room.TypeConverter
import com.carstore.app.models.Car
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CarsTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun carToString (car: List<Car>) : String{
        return gson.toJson(car)
    }



    @TypeConverter
    fun stringToCar(string: String) : List<Car> {
        val listType = object : TypeToken<List<Car>>() {}.type
        return gson.fromJson(string, listType)
    }


    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(string: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(string, type)
    }

}