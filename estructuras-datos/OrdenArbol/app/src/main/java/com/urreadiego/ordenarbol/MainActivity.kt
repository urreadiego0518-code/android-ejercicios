package com.urreadiego.ordenarbol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                ArbolBinarioVisual()
            }
        }
    }
}

@Composable
fun ArbolBinarioVisual() {
    var root by remember { mutableStateOf<Node?>(null) }
    var inputText by remember { mutableStateOf("") }
    var ordenadoText by remember { mutableStateOf("Recorrido: -") }

    // Estado para resaltar el nodo actual durante el recorrido
    var nodoResaltado by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    var estaRecorriendo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Árbol de Búsqueda Binaria", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Número") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaRecorriendo
        )

        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val valor = inputText.toIntOrNull()
                if (valor != null) {
                    root = insert(root, valor)
                    inputText = ""
                }
            }, enabled = !estaRecorriendo) {
                Text("Agregar")
            }

            Button(onClick = {
                scope.launch {
                    estaRecorriendo = true
                    ordenadoText = "Recorriendo..."
                    val lista = mutableListOf<Int>()

                    // Función de recorrido con pausa y resaltado
                    fun recorridoAnimado(nodo: Node?) {
                        if (nodo == null) return
                        recorridoAnimado(nodo.left)

                        // Acción al visitar el nodo
                        nodoResaltado = nodo.value
                        lista.add(nodo.value)
                        ordenadoText = "In-Order: ${lista.joinToString(", ")}"

                        // Espera 800ms para que el usuario vea el cambio de color
                        Thread.sleep(800)

                        recorridoAnimado(nodo.right)
                    }

                    // Ejecutamos en un hilo secundario para no bloquear la UI,
                    // pero usamos withContext o simplemente confiamos en el delay de corrutinas
                    // Nota: Para Compose es mejor usar delay()
                }

                // Versión corregida con Corrutinas Reales:
                scope.launch {
                    estaRecorriendo = true
                    val listaVisual = mutableListOf<Int>()

                    suspend fun traverse(node: Node?) {
                        if (node == null) return
                        traverse(node.left)

                        nodoResaltado = node.value
                        listaVisual.add(node.value)
                        ordenadoText = "In-Order: ${listaVisual.joinToString(", ")}"
                        delay(1000) // Pausa de 1 segundo

                        traverse(node.right)
                    }

                    traverse(root)
                    nodoResaltado = null // Quitar resaltado al terminar
                    estaRecorriendo = false
                }
            }, enabled = !estaRecorriendo && root != null) {
                Text("Ordenar")
            }

            // BOTÓN LIMPIAR
            Button(
                onClick = {
                    root = null
                    ordenadoText = "Recorrido: -"
                    nodoResaltado = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                enabled = !estaRecorriendo
            ) {
                Text("Limpiar", color = Color.White)
            }
        }

        Text(ordenadoText, modifier = Modifier.padding(8.dp))

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            TreeCanvas(root, nodoResaltado)
        }
    }
}

@Composable
fun TreeCanvas(root: Node?, resaltado: Int?) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        root?.let {
            drawNode(it, size.width / 2, 80f, size.width / 4, resaltado)
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNode(
    node: Node,
    x: Float,
    y: Float,
    horizontalSpacing: Float,
    resaltado: Int?
) {
    val radius = 45f
    val verticalSpacing = 160f

    // Color del nodo: Si es el resaltado, usamos naranja, si no, el morado original
    val colorNodo = if (node.value == resaltado) Color(0xFFFF9800) else Color(0xFF6200EE)

    node.left?.let {
        drawLine(Color.LightGray, Offset(x, y), Offset(x - horizontalSpacing, y + verticalSpacing), 4f)
        drawNode(it, x - horizontalSpacing, y + verticalSpacing, horizontalSpacing / 1.7f, resaltado)
    }

    node.right?.let {
        drawLine(Color.LightGray, Offset(x, y), Offset(x + horizontalSpacing, y + verticalSpacing), 4f)
        drawNode(it, x + horizontalSpacing, y + verticalSpacing, horizontalSpacing / 1.7f, resaltado)
    }

    drawCircle(color = colorNodo, radius = radius, center = Offset(x, y))

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 35f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
        drawText(node.value.toString(), x, y + 12f, paint)
    }
}

// Clases de lógica base
class Node(
    val value: Int,
    var left: Node? = null,
    var right: Node? = null)

fun insert(root: Node?, value: Int): Node {
    if (root == null) return Node(value)
    if (value < root.value) root.left = insert(root.left, value)
    else if (value > root.value) root.right = insert(root.right, value)
    return root
}