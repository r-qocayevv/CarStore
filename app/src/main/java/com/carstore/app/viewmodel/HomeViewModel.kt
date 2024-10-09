package com.carstore.app.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carstore.app.models.Car
import com.carstore.app.ui.activities.AuthActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeViewModel (application: Application) : AndroidViewModel(application) {
    val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private var _allCarPost : MutableLiveData<List<Car>> = MutableLiveData()
    val allCarPost : LiveData<List<Car>> = _allCarPost

    private var _documentIdAndCarPostHashMap : MutableLiveData<HashMap<Car,String>> = MutableLiveData()
    val documentIdAndCarPostHashMap : LiveData<HashMap<Car,String>> = _documentIdAndCarPostHashMap


    fun showAllPost () {
        val documnetIDAndCarPostList = HashMap<Car,String>()
        val allCarPostList = mutableListOf<Car>()
        firestore.collection("posts").get().addOnSuccessListener {
            for (documentSnapshot in it.documents){
                val documentId = documentSnapshot.id
                val carPost = documentSnapshot.data?.let { convertMapToCarClass(it) }
                if (carPost != null){
                    documnetIDAndCarPostList[carPost] = documentId
                    allCarPostList.add(carPost)
                    _documentIdAndCarPostHashMap.value = documnetIDAndCarPostList
                    _allCarPost.value = allCarPostList
                }
            }
        }
    }

    fun logout (activity : Activity,context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Do you want to log out?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            val intent = Intent(activity, AuthActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
        alertDialog.setNegativeButton("No"){ _, _ ->
        }
        alertDialog.show()

    }

    private fun convertMapToCarClass (map : Map<String,Any>) : Car{
        val uidOfSharingUser = map["uidOfSharingUser"] as String
        val model = map["model"] as String
        val brand = map["brand"] as String
        val description = map["description"] as String
        val location = map["location"] as String
        val new = map["new"] as Boolean
        val price = map["price"] as Long
        val year = map["year"] as Long
        val image = map["image"] as List<String>

        return Car(uidOfSharingUser,model,price.toInt(),year.toInt(),brand,location,description,image,new)
    }

}