// GameViewModel.kt
package com.lggpmlrg.futbolitopocket

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlin.math.max
import kotlin.math.min

// Este ViewModel contiene la lógica del juego:
// rebotes, anotaciones y estado de la pelota.
class GameViewModel : ViewModel() {

    private val fieldWidth = 800f
    private val fieldHeight = 1600f
    private val ballRadius = 40f
    private val goalSize = 200f

    private val _uiState = mutableStateOf(GameState())
    val uiState: State<GameState> = _uiState

    fun updatePhysics(xAccel: Float, yAccel: Float) {
        val state = _uiState.value

        var xSpeed = state.xSpeed + xAccel * 2
        var ySpeed = state.ySpeed + yAccel * 2

        var xPos = state.xPos + xSpeed
        var yPos = state.yPos + ySpeed

        // Rebotes
        if (xPos - ballRadius < 0 || xPos + ballRadius > fieldWidth) {
            xSpeed = -xSpeed * 0.8f
            xPos = min(max(xPos, ballRadius), fieldWidth - ballRadius)
        }
        if (yPos - ballRadius < 0 || yPos + ballRadius > fieldHeight) {
            ySpeed = -ySpeed * 0.8f
            yPos = min(max(yPos, ballRadius), fieldHeight - ballRadius)
        }

        var scoreLeft = state.scoreLeft
        var scoreRight = state.scoreRight

        // Goles
        if (xPos - ballRadius <= 0 && yPos in (fieldHeight / 2 - goalSize / 2)..(fieldHeight / 2 + goalSize / 2)) {
            scoreRight++
            xPos = fieldWidth / 2
            yPos = fieldHeight / 2
            xSpeed = 0f
            ySpeed = 0f
        }
        if (xPos + ballRadius >= fieldWidth && yPos in (fieldHeight / 2 - goalSize / 2)..(fieldHeight / 2 + goalSize / 2)) {
            scoreLeft++
            xPos = fieldWidth / 2
            yPos = fieldHeight / 2
            xSpeed = 0f
            ySpeed = 0f
        }

        _uiState.value = GameState(xPos, yPos, xSpeed, ySpeed, scoreLeft, scoreRight)
    }
}

// GameState.kt
data class GameState(
    val xPos: Float = 400f,
    val yPos: Float = 800f,
    val xSpeed: Float = 0f,
    val ySpeed: Float = 0f,
    val scoreLeft: Int = 0,
    val scoreRight: Int = 0
)
