package com.example.tictactoe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.model.Difficulty
import com.example.tictactoe.model.GameMode

@Composable
fun StartScreen(
    onGameModeSelected: (GameMode, Difficulty?) -> Unit
) {
    val showDifficultySelector = remember { mutableStateOf(false) }
    
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Крестики-Нолики",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 50.dp)
            )
            
            // Если не выбран режим игры против компьютера, показываем выбор режима
            if (!showDifficultySelector.value) {
                GameModeSelector(
                    onHumanVsHumanSelected = {
                        onGameModeSelected(GameMode.PLAYER_VS_PLAYER, null)
                    },
                    onHumanVsComputerSelected = {
                        showDifficultySelector.value = true
                    }
                )
            } else {
                DifficultySelector(
                    onDifficultySelected = { difficulty ->
                        onGameModeSelected(GameMode.PLAYER_VS_COMPUTER, difficulty)
                    },
                    onBack = {
                        showDifficultySelector.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun GameModeSelector(
    onHumanVsHumanSelected: () -> Unit,
    onHumanVsComputerSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModeButton(
            title = "Игрок vs Игрок",
            subtitle = "Играйте против друга",
            onClick = onHumanVsHumanSelected,
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFF2196F3), Color(0xFF3F51B5))
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ModeButton(
            title = "Игрок vs Компьютер",
            subtitle = "Играйте против ИИ",
            onClick = onHumanVsComputerSelected,
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
            )
        )
    }
}

@Composable
fun DifficultySelector(
    onDifficultySelected: (Difficulty) -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Выберите сложность",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        
        DifficultyButton(
            title = "Легкий",
            description = "Высокий шанс победы",
            onClick = { onDifficultySelected(Difficulty.EASY) },
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
            )
        )
        
        Spacer(modifier = Modifier.height(15.dp))
        
        DifficultyButton(
            title = "Средний",
            description = "Шансы 50/50",
            onClick = { onDifficultySelected(Difficulty.MEDIUM) },
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFFEB3B), Color(0xFFFFC107))
            )
        )
        
        Spacer(modifier = Modifier.height(15.dp))
        
        DifficultyButton(
            title = "Сложный",
            description = "Почти непобедимый",
            onClick = { onDifficultySelected(Difficulty.HARD) },
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFF44336), Color(0xFFE91E63))
            )
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF616161)
            ),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Назад")
        }
    }
}

@Composable
fun ModeButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DifficultyButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
} 