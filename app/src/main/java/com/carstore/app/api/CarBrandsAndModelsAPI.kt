package com.carstore.app.api

import com.carstore.app.models.CarBrandsAndModels
import retrofit2.Response
import retrofit2.http.GET

interface CarBrandsAndModelsAPI {

    @GET("/r-qocayevv/CarStore/refs/heads/main/all-car-brands-and-model.json")

    suspend fun getBrandsAndModels () : Response<List<CarBrandsAndModels>>

}