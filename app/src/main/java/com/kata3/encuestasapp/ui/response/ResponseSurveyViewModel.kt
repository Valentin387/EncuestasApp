package com.kata3.encuestasapp.ui.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResponseSurveyViewModel(private val surveyRepository: SurveyRepository) : ViewModel() {

    private val _survey = MutableLiveData<SurveyDto?>()
    val survey: LiveData<SurveyDto?> = _survey

    private val _responseResult = MutableLiveData<Boolean?>()
    val responseResult: LiveData<Boolean?> = _responseResult

    fun fetchSurvey(surveyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val survey = surveyRepository.getSurvey(surveyId)
                _survey.postValue(survey)
                _responseResult.postValue(null)
            } catch (e: Exception) {
                _survey.postValue(null)
                _responseResult.postValue(null)
            }
        }
    }

    fun submitResponse(surveyId: String, response: ResponseDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = surveyRepository.submitResponse(surveyId, response)
                _responseResult.postValue(success)
            } catch (e: Exception) {
                _responseResult.postValue(false)
            }
        }
    }
}