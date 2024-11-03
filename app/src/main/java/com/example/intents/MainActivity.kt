package com.example.intents

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.intents.ui.theme.IntentsTheme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var field1Value by remember { mutableStateOf("") }
            var field2Value by remember { mutableStateOf("") }

            val currentContext = LocalContext.current

            IntentsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        TextField(
                            value = field1Value,
                            onValueChange = { field1Value = it },
                            placeholder = { Text(text = "SMS number / Address / Email / Contact 1 / Event date and time") }
                        )

                        TextField(
                            value = field2Value,
                            onValueChange = { field2Value = it },
                            placeholder = { Text(text = "SMS content / Mail title / Phone number / Contact 2 / Event title") }
                        )

                        // Buttons

//                        Button(onClick = { sendSMS(field1Value, field2Value) }) {
//                            Text(text = "Send SMS")
//                        }
                        IntentButton(text = "Send SMS") { sendSMS(field1Value, field2Value) }
                        IntentButton(text = "Find a place") { openMap(field1Value) }
                        IntentButton(text = "Create a document") { createDocument() }
                        IntentButton(text = "Send email") { sendEmail(field1Value, field2Value) }
                        IntentButton(text = "Make a call") { dial(field2Value) }
                        IntentButton(text = "Open music app") { launchMusicApp() }
                        IntentButton(text = "Second activity") { openSecondActivity(listOf(field1Value, field2Value)) }
                        IntentButton(text = "Add event") { addCalendarEvent(field2Value, field1Value) }
                        IntentButton(text = "Close app") { (currentContext as? Activity)?.finish() }

                    }
                }
            }
        }
    }

    // Button handlers
    fun sendSMS(number: String, content: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:$number")
            putExtra("sms_body", content)
        }

        startActivity(intent)
    }

    fun openMap(location: String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$location")
        }

        startActivity(intent)
    }

    fun createDocument(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        if(intent.resolveActivity(packageManager) == null){
            Toast.makeText(applicationContext, "Unable to locate document application", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(intent);

    }

    fun sendEmail(address: String, subject: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        startActivity(intent)
    }

    fun dial(number: String){
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }

        startActivity(intent)
    }

    fun launchMusicApp(){
        val intent = Intent(Intent.CATEGORY_APP_MUSIC)

        startActivity(intent)
    }

    fun addCalendarEvent(title: String, dateAndTime: String){

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val date = formatter.parse(dateAndTime)

        if(date == null){
            Toast.makeText(applicationContext, "Incorrect date time format", Toast.LENGTH_SHORT).show()
            return;
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Events.CONTENT_URI
            putExtra(Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
        }

        startActivity(intent)
    }

    fun openSecondActivity(contacts: List<String>){
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("contacts", contacts.toTypedArray())
        }

        startActivity(intent)
    }

}

@Composable
fun IntentButton(text: String, onClick: ()->Unit){
    Button(onClick = onClick) {
        Text(text = text)
    }
}