package com.urreadiego.shellsort // <-- Asegúrate de que coincida con tu proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PantallaShellSort()
            }
        }
    }
}

@Composable
fun PantallaShellSort() {
    // 1. ESTADO: Lista y control de animación
    var listaNumeros by remember { mutableStateOf(listOf(40, 15, 50, 10, 25, 5, 35, 20, 45, 30)) }
    var estaOrdenando by remember { mutableStateOf(false) }
    var gapActual by remember { mutableIntStateOf(0) } // Para mostrar la brecha actual
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Algoritmo Shell Sort", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = if (gapActual > 0) "Brecha actual (Gap): $gapActual" else "Mejora del Insertion Sort",
            style = MaterialTheme.typography.bodyMedium,
            color = if (gapActual > 0) Color(0xFFE91E63) else Color.Gray
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 2. REPRESENTACIÓN GRÁFICA
        Row(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            listaNumeros.forEach { valor ->
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height((valor * 4).dp)
                        .background(
                            color = if (estaOrdenando) Color(0xFF673AB7) else Color(0xFF4CAF50),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "$valor",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 3. BOTONES DE CONTROL
        Button(
            onClick = {
                if (!estaOrdenando) {
                    estaOrdenando = true
                    scope.launch {
                        val copiaMutable = listaNumeros.toMutableList()
                        ejecutarShellSortAnimado(copiaMutable) { nuevaLista, gap ->
                            listaNumeros = nuevaLista
                            gapActual = gap
                        }
                        estaOrdenando = false
                        gapActual = 0
                    }
                }
            },
            enabled = !estaOrdenando,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
        ) {
            Text(if (estaOrdenando) "Reduciendo Brechas..." else "Comenzar Shell Sort")
        }

        if (!estaOrdenando) {
            TextButton(onClick = { listaNumeros = listaNumeros.shuffled() }) {
                Text("Mezclar Arreglo")
            }
        }
    }
}

/**
 * Lógica de Shell Sort con pausas para animación
 */
suspend fun ejecutarShellSortAnimado(
    arr: MutableList<Int>,
    onActualizar: (List<Int>, Int) -> Unit
) {
    val n = arr.size
    var gap = n / 2

    while (gap > 0) {
        // Notificamos el cambio de gap a la interfaz
        onActualizar(arr.toList(), gap)
        delay(800) // Pausa para que el usuario note el cambio de gap

        for (i in gap until n) {
            val temp = arr[i]
            var j = i

            while (j >= gap && arr[j - gap] > temp) {
                arr[j] = arr[j - gap]
                j -= gap

                // Actualizamos la interfaz en cada desplazamiento interno
                onActualizar(arr.toList(), gap)
                delay(300)
            }
            arr[j] = temp

            // Actualizamos al insertar el elemento en su posición relativa
            onActualizar(arr.toList(), gap)
            delay(300)
        }
        gap /= 2
    }
}