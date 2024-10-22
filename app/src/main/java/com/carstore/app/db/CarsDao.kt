package com.carstore.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CarsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData (car : CarsEntity)

    @Query("SELECT * from cars_table")
    fun getAll () : Flow<CarsEntity>
}