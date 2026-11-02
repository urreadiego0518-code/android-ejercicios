package com.urreadiego.arboles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urreadiego.arboles.ui.theme.ArbolesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Stack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArbolesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaExpresiones(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaExpresiones(modifier: Modifier = Modifier) {
    val arbol = remember { ArbolBinario() }
    val scope = rememberCoroutineScope()

    var expresionInput by remember { mutableStateOf("(3+5)*2") }
    var recorridoTexto by remember { mutableStateOf("") }
    var nodoResaltado by remember { mutableStateOf<NodoExp?>(null) }
    var tick by remember { mutableIntStateOf(0) }

    // Función para animar el recorrido
    fun animarRecorrido(nodos: List<NodoExp>) {
        scope.launch {
            recorridoTexto = ""
            for (nodo in nodos) {
                nodoResaltado = nodo
                recorridoTexto += "${nodo.valor} "
                delay(600) // Tiempo de espera entre cada nodo (600ms)
            }
            nodoResaltado = null
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Visualizador de Recorridos", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = expresionInput,
            onValueChange = { expresionInput = it },
            label = { Text("Expresión aritmética") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(onClick = {
                arbol.construirDesdeExpresion(expresionInput)
                tick++
                val lista = mutableListOf<NodoExp>()
                arbol.generarListaInOrden(arbol.raiz, lista)
                animarRecorrido(lista)
            }, modifier = Modifier.weight(1f)) { Text("In") }

            Button(onClick = {
                arbol.construirDesdeExpresion(expresionInput)
                tick++
                val lista = mutableListOf<NodoExp>()
                arbol.generarListaPreOrden(arbol.raiz, lista)
                animarRecorrido(lista)
            }, modifier = Modifier.weight(1f)) { Text("Pre") }

            Button(onClick = {
                arbol.construirDesdeExpresion(expresionInput)
                tick++
                val lista = mutableListOf<NodoExp>()
                arbol.generarListaPostOrden(arbol.raiz, lista)
                animarRecorrido(lista)
            }, modifier = Modifier.weight(1f)) { Text("Post") }
        }

        Button(
            onClick = {
                arbol.raiz = null
                expresionInput = ""
                recorridoTexto = ""
                nodoResaltado = null
                tick++
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
        ) { Text("Limpiar") }

        Text(
            text = recorridoTexto,
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(400.dp).background(Color.White, shape = MaterialTheme.shapes.medium)
        ) {
            key(tick, nodoResaltado) { // Se redibuja cuando cambia el tick o el resaltado
                ArbolCanvas(arbol.raiz, nodoResaltado)
            }
        }
    }
}

@Composable
fun ArbolCanvas(raiz: NodoExp?, resaltado: NodoExp?) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (raiz != null) {
            dibujarNodo(raiz, size.width / 2, 60f, size.width / 4, resaltado)
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.dibujarNodo(
    nodo: NodoExp?, x: Float, y: Float, xOffset: Float, resaltado: NodoExp?
) {
    if (nodo == null) return

    val radio = 45f
    val distanciaVertical = 120f

    // Si este es el nodo que se está visitando, usamos un color llamativo
    val colorNodo = if (nodo === resaltado) Color(0xFFFF9800) else Color(0xFF2196F3)
    val grosorBorde = if (nodo === resaltado) 8f else 0f

    nodo.izquierdo?.let {
        drawLine(Color.DarkGray, Offset(x, y), Offset(x - xOffset, y + distanciaVertical), strokeWidth = 4f)
        dibujarNodo(it, x - xOffset, y + distanciaVertical, xOffset / 2, resaltado)
    }
    nodo.derecho?.let {
        drawLine(Color.DarkGray, Offset(x, y), Offset(x + xOffset, y + distanciaVertical), strokeWidth = 4f)
        dibujarNodo(it, x + xOffset, y + distanciaVertical, xOffset / 2, resaltado)
    }

    // Dibujar el círculo (con resaltado si corresponde)
    drawCircle(colorNodo, radio + grosorBorde, Offset(x, y))

    drawContext.canvas.nativeCanvas.drawText(
        nodo.valor, x, y + 15f,
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 45f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
    )
}

// --- ESTRUCTURA DE DATOS (Añadí métodos para generar listas de nodos) ---

class NodoExp(var valor: String) {
    var izquierdo: NodoExp? = null
    var derecho: NodoExp? = null
}

class ArbolBinario {
    var raiz: NodoExp? = null

    fun construirDesdeExpresion(expresion: String) {
        val caracteres = expresion.replace(" ", "")
        val nodos = Stack<NodoExp>()
        val operadores = Stack<Char>()
        fun prioridad(op: Char): Int = when (op) { '+', '-' -> 1; '*', '/' -> 2; else -> 0 }
        fun procesarOperador() {
            if (nodos.size >= 2) {
                val nodo = NodoExp(operadores.pop().toString())
                nodo.derecho = nodos.pop(); nodo.izquierdo = nodos.pop()
                nodos.push(nodo)
            }
        }
        var i = 0
        while (i < caracteres.length) {
            val c = caracteres[i]
            when {
                c == '(' -> operadores.push(c)
                c.isDigit() -> {
                    var num = ""; while (i < caracteres.length && caracteres[i].isDigit()) { num += caracteres[i]; i++ }
                    nodos.push(NodoExp(num)); continue
                }
                c == ')' -> { while (operadores.peek() != '(') procesarOperador(); operadores.pop() }
                else -> { while (operadores.isNotEmpty() && prioridad(operadores.peek()) >= prioridad(c)) procesarOperador(); operadores.push(c) }
            }
            i++
        }
        while (operadores.isNotEmpty()) procesarOperador()
        if (nodos.isNotEmpty()) raiz = nodos.pop()
    }

    // Métodos para llenar listas y poder animar
    fun generarListaInOrden(n: NodoExp?, l: MutableList<NodoExp>) {
        if (n == null) return
        generarListaInOrden(n.izquierdo, l)
        l.add(n)
        generarListaInOrden(n.derecho, l)
    }

    fun generarListaPreOrden(n: NodoExp?, l: MutableList<NodoExp>) {
        if (n == null) return
        l.add(n)
        generarListaPreOrden(n.izquierdo, l)
        generarListaPreOrden(n.derecho, l)
    }

    fun generarListaPostOrden(n: NodoExp?, l: MutableList<NodoExp>) {
        if (n == null) return
        generarListaPostOrden(n.izquierdo, l)
        generarListaPostOrden(n.derecho, l)
        l.add(n)
    }
}