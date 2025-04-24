package com.example.connect4.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connect4.model.GameBoard
import com.example.connect4.model.GameCellState
import com.example.connect4.model.VictoryChecker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val gameBoard = GameBoard()

    var board by mutableStateOf(gameBoard.board)
        private set

    var isPlayerTurn by mutableStateOf(true)
        private set

    var gameState by mutableStateOf(GameStatus("Player's Turn"))
        private set

    var animatingRow by mutableStateOf(-1)
        private set

    var animatingColumn by mutableStateOf(-1)
        private set

    var isAnimating by mutableStateOf(false)
        private set

    var animatingPlayer: GameCellState? by mutableStateOf(null)
        private set

    fun playMove(col: Int) {
        if (gameState.finished || isAnimating) return

        val row = gameBoard.findDropRow(col) ?: return

        animateDrop(col, row) {
            gameBoard.dropToken(row, col, if (isPlayerTurn) GameCellState.PLAYER else GameCellState.AI)
            board = gameBoard.board

            if (VictoryChecker.checkVictory(gameBoard.board, row, col)) {
                gameState = GameStatus("${if (isPlayerTurn) "Player" else "AI"} Wins!", true)
            } else if (gameBoard.isFull()) {
                gameState = GameStatus("Draw!", true)
            } else {
                isPlayerTurn = !isPlayerTurn
                if (!isPlayerTurn) {
                    gameState = GameStatus("AI's Turn")
                    aiMove()
                } else {
                    gameState = GameStatus("Player's Turn")
                }
            }
        }
    }

    private fun aiMove() {
        val available = gameBoard.getAvailableColumns()
        if (available.isNotEmpty()) {
            val randomCol = available.random()
            playMove(randomCol)
        }
    }

    fun animateDrop(column: Int, finalRow: Int, onComplete: () -> Unit) {
        animatingPlayer = if (isPlayerTurn) GameCellState.PLAYER else GameCellState.AI
        isAnimating = true

        viewModelScope.launch {
            for (row in 0..finalRow) {
                animatingRow = row
                animatingColumn = column
                delay(100)
            }
            animatingRow = -1
            animatingColumn = -1
            isAnimating = false
            animatingPlayer = null
            onComplete()
        }
    }

    fun resetGame() {
        gameBoard.reset()
        board = gameBoard.board
        isPlayerTurn = true
        gameState = GameStatus("Player's Turn")
    }
}

data class GameStatus(val statusText: String, val finished: Boolean = false)