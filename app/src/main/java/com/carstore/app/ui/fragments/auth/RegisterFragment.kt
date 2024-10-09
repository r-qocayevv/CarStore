package com.carstore.app.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carstore.app.R
import com.carstore.app.databinding.FragmentRegisterBinding
import com.carstore.app.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {
    private var _binding : FragmentRegisterBinding? = null
    val binding get() = _binding!!
    private val registerViewModel : RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.backgroundColor)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.backgroundColor)

        registerViewModel.loadingProgressBar.observe(viewLifecycleOwner){ boolean ->
            if(boolean){
                binding.progressBar.visibility = View.VISIBLE
                binding.signUpBtn.visibility = View.INVISIBLE
            }else {
                binding.progressBar.visibility = View.GONE
                binding.signUpBtn.visibility = View.VISIBLE
            }
        }

        binding.signUpBtn.setOnClickListener {
            val emailAddress = binding.emailAddressET.text.toString()
            val password  = binding.passwordET.text.toString()
            val fullName = binding.fullNameET.text.toString()
            val phoneNum = binding.phoneNumberET.text.toString()
            registerViewModel.signUp(fullName,phoneNum,emailAddress,password,requireActivity(),it)
        }

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()

    }

}