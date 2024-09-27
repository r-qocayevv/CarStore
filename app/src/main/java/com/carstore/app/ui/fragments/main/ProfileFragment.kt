package com.carstore.app.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.carstore.app.R
import com.carstore.app.databinding.FragmentProfileBinding
import com.carstore.app.ui.activities.AuthActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    val binding get() = _binding!!
    lateinit var auth : FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.orange)

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(),AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}