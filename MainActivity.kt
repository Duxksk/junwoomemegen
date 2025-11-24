package com.example.memeapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var api: OpenAIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIService::class.java)

        val promptInput = findViewById<EditText>(R.id.promptInput)
        val generateBtn = findViewById<Button>(R.id.generateBtn)
        val videoView = findViewById<VideoView>(R.id.videoView)

        generateBtn.setOnClickListener {
            val prompt = promptInput.text.toString()
            if (prompt.isEmpty()) {
                Toast.makeText(this, "프롬프트를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            generateBtn.isEnabled = false
            generateBtn.text = "생성 중..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val key = "Bearer ${BuildConfig.OPENAI_API_KEY}"

                    val response = api.createVideo(
                        auth = key,
                        body = VideoRequest(prompt = prompt)
                    )

                    val url = response.video_url
                    if (url == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "영상 URL 없음", Toast.LENGTH_SHORT).show()
                            generateBtn.isEnabled = true
                            generateBtn.text = "영상 생성"
                        }
                        return@launch
                    }

                    val file = Downloader.download(this@MainActivity, url)

                    withContext(Dispatchers.Main) {
                        videoView.setVideoURI(Uri.fromFile(file))
                        videoView.start()
                        generateBtn.isEnabled = true
                        generateBtn.text = "영상 생성"
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "오류 발생: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        generateBtn.isEnabled = true
                        generateBtn.text = "영상 생성"
                    }
                }
            }
        }
    }
}
