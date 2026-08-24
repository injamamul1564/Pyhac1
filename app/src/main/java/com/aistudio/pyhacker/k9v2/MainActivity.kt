package com.aistudio.pyhacker.k9v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.aistudio.pyhacker.k9v2.ui.theme.PyHackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PyHackerTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    Surface {
        Text(text = "Hello PyHacker")
    }
}
