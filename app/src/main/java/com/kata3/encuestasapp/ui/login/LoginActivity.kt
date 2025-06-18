package com.kata3.encuestasapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kata3.encuestasapp.data.repositories.AuthRepository
import com.kata3.encuestasapp.databinding.ActivityLoginBinding
import com.kata3.encuestasapp.io.AuthService
import com.kata3.encuestasapp.ui.main.MainActivity
import com.kata3.encuestasapp.ui.signup.SignupActivity
import com.kata3.encuestasapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authService: AuthService by lazy {
        AuthService.create(applicationContext)
    }
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(AuthRepository(authService)) as T
            }
        })[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EncryptedPrefsManager.init(applicationContext)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkStoredToken()) {
            goToMainActivity()
            return
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (validateInput(email, password)) {
                showLoadingSpinner()
                loginViewModel.loginUser(email, password)
            } else {
                Toast.makeText(this, "Invalid input. Check email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.length >= 8
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginResult.collect { result ->
                hideLoadingSpinner()
                if (result != null) {
                    when (result) {
                        is LoginResult.Success -> {
                            saveToken(result.token)
                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                            goToMainActivity()
                        }
                        is LoginResult.Error -> {
                            Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun checkStoredToken(): Boolean {
        val preferences = EncryptedPrefsManager.getPreferences()
        val token = preferences.getString("jwt_token", null)
        return token != null
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