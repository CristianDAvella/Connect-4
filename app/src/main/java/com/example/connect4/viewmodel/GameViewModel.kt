package com.example.connect4.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.connect4.model.GameCellState

class GameViewModel : ViewModel() {

    var board by mutableStateOf(List(6) { MutableList(7) { GameCellState.EMPTY } })
        private set

    var isPlayerTurn by mutableStateOf(true)
        private set

    var gameState by mutableStateOf(GameStatus("Player's Turn"))
        private set

    fun playMove(col: Int) {
        if (gameState.finished) return

        val row = board.indexOfLast { it[col] == GameCellState.EMPTY }
        if (row == -1) return

        board[row][col] = if (isPlayerTurn) GameCellState.PLAYER else GameCellState.AI

        if (checkVictory(row, col)) {
            gameState = GameStatus("${if (isPlayerTurn) "Player" else "AI"} Wins!", true)
        } else if (isBoardFull()) {
            gameState = GameStatus("Draw!", true)
        } else {
            isPlayerTurn = !isPlayerTurn
            if (!isPlayerTurn) {
                aiMove()
            } else {
                gameState = GameStatus("Player's Turn")
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
