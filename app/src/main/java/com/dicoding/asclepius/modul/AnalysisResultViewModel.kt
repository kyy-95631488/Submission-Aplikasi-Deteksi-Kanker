package com.dicoding.asclepius.modul

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.room_database.AnalysisResult
import java.text.SimpleDateFormat
import java.util.*

class AnalysisResultViewModel(private val results: List<AnalysisResult>) : RecyclerView.Adapter<AnalysisResultViewModel.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultImageView: ImageView = view.findViewById(R.id.resultImageView)
        val resultTextView: TextView = view.findViewById(R.id.resultTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_analysis_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analysisResult = results[position]
        holder.resultTextView.text = analysisResult.result
        holder.timestampTextView.text = formatTimestamp(analysisResult.timestamp)

        Glide.with(holder.itemView.context)
            .load(analysisResult.imageUri)
            .into(holder.resultImageView)
    }

    override fun getItemCount(): Int = results.size

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))
        return formatter.format(date)
    }
}
