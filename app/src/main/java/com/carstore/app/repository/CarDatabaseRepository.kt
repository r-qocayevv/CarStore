package com.carstore.app.repository

import com.carstore.app.db.CarsDao
import com.carstore.app.db.CarsEntity
import javax.inject.Inject

class CarDatabaseRepository @Inject constructor(private val dao : CarsDao) {

    suspend fun insertData (car : CarsEntity)  {
        dao.insertData(car)
    }

    fun getAllCar () = dao.getAll()

}