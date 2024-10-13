package com.carstore.app.ui.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carstore.app.R
import com.carstore.app.databinding.FragmentProfileBinding
import com.carstore.app.viewmodel.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.database
    val profileViewModel: ProfileViewModel by viewModels()
    private var userFullName = ""
    private var userPhoneNumber = ""
    private var userEmailAddress = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.orange)

        binding.uid.text = auth.currentUser!!.uid

        profileViewModel.userInfo(view, db, auth)

        profileViewModel.progressBarLoading.observe(viewLifecycleOwner) { progressBarLoading ->
            visibilityActions(progressBarLoading)
        }

        profileViewModel.userInfoFromFirebaseDB.observe(viewLifecycleOwner) { userInfoHashMap ->
            userFullName = userInfoHashMap["fullName"] as String
            binding.usernameET.setText(userFullName)

            userPhoneNumber = userInfoHashMap["phoneNumber"] as String
            binding.phoneNumberET.setText(userPhoneNumber)

            userEmailAddress = userInfoHashMap["emailAddress"] as String
            binding.emailAddressET.setText(userEmailAddress)
        }


        binding.saveBtn.setOnClickListener { viewFromBtn ->
            val fullName = binding.usernameET.text.toString()
            val emailAddress = binding.emailAddressET.text.toString()
            val phoneNumber = binding.phoneNumberET.text.toString()
            profileViewModel.editUserInfoInDB(
                viewFromBtn,
                auth,
                db,
                userEmailAddress,
                userPhoneNumber,
                userFullName,
                fullName,
                phoneNumber,
                emailAddress
            )
        }

        binding.changePasswordCardView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

    }


    fun visibilityActions(progressBarLoading: Boolean) {
        if (progressBarLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.constraintLayout.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.constraintLayout.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}