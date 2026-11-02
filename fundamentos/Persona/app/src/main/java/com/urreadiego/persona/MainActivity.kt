package com.urreadiego.persona

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.urreadiego.persona.ui.theme.PersonaTheme

class MainActivity : ComponentActivity() {
    // aquí deaclaramos la lista de personas
    private val listaPersonas = mutableStateListOf<Persona>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Se llama a la función de diseño par aqu eel codigo este más organizado
                        DisenoPersona(listaPersonas)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        // Aquí se llama a la lista para mostarar los datos resultantes
                        Text("Personas registradas en la lista:", style = MaterialTheme.typography.titleMedium)
                        ListaPersonasUI(listaPersonas)
                    }
                }
            }
        }
    }
}

@Composable
fun ListaPersonasUI(personas: List<Persona>) {
    Column {
        personas.forEachIndexed { index, persona -> // Se agrega el índice a la lista
            Card(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Mostramos el índice destacado
                    Text(
                        text = "[$index]",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = PersonaManager.obtenerDetalles(persona),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
