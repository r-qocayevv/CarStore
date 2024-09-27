package com.carstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ViewModelScoped

@ViewModelScoped
class SplashScreenViewModel(application : Application) : AndroidViewModel(application){

    fun checkUser (auth: FirebaseAuth) : Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return true
        }else {
            return false
        }

    }


}