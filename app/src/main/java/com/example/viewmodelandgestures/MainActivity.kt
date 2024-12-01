package com.example.viewmodelandgestures

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.viewmodelandgestures.ui.theme.ViewModelAndGesturesTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViewModelAndGesturesTheme {
                AppContent()
            }
        }
    }

    @Composable
    private fun AppContent(){

        val viewModel: AppViewModel by viewModels()

        var currentDisplay by remember { mutableIntStateOf(0) }
        val displayCount = 3

        var totalDrag by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = {change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
//                        if(currentDisplay == 0 && totalDrag > 50){
//                            currentDisplay++
//                        }
//                        else if (currentDisplay == 1){
//                            if(totalDrag > 50){ currentDisplay++ }
//                            else if (totalDrag < -50){ currentDisplay-- }
//                        }
//                        else if (currentDisplay == 2 && totalDrag < -50){
//                            currentDisplay--
//                        }
                        if(currentDisplay <= 1 && totalDrag > 50){
                            currentDisplay++
                        }
                        else if (currentDisplay >= 1 && totalDrag < -50){
                            currentDisplay--
                        }

                        totalDrag = 0f
                    }
                )
            }
        ) {
            var showNDisplays by remember { mutableIntStateOf(1) }

            val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            val verticalScreenModifier = Modifier.fillMaxSize().statusBarsPadding()
            val horizontalScreenModifier = Modifier.fillMaxHeight()


            if(isVertical){
                when(currentDisplay){
                    0 -> Screen3(viewModel, verticalScreenModifier)
                    1 -> Screen2(viewModel, verticalScreenModifier)
                    2 -> Screen1(viewModel, verticalScreenModifier)
                }
            }
            else{
                Column (
                    modifier = Modifier.statusBarsPadding()
                ){
                    ScreenDropdownMenu(displayCount) {
                        showNDisplays = it
                    }

                    Row (
                        modifier = Modifier.fillMaxSize()
                    ){
                        if(showNDisplays >= 1) Screen1(viewModel, horizontalScreenModifier.weight(1f))
                        if(showNDisplays >= 2) Screen2(viewModel, horizontalScreenModifier.weight(1f))
                        if(showNDisplays >= 3) Screen3(viewModel, horizontalScreenModifier.weight(1f))
                    }
                }
            }
        }
    }

    @Composable
    private fun ScreenDropdownMenu(screenCount: Int, onScreenSelect: (selected: Int) -> Unit){
        var isExpanded by remember { mutableStateOf(false) }

        Box{
            Button(onClick = { isExpanded = true }) {
                Text("Select screen count")
            }

            DropdownMenu(isExpanded, { isExpanded = false }) {
                (1..screenCount).forEach{
                    DropdownMenuItem(
                        text = { Text("$it") },
                        onClick = {
                            onScreenSelect(it)
                            isExpanded = false;
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun Screen1(viewModel: AppViewModel, modifier: Modifier = Modifier){
        val phoneNumber = viewModel.phoneNumber.collectAsState().value
        val message = viewModel.message.collectAsState().value

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.background(Color(0xff9ea4d8)),
        ){
            TextField(
                value = phoneNumber,
                onValueChange = { viewModel.setPhoneNumber(it) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(.9f),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
            )

            TextField(
                value = message,
                onValueChange = { viewModel.setMessage(it) },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(.9f),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
            )
        }
    }

    @Composable
    private fun Screen2(viewModel: AppViewModel, modifier: Modifier = Modifier){
        val phoneNumber = viewModel.phoneNumber.collectAsState().value
        val message = viewModel.message.collectAsState().value

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.background(Color(0xffa5d89e)),
        ) {
            Text("Phone Number: $phoneNumber")
            Text("Message:  $message")
        }
    }

    @Composable
    private fun Screen3(viewModel: AppViewModel, modifier: Modifier = Modifier){
        val phoneNumber = viewModel.phoneNumber.collectAsState().value
        val message = viewModel.message.collectAsState().value

        val context = LocalContext.current

        Column(
            modifier = modifier.background(Color(0xffd89e9e)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = { sendSMS(context, phoneNumber, message) }
            ){
                Text("\uD83D\uDC80 S E N D \uD83D\uDC80")
            }
        }
    }

    private fun sendSMS(context: Context, phoneNumber: String, message: String){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:$phoneNumber")
            putExtra("sms_body", message)
        }

        if(intent.resolveActivity(packageManager) == null){
            Toast.makeText(
                context,
                "Unable to open messaging app",
                Toast.LENGTH_SHORT).show()

            return
        }

        startActivity(intent)
    }
}
