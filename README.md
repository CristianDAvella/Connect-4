# Connect-4 MVVM Android (Jetpack Compose)

This project implements the classic **Connect 4** game using **Kotlin**, **Android Studio**, and the **MVVM** architecture, with a declarative UI via **Jetpack Compose**.

---

## 🎮 Features
- Functional 6x7 board
- Automatic win or draw detection
- Token drop animation
- Alternating turns between player and AI
- Game restart button

---

## 🧠 Architecture
- **ViewModel:** handles game state, turns, and animations
- **Model:** manages board logic and victory detection
- **View (Compose):** renders the board, controls, and status messages

### Main structure
```
com.example.connect4/
├── model/
│   ├── GameBoard.kt
│   ├── GameCellState.kt
│   └── VictoryChecker.kt
├── viewmodel/
│   └── GameViewModel.kt
├── view/
│   ├── MainActivity.kt
│   └── GameScreen.kt
└── test/
    ├── GameBoardTest.kt
    └── VictoryCheckerTest.kt
```

---

## 🧪 Included Tests
Executable via Android Studio (JUnit4):
- `GameBoardTest.kt`: tests for board mechanics
- `VictoryCheckerTest.kt`: tests for victory logic in all directions

---

## 🚀 How to Run
1. Clone the repository
2. Open the project in Android Studio
3. Launch `MainActivity` on an emulator or device
4. Run tests by right-clicking test files or the `test` folder

---

## 👥 Authors
- [CristianDAvella](https://github.com/CristianDAvella)
- [santiagomontanes](https://github.com/santiagomontanes)
- [Dvelastegui58](https://github.com/Dvelastegui58)

---

## 📜 License
MIT
