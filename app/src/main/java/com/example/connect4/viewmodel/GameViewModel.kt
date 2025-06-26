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
import com.example.connect4.model.WordPair
import com.example.connect4.model.wordList


class GameViewModel : ViewModel() {

    // Estado del tablero
    var gameBoard by mutableStateOf(GameBoard())
        private set

    var board by mutableStateOf(gameBoard.board)
        private set

    // Estado del juego
    var gameState by mutableStateOf(GameStatus("Player's Turn"))
        private set

    var isPlayerTurn by mutableStateOf(true)
        private set

    var isAnimating by mutableStateOf(false)
        private set

    var animatingRow by mutableStateOf(-1)
        private set

    var animatingColumn by mutableStateOf(-1)
        private set

    var animatingPlayer by mutableStateOf(GameCellState.EMPTY)

    // Pregunta de traducción
    var currentWordPair by mutableStateOf<WordPair?>(null)
        private set

    var canPlay by mutableStateOf(false)
        private set

    var showTranslationPrompt by mutableStateOf(true)
        private set

    // Función para iniciar una nueva pregunta
    fun askTranslation() {
        currentWordPair = wordList.random()
        showTranslationPrompt = true
        canPlay = false
    }

    // Función para verificar si la respuesta es correcta
    fun checkTranslation(answer: String) {
        val correct = currentWordPair?.spanish?.trim()?.lowercase()
        if (answer.trim().lowercase() == correct) {
            canPlay = true
            showTranslationPrompt = false
        } else {
            isPlayerTurn = !isPlayerTurn
            gameState = GameStatus(if (isPlayerTurn) "Player's Turn" else "Player 2's Turn")
            askTranslation()
        }
    }

    // Jugada del jugador (solo si puede jugar)
    fun playMove(col: Int) {
        if (gameState.finished || isAnimating || !canPlay) return

        val row = gameBoard.findDropRow(col) ?: return

        animateDrop(col, row) {
            gameBoard.dropToken(row, col, if (isPlayerTurn) GameCellState.PLAYER else GameCellState.PLAYER2)
            board = gameBoard.board

            if (VictoryChecker.checkVictory(gameBoard.board, row, col)) {
                gameState = GameStatus("${if (isPlayerTurn) "Player" else "Player 2"} Wins!", true)
            } else if (gameBoard.isFull()) {
                gameState = GameStatus("Draw!", true)
            } else {
                isPlayerTurn = !isPlayerTurn
                gameState = GameStatus(if (isPlayerTurn) "Player's Turn" else "Player 2's Turn")
                askTranslation()
            }
        }
    }

    // Animación del token que cae
    private fun animateDrop(col: Int, row: Int, onDropComplete: () -> Unit) {
        viewModelScope.launch {
            isAnimating = true
            animatingColumn = col
            animatingPlayer = if (isPlayerTurn) GameCellState.PLAYER else GameCellState.PLAYER2

            for (r in 0..row) {
                animatingRow = r
                delay(50)
            }

            isAnimating = false
            animatingRow = -1
            animatingColumn = -1
            onDropComplete()
        }
    }

    // Reiniciar el juego
    fun resetGame() {
        gameBoard = GameBoard()
        board = gameBoard.board
        isPlayerTurn = true
        gameState = GameStatus("Player's Turn")
        canPlay = false
        showTranslationPrompt = true
        askTranslation()
    }
}


data class GameStatus(val statusText: String, val finished: Boolean = false)