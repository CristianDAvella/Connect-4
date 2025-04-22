package com.example.connect4.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.connect4.ui.theme.Connect4Theme
import com.example.connect4.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Connect4Theme {
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
