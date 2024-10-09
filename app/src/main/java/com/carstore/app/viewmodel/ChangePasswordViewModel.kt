package com.carstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordViewModel (application: Application) : AndroidViewModel(application){
    val auth = FirebaseAuth.getInstance()

    fun changePassword (currentPassword : String,newPassword : String, repatedPassword : String) {
        auth.currentUser?.updatePassword(currentPassword)
    }


}