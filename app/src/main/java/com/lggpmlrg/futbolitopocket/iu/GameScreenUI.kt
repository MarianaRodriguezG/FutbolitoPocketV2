// Este archivo se encarga de dibujar el campo, pelota, marcador y visualiza
// porterias escalando coordenadas de estas al tamaño real del lienzo de trabajo.
// Usa SensorManager nativo*
//sensor-composer presentaba detalles en su implementación
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lggpmlrg.futbolitopocket.GameViewModel
import com.lggpmlrg.futbolitopocket.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

@Composable
fun rememberAccelerometerValues(context: Context): Triple<Float, Float, Float> {
    var values by remember { mutableStateOf(Triple(0f, 0f, 0f)) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val gravity = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val alpha = 0.8f
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                val linearX = event.values[0] - gravity[0]
                val linearY = event.values[1] - gravity[1]
                val linearZ = event.values[2] - gravity[2]

                values = Triple(linearX, linearY, linearZ)
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
    val ballRadius = viewModel.ballRadius

    LaunchedEffect(xAccel, yAccel) {
        viewModel.updatePhysics(xAccel, yAccel)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🐐GDL: ${uiState.scoreLeft} - 🦅AME: ${uiState.scoreRight}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) { val painter = painterResource(id = R.drawable.cancha_futbolito)


                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds //llena con toda la imagen de fondo
                    )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val scaleX = size.width / viewModel.fieldWidth
                val scaleY = size.height / viewModel.fieldHeight

                // Dibujar portería superior (la del america es amarilla)
                drawRect(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    topLeft = Offset(viewModel.goalLeft * scaleX, 0f),
                    size = Size(
                        (viewModel.goalRight - viewModel.goalLeft) * scaleX,
                        viewModel.goalHeight * scaleY
                    )
                )
                // Dibujar portería inferior (azul)
                drawRect(
                    color = Color.Blue.copy(alpha = 0.5f),
                    topLeft = Offset(
                        viewModel.goalLeft * scaleX,
                        (viewModel.fieldHeight - viewModel.goalHeight) * scaleY
                    ),
                    size = Size(
                        (viewModel.goalRight - viewModel.goalLeft) * scaleX,
                        viewModel.goalHeight * scaleY
                    )
                )

                // Dibuja la pelota escalada
                drawCircle(
                    Color.Blue,
                    radius = ballRadius * scaleX,
                    center = Offset(uiState.xPos * scaleX, uiState.yPos * scaleY)
                )
            }
        }
    }
}
