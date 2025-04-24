package com.example.connect4.model

class GameBoard {
    var board: List<MutableList<GameCellState>> = List(6) { MutableList(7) { GameCellState.EMPTY } }
        private set

    fun reset() {
        board = List(6) { MutableList(7) { GameCellState.EMPTY } }
    }

    fun findDropRow(col: Int): Int? {
        return board.indexOfLast { it[col] == GameCellState.EMPTY }.takeIf { it != -1 }
    }

    fun dropToken(row: Int, col: Int, player: GameCellState) {
        board[row][col] = player
    }

    fun getAvailableColumns(): List<Int> {
        return (0..6).filter { col -> board.any { it[col] == GameCellState.EMPTY } }
    }

    fun isFull(): Boolean {
        return board.all { row -> row.none { it == GameCellState.EMPTY } }
    }
}
