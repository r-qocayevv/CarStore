package com.carstore.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database([CarsEntity::class], version = 1)
@TypeConverters(CarsTypeConverter::class)
abstract class CarsDatabase : RoomDatabase() {

    abstract fun carsDao() : CarsDao

}