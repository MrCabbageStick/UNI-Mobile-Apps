package com.example.firstmobileapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.firstmobileapp.ui.theme.FirstMobileAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var inputText by remember { mutableStateOf("") }
            var displayText by remember { mutableStateOf("") }

            val localContext = LocalContext.current;

            FirstMobileAppTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("First app") })
                    },
                    modifier = Modifier.fillMaxSize()
                ){innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxHeight(),
//                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Greeting(
                            name = "there, old sport",
                            Modifier
                                .padding(innerPadding)
                                .fillMaxHeight(.1f)
                        )

                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.6f)
                        ){
                            val texts = listOf("Jetpack", "Works", "Almost", "Like", "React")
                            for (text in texts){
                                Text(text)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxHeight(.30f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextField(
                                value = inputText, 
                                onValueChange = { inputText = it },
                                modifier = Modifier
                                    .fillMaxWidth(.5f)
                            )
                            Button(
                                onClick = {
                                    Toast.makeText(localContext, inputText, Toast.LENGTH_SHORT).show();
                                    displayText = inputText
                                }
                                ) {
                                Text(text = "Click me")
                            }
                        }

                        Text(text = displayText)

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirstMobileAppTheme {
        Greeting("Android")
    }
}