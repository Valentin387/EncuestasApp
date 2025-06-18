package com.kata3.encuestasapp.ui.main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kata3.encuestasapp.R
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.databinding.FragmentDetailsBinding
import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.response.models.Question
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import com.kata3.encuestasapp.utils.EncryptedPrefsManager

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val surveyService: SurveyService by lazy {
        SurveyService.create(requireContext())
    }

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailsViewModel(SurveyRepository(surveyService)) as T
            }
        })[DetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val surveyId = arguments?.getString("surveyId") ?: ""
        val token = fetchTokenFromPreferences()

        setupClickListeners()
        observeViewModel()

        showLoadingSpinner()
        viewModel.fetchSurveyDetails(token, surveyId)
    }

    private fun setupClickListeners() {
        binding.btDelete.setOnClickListener {
            val surveyId = arguments?.getString("surveyId") ?: ""
            val token = fetchTokenFromPreferences()
            showLoadingSpinner()
            viewModel.deleteSurvey(token, surveyId, false)
        }

        binding.btDeleteCascade.setOnClickListener {
            val surveyId = arguments?.getString("surveyId") ?: ""
            val token = fetchTokenFromPreferences()
            showLoadingSpinner()
            viewModel.deleteSurvey(token, surveyId, true)
        }
    }

    private fun observeViewModel() {
        viewModel.survey.observe(viewLifecycleOwner) { survey ->
            hideLoadingSpinner()
            if (survey != null) {
                binding.tvTitle.text = survey.title
                binding.tvDescription.text = survey.description
                renderQuestions(survey.questions)
            } else {
                Toast.makeText(context, "Survey not found", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.responses.observe(viewLifecycleOwner) { responses ->
            renderResponses(responses)
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            hideLoadingSpinner()
            if (success == true) {
                Toast.makeText(context, "Survey deleted successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Failed to delete survey", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderQuestions(questions: List<Question>) {
        binding.llQuestions.removeAllViews()
        questions.forEachIndexed { index, question ->
            val questionText = TextView(context).apply {
                text = "${index + 1}. ${question.questionText} (${question.type})" + if (question.required) " *" else ""
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            binding.llQuestions.addView(questionText)
        }
    }

    private fun renderResponses(responses: List<ResponseDto>) {
        binding.llResponses.removeAllViews()
        responses.forEachIndexed { index, response ->
            val responseLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 16, 0, 16)
            }
            val respondentText = TextView(context).apply {
                text = "Respondent ${index + 1}: ${response.respondent.firstname} ${response.respondent.lastname} (${response.respondent.email})"
                textSize = 16f
            }
            responseLayout.addView(respondentText)
            response.answers.forEach { answer ->
                val answerText = TextView(context).apply {
                    text = "Q: ${answer.questionId}, A: ${answer.answer}"
                    setPadding(16, 4, 0, 4)
                }
                responseLayout.addView(answerText)
            }
            binding.llResponses.addView(responseLayout)
        }
    }

    private fun fetchTokenFromPreferences(): String {
        val preferences = EncryptedPrefsManager.getPreferences()
        return preferences.getString("jwt_token", "") ?: ""
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