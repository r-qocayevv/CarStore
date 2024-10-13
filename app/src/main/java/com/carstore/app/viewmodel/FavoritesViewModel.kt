package com.carstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private var _carsDetailMap : MutableLiveData<List<Map<String,Any>>> = MutableLiveData()
    val carsDetailMap : LiveData<List<Map<String,Any>>> = _carsDetailMap

    private var _postIDList : MutableLiveData<List<String>> = MutableLiveData()
    val postIDList : LiveData<List<String>> = _postIDList

    private var _likedPostListIsEmpty : MutableLiveData<Boolean> = MutableLiveData()
    val likedPostListIsEmpty : LiveData<Boolean> = _likedPostListIsEmpty

    fun getLikedPostID (db : DatabaseReference,currentUserUID: String,) {
        val likedPostsID = mutableListOf<String>()
        viewModelScope.launch{
            db.child(currentUserUID).child("favorites").get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.value != null){
                    _likedPostListIsEmpty.value = false
                    for (postID in dataSnapshot.value as HashMap<*, *>){
                        likedPostsID.add(postID.key.toString())
                    }
                }else {
                    //No liked Post
                    _likedPostListIsEmpty.value = true

                }
                _postIDList.value = likedPostsID
            }
        }
    }

    fun getMapFromFirestore (firestore: FirebaseFirestore,likedPostsID : List<String>) {
        val carDetailMap = mutableListOf<Map<String,Any>>()
        likedPostsID.forEach {
            viewModelScope.launch {
                firestore.collection("posts").document(it).get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.data != null) {
                        carDetailMap.add(documentSnapshot.data!!)
                    }
                    _carsDetailMap.value = carDetailMap
                }
            }
        }

    }
}