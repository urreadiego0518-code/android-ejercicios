package com.urreadiego.metodoburbuja

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
        // Punto de entrada: aquí se define qué se va a mostrar en pantalla
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PantallaOrdenamiento()
            }
        }
    }
}

@Composable
fun PantallaOrdenamiento() {
    // 1. ESTADO: La lista de números que queremos ordenar.
    // Usamos 'remember' para que Android no olvide los datos al redibujar la pantalla.
    var listaNumeros by remember { mutableStateOf(listOf(45, 30, 15, 50, 5, 20, 18, 11, 95)) }

    // 2. COROUTINE SCOPE: Necesario para hacer pausas (delay) sin congelar el celular.
    val scope = rememberCoroutineScope()

    // 3. VARIABLE DE CONTROL: Para saber si el algoritmo está trabajando.
    var estaOrdenando by remember { mutableStateOf(false) }

    // ESTRUCTURA VISUAL (Columna principal)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Método Burbuja Simple", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(50.dp))

        // 4. REPRESENTACIÓN GRÁFICA: Dibujamos una barra por cada número.
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
                        .width(35.dp)
                        .height((valor * 3).dp) // La altura depende del valor del número
                        .background(Color(0xFF6200EE), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(text = "$valor", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 5. BOTÓN DE ACCIÓN: Ejecuta el algoritmo al presionar.
        Button(
            onClick = {
                if (!estaOrdenando) {
                    estaOrdenando = true
                    // Lanzamos el proceso en segundo plano
                    scope.launch {
                        val copiaLista = listaNumeros.toMutableList()
                        val n = copiaLista.size

                        // --- ALGORITMO BURBUJA SIMPLE ---
                        for (i in 0 until n - 1) {
                            for (j in 0 until n - i - 1) {
                                // Si el de la izquierda es mayor que el de la derecha...
                                if (copiaLista[j] > copiaLista[j + 1]) {
                                    // ...se intercambian
                                    val temporal = copiaLista[j]
                                    copiaLista[j] = copiaLista[j + 1]
                                    copiaLista[j + 1] = temporal

                                    // Actualizamos la interfaz para que el usuario vea el cambio
                                    listaNumeros = copiaLista.toList()
                                    delay(400) // Pausa de 400ms para ver la "animación"
                                }
                            }
                        }
                        estaOrdenando = false
                    }
                }
            },
            enabled = !estaOrdenando // El botón se desactiva mientras ordena
        ) {
            Text(if (estaOrdenando) "Ordenando..." else "Comenzar Ordenamiento")
        }

        // 6. REINICIAR: Botón extra para mezclar los números de nuevo.
        if (!estaOrdenando) {
            TextButton(onClick = { listaNumeros = listaNumeros.shuffled() }) {
                Text("Mezclar Números")
            }
        }
    }
}