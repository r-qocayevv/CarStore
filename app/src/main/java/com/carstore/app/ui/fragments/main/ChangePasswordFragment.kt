package com.carstore.app.ui.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.carstore.app.R
import com.carstore.app.viewmodel.ChangePasswordViewModel


class ChangePasswordFragment : Fragment() {
    val changePasswordViewModel : ChangePasswordViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

}