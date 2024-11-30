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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.example.intents.ui.theme.IntentsTheme
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.Date
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
                            placeholder = { Text(text = "SMS number / Address / Email / Contact 1 / Event date and time") },
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )

                        TextField(
                            value = field2Value,
                            onValueChange = { field2Value = it },
                            placeholder = { Text(text = "SMS content / Mail title / Phone number / Contact 2 / Event title") },
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )

                        IntentButton(text = "Send SMS") { sendSMS(field1Value, field2Value) } // Works
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

    private fun resolve(intent: Intent, errorMessage: String): Boolean{
        if(intent.resolveActivity(packageManager) == null){
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            return false;
        }

        return true;
    }

    // Button handlers
    private fun sendSMS(number: String, content: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:$number")
            putExtra("sms_body", content)
        }

        if(resolve(intent, "SMS application not available")){
            startActivity(intent);
        }
    }

    private fun openMap(location: String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$location")
        }

        // For some reason it cannot resolve the intent
        // Yet application opens properly
//        if(resolve(intent, "Map application not available")){
//            startActivity(intent);
//        }

        startActivity(intent);
    }

    private fun createDocument(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        if(resolve(intent, "Unable to locate document application")){
            startActivity(intent);
        }
    }

    private fun sendEmail(address: String, subject: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "vnd.android.cursor.item/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if(resolve(intent, "Email app not available")) {
            startActivity(intent)
        }
    }

    private fun dial(number: String){
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }

        if(resolve(intent, "Phone application not available")){
            startActivity(intent)
        }
    }

    private fun launchMusicApp(){
        val intent = Intent(Intent.ACTION_MAIN).apply{
            addCategory(Intent.CATEGORY_APP_MUSIC)
        }

        startActivity(intent)
    }

    private fun addCalendarEvent(title: String, dateAndTime: String){

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        var date: Date? = null;

        try {
            date = formatter.parse(dateAndTime)
        }
        catch (err: ParseException){
            Toast.makeText(applicationContext, "Incorrect date time format", Toast.LENGTH_SHORT).show()
            return;
        }

        if(date == null){
            Toast.makeText(applicationContext, "Incorrect date time format", Toast.LENGTH_SHORT).show()
            return;
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Events.CONTENT_URI
            putExtra(Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
        }

        if(resolve(intent, "Unable to find a calendar application")){
            startActivity(intent)
        }
    }

    private fun openSecondActivity(contacts: List<String>){
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("contacts", contacts.toTypedArray())
        }

        if(resolve(intent, "Unable to launch seconds activity")){
            startActivity(intent)
        }
    }

}

@Composable
fun IntentButton(text: String, onClick: ()->Unit){
    Button(onClick = onClick) {
        Text(text = text)
    }
}