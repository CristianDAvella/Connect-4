package com.example.connect4.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.connect4.viewmodel.GameViewModel
import com.example.connect4.model.GameCellState

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val board = viewModel.board
    val gameState = viewModel.gameState

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = gameState.statusText, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        for (row in 0 until 6) {
            Row {
                for (col in 0 until 7) {
                    val cell = board[row][col]
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .background(
                                color = when (cell) {
                                    GameCellState.PLAYER -> Color.Red
                                    GameCellState.AI -> Color.Yellow
                                    else -> Color.LightGray
                                },
                                shape = CircleShape
                            )
                            .clickable(enabled = viewModel.isPlayerTurn) {
                                viewModel.playMove(col)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.resetGame() }) {
            Text("Restart Game")
        }
    }
}
