package com.example.ahmedahmedsensormanager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.ahmedahmedsensormanager.ui.theme.AHMEDAHMEDSensorManagerTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var sensorText by mutableStateOf("Waiting for sensor...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Get the accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            sensorText = "Accelerometer not available."
        }

        setContent {
            AHMEDAHMEDSensorManagerTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Accelerometer Sensor",
                        fontSize = 26.sp
                    )

                    Text(
                        text = sensorText,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        accelerometer?.also { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            // BREAKPOINT HERE
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            sensorText = String.format(
                "X: %.2f\nY: %.2f\nZ: %.2f",
                x,
                y,
                z
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Accuracy changes are not needed for this project.
    }
}