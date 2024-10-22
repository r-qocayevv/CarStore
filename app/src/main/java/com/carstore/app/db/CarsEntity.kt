package com.carstore.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carstore.app.models.Car


@Entity("cars_table")
class CarsEntity(val car : List<Car>) {
    @PrimaryKey(autoGenerate = false)
    var id : Int = 0
}