package com.carstore.app.repository

import com.carstore.app.api.CarBrandsAndModelsAPI
import com.carstore.app.models.CarBrandsAndModels
import retrofit2.Response
import javax.inject.Inject


class CarBrandsAndModelsRepository @Inject constructor(private val carBrandsAndModelsApi : CarBrandsAndModelsAPI) {

    suspend fun getBrandsAndModels () : Response<List<CarBrandsAndModels>> {
        return carBrandsAndModelsApi.getBrandsAndModels()
    }

}