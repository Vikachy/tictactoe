package com.example.tictactoe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.model.Difficulty
import com.example.tictactoe.model.GameMode
import com.example.tictactoe.model.GameModel
import com.example.tictactoe.model.GameState
import com.example.tictactoe.model.Player

@Composable
fun GameScreen(
    gameModel: GameModel,
    onBackToMenu: () -> Unit
) {
    // Используем rememberUpdatedState для обертывания gameModel,
    // чтобы избежать проблем с обновлением при рестарте
    val gameModelState = rememberUpdatedState(gameModel)
    
    var board by remember { mutableStateOf(gameModel.getBoard()) }
    var currentPlayer by remember { mutableStateOf(gameModel.currentPlayer) }
    var gameState by remember { mutableStateOf(gameModel.gameState) }
    
    // Функция для обновления состояния игры
    fun updateGameState() {
        board = gameModelState.value.getBoard().map { it.clone() }.toTypedArray()
        currentPlayer = gameModelState.value.currentPlayer
        gameState = gameModelState.value.gameState
    }
    
    // Эффект для начальной инициализации и обновления при изменении gameModel
    DisposableEffect(gameModelState.value) {
        updateGameState()
        onDispose { }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F1F1F),
                        Color(0xFF121212)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Добавляем отступ от статус бара
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Дополнительный отступ сверху
            Spacer(modifier = Modifier.height(16.dp))
            
            // Заголовок и информация об игре
            GameInfo(
                gameMode = gameModelState.value.gameMode,
                difficulty = gameModelState.value.difficulty,
                currentPlayer = currentPlayer,
                gameState = gameState
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Игровое поле
            GameBoard(
                board = board,
                enabled = gameState == GameState.ONGOING && 
                         !(gameModelState.value.gameMode == GameMode.PLAYER_VS_COMPUTER && currentPlayer == Player.O),
                onCellClick = { row, col ->
                    if (gameModelState.value.makeMove(row, col)) {
                        updateGameState()
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопки управления
            GameControls(
                gameState = gameState,
                onRestart = {
                    gameModelState.value.resetGame()
                    updateGameState()
                },
                onBackToMenu = onBackToMenu
            )
            
            // Показываем результат игры
            AnimatedVisibility(
                visible = gameState != GameState.ONGOING,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                GameResult(
                    gameState = gameState,
                    onPlayAgain = {
                        gameModelState.value.resetGame()
                        updateGameState()
                    }
                )
            }
        }
    }
}

@Composable
fun GameInfo(
    gameMode: GameMode,
    difficulty: Difficulty,
    currentPlayer: Player,
    gameState: GameState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Крестики-Нолики",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val modeText = if (gameMode == GameMode.PLAYER_VS_PLAYER) "Игрок vs Игрок" else "Игрок vs Компьютер"
            
            Text(
                text = modeText,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            if (gameMode == GameMode.PLAYER_VS_COMPUTER) {
                Text(
                    text = " (${
                        when(difficulty) {
                            Difficulty.EASY -> "Легкий"
                            Difficulty.MEDIUM -> "Средний"
                            Difficulty.HARD -> "Сложный"
                        }
                    })",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        if (gameState == GameState.ONGOING) {
            Text(
                text = "Ход: ${if (currentPlayer == Player.X) "X" else "O"}",
                style = MaterialTheme.typography.titleMedium,
                color = if (currentPlayer == Player.X) Color(0xFF2196F3) else Color(0xFFFF5722),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GameBoard(
    board: Array<Array<Player>>,
    enabled: Boolean,
    onCellClick: (Int, Int) -> Unit
) {
    val boardColor = Color(0xFF2D2D2D)
    val lineColor = Color.White.copy(alpha = 0.3f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = boardColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val width = size.width
                    val height = size.height
                    val cellWidth = width / 3
                    val cellHeight = height / 3
                    
                    // Рисуем линии сетки
                    drawLine(
                        color = lineColor,
                        start = Offset(cellWidth, 0f),
                        end = Offset(cellWidth, height),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(cellWidth * 2, 0f),
                        end = Offset(cellWidth * 2, height),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, cellHeight),
                        end = Offset(width, cellHeight),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, cellHeight * 2),
                        end = Offset(width, cellHeight * 2),
                        strokeWidth = 5f
                    )
                }
        ) {
            // Рисуем доску 3x3
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                for (i in 0..2) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (j in 0..2) {
                            GameCell(
                                player = board[i][j],
                                onCellClick = { onCellClick(i, j) },
                                enabled = enabled && board[i][j] == Player.NONE,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    player: Player,
    onCellClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    // Добавляем визуальный эффект при наведении и нажатии для лучшей обратной связи
    Box(
        modifier = modifier
            .clickable(enabled = enabled) { onCellClick() }
            .padding(8.dp)
            .background(
                color = if (player != Player.NONE) Color.Transparent
                else if (enabled) Color(0xFF22222A).copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        when (player) {
            Player.X -> XShape()
            Player.O -> OShape()
            else -> {
                // Показываем прозрачную область, чтобы обозначить кликабельную область
                if (enabled) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun XShape() {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "X Animation"
    )
    
    Canvas(
        modifier = Modifier.size(70.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val strokeWidth = size.width * 0.12f
        
        // Рисуем X с более яркими цветами и эффектом тени
        drawLine(
            color = Color(0xFF2196F3),
            start = Offset(0f, 0f),
            end = Offset(canvasWidth * animatedProgress, canvasHeight * animatedProgress),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        drawLine(
            color = Color(0xFF2196F3),
            start = Offset(canvasWidth, 0f),
            end = Offset(canvasWidth - (canvasWidth * animatedProgress), canvasHeight * animatedProgress),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun OShape() {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "O Animation"
    )
    
    Canvas(
        modifier = Modifier.size(70.dp)
    ) {
        val radius = (size.minDimension / 2) * 0.8f
        val strokeWidth = size.width * 0.12f
        
        // Рисуем O с более яркими цветами
        drawArc(
            color = Color(0xFFFF5722),
            startAngle = 0f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun GameControls(
    gameState: GameState,
    onRestart: () -> Unit,
    onBackToMenu: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("Сбросить")
        }
        
        Button(
            onClick = onBackToMenu,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF757575)
            )
        ) {
            Text("В Меню")
        }
    }
}

@Composable
fun GameResult(
    gameState: GameState,
    onPlayAgain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF212121)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val resultText = when (gameState) {
                    GameState.X_WON -> "Победил X!"
                    GameState.O_WON -> "Победил O!"
                    GameState.DRAW -> "Ничья!"
                    else -> ""
                }
                
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (gameState) {
                        GameState.X_WON -> Color(0xFF2196F3)
                        GameState.O_WON -> Color(0xFFFF5722)
                        else -> Color.White
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Играть ещё раз")
                }
            }
        }
    }
} 