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
import com.google.firebase.auth.FirebaseAuth
import com.example.connect4.service.FirebaseGameService

class GameViewModel : ViewModel() {

    var playerId by mutableStateOf("")
        private set

    var firebaseService: FirebaseGameService? = null
        private set

    var playerRole by mutableStateOf("")
        private set

    init {
        signInAnonymously()
    }

    private fun signInAnonymously() {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { authResult ->
                playerId = authResult.user?.uid ?: ""
            }
    }

    fun initializeOnlineGame(service: FirebaseGameService, role: String) {
        firebaseService = service
        playerRole = role
        listenForRemoteUpdates()
    }

    private fun listenForRemoteUpdates() {
        firebaseService?.listenToBoard { newBoardInts ->
            val newBoard = newBoardInts.map { row ->
                row.map {
                    when (it) {
                        1 -> GameCellState.PLAYER
                        2 -> GameCellState.PLAYER2
                        else -> GameCellState.EMPTY
                    }
                }.toMutableList()
            }
            gameBoard.board = newBoard
            board = newBoard
        }

        firebaseService?.listenToTurn { turn ->
            isPlayerTurn = (turn == playerRole)
            gameState = GameStatus(if (isPlayerTurn) "Your Turn" else "Opponent's Turn")
        }

        firebaseService?.listenToWinner { winner ->
            if (winner != null) {
                gameState = GameStatus("${if (winner == playerRole) "You win!" else "You lose!"}", true)
            }
        }
    }

    var gameBoard by mutableStateOf(GameBoard())
        private set

    var board by mutableStateOf(gameBoard.board)
        private set

    var gameState by mutableStateOf(GameStatus("Waiting for turn..."))
        private set

    var isPlayerTurn by mutableStateOf(false)
        private set

    var isAnimating by mutableStateOf(false)
        private set

    var animatingRow by mutableStateOf(-1)
        private set

    var animatingColumn by mutableStateOf(-1)
        private set

    var animatingPlayer by mutableStateOf(GameCellState.EMPTY)

    var currentWordPair by mutableStateOf<WordPair?>(null)
        private set

    var canPlay by mutableStateOf(false)
        private set

    var showTranslationPrompt by mutableStateOf(true)
        private set

    fun askTranslation() {
        currentWordPair = wordList.random()
        showTranslationPrompt = true
        canPlay = false
    }

    fun checkTranslation(answer: String) {
        val correct = currentWordPair?.spanish?.trim()?.lowercase()
        if (answer.trim().lowercase() == correct) {
            canPlay = true
            showTranslationPrompt = false
        } else {
            isPlayerTurn = !isPlayerTurn
            gameState = GameStatus(if (isPlayerTurn) "Your Turn" else "Opponent's Turn")
            askTranslation()
        }
    }

    fun playMove(col: Int) {
        if (gameState.finished || isAnimating || !canPlay || firebaseService == null || !isPlayerTurn) return

        val row = gameBoard.findDropRow(col) ?: return

        animateDrop(col, row) {
            gameBoard.dropToken(row, col, if (playerRole == "player1") GameCellState.PLAYER else GameCellState.PLAYER2)
            board = gameBoard.board

            val boardInts = gameBoard.board.map { row ->
                row.map {
                    when (it) {
                        GameCellState.PLAYER -> 1
                        GameCellState.PLAYER2 -> 2
                        else -> 0
                    }
                }
            }

            if (VictoryChecker.checkVictory(gameBoard.board, row, col)) {
                gameState = GameStatus("You Win!", true)
                firebaseService?.sendWinner(playerRole)
            } else if (gameBoard.isFull()) {
                gameState = GameStatus("Draw!", true)
                firebaseService?.sendWinner("draw")
            } else {
                isPlayerTurn = false
                gameState = GameStatus("Opponent's Turn")
                askTranslation()
                firebaseService?.sendMove(boardInts, if (playerRole == "player1") "player2" else "player1")
            }
        }
    }

    private fun animateDrop(col: Int, row: Int, onDropComplete: () -> Unit) {
        viewModelScope.launch {
            isAnimating = true
            animatingColumn = col
            animatingPlayer = if (playerRole == "player1") GameCellState.PLAYER else GameCellState.PLAYER2

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

    fun resetGame() {
        gameBoard = GameBoard()
        board = gameBoard.board
        isPlayerTurn = (playerRole == "player1")
        gameState = GameStatus(if (isPlayerTurn) "Your Turn" else "Opponent's Turn")
        canPlay = false
        showTranslationPrompt = true
        askTranslation()
    }
}

data class GameStatus(val statusText: String, val finished: Boolean = false)
