package com.example.connect4.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseGameService(val roomId: String, private val playerId: String) {

    private val db = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)

    var isConnected: Boolean = false
        private set

    fun joinGame(onAssignedRole: (String) -> Unit, onStatus: (String) -> Unit) {
        onStatus("Connecting to room: $roomId...")
        db.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val players = snapshot.children.map { it.value as String }
                when {
                    players.contains(playerId) -> {
                        val role = if (snapshot.child("player1").value == playerId) "player1" else "player2"
                        onAssignedRole(role)
                        onStatus("Reconnected as $role")
                        isConnected = true
                    }
                    !snapshot.hasChild("player1") -> {
                        db.child("players/player1").setValue(playerId)
                        db.child("currentTurn").setValue("player1")
                        onAssignedRole("player1")
                        onStatus("Connected as player1")
                        isConnected = true
                    }
                    !snapshot.hasChild("player2") -> {
                        db.child("players/player2").setValue(playerId)
                        onAssignedRole("player2")
                        onStatus("Connected as player2")
                        isConnected = true
                    }
                    else -> {
                        onStatus("Room is full")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onStatus("Connection failed: ${error.message}")
            }
        })
    }

    fun sendMove(board: List<List<Int>>, nextTurn: String) {
        db.child("board").setValue(board)
        db.child("currentTurn").setValue(nextTurn)
    }

    fun listenToBoard(onUpdate: (List<List<Int>>) -> Unit) {
        db.child("board").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val raw = snapshot.value as? List<List<Long>> ?: return
                val board = raw.map { row -> row.map { it.toInt() } }
                onUpdate(board)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun listenToTurn(onTurnChange: (String) -> Unit) {
        db.child("currentTurn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val turn = snapshot.getValue(String::class.java) ?: return
                onTurnChange(turn)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendWinner(winner: String?) {
        db.child("winner").setValue(winner)
    }

    fun listenToWinner(onWin: (String?) -> Unit) {
        db.child("winner").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val winner = snapshot.getValue(String::class.java)
                onWin(winner)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun listenConnectionStatus(onStatus: (Boolean) -> Unit) {
        db.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onStatus(snapshot.exists())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
