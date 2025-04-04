package com.lggpmlrg.futbolitopocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.lggpmlrg.futbolitopocket.iu.GameScreenWithComposeSensors

// Esta clase representa la actividad principal de la app.
// se encarga de iniciar la UI y conectar con el ViewModel.
class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameScreenWithComposeSensors(gameViewModel)
            }
        }
    }
}
