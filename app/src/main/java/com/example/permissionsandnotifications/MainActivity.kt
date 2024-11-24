package com.example.permissionsandnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.permissionsandnotifications.ui.theme.PermissionsAndNotificationsTheme

class NotificationHandler{
    companion object {
        const val CHANNEL_ID = "default_channel"
        const val NOTIFICATION_ID = 1
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionsAndNotificationsTheme {
                AppContent(Modifier.fillMaxSize())
            }
        }
    }
}

enum class NotificationStyle(val label: String){
    DEFAULT("Default"),
    BIG_TEXT("Big Text"),
    BIG_PICTURE("Big picture"),
}

enum class SelectableImage(val resource: Int){
    NONE(-1),
    CAT(R.drawable.cat_with_clock),
    DOG(R.drawable.dog_with_clock),
}


@Composable
fun AppContent(modifier: Modifier = Modifier){

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var extendedDescription by remember { mutableStateOf("") }
    var notificationStyle by remember { mutableStateOf( NotificationStyle.DEFAULT ) }
    var selectedImage by remember { mutableStateOf( SelectableImage.NONE ) }

    val textFieldStyle = Modifier.fillMaxWidth()


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
                if(it != SelectableImage.NONE){
                    SelectedImage(
                        selectedImage == it,
                        painterResource(it.resource),
                        Modifier.weight(1f).clickable { selectedImage = it }
                    )
                }
            }
        }

        Button(
            onClick = {
                // Sending notification
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                    return@Button
                }

                val channel = NotificationChannel(
                    NotificationHandler.CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                }
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