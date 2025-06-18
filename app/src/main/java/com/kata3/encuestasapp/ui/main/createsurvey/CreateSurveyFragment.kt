package com.kata3.encuestasapp.ui.main.createsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kata3.encuestasapp.R
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.databinding.FragmentCreateSurveyBinding
import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.response.models.Question
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import com.kata3.encuestasapp.utils.EncryptedPrefsManager
import java.util.UUID

class CreateSurveyFragment : Fragment() {

    private var _binding: FragmentCreateSurveyBinding? = null
    private val binding get() = _binding!!
    private val questions = mutableListOf<Question>()

    private val surveyService: SurveyService by lazy {
        SurveyService.create(requireContext())
    }

    private val viewModel: CreateSurveyViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateSurveyViewModel(SurveyRepository(surveyService)) as T
            }
        })[CreateSurveyViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btAddQuestion.setOnClickListener {
            val questionText = binding.etQuestionText.text.toString().trim()
            val type = binding.spQuestionType.selectedItem.toString().lowercase()
            val required = binding.cbRequired.isChecked

            if (questionText.isNotEmpty()) {
                val question = Question(
                    questionId = UUID.randomUUID().toString(),
                    questionText = questionText,
                    type = type,
                    required = required
                )
                questions.add(question)
                renderQuestions()
                binding.etQuestionText.text.clear()
                binding.cbRequired.isChecked = false
            } else {
                Toast.makeText(context, "Enter question text", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val company = fetchCompanyFromPreferences()
            val token = fetchTokenFromPreferences()

            if (title.isNotEmpty() && questions.isNotEmpty()) {
                val survey = SurveyDto("", company, title, description, questions)
                showLoadingSpinner()
                viewModel.createSurvey(token, survey)
            } else {
                Toast.makeText(context, "Enter title and at least one question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderQuestions() {
        binding.llQuestions.removeAllViews()
        questions.forEachIndexed { index, question ->
            val questionLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }
            val questionText = TextView(context).apply {
                text = "${index + 1}. ${question.questionText} (${question.type})" + if (question.required) " *" else ""
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val removeButton = Button(context).apply {
                text = "Remove"
                setOnClickListener { questions.removeAt(index); renderQuestions() }
            }
            questionLayout.addView(questionText)
            questionLayout.addView(removeButton)
            binding.llQuestions.addView(questionLayout)
        }
    }

    private fun observeViewModel() {
        viewModel.createResult.observe(viewLifecycleOwner) { success ->
            hideLoadingSpinner()
            if (success == true) {
                Toast.makeText(context, "Survey created successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createSurveyFragment_to_mySurveysFragment)
            } else {
                Toast.makeText(context, "Failed to create survey", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchTokenFromPreferences(): String {
        val preferences = EncryptedPrefsManager.getPreferences()
        return preferences.getString("jwt_token", "") ?: ""
    }

    private fun fetchCompanyFromPreferences(): String {
        val preferences = EncryptedPrefsManager.getPreferences()
        return preferences.getString("company", "") ?: ""
    }

    private fun showLoadingSpinner() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}