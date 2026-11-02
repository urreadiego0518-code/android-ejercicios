package com.urreadiego.calculadoraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaSumatoria()
        }
    }

    @Composable
    fun PantallaSumatoria() {
        var numeroInput by remember { mutableStateOf("") }
        var resultadoText by remember { mutableStateOf("El resultado es: ") }

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Ejercicio Recursividad Sumatoria", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numeroInput,
                onValueChange = { numeroInput = it },
                label = { Text("Ingresa un número") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val n = numeroInput.toIntOrNull() ?: 0
                // llama a la función sumatoria
                val res = sumatoria(n)
                resultadoText = "Resultado: $res"
            }) {
                Text("Calcular")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = resultadoText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// funcion sumatoria
fun sumatoria(n: Int): Int {
    return if (n <= 0) {
        0
    } else {
        n + sumatoria(n - 1)
    }
}
