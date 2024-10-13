package com.carstore.app.ui.fragments.auth

import android.app.AlertDialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carstore.app.R
import com.carstore.app.databinding.FragmentLoginBinding
import com.carstore.app.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.backgroundColor)
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.backgroundColor)

        loginViewModel.loadingProgressBar.observe(viewLifecycleOwner) { boolean ->
            if (boolean) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener { viewFromBtn ->
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()
            loginViewModel.login(email, password, viewFromBtn, requireActivity(), auth)
        }

        binding.forgotPassword.setOnClickListener { viewFromBtn ->
            val alert_view =
                LayoutInflater.from(requireContext()).inflate(R.layout.alert_view, null)
            val emailAddressEt = alert_view.findViewById<EditText>(R.id.emailAddressET)
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Reset password")
            alertDialog.setView(alert_view)
            alertDialog.setPositiveButton("Send link", OnClickListener { dialog, which ->
                val emailForSendResetLink = emailAddressEt.text.toString()
                loginViewModel.resetPassword(emailForSendResetLink, viewFromBtn, auth)
            })
            alertDialog.show()

        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}