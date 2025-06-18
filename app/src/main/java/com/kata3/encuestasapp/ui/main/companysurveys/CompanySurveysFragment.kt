package com.kata3.encuestasapp.ui.main.companysurveys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kata3.encuestasapp.R
import com.kata3.encuestasapp.data.repositories.SurveyRepository
import com.kata3.encuestasapp.databinding.FragmentCompanySurveysBinding
import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.main.SurveyAdapter
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import com.kata3.encuestasapp.utils.EncryptedPrefsManager

class CompanySurveysFragment : Fragment() {

    private var _binding: FragmentCompanySurveysBinding? = null
    private val binding get() = _binding!!
    private lateinit var llmanager: LinearLayoutManager
    private lateinit var adapter: SurveyAdapter

    private val surveyService: SurveyService by lazy {
        SurveyService.create(requireContext())
    }

    private val viewModel: CompanySurveysViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CompanySurveysViewModel(SurveyRepository(surveyService)) as T
            }
        })[CompanySurveysViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanySurveysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = fetchTokenFromPreferences()
        val company = fetchCompanyFromPreferences()
        initRecyclerView()
        observeViewModel()

        showLoadingSpinner()
        viewModel.fetchCompanySurveys(token, company)
    }

    private fun initRecyclerView() {
        llmanager = LinearLayoutManager(context)
        val surveyList = viewModel.surveyList.value ?: emptyList()
        adapter = SurveyAdapter(
            surveyList = surveyList,
            onClickListener = { survey -> onSurveySelected(survey) }
        )
        val decoration = DividerItemDecoration(context, llmanager.orientation)
        binding.rvSurveys.layoutManager = llmanager
        binding.rvSurveys.adapter = adapter
        binding.rvSurveys.addItemDecoration(decoration)
    }

    private fun observeViewModel() {
        viewModel.surveyList.observe(viewLifecycleOwner) { surveys ->
            hideLoadingSpinner()
            adapter.updateList(surveys)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            hideLoadingSpinner()
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSurveySelected(survey: SurveyDto) {
        val bundle = Bundle().apply { putString("surveyId", survey.id) }
        findNavController().navigate(R.id.action_companySurveysFragment_to_detailsFragment, bundle)
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