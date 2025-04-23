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
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val board = viewModel.board
    val gameState = viewModel.gameState

    // Get screen dimensions and orientation
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Set top spacer height based on orientation
    val topSpacerHeight = if (isLandscape) 16.dp else 170.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B2B34)) // Set background color for the board
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Add vertical space above the board depending on orientation
        Spacer(modifier = Modifier.height(topSpacerHeight))

        // Display current game status (e.g., turn info, winner message)
        Text(
            text = gameState.statusText,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )

        // Calculate the cell size based on screen orientation
        val cellSize = if (screenWidth > screenHeight) {
            screenWidth / 8 // Smaller cells in landscape
        } else {
            screenWidth / 7 // Larger cells in portrait
        }

        // Iterate over rows and columns to create the game board
        for (row in 0 until 6) {
            Row {
                for (col in 0 until 7) {
                    val cell = board[row][col]
                    val isAnimating = row == viewModel.animatingRow && col == viewModel.animatingColumn

                    // Determine cell color based on its state (empty, player, AI, animation)
                    val cellColor = when {
                        isAnimating && viewModel.animatingPlayer == GameCellState.PLAYER -> Color(0xFFE57373) // Animation for player
                        isAnimating && viewModel.animatingPlayer == GameCellState.AI -> Color(0xFF4DB6AC) // Animation for AI
                        cell == GameCellState.PLAYER -> Color(0xFFE57373) // Static cell for player
                        cell == GameCellState.AI -> Color(0xFF4DB6AC) // Static cell for AI
                        else -> Color(0xFF0F1A20) // Empty cell
                    }

                    // Draw each game cell with clickable action if it's player's turn and not animating
                    Box(
                        modifier = Modifier
                            .size(48.dp) // Fixed size for all cells
                            .padding(4.dp) // Padding around each cell
                            .background(color = cellColor, shape = CircleShape) // Circular cell with specific color
                            .clickable(
                                enabled = viewModel.isPlayerTurn && !viewModel.isAnimating
                            ) {
                                viewModel.playMove(col) // Make a move in the selected column
                            }
                    )
                }
            }
        }

        // Restart button to reset the game state
        Button(onClick = { viewModel.resetGame() }) {
            Text("Restart Game")
        }
    }
}
