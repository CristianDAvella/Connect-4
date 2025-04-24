package com.example.connect4

import com.example.connect4.model.GameBoard
import com.example.connect4.model.GameCellState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameBoardTest {
    private lateinit var board: GameBoard

    @Before
    fun setup() {
        board = GameBoard()
    }

    @Test
    fun testDropToken() {
        val col = 3
        val row = board.findDropRow(col)!!
        board.dropToken(row, col, GameCellState.PLAYER)
        assertEquals(GameCellState.PLAYER, board.board[row][col])
    }

    @Test
    fun testIsFullFalse() {
        assertFalse(board.isFull())
    }

    @Test
    fun testFindDropRow() {
        val row = board.findDropRow(0)
        assertEquals(5, row)
    }

    @Test
    fun testGetAvailableColumns() {
        val available = board.getAvailableColumns()
        assertEquals(7, available.size)
    }
}