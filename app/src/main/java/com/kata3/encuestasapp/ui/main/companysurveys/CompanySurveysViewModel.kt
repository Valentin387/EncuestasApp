package com.kata3.encuestasapp.ui.main.companysurveys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompanySurveysViewModel(private val surveyRepository: SurveyRepository) : ViewModel() {

    private val _surveyList = MutableLiveData<List<SurveyDto>>()
    val surveyList: LiveData<List<SurveyDto>> = _surveyList

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchCompanySurveys(token: String, company: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val surveys = surveyRepository.getCompanySurveys(token, company)
                if (surveys != null) {
                    _surveyList.postValue(surveys)
                    _errorMessage.postValue(null)
                } else {
                    _errorMessage.postValue("Failed to load company surveys")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }
}