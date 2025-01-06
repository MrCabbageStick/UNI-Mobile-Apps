package com.example.broadcasts

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.broadcasts.ui.theme.BroadcastsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BroadcastsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppContent(Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return
        }

        val channelId = "default_channel"
        val channelName = "uni_app_notification_channel"
        val channelDescription = "Notification channel for uni apps"
        val channelImportance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun checkNotificationPermission(context: Context): Boolean{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        val permissionState = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )

        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    return true
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun postNotification(context: Context){
    val notificationBuilder = NotificationCompat.Builder(context, "default_channel").apply {
        setContentTitle("A new Book just finished downloading!")
        setSmallIcon(R.mipmap.ic_launcher_round)
    }

    with(NotificationManagerCompat.from(context)){
        notify(1, notificationBuilder.build())
    }
}


@SuppressLint("UnspecifiedRegisterReceiverFlag")
@Composable
fun AppContent(modifier: Modifier = Modifier){

    var books by rememberSaveable { mutableStateOf(emptySet<BookData>()) }

    val context = LocalContext.current
    val downloadBooksIntent = Intent(context, BookDownloadService::class.java)


    DisposableEffect(Unit) {
        val receiver = BookBroadcastReceiver {
            books = books.plus(it)

            if(!checkNotificationPermission(context)){
                Toast.makeText(context, "⛔⛔⛔", Toast.LENGTH_SHORT).show()
            }
            else {
                postNotification(context)
            }
        }
        val intentFilter = IntentFilter("com.example.DATA_DOWNLOADED")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            context.registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
            Log.d("[Register Receiver]", "Receiver Registered")
        }
        else{
            context.registerReceiver(receiver, intentFilter)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }


    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                context.startService(downloadBooksIntent)
                Log.d("[Button]", "Clicked")
            }
        ) {
            Text("\uD83D\uDCE5\uD83D\uDCD6Download a Book\uD83D\uDCD6\uD83D\uDCE5")
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .height(96.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                TableCell("Title")
                TableCell("Letter Count")
                TableCell("Word count")
                TableCell("Most common word")
            }


            LazyColumn {
                items(books.toList()){
                    Row(
                        Modifier.height(128.dp)
                    ) {
                        TableCell(it.title)
                        TableCell(it.letterCount.toString())
                        TableCell(it.wordCount.toString())
                        TableCell(it.mostCommonWord)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String){
    Text(
        text = text,
        modifier = Modifier
            .weight(1f)
            .border(1.dp, Color.DarkGray)
            .padding(8.dp)
            .fillMaxHeight(),
    )
}