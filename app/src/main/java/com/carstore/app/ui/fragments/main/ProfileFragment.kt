package com.carstore.app.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carstore.app.R
import com.carstore.app.databinding.FragmentProfileBinding
import com.carstore.app.ui.activities.AuthActivity
import com.carstore.app.viewmodel.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    val binding get() = _binding!!
    val profileViewModel : ProfileViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.orange)

        profileViewModel.userInfo(view)

        profileViewModel.progressBarLoading.observe(viewLifecycleOwner) { progressBarLoading->
            visibilityActions(progressBarLoading)
        }

        profileViewModel.uid.observe(viewLifecycleOwner) { uid->
            binding.uid.text = uid
        }

        profileViewModel.fullName.observe(viewLifecycleOwner) {name ->
            binding.usernameET.setText(name)
        }

        profileViewModel.phoneNumber.observe(viewLifecycleOwner){ phoneNum ->
            binding.phoneNumberET.setText(phoneNum)
        }

        profileViewModel.emailAddress.observe(viewLifecycleOwner) { email->
            binding.emailAddressET.setText(email)
        }

        binding.saveBtn.setOnClickListener {
            val fullName = binding.usernameET.text.toString()
            val emailAddress = binding.emailAddressET.text.toString()
            val phoneNumber = binding.phoneNumberET.text.toString()
            profileViewModel.editUserInfoInDB(view,fullName,phoneNumber,emailAddress)
        }

        binding.changePasswordCardView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    fun visibilityActions (progressBarLoading : Boolean) {
        if (progressBarLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.constraintLayout.visibility = View.INVISIBLE
        }else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.constraintLayout.visibility = View.VISIBLE
        }
    }
}