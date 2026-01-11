package com.valentinerutto.divinedatagpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinerutto.divinedatagpt.ui.theme.DivineDataGPTTheme
import com.valentinerutto.divinedatagpt.ui.theme.screens.ChatBubble
import com.valentinerutto.divinedatagpt.ui.theme.screens.ChatBubbleScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.ChatMessage
import com.valentinerutto.divinedatagpt.ui.theme.screens.ChatPageScreen
import com.valentinerutto.divinedatagpt.ui.theme.screens.EmotionScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DivineDataGPTTheme {
                Scaffold(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    innerPadding ->
                           EmotionScreen(modifier = Modifier.padding(innerPadding))
                   // ChatBubbleScreen(listOf(ChatMessage("hello")), onSend = ({}))
                    //ChatPageScreen(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}
@Composable
fun SpiritRoute(viewModel: DivineDataViewModel = viewModel()) {
    val model = viewModel.uiModel

  // EmotionScreen()
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
    DivineDataGPTTheme {
        Greeting("Android")
    }
}