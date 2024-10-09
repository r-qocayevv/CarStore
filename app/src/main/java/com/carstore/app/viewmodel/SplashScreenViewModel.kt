package com.carstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ViewModelScoped

@ViewModelScoped
class SplashScreenViewModel(application : Application) : AndroidViewModel(application){
    private val _currentUserIsNull : MutableLiveData<Boolean> = MutableLiveData()
    var currentUserIsNull : LiveData<Boolean> = _currentUserIsNull

    fun checkUser (auth: FirebaseAuth)  {
        if (auth.currentUser != null) {
            auth.currentUser?.reload()?.addOnCompleteListener {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    _currentUserIsNull.value = false
                }else {
                    _currentUserIsNull.value = true
                }
            }
        }else {
            _currentUserIsNull.value = true
        }
    }


}