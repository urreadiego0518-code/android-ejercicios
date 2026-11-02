package com.urreadiego.ciclosfors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.urreadiego.ciclosfors.ui.theme.CiclosForsTheme

/*
 - Pantalla principal que muestra los resultados del análisis de eficiencia.
 * Se encarga de la interfaz de usuario y de llamar a los métodos de cálculo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Configura la app para usar toda la pantalla
        setContent {
            CiclosForsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Column con scroll para evitar que el contenido se corte en pantallas pequeñas
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // n es el tamaño de entrada. Usamos 1000 para observar diferencias reales de tiempo.
                        val n = 1000

                        // Se ejecutan los análisis y se guardan los resultados
                        val resA = analizarCicloA(n)
                        val resB = analizarCicloB(n)
                        val resC = analizarCicloC(n)
                        val resD = analizarCicloD(n)

                        // Título en la aplicación
                        Text("Análisis de Algoritmos (n = $n)",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp))

                        // Segmento en donde se mostrara los detalles de los ciclos
                        InfoCard("Ciclo A", "O(n²)", resA)
                        InfoCard("Ciclo B", "O(n)", resB)
                        InfoCard("Ciclo C", "O(n²)", resC)
                        InfoCard("Ciclo D", "O(n²)", resD)
                    }
                }
            }
        }
    }
}

/*
 - Función que crea una tarjeta con los datos de eficiencia.
 - @param título Nombre del ciclo.
 - @param complejidad Notación Big O teórica.
 - @param resultado Objeto con el conteo de iteraciones y tiempo medido.
 */
@Composable
fun InfoCard(titulo: String, complejidad: String, resultado: Resultado) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge)
            Text("Complejidad: $complejidad", color = MaterialTheme.colorScheme.primary)

            // Línea divisoria estética
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Muestra cuántas veces se ejecutó la operación crítica
            Text("Ejecuciones: ${resultado.conteo}")

            // Muestra el tiempo en microsegundos para mayor precisión
            Text("Tiempo: ${resultado.tiempoMicro} microsegundos",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold)
        }
    }
}
