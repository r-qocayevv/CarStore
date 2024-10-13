package com.carstore.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch

class CarDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private var _likeStatus : MutableLiveData<Boolean> = MutableLiveData()
    val likeStatus :LiveData<Boolean> = _likeStatus

    private var _loadingLikeStatus : MutableLiveData<Boolean> = MutableLiveData()
    val loadingLikeStatus : LiveData<Boolean> = _loadingLikeStatus

    private var _phoneNumber : MutableLiveData<String> = MutableLiveData()
    val phoneNumber : LiveData<String> = _phoneNumber

    fun getLikeStatus(database: DatabaseReference, currentUserUID: String, postID: String) {
        _loadingLikeStatus.value = true
        viewModelScope.launch {
            database.child(currentUserUID).child("favorites").child(postID).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.value == null) {
                        _likeStatus.value = false
                    } else {
                        _likeStatus.value = true
                    }
                    _loadingLikeStatus.value = false
                }
        }
    }

    fun deleteOrAddFavorite (likeStatus : Boolean, database : DatabaseReference,auth : FirebaseAuth,postID : String){
        if (likeStatus) {
            database.child(auth.currentUser!!.uid).child("favorites").child(postID).removeValue()
        }else {
            database.child(auth.currentUser!!.uid).child("favorites").child(postID).setValue(true)
        }
    }

    fun getUserPhoneNumber(uid : String?,db: DatabaseReference) {
        if (uid != null) {
            viewModelScope.launch {
                db.child(uid).child("phoneNumber").get().addOnSuccessListener {
                    _phoneNumber.value = it.value.toString()
                }.addOnFailureListener { exception ->
                    _phoneNumber.value = ""
                    Toast.makeText(getApplication(),exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}