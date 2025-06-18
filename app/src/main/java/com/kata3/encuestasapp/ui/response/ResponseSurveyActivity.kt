package com.kata3.encuestasapp.ui.response

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.databinding.ActivityResponseSurveyBinding
import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.response.models.Question
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import com.kata3.encuestasapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class ResponseSurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResponseSurveyBinding
    private val surveyService: SurveyService by lazy {
        SurveyService.create(applicationContext)
    }
    private val viewModel: ResponseSurveyViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ResponseSurveyViewModel(SurveyRepository(surveyService)) as T
            }
        })[ResponseSurveyViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EncryptedPrefsManager.init(applicationContext)
        binding = ActivityResponseSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val surveyId = intent.data?.pathSegments?.lastOrNull() ?: ""
        if (surveyId.isNotEmpty()) {
            showLoadingSpinner()
            viewModel.fetchSurvey(surveyId)
        } else {
            Toast.makeText(this, "Invalid survey ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btSubmit.setOnClickListener {
            val survey = viewModel.survey.value
            if (survey != null) {
                val answers = collectAnswers(survey.questions)
                if (validateAnswers(survey.questions, answers)) {
                    showLoadingSpinner()
                    val respondent = ResponseDto.Respondent(
                        email = binding.etEmail.text.toString().trim(),
                        firstname = binding.etFirstname.text.toString().trim(),
                        lastname = binding.etLastname.text.toString().trim()
                    )
                    viewModel.submitResponse(survey.id, ResponseDto(respondent, answers))
                } else {
                    Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectAnswers(questions: List<Question>): List<ResponseDto.Answer> {
        val answers = mutableListOf<ResponseDto.Answer>()
        for (question in questions) {
            val container = binding.llQuestions.findViewWithTag<LinearLayout>("question_${question.questionId}")
            when (question.type) {
                "text" -> {
                    val editText = container.findViewWithTag<EditText>("input_${question.questionId}")
                    answers.add(ResponseDto.Answer(question.questionId, editText.text.toString().trim()))
                }
                "age" -> {
                    val editText = container.findViewWithTag<EditText>("input_${question.questionId}")
                    val answer = editText.text.toString().toIntOrNull() ?: 0
                    answers.add(ResponseDto.Answer(question.questionId, answer))
                }
                "rating" -> {
                    val radioGroup = container.findViewWithTag<RadioGroup>("input_${question.questionId}")
                    val selectedId = radioGroup.checkedRadioButtonId
                    val answer = if (selectedId != -1) {
                        findViewById<RadioButton>(selectedId).text.toString().toIntOrNull() ?: 0
                    } else 0
                    answers.add(ResponseDto.Answer(question.questionId, answer))
                }
            }
        }
        return answers
    }

    private fun validateAnswers(questions: List<Question>, answers: List<ResponseDto.Answer>): Boolean {
        for (question in questions.filter { it.required }) {
            val answer = answers.find { it.questionId == question.questionId }?.answer
            when (question.type) {
                "text" -> if (answer == null || (answer as String).isEmpty() || answer.length > 300) return false
                "age" -> if (answer == null || (answer as Int) <= 0) return false
                "rating" -> if (answer == null || (answer as Int) !in 1..5) return false
            }
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.survey.observe(this) { survey ->
            hideLoadingSpinner()
            if (survey != null) {
                binding.tvTitle.text = survey.title
                binding.tvDescription.text = survey.description
                renderQuestions(survey.questions)
            } else {
                Toast.makeText(this, "Survey not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.responseResult.observe(this) { success ->
            hideLoadingSpinner()
            if (success == true) {
                Toast.makeText(this, "Response submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to submit response", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderQuestions(questions: List<Question>) {
        binding.llQuestions.removeAllViews()
        for (question in questions) {
            val questionLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 16, 0, 16)
                tag = "question_${question.questionId}"
            }
            val questionText = TextView(this).apply {
                text = question.questionText + if (question.required) " *" else ""
                textSize = 18f
            }
            questionLayout.addView(questionText)

            when (question.type) {
                "text" -> {
                    val editText = EditText(this).apply {
                        hint = "Enter your answer"
                        tag = "input_${question.questionId}"
                    }
                    questionLayout.addView(editText)
                }
                "age" -> {
                    val editText = EditText(this).apply {
                        hint = "Enter your age"
                        inputType = android.text.InputType.TYPE_CLASS_NUMBER
                        tag = "input_${question.questionId}"
                    }
                    questionLayout.addView(editText)
                }
                "rating" -> {
                    val radioGroup = RadioGroup(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        tag = "input_${question.questionId}"
                    }
                    for (i in 1..5) {
                        val radioButton = RadioButton(this).apply {
                            text = i.toString()
                            id = View.generateViewId()
                        }
                        radioGroup.addView(radioButton)
                    }
                    questionLayout.addView(radioGroup)
                }
            }
            binding.llQuestions.addView(questionLayout)
        }
    }

    private fun showLoadingSpinner() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
        binding.progressBar.visibility = View.GONE
    }
}