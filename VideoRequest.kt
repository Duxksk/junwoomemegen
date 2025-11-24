package com.example.memeapp

data class VideoRequest(
    val model: String = "gpt-4o-mini",
    val prompt: String
)
