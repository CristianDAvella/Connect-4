package com.example.connect4.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.connect4.service.FirebaseGameService
import com.example.connect4.ui.theme.Connect4Theme
import com.example.connect4.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Connect4Theme {
                val navController = rememberNavController()
                var firebaseService by remember { mutableStateOf<FirebaseGameService?>(null) }
                var playerRole by remember { mutableStateOf("") }

                NavHost(navController = navController, startDestination = "lobby") {
                    composable("lobby") {
                        LobbyScreen(
                            playerId = viewModel.playerId,
                            navController = navController,
                            viewModel = viewModel,
                            onServiceReady = { service, role ->
                                firebaseService = service
                                playerRole = role
                            }
                        )
                    }
                    composable("game") {
                        GameScreen(
                            viewModel = viewModel,
                            firebaseService = firebaseService!!,
                            playerRole = playerRole
                        )
                    }
                }
            }
        }
    }
}

