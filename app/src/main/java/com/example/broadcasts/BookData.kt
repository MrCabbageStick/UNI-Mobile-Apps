package com.example.broadcasts

import android.content.Intent

data class BookData(
    val title: String,
    val letterCount: Int,
    val wordCount: Int,
    val mostCommonWord: String,
)

fun bookDataToIntent(bookData: BookData, intent: Intent){
    intent.putExtra("title", bookData.title)
    intent.putExtra("letterCount", bookData.letterCount)
    intent.putExtra("wordCount", bookData.wordCount)
    intent.putExtra("mostCommonWord", bookData.mostCommonWord)
}

fun intentToBookData(intent: Intent?): BookData?{
    if(intent == null) return null

    val title: String = intent.getStringExtra("title") ?: return null
    val letterCount: Int = intent.getIntExtra("letterCount", -1)
    val wordCount: Int = intent.getIntExtra("wordCount", -1)
    val mostCommonWord: String = intent.getStringExtra("mostCommonWord") ?: return null

    if(letterCount == -1 || wordCount == -1) return null

    return BookData(title, letterCount, wordCount, mostCommonWord)
}