package com.carstore.app.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.carstore.app.R
import com.carstore.app.models.Car
import com.carstore.app.models.CarBrandsAndModels
import com.carstore.app.repository.CarBrandsAndModelsRepository
import com.carstore.app.ui.activities.MainActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SellCarViewModel @Inject constructor(private val repository : CarBrandsAndModelsRepository,application: Application)  : AndroidViewModel(application) {
    private val storageRef : FirebaseStorage = FirebaseStorage.getInstance()
    val currentsUserUID = FirebaseAuth.getInstance().currentUser?.uid
    private val firestore  = Firebase.firestore

    private var response : MutableLiveData<List<CarBrandsAndModels>> = MutableLiveData()


    private var _brandList : MutableLiveData<List<String>> = MutableLiveData()
    val brandList : LiveData<List<String>> = _brandList

    private var _modelList : MutableLiveData<List<String>> = MutableLiveData()
    val modelList : LiveData<List<String>> = _modelList

    private var _imageFirebaseUrlList : MutableLiveData<List<String>> = MutableLiveData()
    val imageFirebaseUrlList : LiveData<List<String>> = _imageFirebaseUrlList

    fun uploadImagesToFirebaseStorage(imagesList: List<String>) {
        val imageUrlList = mutableListOf<String>()
        val uploadTasks = mutableListOf<Task<Uri>>()

        imagesList.forEach { uri ->
            val uuid = UUID.randomUUID().toString()
            if (currentsUserUID != null) {
                val uploadTask = storageRef.reference.child(currentsUserUID).child(uuid).putFile(Uri.parse(uri))
                uploadTask.addOnFailureListener { exception ->
                    Toast.makeText(getApplication<Application>().applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.reference.child(currentsUserUID).child(uuid).downloadUrl
                }.addOnSuccessListener { imageUrl ->
                    imageUrlList.add(imageUrl.toString())
                }

                uploadTasks.add(urlTask)
            } else {
                Toast.makeText(getApplication<Application>().applicationContext, "User not found", Toast.LENGTH_LONG).show()
            }
        }
        Tasks.whenAllSuccess<String>(uploadTasks).addOnSuccessListener {
            _imageFirebaseUrlList.value = imageUrlList

        }.addOnFailureListener {
            Toast.makeText(getApplication<Application>().applicationContext, "Error uploading images", Toast.LENGTH_LONG).show()
        }
    }

    fun uploadCarInfoToFirestore (carInfo : Car,view : View) {
        if (currentsUserUID != null) {
            firestore.collection("posts").add(carInfo)
            Toast.makeText(getApplication<Application>().applicationContext, "Your car has been successfully shared", Toast.LENGTH_LONG).show()
            // Navigate to Home Fragment
            view.findNavController().navigate(R.id.action_sellCarFragment_to_homeFragment)

        }else {
            Toast.makeText(getApplication<Application>().applicationContext,"User not found",Toast.LENGTH_LONG).show()
        }
    }

    fun isCarNew (usage : String) : Boolean {
        if (usage == "New") {
            return true
        }else  {
            return false
        }
    }

    //Retrofit
    fun getBrands () = viewModelScope.launch {
        response.value = repository.getBrandsAndModels().body()
        val list = mutableListOf<String>()
        response.value?.let {
            for (brand in it){
                list.add(brand.brand)
            }
            _brandList.value = list
        }
    }

    fun getModels (index : Int) = viewModelScope.launch {
        val response = response.value
        _modelList.value = response?.get(index)?.models
    }

}