package com.example.connect4.model

object VictoryChecker {
    fun checkVictory(
        board: List<List<GameCellState>>,
        row: Int,
        col: Int
    ): Boolean {
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
}
