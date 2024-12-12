package com.dicoding.tumoranger.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tumoranger.R
import com.dicoding.tumoranger.api.response.DiagnosisHistoryItem
import java.util.Locale

class HistoryAdapter(private val historyList: List<DiagnosisHistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = historyList.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientNameTextView: TextView = itemView.findViewById(R.id.patientNameTextView)
        private val genderTextView: TextView = itemView.findViewById(R.id.genderTextView)
        private val birthdateTextView: TextView = itemView.findViewById(R.id.birthdateTextView)
        private val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)
        private val confidenceScoreTextView: TextView = itemView.findViewById(R.id.confidenceScoreTextView)
        private val diagnosisDateTextView: TextView = itemView.findViewById(R.id.diagnosisDateTextView)
        private val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)

        fun bind(item: DiagnosisHistoryItem) {
            patientNameTextView.text = item.patient_name
            genderTextView.text = item.gender
            birthdateTextView.text = item.birthdate
            resultTextView.text = item.result
            confidenceScoreTextView.text = String.format(Locale.ROOT, "%d%%", item.confidence_score.toInt())
            diagnosisDateTextView.text = item.diagnosis_date

            // Load the image using Glide
            Glide.with(itemView.context)
                .load(item.file_path)
                .into(imagePreview)
        }
    }
}