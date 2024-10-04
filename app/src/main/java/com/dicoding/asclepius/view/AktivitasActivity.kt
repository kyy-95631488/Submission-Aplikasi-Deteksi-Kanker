package com.dicoding.asclepius.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.room_database.AppDatabase
import com.dicoding.asclepius.databinding.ActivityNewBinding
import com.dicoding.asclepius.modul.AnalysisResultViewModel

class AktivitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewBinding
    private lateinit var analysisResultAdapter: AnalysisResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setBackgroundColor(getColor(R.color.colorPrimary))

        binding.toolbar.navigationIcon?.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        analysisResultAdapter = AnalysisResultViewModel(emptyList())
        binding.recyclerView.adapter = analysisResultAdapter

        loadAnalysisResults()
    }

    private fun loadAnalysisResults() {
        val db = AppDatabase.getDatabase(this)
        db.analysisResultDao().getAllResults().observe(this, Observer { analysisResults ->
            analysisResultAdapter = AnalysisResultViewModel(analysisResults)
            binding.recyclerView.adapter = analysisResultAdapter

            if (analysisResults.isEmpty()) {
                binding.emptyMessageTextView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyMessageTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
