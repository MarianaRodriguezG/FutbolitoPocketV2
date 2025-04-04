package com.lggpmlrg.futbolitopocket

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlin.math.max
import kotlin.math.min

class GameViewModel : ViewModel() {

    // Dimensiones reales de la imagen
    val fieldWidth = 433f
    val fieldHeight = 693f
    val ballRadius = 10f //radio de la pelotita

    // Zonas de las porterías
    val goalLeft = fieldWidth / 2 - 70f
    val goalRight = fieldWidth / 2 + 70f
    val goalHeight = 50f

    private val _uiState = mutableStateOf(GameState())
    val uiState: State<GameState> = _uiState
// esta función permite actualizar las físicas, como posición, velocidad, etc
    fun updatePhysics(xAccel: Float, yAccel: Float) {
        val state = _uiState.value

        var xSpeed = state.xSpeed + xAccel * 2
        var ySpeed = state.ySpeed + yAccel * 2

        var xPos = state.xPos + xSpeed
        var yPos = state.yPos + ySpeed

        var scoreLeft = state.scoreLeft
        var scoreRight = state.scoreRight

        val isInsideGoalX = xPos in goalLeft..goalRight

        // Rebotar en lados izquierdo y derecho
        if (xPos - ballRadius < 0 || xPos + ballRadius > fieldWidth) {
            xSpeed = -xSpeed * 0.7f
            xPos = min(max(xPos, ballRadius), fieldWidth - ballRadius)
        }

        // gol en la porteria superior
        if (yPos - ballRadius <= 0 && isInsideGoalX) {
            scoreLeft++
            xPos = fieldWidth / 2
            yPos = fieldHeight / 2
            xSpeed = 0f
            ySpeed = 0f
        }
        // gol en la porteria inferior
        else if (yPos + ballRadius >= fieldHeight && isInsideGoalX) {
            scoreRight++
            xPos = fieldWidth / 2
            yPos = fieldHeight / 2
            xSpeed = 0f
            ySpeed = 0f
        }
        // rebote en las esquinas para cuando no se trata de una porteria
        else if (
            (yPos - ballRadius < 0 && !isInsideGoalX) ||
            (yPos + ballRadius > fieldHeight && !isInsideGoalX)
        ) {
            ySpeed = -ySpeed * 0.7f
            yPos = min(max(yPos, ballRadius), fieldHeight - ballRadius)
        }

        _uiState.value = GameState(xPos, yPos, xSpeed, ySpeed, scoreLeft, scoreRight)
    }
}
// este es el estado del juego, es decir, el score del partido
data class GameState(
    val xPos: Float = 433f / 2,
    val yPos: Float = 693f / 2,
    val xSpeed: Float = 0f,
    val ySpeed: Float = 0f,
    val scoreLeft: Int = 0,
    val scoreRight: Int = 0
)
