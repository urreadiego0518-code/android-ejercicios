package com.urreadiego.radixsort // <-- Ajusta según tu paquete

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PantallaRadixSort()
            }
        }
    }
}

@Composable
fun PantallaRadixSort() {
    var listaNumeros by remember { mutableStateOf(listOf(170, 45, 75, 90, 802, 24, 2, 66)) }
    var estaOrdenando by remember { mutableStateOf(false) }
    var digitoActual by remember { mutableIntStateOf(1) } // 1, 10, 100...
    var cubetasVisuales by remember { mutableStateOf(List(10) { listOf<Int>() }) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Algoritmo Radix Sort", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Evaluando posición: ${if (digitoActual == 1) "Unidades" else if (digitoActual == 10) "Decenas" else "Centenas"}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF00BCD4)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // RECUADRO DEL ARREGLO PRINCIPAL
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            listaNumeros.forEach { num ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(40.dp)
                        .background(Color(0xFF673AB7), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$num", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // VISUALIZACIÓN DE CUBETAS (LISTAS LIGADAS)
        Text("Cubetas (Buckets 0-9)", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            cubetasVisuales.forEachIndexed { index, contenido ->
                Column(
                    modifier = Modifier
                        .width(32.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$index", fontWeight = FontWeight.ExtraBold, color = Color.Gray)
                    Divider()
                    contenido.forEach { valCubeta ->
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$valCubeta", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // BOTONES
        Row(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = {
                    if (!estaOrdenando) {
                        estaOrdenando = true
                        scope.launch {
                            ejecutarRadixSortAnimado(
                                listaNumeros.toMutableList(),
                                onUpdateList = { listaNumeros = it },
                                onUpdateBuckets = { cubetasVisuales = it },
                                onUpdateExp = { digitoActual = it }
                            )
                            estaOrdenando = false
                        }
                    }
                },
                enabled = !estaOrdenando
            ) {
                Text(if (estaOrdenando) "Ordenando..." else "Iniciar Radix Sort")
            }

            Spacer(modifier = Modifier.width(10.dp))

            TextButton(
                onClick = { listaNumeros = listOf(170, 45, 75, 90, 802, 24, 2, 66).shuffled() },
                enabled = !estaOrdenando
            ) {
                Text("Mezclar")
            }
        }
    }
}

suspend fun ejecutarRadixSortAnimado(
    arr: MutableList<Int>,
    onUpdateList: (List<Int>) -> Unit,
    onUpdateBuckets: (List<List<Int>>) -> Unit,
    onUpdateExp: (Int) -> Unit
) {
    val max = arr.maxOrNull() ?: 0
    var exp = 1

    while (max / exp > 0) {
        onUpdateExp(exp)
        // 1. Crear cubetas usando LinkedList (Lista Ligada)
        val buckets = Array(10) { LinkedList<Int>() }

        // 2. Distribución: Mover del arreglo a las cubetas
        for (i in arr.indices) {
            val digit = (arr[i] / exp) % 10
            buckets[digit].add(arr[i])

            // Actualizar visualmente las cubetas
            onUpdateBuckets(buckets.map { it.toList() })
            delay(400) // Velocidad lenta para observar el proceso
        }

        delay(1000) // Pausa antes de recolectar

        // 3. Recolección: Mover de las cubetas al arreglo
        var index = 0
        for (i in 0..9) {
            while (buckets[i].isNotEmpty()) {
                arr[index++] = buckets[i].removeFirst()

                // Actualizar visualmente tanto el arreglo como las cubetas
                onUpdateList(arr.toList())
                onUpdateBuckets(buckets.map { it.toList() })
                delay(300)
            }
        }
        exp *= 10
        delay(800)
    }
}