package com.kata3.encuestasapp.ui.main.createsurvey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateSurveyViewModel(private val surveyRepository: SurveyRepository) : ViewModel() {

    private val _createResult = MutableLiveData<Boolean?>()
    val createResult: LiveData<Boolean?> = _createResult

    fun createSurvey(token: String, survey: SurveyDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = surveyRepository.createSurvey(token, survey)
                _createResult.postValue(success)
            } catch (e: Exception) {
                _createResult.postValue(false)
            }
        }
    }
}