package com.example.broadcasts

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.math.log

class BookDownloadService: Service() {
    var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    companion object{
        val books = listOf(
            Pair("The shadow on the spark", "https://www.gutenberg.org/ebooks/75049.txt.utf-8"),
            Pair("Borderland", "https://www.gutenberg.org/ebooks/75051.txt.utf-8"),
            Pair("The King in Yellow", "https://www.gutenberg.org/ebooks/8492.txt.utf-8"),
            Pair("The Bee-Master of Warrilow", "https://www.gutenberg.org/ebooks/63208.txt.utf-8"),
            Pair("The Yellow Fairy Book", "https://www.gutenberg.org/ebooks/28314.txt.utf-8"),
        );

        val skippedSymbols = listOf(',', '"', '(', ')')
        val skippedWords = listOf<String>()
    }

    private val handler = Handler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService ----> ", "onStartCommand()")
        download()
        return START_STICKY
    }

    private fun download() {

        Log.d("MyService ----> ", "downloading")

        coroutineScope.launch {
            books.map { pair -> coroutineScope.async {
                val bookContent = getRequest(pair.second) ?: "Oops"

                val bookData = BookData(
                    pair.first,
                    countLetters(bookContent),
                    countWords(bookContent),
                    findMostCommonWord(bookContent) ?: "n/a"
                )

                val broadcastIntent = Intent("com.example.DATA_DOWNLOADED")
                bookDataToIntent(bookData, broadcastIntent)
                sendBroadcast(broadcastIntent)
            }}.awaitAll()

            stopSelf()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("MyService ----> ", "onBind()")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        handler.removeCallbacksAndMessages(null)
        Log.d("MyService ----> ", "onDestroy()")
    }

    suspend fun getRequest(url: String): String? = withContext(Dispatchers.IO){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build();

        try{
            client.newCall(request).execute().use { response ->
                if(!response.isSuccessful){
                    Log.d("", "Http Error: ${response.code}")
                }

                response.body?.string().orEmpty()
            }
        }
        catch(exc: IOException){
            Log.d("", "Exception: ${exc.message}")
            return@withContext null
        }
    }


    private fun countWords(text: String): Int{
        return text
            .filterNot { skippedSymbols.plus('\'').contains(it) }
            .split(" ")
            .size
    }

    private fun countLetters(text: String): Int{
        return text.filterNot { skippedSymbols.plus('\'').contains(it) }.length
    }

    private fun findMostCommonWord(text: String): String?{
        val words = text
            .filterNot { skippedSymbols.contains(it) }
            .split(" ")
            .filterNot { it == "" || it == " " }
            // Removes apostrophes
            .map { it.replace("'[a-z]".toRegex(), "") }
            .filterNot { skippedWords.contains(it) }

        return words
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
}