package com.example.intents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.intents.ui.theme.IntentsTheme

class SecondActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedContacts = intent.getStringArrayExtra("contacts")

        setContent {
            SecondScreen(contacts = receivedContacts?.asList() ?: listOf() )
        }
    }

    @Composable
    fun SecondScreen(contacts: List<String>){
        IntentsTheme {
            // A surface container using the 'background' color from the theme
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                val currentContext = LocalContext.current

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var nContacts = 1
                    contacts.forEach { contact ->
                        Text(text = "Contact ${nContacts++}: $contact")
                    }

                    Button(onClick = {
                        (currentContext as? Activity)?.finish()
                    }) {
                        Text(text = "Close activity")
                    }

                    Button(onClick = {
                        openMainActivity()
                    }) {
                        Text(text = "Open main activity")
                    }
                }

            }
        }
    }

    fun openMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}