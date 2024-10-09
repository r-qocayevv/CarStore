package com.carstore.app.models


import com.google.gson.annotations.SerializedName

data class CarBrandsAndModels(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("models")
    val models: List<String>
)