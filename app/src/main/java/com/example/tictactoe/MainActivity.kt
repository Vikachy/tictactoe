package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tictactoe.model.Difficulty
import com.example.tictactoe.model.GameMode
import com.example.tictactoe.model.GameModel
import com.example.tictactoe.ui.screens.GameScreen
import com.example.tictactoe.ui.screens.StartScreen
import com.example.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeApp()
                }
            }
        }
    }
}

@Composable
fun TicTacToeApp() {
    // Состояние для контроля навигации
    var currentScreen by remember { mutableStateOf(Screen.START) }
    
    // Модель игры
    val gameModel = remember { GameModel() }
    
    // В зависимости от текущего экрана, отображаем соответствующий UI
    when (currentScreen) {
        Screen.START -> {
            StartScreen(
                onGameModeSelected = { mode, difficulty ->
                    gameModel.gameMode = mode
                    
                    if (mode == GameMode.PLAYER_VS_COMPUTER && difficulty != null) {
                        gameModel.difficulty = difficulty
                    }
                    
                    // Сбрасываем игру и переходим на экран игры
                    gameModel.resetGame()
                    currentScreen = Screen.GAME
                }
            )
        }
        
        Screen.GAME -> {
            GameScreen(
                gameModel = gameModel,
                onBackToMenu = {
                    currentScreen = Screen.START
                }
            )
        }
    }
}

enum class Screen {
    START, GAME
}