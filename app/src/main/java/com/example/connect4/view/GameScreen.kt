package com.example.connect4.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.connect4.viewmodel.GameViewModel
import com.example.connect4.model.GameCellState
import com.example.connect4.service.FirebaseGameService

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    firebaseService: FirebaseGameService,
    playerRole: String
) {
    val board = viewModel.board
    val gameState = viewModel.gameState

    LaunchedEffect(Unit) {
        viewModel.askTranslation()
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val topSpacerHeight = if (isLandscape) 16.dp else 170.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B2B34))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight))

        Text(
            text = gameState.statusText,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        if (viewModel.showTranslationPrompt && !gameState.finished) {
            val word = viewModel.currentWordPair
            var answer by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Translate: ${word?.english ?: ""}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Enter translation") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2A3A44),
                        unfocusedContainerColor = Color(0xFF2A3A44),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    viewModel.checkTranslation(answer)
                    answer = ""
                }) {
                    Text("Submit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        for (row in 0 until 6) {
            Row {
                for (col in 0 until 7) {
                    val cell = board[row][col]
                    val isAnimating = row == viewModel.animatingRow && col == viewModel.animatingColumn

                    val cellColor = when {
                        isAnimating && viewModel.animatingPlayer == GameCellState.PLAYER -> Color(0xFFE57373)
                        isAnimating && viewModel.animatingPlayer == GameCellState.PLAYER2 -> Color(0xFF4DB6AC)
                        cell == GameCellState.PLAYER -> Color(0xFFE57373)
                        cell == GameCellState.PLAYER2 -> Color(0xFF4DB6AC)
                        else -> Color(0xFF0F1A20)
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .background(color = cellColor, shape = CircleShape)
                            .clickable(
                                enabled = viewModel.canPlay && !viewModel.isAnimating &&
                                        ((playerRole == "player1" && viewModel.isPlayerTurn) ||
                                                (playerRole == "player2" && !viewModel.isPlayerTurn))
                            ) {
                                viewModel.playMove(col)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.resetGame() }) {
            Text("Restart Game")
        }
    }
}
