package com.carstore.app.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.carstore.app.R
import com.carstore.app.databinding.ActivitySplashBinding
import com.carstore.app.viewmodel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity(),LifecycleOwner {
    private var _binding : ActivitySplashBinding? = null
    val binding get() = _binding!!
    private val splashScreenViewModel : SplashScreenViewModel by viewModels()
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.orange)

        auth = FirebaseAuth.getInstance()

        splashScreenViewModel.checkUser(auth)
        checkUser()

    }

    private fun checkUser () {
            splashScreenViewModel.currentUserIsNull.observe(this@SplashActivity){ currentUserIsNull ->
                lifecycleScope.launch {
                    delay(2000)
                    if (currentUserIsNull){
                        val intent = Intent(this@SplashActivity,AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this@SplashActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

            }


    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}