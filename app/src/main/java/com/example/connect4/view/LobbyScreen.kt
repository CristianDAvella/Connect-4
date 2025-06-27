package com.example.connect4.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.connect4.service.FirebaseGameService
import com.example.connect4.service.RoomManager
import com.example.connect4.viewmodel.GameViewModel
import com.google.firebase.database.*

enum class LobbyMode { CHOOSE, JOIN, WAITING }

@Composable
fun LobbyScreen(
    playerId: String,
    navController: NavController,
    viewModel: GameViewModel,
    onServiceReady: (FirebaseGameService, String) -> Unit
) {
    var mode by remember { mutableStateOf(LobbyMode.CHOOSE) }
    var status by remember { mutableStateOf("") }
    var roomIdInput by remember { mutableStateOf("") }
    var currentRoomId by remember { mutableStateOf("") }
    var firebaseService by remember { mutableStateOf<FirebaseGameService?>(null) }
    var role by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Room ID", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Room ID copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(mode, currentRoomId, role) {
        if (mode == LobbyMode.WAITING && firebaseService != null && role == "player1") {
            val dbRef = FirebaseDatabase.getInstance().getReference("rooms/$currentRoomId/players/player2")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        viewModel.initializeOnlineGame(firebaseService!!, role)
                        onServiceReady(firebaseService!!, role)
                        navController.navigate("game")
                        dbRef.removeEventListener(this)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (mode) {
            LobbyMode.CHOOSE -> {
                Button(onClick = {
                    val service = RoomManager.createNewRoom(playerId)
                    currentRoomId = service.roomId
                    firebaseService = service
                    mode = LobbyMode.WAITING
                    service.joinGame(
                        onAssignedRole = { r -> role = r },
                        onStatus = { msg -> status = msg }
                    )
                }) { Text("Crear sala") }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { mode = LobbyMode.JOIN }) { Text("Unirse a sala") }
            }

            LobbyMode.JOIN -> {
                TextField(
                    value = roomIdInput,
                    onValueChange = { roomIdInput = it },
                    label = { Text("Código de sala") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val service = FirebaseGameService(roomIdInput, playerId)
                    currentRoomId = roomIdInput
                    firebaseService = service
                    service.joinGame(
                        onAssignedRole = { r ->
                            role = r
                            viewModel.initializeOnlineGame(service, r)
                            onServiceReady(service, r)
                            navController.navigate("game")
                        },
                        onStatus = { msg -> status = msg }
                    )
                }) { Text("Conectar") }
            }

            LobbyMode.WAITING -> {
                Text(
                    text = "Código de sala:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentRoomId,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        copyToClipboard(context, currentRoomId)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Esperando oponente...")
                Spacer(modifier = Modifier.height(16.dp))
                Text(status)
            }
        }
    }
}
