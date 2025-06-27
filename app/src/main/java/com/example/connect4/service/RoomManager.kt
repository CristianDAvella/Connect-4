package com.example.connect4.service

import java.util.UUID

object RoomManager {
    fun createNewRoom(playerId: String): FirebaseGameService {
        val newRoomId = UUID.randomUUID().toString()
        return FirebaseGameService(newRoomId, playerId)
    }
}
