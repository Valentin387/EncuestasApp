package com.kata3.encuestasapp.ui.main.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(private val surveyRepository: SurveyRepository) : ViewModel() {

    private val _survey = MutableLiveData<SurveyDto?>()
    val survey: LiveData<SurveyDto?> = _survey

    private val _responses = MutableLiveData<List<ResponseDto>>()
    val responses: LiveData<List<ResponseDto>> = _responses

    private val _deleteResult = MutableLiveData<Boolean?>()
    val deleteResult: LiveData<Boolean?> = _deleteResult

    fun fetchSurveyDetails(token: String, surveyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("DetailsViewModel", "Fetching survey details for ID: $surveyId")
                val survey = surveyRepository.getSurvey(surveyId)
                _survey.postValue(survey)
                val responses = surveyRepository.getSurveyResponses(token, surveyId)
                _responses.postValue(responses ?: emptyList())
            } catch (e: Exception) {
                _survey.postValue(null)
                _responses.postValue(emptyList())
            }
        }
    }

    fun deleteSurvey(token: String, surveyId: String, cascade: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = if (cascade) {
                    surveyRepository.deleteSurveyCascade(token, surveyId)
                } else {
                    surveyRepository.deleteSurvey(token, surveyId)
                }
                _deleteResult.postValue(success)
            } catch (e: Exception) {
                _deleteResult.postValue(false)
            }
        }
    }
}