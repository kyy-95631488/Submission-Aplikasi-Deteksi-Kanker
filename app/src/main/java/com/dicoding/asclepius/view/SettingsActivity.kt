package com.dicoding.asclepius.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivitySettingsBinding
import com.dicoding.asclepius.helper.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(getColor(R.color.colorPrimary))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set the toolbar title
        supportActionBar?.title = "Settings"

        // Initialize the dark mode switch
        binding.switchDarkMode.isChecked = PreferenceManager.isDarkModeEnabled(this)

        // Set the switch listener
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.setDarkMode(isChecked, this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

