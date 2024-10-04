package com.dicoding.asclepius.view

import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.model_news.Article
import com.dicoding.asclepius.model_news.NewsApiService
import com.dicoding.asclepius.model_news.NewsResponse
import com.dicoding.asclepius.modul.ArticlesAdapter
import com.dicoding.asclepius.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsActivity : AppCompatActivity() {

    private lateinit var messageTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "News"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setTitleTextColor(Color.WHITE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)

        messageTextView = findViewById(R.id.messageTextView)
        progressBar = findViewById(R.id.progressBar)

        fetchNews()
    }

    private fun fetchNews() {
        if (!isNetworkAvailable()) {
            displayMessage("Tidak ada koneksi internet")
            return
        }

        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NewsApiService::class.java)
        service.getTopHeadlines("cancer", "health", "en", BuildConfig.API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        if (articles.isEmpty()) {
                            displayMessage("Data tidak tersedia")
                        } else {
                            displayArticles(articles)
                            messageTextView.visibility = View.GONE
                        }
                    } else {
                        displayMessage("Terjadi kesalahan saat mengambil data")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    displayMessage("Terjadi kesalahan: ${t.message}")
                }
            })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun displayMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
    }

    private fun displayArticles(articles: List<Article>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ArticlesAdapter(articles)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
