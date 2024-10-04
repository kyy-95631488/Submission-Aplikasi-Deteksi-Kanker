package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val result = intent.getStringExtra("RESULT") ?: getString(R.string.no_result)
        binding.resultText.text = result

        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
        } else {
            binding.resultImage.visibility = View.GONE
        }

        val timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis())
        binding.timestampText?.text = formatTimestamp(timestamp)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)

        val dayNames = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val timeZoneId = if (calendar.timeZone.id == "Asia/Jakarta") "WIB" else "WITA"
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))

        return "${dayNames[dayOfWeek]}, ${dateFormat.format(date)} $timeZoneId"
    }
}
