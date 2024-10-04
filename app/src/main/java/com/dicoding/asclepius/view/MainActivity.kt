package com.dicoding.asclepius.view

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.PreferenceManager
import com.dicoding.asclepius.room_database.AnalysisResult
import com.dicoding.asclepius.room_database.AppDatabase
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PreferenceManager
        PreferenceManager.initialize(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))

        // Activity Icon
        val activityIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_activity)
            setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            background = RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), null, null)
            val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END
                marginEnd = 16
            }
            layoutParams = params
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                val intent = Intent(this@MainActivity, AktivitasActivity::class.java)
                startActivity(intent)
            }
        }

        // News Icon
        val newsIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_news)
            setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            background = RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), null, null)
            val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END
                marginEnd = 16
            }
            layoutParams = params
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                val intent = Intent(this@MainActivity, NewsActivity::class.java)
                startActivity(intent)
            }
        }

        val settingsIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_settings)
            setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            background = RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), null, null)
            val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END
                marginEnd = 16
            }
            layoutParams = params
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        toolbar.addView(activityIcon)
        toolbar.addView(newsIcon)
        toolbar.addView(settingsIcon) // Add settings icon to toolbar

        binding.galleryButton.setOnClickListener {
            checkStoragePermission()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

    }

    private fun checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
            } else {
                startGallery()
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                startGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGallery()
                } else {
                    showToast("Permission denied. Unable to access gallery.")
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cropped_image.jpg"))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(800, 800)
            .start(this)

        Log.d("CROP", "Cropped image will be saved at: $destinationUri")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            data?.let {
                val croppedUri = UCrop.getOutput(it)
                if (croppedUri != null) {
                    currentImageUri = croppedUri
                    showImage(croppedUri)

                    duplicateCroppedImage(croppedUri)
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                currentImageUri = uri
                startCropActivity(uri)
            }
        }
    }


    private fun duplicateCroppedImage(croppedUri: Uri) {
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (picturesDir != null) {
            val newFileName = getNextFileName(picturesDir, "jpg")
            val destinationFile = File(picturesDir, newFileName)

            try {
                val sourceFile = File(croppedUri.path!!)
                sourceFile.copyTo(destinationFile, overwrite = true)
                Log.d("CROP", "Cropped image duplicated at: ${destinationFile.path}")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to duplicate image: ${e.message}")
            }
        }
    }

    private fun getNextFileName(directory: File, extension: String): String {
        var fileIndex = 1
        var newFile: File

        do {
            val fileName = "cropped_image_$fileIndex.$extension"
            newFile = File(directory, fileName)
            fileIndex++
        } while (newFile.exists())

        return newFile.name
    }

    private fun showImage(croppedUri: Uri) {
        binding.previewImageView.setImageURI(null)
        binding.previewImageView.setImageURI(croppedUri)
        binding.previewImageView.invalidate()
        binding.previewImageView.requestLayout()
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->

            binding.progressIndicator.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            binding.progressIndicator.setProgressCompat(0, true)

            val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val extension = "jpg"

            val duplicateFileName = getNextFileName(directory!!, extension)
            val duplicateFile = File(directory, duplicateFileName)
            val duplicateUri = Uri.fromFile(duplicateFile)

            try {
                copyFile(uri, duplicateUri)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to duplicate image.")
                return
            }

            val imageClassifierHelper = ImageClassifierHelper(this)

            Thread {
                val result = imageClassifierHelper.classifyStaticImage(duplicateUri)
                imageClassifierHelper.closeModel()

                runOnUiThread {
                    val animator = ValueAnimator.ofInt(0, 100)
                    animator.duration = 3000
                    animator.addUpdateListener { animation ->
                        val progress = animation.animatedValue as Int
                        binding.progressIndicator.setProgressCompat(progress, true)
                    }
                    animator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.progressIndicator.visibility = View.GONE
                            binding.progressText.visibility = View.GONE

                            if (result != null) {
                                saveResultToDatabase(result, duplicateUri.toString())
                                moveToResult(result, duplicateUri.toString()) // Call the moveToResult method
                            } else {
                                showToast("Failed to analyze image.")
                            }
                        }
                    })
                    animator.start()
                }
            }.start()
        } ?: showToast("Please select an image first.")
    }

    private fun moveToResult(result: String, imageUri: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT", result)
            putExtra("IMAGE_URI", imageUri)
            putExtra("TIMESTAMP", System.currentTimeMillis())
        }
        startActivity(intent)
    }

    private fun copyFile(sourceUri: Uri, destinationUri: Uri) {
        val inputStream = contentResolver.openInputStream(sourceUri)
        val outputStream = contentResolver.openOutputStream(destinationUri)
        inputStream?.use { input ->
            outputStream?.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun saveResultToDatabase(result: String, imageUri: String) {
        val db = AppDatabase.getDatabase(this)
        val analysisResultDao = db.analysisResultDao()

        CoroutineScope(Dispatchers.IO).launch {
            analysisResultDao.insert(AnalysisResult(result = result, imageUri = imageUri))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val GALLERY_REQUEST_CODE = 100
        const val STORAGE_PERMISSION_CODE = 101
    }
}
