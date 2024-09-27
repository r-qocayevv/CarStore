package com.carstore.app.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.carstore.app.ui.activities.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ViewModelScoped

@ViewModelScoped
class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    val auth  : FirebaseAuth = FirebaseAuth.getInstance()

    private var _loadingProgressBar : MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingProgressBar : LiveData<Boolean> = _loadingProgressBar

    fun signUp (fullName : String , phoneNumber: String, email : String, password : String, activity: Activity, view: View) {
        if (fullName.isNotEmpty() || phoneNumber.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()){
            _loadingProgressBar.value = true
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task ->
                _loadingProgressBar.value = false
                if (task.isSuccessful) {
                    //Start MainActivity
                    val intent = Intent(getApplication(), MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }else {
                    task.exception?.localizedMessage?.let { Snackbar.make(view, it,Toast.LENGTH_SHORT).show() }
                }
            }.addOnFailureListener{ exception ->
                exception.localizedMessage?.let { Snackbar.make(view, it,Toast.LENGTH_SHORT).show() }
            }
        }else {
            Snackbar.make(view,"Please fill in all fields",Toast.LENGTH_SHORT).show()
        }
    }
}