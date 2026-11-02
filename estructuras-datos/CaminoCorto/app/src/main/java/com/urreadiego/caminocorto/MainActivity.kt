package com.urreadiego.caminocorto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urreadiego.caminocorto.ui.theme.CaminoCortoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.PriorityQueue

// --- MODELOS ---
data class Arista(val destino: Int, val peso: Int)
data class NodoPrioridad(val id: Int, val distancia: Int) : Comparable<NodoPrioridad> {
    override fun compareTo(other: NodoPrioridad): Int = this.distancia.compareTo(other.distancia)
}
data class PosicionNodo(val x: Float, val y: Float)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaminoCortoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaVisualDijkstra(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaVisualDijkstra(modifier: Modifier = Modifier) {
    val nombres = listOf("A", "B", "C", "D", "E", "F", "G", "H")
    val scope = rememberCoroutineScope()

    // ESTADOS
    var nodoActual by remember { mutableStateOf(-1) }
    var visitados by remember { mutableStateOf(setOf<Int>()) }
    var distanciasUI by remember { mutableStateOf(IntArray(8) { Int.MAX_VALUE }) }
    var rutaFinal by remember { mutableStateOf("") }
    var estaAnimando by remember { mutableStateOf(false) }

    val grafo = remember {
        mapOf(
            0 to listOf(Arista(1, 3), Arista(2, 5), Arista(3, 2), Arista(7, 10)),
            1 to listOf(Arista(4, 4), Arista(6, 6), Arista(3, 8), Arista(7, 6)),
            2 to listOf(Arista(5, 7), Arista(6, 9), Arista(4, 1)),
            3 to listOf(Arista(0, 2), Arista(1, 8), Arista(6, 12), Arista(7, 14)),
            4 to listOf(Arista(1, 4), Arista(2, 1), Arista(6, 15)),
            5 to listOf(Arista(2, 7), Arista(7, 9)),
            6 to listOf(Arista(1, 6), Arista(2, 9), Arista(3, 12), Arista(7, 3)),
            7 to listOf(Arista(0, 10), Arista(1, 6), Arista(6, 3), Arista(5, 9))
        )
    }

    val posiciones = remember {
        listOf(
            PosicionNodo(0.35f, 0.15f), PosicionNodo(0.5f, 0.45f),
            PosicionNodo(0.65f, 0.15f), PosicionNodo(0.25f, 0.75f),
            PosicionNodo(0.85f, 0.45f), PosicionNodo(0.85f, 0.15f),
            PosicionNodo(0.65f, 0.85f), PosicionNodo(0.1f, 0.45f)
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Dijkstra Visual Paso a Paso", style = MaterialTheme.typography.headlineSmall)

        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF7F7F7), RoundedCornerShape(12.dp))) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radio = 22.dp.toPx()

                // 1. Dibujar Aristas y Pesos
                grafo.forEach { (id, aristas) ->
                    val p1 = posiciones[id]
                    aristas.forEach { arista ->
                        val p2 = posiciones[arista.destino]
                        val start = Offset(p1.x * size.width, p1.y * size.height)
                        val end = Offset(p2.x * size.width, p2.y * size.height)

                        drawLine(Color.LightGray, start, end, strokeWidth = 2.dp.toPx())

                        // Dibujar Peso en el centro de la línea
                        drawContext.canvas.nativeCanvas.drawText(
                            arista.peso.toString(), (start.x + end.x) / 2, (start.y + end.y) / 2,
                            android.graphics.Paint().apply { color = android.graphics.Color.GRAY; textSize = 12.sp.toPx() }
                        )
                    }
                }

                // 2. Dibujar Nodos
                posiciones.forEachIndexed { i, pos ->
                    val centro = Offset(pos.x * size.width, pos.y * size.height)
                    val color = when {
                        i == nodoActual -> Color.Red
                        visitados.contains(i) -> Color(0xFF4CAF50)
                        else -> Color.White
                    }
                    drawCircle(color, radio, centro)
                    drawCircle(Color.Black, radio, centro, style = Stroke(2.dp.toPx()))
                    drawContext.canvas.nativeCanvas.drawText(nombres[i], centro.x, centro.y + 10f, android.graphics.Paint().apply { textSize = 20.sp.toPx(); textAlign = android.graphics.Paint.Align.CENTER; isFakeBoldText = true })
                }
            }
        }

        if (rutaFinal.isNotEmpty()) {
            Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Text(rutaFinal, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }

        Button(
            onClick = {
                visitados = emptySet(); rutaFinal = ""; estaAnimando = true
                scope.launch {
                    val resultado = ejecutarDijkstraConRuta(8, 0, grafo, { nodoActual = it }, { distanciasUI = it }, { visitados = visitados + it })
                    rutaFinal = "Camino más corto a G: ${resultado.joinToString(" -> ") { nombres[it] }}"
                    estaAnimando = false; nodoActual = -1
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            enabled = !estaAnimando
        ) { Text(if (estaAnimando) "Calculando..." else "INICIAR RECORRIDO") }
    }
}

suspend fun ejecutarDijkstraConRuta(n: Int, inicio: Int, grafo: Map<Int, List<Arista>>, onNodo: (Int) -> Unit, onDist: (IntArray) -> Unit, onVisit: (Int) -> Unit): List<Int> {
    val dists = IntArray(n) { Int.MAX_VALUE }
    val padres = IntArray(n) { -1 }
    dists[inicio] = 0
    val cola = PriorityQueue<NodoPrioridad>().apply { add(NodoPrioridad(inicio, 0)) }

    while (cola.isNotEmpty()) {
        val (actual, _) = cola.poll()!!
        onNodo(actual); delay(800)

        grafo[actual]?.forEach { arista ->
            val nueva = dists[actual] + arista.peso
            if (nueva < dists[arista.destino]) {
                dists[arista.destino] = nueva
                padres[arista.destino] = actual
                cola.add(NodoPrioridad(arista.destino, nueva))
                onDist(dists.copyOf()); delay(400)
            }
        }
        onVisit(actual); delay(400)
    }

    // Reconstruir camino al nodo G (ID 6)
    val camino = mutableListOf<Int>()
    var temp = 6
    while (temp != -1) { camino.add(0, temp); temp = padres[temp] }
    return camino
}