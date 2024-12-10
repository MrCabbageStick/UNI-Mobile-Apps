package com.example.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BookBroadcastReceiver(private val onDataReceived: (List<String>) -> Unit): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action != "com.example.DATA_DOWNLOADED") return;

        Log.d("MyBroadcastReceiver", "Data downloaded")

        val dataList = p1.getStringArrayListExtra("BOOKS")

        if(dataList != null){
            onDataReceived(dataList)
        }
    }
}