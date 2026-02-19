package com.valentinerutto.divinedatagpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.valentinerutto.divinedatagpt.ui.theme.DivineDataGPTTheme
import com.valentinerutto.divinedatagpt.util.NavGraph

class MainActivity : ComponentActivity() {
    val viewModel: DivineDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DivineDataGPTTheme {

                val navController = rememberNavController()
                NavGraph(navController = navController)
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
    DivineDataGPTTheme {
        Greeting("Android")
    }
}