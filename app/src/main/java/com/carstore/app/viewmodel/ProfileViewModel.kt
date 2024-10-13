package com.carstore.app.viewmodel

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carstore.app.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var toast: Toast? = null

    private var _progressBarLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val progressBarLoading: LiveData<Boolean> = _progressBarLoading

    private var _userInfoFromFirebaseDB: MutableLiveData<HashMap<*, *>> = MutableLiveData()
    val userInfoFromFirebaseDB: LiveData<HashMap<*, *>> = _userInfoFromFirebaseDB

    fun userInfo(view: View, db: FirebaseDatabase, auth: FirebaseAuth) {
        _progressBarLoading.value = true
        db.getReference(auth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _progressBarLoading.value = false
                snapshot.value?.let {
                    _userInfoFromFirebaseDB.value = snapshot.value as HashMap<*, *>
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(view, error.message, Snackbar.LENGTH_LONG).show()
            }

        })

    }

    fun editUserInfoInDB(
        view: View,
        auth: FirebaseAuth,
        db: FirebaseDatabase,
        currentEmail: String,
        currentPhoneNumber: String,
        currentFullName: String,
        newFullName: String,
        newPhoneNumber: String,
        newEmailAddress: String
    ) {
        if (newFullName.isNotEmpty() && newPhoneNumber.isNotEmpty() && newEmailAddress.isNotEmpty()) {
            if (newEmailAddress != currentEmail) {
                viewModelScope.launch {
                    auth.currentUser?.verifyBeforeUpdateEmail(newEmailAddress)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Snackbar.make(
                                    view,
                                    "Please log in with the new email after verifying the email",
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                    .setAction("OK") {}.show()
                            } else {
                                task.exception?.localizedMessage?.let {
                                    Snackbar.make(view, it, Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK") {}.show()
                                }
                            }
                        }?.addOnFailureListener { exception ->
                        exception.localizedMessage?.let {
                            Snackbar.make(view, it, Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK") {}.show()
                        }
                    }
                }
            }
            if (newFullName != currentFullName || newPhoneNumber != currentPhoneNumber) {
                addNewInfoInDB(newFullName, newPhoneNumber, newEmailAddress, auth, db)
            }
        } else {
            toast?.cancel()
            toast = Toast.makeText(
                getApplication(),
                "Please fill in all the blanks",
                Toast.LENGTH_SHORT
            )
            toast?.show()
        }
    }

    fun addNewInfoInDB(
        fullName: String,
        phoneNumber: String,
        emailAddress: String,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ) {
        viewModelScope.launch {
            db.getReference(auth.uid!!).setValue(User(fullName, emailAddress, phoneNumber))
                .addOnSuccessListener {
                    Toast.makeText(
                        getApplication(),
                        "Your information has been updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { exception ->
                Toast.makeText(getApplication(), exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun checkUserEmail(
        emailAddressFromDB: String,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ): String {
        val currentUserEmail = auth.currentUser?.email
        if (emailAddressFromDB != currentUserEmail) {
            db.getReference(auth.uid!!).child("emailAddress").setValue(currentUserEmail)
            return currentUserEmail!!
        } else {
            return emailAddressFromDB
        }
    }
}