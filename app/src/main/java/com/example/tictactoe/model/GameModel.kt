package com.example.tictactoe.model

import kotlin.random.Random

enum class Player { X, O, NONE }
enum class GameMode { PLAYER_VS_PLAYER, PLAYER_VS_COMPUTER }
enum class Difficulty { EASY, MEDIUM, HARD }
enum class GameState { ONGOING, X_WON, O_WON, DRAW }

class GameModel {
    private val board = Array(3) { Array(3) { Player.NONE } }
    var currentPlayer = Player.X
    var gameMode = GameMode.PLAYER_VS_PLAYER
    var difficulty = Difficulty.MEDIUM
    var gameState = GameState.ONGOING
    
    fun resetGame() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = Player.NONE
            }
        }
        currentPlayer = Player.X
        gameState = GameState.ONGOING
    }
    
    fun makeMove(row: Int, col: Int): Boolean {
        // Проверяем, возможен ли ход
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != Player.NONE || gameState != GameState.ONGOING) {
            return false
        }
        
        // Делаем ход
        board[row][col] = currentPlayer
        
        // Проверка состояния игры
        gameState = checkGameState()
        
        if (gameState == GameState.ONGOING) {
            // Переключаем игрока
            currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            
            // Если компьютерный режим и ход компьютера
            if (gameMode == GameMode.PLAYER_VS_COMPUTER && currentPlayer == Player.O && gameState == GameState.ONGOING) {
                // Делаем ход компьютера
                makeComputerMove()
                
                // Повторно проверяем состояние после хода компьютера
                gameState = checkGameState()
                
                // Переключаем игрока обратно на X, если игра продолжается
                if (gameState == GameState.ONGOING) {
                    currentPlayer = Player.X
                }
            }
        }
        
        return true
    }
    
    fun getBoard(): Array<Array<Player>> {
        // Возвращаем копию доски, чтобы избежать изменений извне
        return Array(3) { i -> Array(3) { j -> board[i][j] } }
    }
    
    private fun makeComputerMove() {
        when (difficulty) {
            Difficulty.EASY -> makeEasyMove()
            Difficulty.MEDIUM -> makeMediumMove()
            Difficulty.HARD -> makeHardMove()
        }
    }
    
    private fun makeEasyMove() {
        // В легком режиме компьютер делает случайные ходы с приоритетом на неоптимальные
        val emptyPositions = mutableListOf<Pair<Int, Int>>()
        val cornerPositions = mutableListOf<Pair<Int, Int>>()
        
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    emptyPositions.add(Pair(i, j))
                    
                    // Проверка на угол
                    if ((i == 0 || i == 2) && (j == 0 || j == 2)) {
                        cornerPositions.add(Pair(i, j))
                    }
                }
            }
        }
        
        // 70% шанс выбрать не угол, если возможно
        if (emptyPositions.isNotEmpty()) {
            val nonCornerPositions = emptyPositions.filter { it !in cornerPositions }
            val position = if (nonCornerPositions.isNotEmpty() && Random.nextFloat() < 0.7f) {
                nonCornerPositions.random()
            } else {
                emptyPositions.random()
            }
            
            board[position.first][position.second] = Player.O
        }
    }
    
    private fun makeMediumMove() {
        // Средний режим: 50% шанс сделать оптимальный ход, 50% - случайный
        if (Random.nextFloat() < 0.5f) {
            makeHardMove()
        } else {
            val emptyPositions = mutableListOf<Pair<Int, Int>>()
            
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == Player.NONE) {
                        emptyPositions.add(Pair(i, j))
                    }
                }
            }
            
            if (emptyPositions.isNotEmpty()) {
                val position = emptyPositions.random()
                board[position.first][position.second] = Player.O
            }
        }
    }
    
    private fun makeHardMove() {
        // В сложном режиме используем minimax алгоритм
        var bestScore = Double.NEGATIVE_INFINITY
        var bestMove: Pair<Int, Int>? = null
        
        // Проверяем, есть ли выигрышный ход
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    board[i][j] = Player.O
                    if (checkWinner() == Player.O) {
                        board[i][j] = Player.NONE
                        board[i][j] = Player.O
                        return
                    }
                    board[i][j] = Player.NONE
                }
            }
        }
        
        // Проверяем, нужно ли блокировать выигрышный ход противника
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    board[i][j] = Player.X
                    if (checkWinner() == Player.X) {
                        board[i][j] = Player.NONE
                        board[i][j] = Player.O
                        return
                    }
                    board[i][j] = Player.NONE
                }
            }
        }
        
        // Предпочитаем центр
        if (board[1][1] == Player.NONE) {
            board[1][1] = Player.O
            return
        }
        
        // Предпочитаем углы
        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        for (corner in corners.shuffled()) {
            if (board[corner.first][corner.second] == Player.NONE) {
                board[corner.first][corner.second] = Player.O
                return
            }
        }
        
        // Если все предыдущие стратегии не сработали, выбираем любую доступную клетку
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    board[i][j] = Player.O
                    return
                }
            }
        }
    }
    
    private fun checkGameState(): GameState {
        val winner = checkWinner()
        
        return when {
            winner == Player.X -> GameState.X_WON
            winner == Player.O -> GameState.O_WON
            isBoardFull() -> GameState.DRAW
            else -> GameState.ONGOING
        }
    }
    
    private fun checkWinner(): Player {
        // Проверка по горизонтали
        for (i in 0..2) {
            if (board[i][0] != Player.NONE && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0]
            }
        }
        
        // Проверка по вертикали
        for (j in 0..2) {
            if (board[0][j] != Player.NONE && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j]
            }
        }
        
        // Проверка по диагонали (сверху слева - снизу справа)
        if (board[0][0] != Player.NONE && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]
        }
        
        // Проверка по диагонали (сверху справа - снизу слева)
        if (board[0][2] != Player.NONE && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]
        }
        
        return Player.NONE
    }
    
    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == Player.NONE) {
                    return false
                }
            }
        }
        return true
    }
} 