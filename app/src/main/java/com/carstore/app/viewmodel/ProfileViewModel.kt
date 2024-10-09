package com.carstore.app.viewmodel

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carstore.app.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.database
    private var toast : Toast?  = null

    private var _progressBarLoading : MutableLiveData<Boolean> = MutableLiveData(true)
    val progressBarLoading : LiveData<Boolean> = _progressBarLoading

    private var _uid : MutableLiveData<String> = MutableLiveData()
    val uid : LiveData<String> = _uid

    private var _fullName : MutableLiveData<String> = MutableLiveData()
    val fullName : LiveData<String> = _fullName

    private var _emailAddress : MutableLiveData<String> = MutableLiveData()
    val emailAddress : LiveData<String> = _emailAddress

    private var _phoneNumber : MutableLiveData<String> = MutableLiveData()
    val phoneNumber : LiveData<String> = _phoneNumber

    fun userInfo (view : View) {
        _progressBarLoading.value = true
        db.getReference(auth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _progressBarLoading.value = false
                snapshot.value?.let {
                    val value = snapshot.value as HashMap<*, *>
                    _uid.value = auth.uid
                    _phoneNumber.value = value["phoneNumber"]?.toString() ?: ""
                    _emailAddress.value = checkUserEmail(value["emailAddress"]?.toString() ?: "",auth)
                    _fullName.value = value["fullName"]?.toString() ?: ""
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(view,error.message,Snackbar.LENGTH_LONG).show()
            }

        })

    }

    fun editUserInfoInDB (view: View, fullName:String, phoneNumber : String, emailAddress : String) {
        if (fullName.isNotEmpty() && phoneNumber.isNotEmpty() && emailAddress.isNotEmpty()){
            if (emailAddress != _emailAddress.value){
                auth.currentUser?.verifyBeforeUpdateEmail(emailAddress)?.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Snackbar.make(view, "Please log in with the new email after verifying the email",Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK") {}.show()
                    }else {
                        task.exception?.localizedMessage?.let {
                            Snackbar.make(view, it,Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK") {}.show()
                        }
                    }
                }?.addOnFailureListener { exception ->
                    exception.localizedMessage?.let {
                        Snackbar.make(view, it,Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK") {}.show()
                    }
                }
            }
            if (fullName != _fullName.value || phoneNumber != _phoneNumber.value){
                addNewInfoInDB(fullName,phoneNumber,emailAddress)
            }
        }else {
            toast?.cancel()
            toast = Toast.makeText(getApplication(),"Please fill in all the blanks",Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    fun addNewInfoInDB (fullName:String, phoneNumber : String, emailAddress : String) {
        db.getReference(auth.uid!!).setValue(User(fullName,emailAddress,phoneNumber)).addOnSuccessListener {
            Toast.makeText(getApplication(),"Your information has been updated successfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {exception ->
            Toast.makeText(getApplication(),exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

    fun checkUserEmail (emailAddressFromDB : String, auth : FirebaseAuth) : String{
        val currentUserEmail = auth.currentUser?.email
        if (emailAddressFromDB != currentUserEmail){
            db.getReference(auth.uid!!).child("emailAddress").setValue(currentUserEmail)
            return currentUserEmail!!
        }else {
            return emailAddressFromDB
        }
    }
}