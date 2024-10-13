package com.carstore.app.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carstore.app.models.Car
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var _allCarPost: MutableLiveData<List<Car>> = MutableLiveData()
    val allCarPost: LiveData<List<Car>> = _allCarPost

    private var _postIdAndCarPostHashMap: MutableLiveData<HashMap<Car, String>> = MutableLiveData()
    val postIdAndCarPostHashMap: LiveData<HashMap<Car, String>> = _postIdAndCarPostHashMap

    fun showAllPost(firestore: FirebaseFirestore) {
        val documnetIDAndCarPostList = HashMap<Car, String>()
        val allCarPostList = mutableListOf<Car>()
        viewModelScope.launch {
            firestore.collection("posts").get().addOnSuccessListener {
                for (documentSnapshot in it.documents) {
                    val documentId = documentSnapshot.id
                    val carPost = documentSnapshot.data?.let { convertMapToCarClass(it) }
                    if (carPost != null) {
                        documnetIDAndCarPostList[carPost] = documentId
                        allCarPostList.add(carPost)
                        _postIdAndCarPostHashMap.value = documnetIDAndCarPostList
                        _allCarPost.value = allCarPostList
                    }
                }
            }
        }
    }


    private fun convertMapToCarClass(map: Map<String, Any>): Car {
        val uidOfSharingUser = map["uidOfSharingUser"] as String
        val model = map["model"] as String
        val brand = map["brand"] as String
        val description = map["description"] as String
        val location = map["location"] as String
        val new = map["new"] as Boolean
        val price = map["price"] as Long
        val year = map["year"] as Long
        val image = map["image"] as List<String>

        return Car(
            uidOfSharingUser,
            model,
            price.toInt(),
            year.toInt(),
            brand,
            location,
            description,
            image,
            new
        )
    }

}