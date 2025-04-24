package com.example.connect4

import com.example.connect4.model.GameCellState
import com.example.connect4.model.VictoryChecker
import org.junit.Assert.*
import org.junit.Test

class VictoryCheckerTest {
    @Test
    fun testHorizontalVictory() {
        val board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        for (i in 0..3) board[5][i] = GameCellState.PLAYER
        assertTrue(VictoryChecker.checkVictory(board, 5, 3))
    }

    @Test
    fun testVerticalVictory() {
        val board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        for (i in 2..5) board[i][0] = GameCellState.PLAYER
        assertTrue(VictoryChecker.checkVictory(board, 5, 0))
    }

    @Test
    fun testDiagonalVictory() {
        val board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        board[2][0] = GameCellState.PLAYER
        board[3][1] = GameCellState.PLAYER
        board[4][2] = GameCellState.PLAYER
        board[5][3] = GameCellState.PLAYER
        assertTrue(VictoryChecker.checkVictory(board, 5, 3))
    }

    @Test
    fun testAntiDiagonalVictory() {
        val board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        board[2][6] = GameCellState.PLAYER
        board[3][5] = GameCellState.PLAYER
        board[4][4] = GameCellState.PLAYER
        board[5][3] = GameCellState.PLAYER
        assertTrue(VictoryChecker.checkVictory(board, 5, 3))
    }

    @Test
    fun testNoVictory() {
        val board = List(6) { MutableList(7) { GameCellState.EMPTY } }
        board[5][0] = GameCellState.PLAYER
        board[5][1] = GameCellState.AI
        board[5][2] = GameCellState.PLAYER
        board[5][3] = GameCellState.AI
        assertFalse(VictoryChecker.checkVictory(board, 5, 3))
    }
}
