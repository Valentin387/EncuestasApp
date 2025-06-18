package com.kata3.encuestasapp.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kata3.encuestasapp.data.repositories.AuthRepository
import com.kata3.encuestasapp.databinding.ActivitySignupBinding
import com.kata3.encuestasapp.io.AuthService
import com.kata3.encuestasapp.ui.main.MainActivity
import com.kata3.encuestasapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val authService: AuthService by lazy {
        AuthService.create(applicationContext)
    }
    private val signupViewModel: SignupViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignupViewModel(AuthRepository(authService)) as T
            }
        })[SignupViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EncryptedPrefsManager.init(applicationContext)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val firstname = binding.etFirstname.text.toString().trim()
            val lastname = binding.etLastname.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val company = binding.etCompany.text.toString().trim()

            if (validateInput(email, firstname, lastname, password, company)) {
                showLoadingSpinner()
                signupViewModel.registerUser(email, firstname, lastname, password, company)
            } else {
                Toast.makeText(this, "Invalid input. Check all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(email: String, firstname: String, lastname: String, password: String, company: String): Boolean {
        return email.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty() && password.length >= 8 && company.isNotEmpty()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            signupViewModel.signupResult.collect { result ->
                hideLoadingSpinner()
                if (result != null) {
                    when (result) {
                        is SignupResult.Success -> {
                            saveToken(result.token)
                            Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()
                            goToMainActivity()
                        }
                        is SignupResult.Error -> {
                            Toast.makeText(this@SignupActivity, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun saveToken(token: String) {
        val preferences = EncryptedPrefsManager.getPreferences()
        val editor = preferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoadingSpinner() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
        binding.progressBar.visibility = View.GONE
    }
}