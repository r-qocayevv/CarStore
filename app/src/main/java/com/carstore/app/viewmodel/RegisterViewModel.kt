package com.carstore.app.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carstore.app.models.User
import com.carstore.app.ui.activities.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch

@ViewModelScoped
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private var _loadingProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingProgressBar: LiveData<Boolean> = _loadingProgressBar

    fun signUp(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        activity: Activity,
        view: View,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ) {
        if (fullName.isNotEmpty() || phoneNumber.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
            _loadingProgressBar.value = true
            viewModelScope.launch {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    _loadingProgressBar.value = false
                    if (task.isSuccessful) {
                        //Start MainActivity
                        addUserInfoToDB(auth, fullName, email, phoneNumber, db)
                        val intent = Intent(getApplication(), MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    } else {
                        task.exception?.localizedMessage?.let {
                            Snackbar.make(
                                view,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.addOnFailureListener { exception ->
                    exception.localizedMessage?.let {
                        Snackbar.make(view, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Snackbar.make(view, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun addUserInfoToDB(
        auth: FirebaseAuth,
        fullName: String,
        emailAddress: String,
        phoneNumber: String,
        db: FirebaseDatabase
    ) {
        db.getReference(auth.uid!!).setValue(User(fullName, emailAddress, phoneNumber))
    }

}