package com.urreadiego.quicksort // <-- CAMBIA ESTO por el nombre de tu paquete real

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
                PantallaQuickSort()
            }
        }
    }
}

@Composable
fun PantallaQuickSort() {
    // 1. ESTADO: Lista de números y control de la interfaz
    var listaNumeros by remember { mutableStateOf(listOf(40, 15, 50, 10, 25, 5, 35, 20)) }
    var estaOrdenando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Algoritmo Quick Sort", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Divide y Vencerás", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(50.dp))

        // 2. REPRESENTACIÓN GRÁFICA: Barras dinámicas
        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            listaNumeros.forEach { valor ->
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height((valor * 3).dp)
                        .background(Color(0xFF00BCD4), RoundedCornerShape(4.dp)),
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

        // 3. BOTÓN PRINCIPAL
        Button(
            onClick = {
                if (!estaOrdenando) {
                    estaOrdenando = true
                    scope.launch {
                        val copiaMutable = listaNumeros.toMutableList()
                        // Llamamos a la función recursiva
                        ejecutarQuickSort(copiaMutable, 0, copiaMutable.size - 1) { nuevaLista ->
                            listaNumeros = nuevaLista
                        }
                        estaOrdenando = false
                    }
                }
            },
            enabled = !estaOrdenando
        ) {
            Text(if (estaOrdenando) "Ordenando con Pivote..." else "Comenzar Quick Sort")
        }

        // 4. BOTÓN REINICIAR
        if (!estaOrdenando) {
            TextButton(onClick = { listaNumeros = listaNumeros.shuffled() }) {
                Text("Mezclar de nuevo")
            }
        }
    }
}

/**
 * Función recursiva de Quick Sort adaptada para Corrutinas (animación)
 */
suspend fun ejecutarQuickSort(
    arr: MutableList<Int>,
    bajo: Int,
    alto: Int,
    onActualizarInterfaz: (List<Int>) -> Unit
) {
    if (bajo < alto) {
        // Obtenemos el índice de la partición (donde queda el pivote)
        val indicePivote = particionAnimada(arr, bajo, alto, onActualizarInterfaz)

        // Ordenamos la parte izquierda (recursivo)
        ejecutarQuickSort(arr, bajo, indicePivote - 1, onActualizarInterfaz)

        // Ordenamos la parte derecha (recursivo)
        ejecutarQuickSort(arr, indicePivote + 1, alto, onActualizarInterfaz)
    }
}

/**
 * Función de partición: El corazón del Quick Sort
 */
suspend fun particionAnimada(
    arr: MutableList<Int>,
    bajo: Int,
    alto: Int,
    onActualizarInterfaz: (List<Int>) -> Unit
): Int {
    val pivote = arr[alto] // Elegimos el último como pivote
    var i = bajo - 1       // Puntero para los elementos más pequeños

    for (j in bajo until alto) {
        if (arr[j] <= pivote) {
            i++
            // Intercambio
            val temp = arr[i]
            arr[i] = arr[j]
            arr[j] = temp

            // --- ANIMACIÓN ---
            onActualizarInterfaz(arr.toList())
            delay(400) // Pausa para que el ojo humano lo vea
        }
    }

    // Ponemos el pivote en su lugar final (i + 1)
    val temp = arr[i + 1]
    arr[i + 1] = arr[alto]
    arr[alto] = temp

    // --- ANIMACIÓN FINAL DEL PIVOTE ---
    onActualizarInterfaz(arr.toList())
    delay(400)

    return i + 1
}