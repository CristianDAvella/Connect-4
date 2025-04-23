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

//dimensiones de la pantalla
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val topSpacerHeight = if (isLandscape) 16.dp else 170.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B2B34)) // fondo del tablero
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight)) // Espacio arriba que depende de la orientación

        Text(
            text = gameState.statusText,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )




        // Ajustar el tamaño de las celdas basado en la orientación
        val cellSize = if (screenWidth > screenHeight) {
            // En modo horizontal, las celdas pueden ser más pequeñas
            screenWidth / 8 // Proporcional a la pantalla
        } else {
            // En modo vertical, las celdas pueden ser un poco más grandes
            screenWidth / 7 // Proporcional a la pantalla
        }


        for (row in 0 until 6) {
            Row {
                for (col in 0 until 7) {
                    val cell = board[row][col]
                    val isAnimating = row == viewModel.animatingRow && col == viewModel.animatingColumn

                    val cellColor = when {
                        isAnimating && viewModel.animatingPlayer == GameCellState.PLAYER -> Color(0xFFE57373)//Animacion para jugador
                        isAnimating && viewModel.animatingPlayer == GameCellState.AI -> Color(0xFF4DB6AC)//Animacion para IA
                        cell == GameCellState.PLAYER -> Color(0xFFE57373)
                        cell == GameCellState.AI -> Color(0xFF4DB6AC)
                        else -> Color(0xFF0F1A20)
                    }




                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .background(color = cellColor, shape = CircleShape)
                            .clickable(
                                enabled = viewModel.isPlayerTurn && !viewModel.isAnimating
                            ) {
                                viewModel.playMove(col)
                            }
                    )
                }
            }
        }

        Button(onClick = { viewModel.resetGame() }) {
            Text("Restart Game")
        }
    }
}

