package com.kata3.encuestasapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kata3.encuestasapp.databinding.ItemSurveyBinding
import com.kata3.encuestasapp.ui.response.models.SurveyDto

class SurveyAdapter(
    private var surveyList: List<SurveyDto>,
    private val onClickListener: (SurveyDto) -> Unit
) : RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    class SurveyViewHolder(private val binding: ItemSurveyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(survey: SurveyDto, onClickListener: (SurveyDto) -> Unit) {
            binding.tvTitle.text = survey.title
            binding.tvDescription.text = survey.description
            binding.root.setOnClickListener { onClickListener(survey) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val binding = ItemSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SurveyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        holder.bind(surveyList[position], onClickListener)
    }

    override fun getItemCount(): Int = surveyList.size

    fun updateList(newList: List<SurveyDto>) {
        surveyList = newList
        notifyDataSetChanged()
    }
}