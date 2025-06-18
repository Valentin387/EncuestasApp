package com.kata3.encuestasapp.ui.fallback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kata3.encuestasapp.databinding.ActivityFallbackBinding

class FallbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFallbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFallbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btSubmit.setOnClickListener {
            val surveyId = binding.etSurveyId.text.toString().trim()
            if (surveyId.isNotEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("myapp://survey/$surveyId"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to open survey. Install EncuestasApp.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.kata3.encuestasapp")))
                }
            } else {
                Toast.makeText(this, "Enter a valid survey ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}