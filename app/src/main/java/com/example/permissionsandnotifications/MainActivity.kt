package com.example.permissionsandnotifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.permissionsandnotifications.ui.theme.PermissionsAndNotificationsTheme

data class NotificationData(
    val context: Context,
    val title: String,
    val description: String,
    @DrawableRes val icon: Int,
    val style: NotificationStyle,
    val bigPicture: Bitmap,
    val extendedDescription: String,
)

data class AlarmData(
    val hour: Int,
    val minutes: Int,
)

data class HourMinutes(
    val hour: Int,
    val minutes: Int,
)

class MainActivity : ComponentActivity() {

    companion object {
        const val CHANNEL_ID = "default_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onStart() {
        super.onStart()
        createNotificationChannel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PermissionsAndNotificationsTheme {
                AppContent(Modifier.fillMaxSize())
            }
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return
        }

        val channelName = "uni_app_notification_channel"
        val channelDescription = "Notification channel for uni apps"
        val channelImportance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotificationPermission(): Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val permissionState = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )

            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(notificationData: NotificationData, alarmData: AlarmData){
        val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "To dusza gryzonia czy obłęd?")
            putExtra(AlarmClock.EXTRA_HOUR, alarmData.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, alarmData.minutes)
        }

        val alarmPendingIntent = PendingIntent.getActivity(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(notificationData.context, CHANNEL_ID).apply {
            setSmallIcon(notificationData.icon)
            setContentTitle(notificationData.title)
            setContentText(notificationData.description)
            setPriority(NotificationCompat.PRIORITY_HIGH)

            addAction(
                R.drawable.cat_with_clock,
                "Set new Alarm",
                alarmPendingIntent
            )


            when(notificationData.style){
                NotificationStyle.DEFAULT -> {}
                NotificationStyle.BIG_TEXT -> setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(notificationData.extendedDescription)
                )
                NotificationStyle.BIG_PICTURE -> setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(notificationData.bigPicture)
                )
            }
        }

        with(NotificationManagerCompat.from(notificationData.context)){
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    @Composable
    fun AppContent(modifier: Modifier = Modifier){

        var permissionGranted by remember { mutableStateOf(checkNotificationPermission()) }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionGranted = isGranted

            if(!isGranted){
                Toast.makeText(
                    this,
                    "Notification permission not granted. App will be unable to show notifications",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var extendedDescription by remember { mutableStateOf("") }
        var notificationStyle by remember { mutableStateOf( NotificationStyle.DEFAULT ) }
        var selectedImage by remember { mutableStateOf( SelectableImage.CAT ) }

        val textFieldStyle = Modifier.fillMaxWidth()

        var time by remember { mutableStateOf(HourMinutes(0, 0)) }

        val context = LocalContext.current

        Column(
            modifier = modifier.statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text related notification thingies
            TextField(title, onValueChange = { title = it }, placeholder = { Text("Title") }, modifier = textFieldStyle)
            TextField(description, onValueChange = { description = it }, placeholder = { Text("Description") }, modifier = textFieldStyle)
            TextField(extendedDescription, onValueChange = { extendedDescription = it }, placeholder = { Text("Extended description") }, modifier = textFieldStyle)

            // Style selection
            NotificationStyle.entries.forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            notificationStyle = it
                        }
                ) {
                    RadioButton(
                        it == notificationStyle,
                        onClick = { notificationStyle = it }
                    )

                    Text(it.label)
                }
            }

            // Image selection
            Row (
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ){
                SelectableImage.entries.forEach {
                    SelectedImage(
                        selectedImage == it,
                        painterResource(it.resource),
                        Modifier.weight(1f).clickable { selectedImage = it }
                    )
                }
            }

            // Alarm time selection
            val timeDialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    time = HourMinutes(hour, minute)
                },
                0,
                0,
                true
            )

            Button(onClick = { timeDialog.show() }) {
                Text("Select alarm time")
            }

            Text("Select time: ${time.hour}:${time.minutes}")

            // Send notification button
            Button(
                onClick = {
                    if(!permissionGranted){
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        return@Button
                    }

                    sendNotification(
                        NotificationData(
                            context, title, description,
                            selectedImage.resource,
                            notificationStyle,
                            BitmapFactory.decodeResource(context.resources, selectedImage.resource),
                            extendedDescription
                        ),
                        AlarmData(time.hour, time.minutes),
                    )
                },
                modifier = Modifier.fillMaxWidth(.7f)
            ) {
                Text("Notify Me!")
            }

        }
    }

    @Composable
    fun SelectedImage(selected: Boolean, painter: Painter, modifier: Modifier = Modifier){
        Image(
            painter = painter,
            null,
            modifier =
            if(selected) modifier.border(4.dp, Color.Cyan, RectangleShape)
            else modifier
        )
    }
}

enum class NotificationStyle(val label: String){
    DEFAULT("Default"),
    BIG_TEXT("Big Text"),
    BIG_PICTURE("Big picture"),
}

enum class SelectableImage(val resource: Int){
    CAT(R.drawable.cat_with_clock),
    DOG(R.drawable.dog_with_clock),
}