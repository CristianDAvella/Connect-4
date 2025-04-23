package com.example.connect4.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.connect4.model.GameCellState
import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope


class GameViewModel : ViewModel() {

    var board by mutableStateOf(List(6) { MutableList(7) { GameCellState.EMPTY } })
        private set

    var isPlayerTurn by mutableStateOf(true)
        private set

    var gameState by mutableStateOf(GameStatus("Player's Turn"))
        private set

    // Row index for the currently animating drop (initially -1, meaning no animation).
    var animatingRow by mutableStateOf(-1)
        private set

    // Column index for the currently animating drop (initially -1).
    var animatingColumn by mutableStateOf(-1)
        private set

    // Boolean to track if the drop animation is in progress.
    var isAnimating by mutableStateOf(false)
        private set

    // The player (either PLAYER or AI) who is currently animating the drop.
    var animatingPlayer: GameCellState? by mutableStateOf(null)
        private set

    // Function that executes when a player plays a move.
    fun playMove(col: Int) {
        if (gameState.finished || isAnimating) return

        // Find the lowest empty row in the selected column.
        val row = board.indexOfLast { it[col] == GameCellState.EMPTY }

        if (row == -1) return

        animateDrop(col, row) {
            board[row][col] = if (isPlayerTurn) GameCellState.PLAYER else GameCellState.AI

            if (checkVictory(row, col)) {
                gameState = GameStatus("${if (isPlayerTurn) "Player" else "AI"} Wins!", true)
            } else if (isBoardFull()) {
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
        val available = (0..6).filter { col -> board.any { it[col] == GameCellState.EMPTY } }
        if (available.isNotEmpty()) {
            val randomCol = available.random()
            playMove(randomCol)
        }
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.none { it == GameCellState.EMPTY } }
    }


    // Function to animate the drop of a piece in the selected column and row.
    fun animateDrop(column: Int, finalRow: Int, onComplete: () -> Unit) {
        // Set the player (either PLAYER or AI) that is currently animating the drop.
        animatingPlayer = if (isPlayerTurn) GameCellState.PLAYER else GameCellState.AI
        isAnimating = true

        // Use a coroutine to animate the drop.
        viewModelScope.launch {
            for (row in 0..finalRow) {
                animatingRow = row
                animatingColumn = column
                delay(100) // Simulate animation with a delay.
            }
            // After animation completes, reset animation state.
            animatingRow = -1
            animatingColumn = -1
            isAnimating = false
            animatingPlayer = null
            onComplete() // Call the provided callback when the animation is finished.
        }
    }

    private fun checkVictory(row: Int, col: Int): Boolean {
        val player = board[row][col]
        fun count(deltaRow: Int, deltaCol: Int): Int {
            var r = row + deltaRow
            var c = col + deltaCol
            var count = 0
            while (r in 0..5 && c in 0..6 && board[r][c] == player) {
                count++
                r += deltaRow
                c += deltaCol
            }
            return count
        }

        return listOf(
            count(0, -1) + count(0, 1),
            count(-1, 0) + count(1, 0),
            count(-1, -1) + count(1, 1),
            count(-1, 1) + count(1, -1)
        ).any { it >= 3 }
    }

    fun resetGame() {
        board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        isPlayerTurn = true
        gameState = GameStatus("Player's Turn")
    }
}


data class GameStatus(val statusText: String, val finished: Boolean = false)