package com.carstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordViewModel(application: Application) : AndroidViewModel(application) {


    fun changePassword(
        currentPassword: String,
        newPassword: String,
        repeatedPassword: String,
        auth: FirebaseAuth
    ) {
        auth.currentUser?.updatePassword(currentPassword)
    }


}