package com.example.memeapp

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var promptInput: EditText
    private lateinit var generateBtn: Button
    private lateinit var downloadBtn: Button

    private lateinit var api: OpenAIService

    private var lastVideoUrl: String? = null  // 다운로드 링크 저장용
    private var authHeader: String = ""       // Bearer + local.properties API KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        promptInput = findViewById(R.id.promptInput)
        generateBtn = findViewById(R.id.generateBtn)
        downloadBtn = findViewById(R.id.downloadBtn)

        // 🔥 API KEY 로드
        authHeader = "Bearer ${BuildConfig.OPENAI_API_KEY}"

        // 🔥 Retrofit 생성
        api = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)

        generateBtn.setOnClickListener { generateVideo() }
        downloadBtn.setOnClickListener { downloadVideo() }
    }

    // ===============================
    // 🔥 영상 생성 기능
    // ===============================
    private fun generateVideo() {
        val prompt = promptInput.text.toString().trim()
        if (prompt.isEmpty()) {
            Toast.makeText(this, "프롬프트를 입력하세요!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "영상 생성 중…", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = VideoRequest(
                    model = "gpt-4o-mini",
                    prompt = prompt
                )

                val response = api.createVideo(
                    auth = authHeader,
                    body = body
                )

                // URL 저장
                lastVideoUrl = response.video_url

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "영상 생성 완료!", Toast.LENGTH_SHORT).show()
                    if (lastVideoUrl != null) {
                        Toast.makeText(
                            this@MainActivity,
                            "다운로드 버튼을 누르세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "오류 발생: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ===============================
    // 🔥 영상 다운로드 기능
    // ===============================
    private fun downloadVideo() {
        val url = lastVideoUrl
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "다운로드할 영상이 없습니다!", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "openai_meme_${System.currentTimeMillis()}.mp4"

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("밈 영상 다운로드")
            .setDescription("OpenAI 영상 생성 다운로드 중…")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)

        Toast.makeText(this, "다운로드 시작됨!", Toast.LENGTH_SHORT).show()
    }
}
