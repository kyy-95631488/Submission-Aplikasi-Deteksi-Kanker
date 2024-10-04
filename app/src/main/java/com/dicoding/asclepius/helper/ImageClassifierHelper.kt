package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Locale

class ImageClassifierHelper(private val context: Context) {
    private lateinit var model: CancerClassification

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        try {
            // Initialize the model
            model = CancerClassification.newInstance(context)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.e("ImageClassifierHelper", "Model initialization failed: Invalid argument - ${e.message}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ImageClassifierHelper", "Model initialization failed: IO error - ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageClassifierHelper", "Model initialization failed: ${e.message}")
        }
    }

    fun classifyImage(bitmap: Bitmap): String {
        // Resize bitmap to the model's input size (e.g., 224x224)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Normalize the bitmap
        val tensorImage = TensorImage.fromBitmap(resizedBitmap)

        val output = model.process(tensorImage)
        val probabilities = output.probabilityAsCategoryList

        // Check if probabilities are available
        if (probabilities.size < 2) {
            return "Classification error: Insufficient probability data."
        }

        val cancerProb = probabilities[1].score
        val noCancerProb = probabilities[0].score

        return if (cancerProb > noCancerProb) {
            "Cancer: ${String.format(Locale.ROOT, "%.2f", cancerProb * 100)}%"
        } else {
            "Non Cancer: ${String.format(Locale.ROOT, "%.2f", noCancerProb * 100)}%"
        }
    }

    fun classifyStaticImage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            classifyImage(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            "File not found: ${e.message}"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error processing image: ${e.message}"
        }
    }

    fun closeModel() {
        model.close()
    }
}
