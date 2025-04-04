// GameScreenUI.kt
// Este archivo contiene la interfaz del juego, que dibuja el campo, la pelota y el marcador.
// Ahora utiliza SensorManager nativo para leer el acelerómetro sin usar compose-sensors.

package com.lggpmlrg.futbolitopocket.iu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lggpmlrg.futbolitopocket.GameViewModel
import com.lggpmlrg.futbolitopocket.R
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun rememberAccelerometerValues(context: Context): Triple<Float, Float, Float> {
    var values by remember { mutableStateOf(Triple(0f, 0f, 0f)) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                values = Triple(event.values[0], event.values[1], event.values[2])
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return values
}

@Composable
fun GameScreenWithComposeSensors(viewModel: GameViewModel = viewModel()) {
    val context = LocalContext.current
    val (x, y, _) = rememberAccelerometerValues(context)

    val xAccel = -x
    val yAccel = y

    GameScreen(xAccel, yAccel, viewModel)
}

@Composable
fun GameScreen(xAccel: Float, yAccel: Float, viewModel: GameViewModel) {
    val uiState by viewModel.uiState
    val ballRadius = 40f

    LaunchedEffect(xAccel, yAccel) {
        viewModel.updatePhysics(xAccel, yAccel)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Text(
            "🏆 Izq: ${uiState.scoreLeft} - Der: ${uiState.scoreRight}",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen PNG como fondo del campo
            val painter = painterResource(id = R.drawable.cancha_futbolito)
            Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())

            // Dibuja la pelota encima del campo
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    Color.White,
                    radius = ballRadius,
                    center = Offset(uiState.xPos, uiState.yPos)
                )
            }
        }
    }
}
