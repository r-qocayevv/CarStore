package com.carstore.app.models

import java.io.Serializable

class Car (val uidOfSharingUser : String,
           val model : String,
           val price : Int,
           val year : Int,
           val brand : String,
           val location : String,
           val description : String,
           val image : List<String>,
           val isNew : Boolean) : Serializable