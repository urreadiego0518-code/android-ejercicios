package com.urreadiego.roomdemo // Nombre del paquete del proyecto

// Importaciones necesarias para el funcionamiento de la actividad y la interfaz Compose
import android.app.Application
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

// Clase principal de la pantalla de registro que hereda de AppCompatActivity
class MainActivity : AppCompatActivity() {
    // Método que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llamada al constructor de la clase padre
        setContent { // Define el contenido de la pantalla con Jetpack Compose
            // Inicializa el ViewModel usando una Factory para pasarle la aplicación
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(application)
            )
            MainScreen(viewModel) // Llama a la función principal de la interfaz
        }
    }
}

// Función Composable que dibuja toda la interfaz de usuario
@Composable
fun MainScreen(viewModel: MainViewModel) {
    // Estados que recuerdan los textos ingresados en los campos de captura
    var nombre by remember { mutableStateOf("") }
    var apPaterno by remember { mutableStateOf("") }
    var apMaterno by remember { mutableStateOf("") }
    var curpMadre by remember { mutableStateOf("") }
    var curpPadre by remember { mutableStateOf("") }
    var fechaTexto by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf(Date()) }
    // Estado para saber si estamos mostrando resultados de búsqueda o toda la lista
    var searching by remember { mutableStateOf(false) }

    // Estados para controlar el menú desplegable y el tipo de teclado
    var expanded by remember { mutableStateOf(false) }
    var buscarPorIdActive by remember { mutableStateOf(false) }

    // Observa los datos de la base de datos (se actualizan solos cuando cambian)
    val allPersonas by viewModel.allProducts.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())
    // Obtiene el contexto actual de la aplicación
    val context = LocalContext.current

    // Configura el diálogo de selección de fecha (Calendario)
    val datePicker = DatePickerDialog(context, { _, y, m, d ->
        fechaTexto = "$d/${m + 1}/$y" // Formatea la fecha para mostrarla
        val cal = Calendar.getInstance().apply { set(y, m, d) } // Crea un calendario con la fecha elegida
        fechaSeleccionada = cal.time // Guarda la fecha como objeto Date
    }, 2024, 0, 1)

    // Contenedor principal vertical con scroll (por si hay muchos registros)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título de la aplicación
        Text(
            text = "Registro Civil - Personas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de entrada que cambia dinámicamente según el modo (Nombre o ID)
        OutlinedTextField(
            value = nombre, // Valor actual del campo
            onValueChange = { nombre = it }, // Actualiza el estado cuando el usuario escribe
            label = { Text(if (buscarPorIdActive) "Escribe el ID (Folio)" else "Nombre(s)") }, // Etiqueta dinámica
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), // Ajuste de ancho y espacio
            singleLine = true, // Evita saltos de línea
            keyboardOptions = KeyboardOptions(
                // Cambia entre teclado de texto o numérico
                keyboardType = if (buscarPorIdActive) KeyboardType.Number else KeyboardType.Text
            )
        )

        // Campos de texto personalizados para el resto de datos
        CustomTextField(label = "Apellido Paterno", value = apPaterno) { apPaterno = it }
        CustomTextField(label = "Apellido Materno", value = apMaterno) { apMaterno = it }
        CustomTextField(label = "CURP de la Madre", value = curpMadre) { curpMadre = it }
        CustomTextField(label = "CURP del Padre", value = curpPadre) { curpPadre = it }

        // Botón para abrir el calendario
        Button(
            onClick = { datePicker.show() }, // Muestra el DatePicker al hacer clic
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
        ) {
            Text(if (fechaTexto.isEmpty()) "Seleccionar Fecha de Nacimiento" else "Fecha: $fechaTexto")
        }

        // Botón principal para Guardar con Biometría
        Button(
            onClick = {
                if (nombre.isNotBlank()) { // Valida que al menos haya un nombre
                    viewModel.registrarPersonaConDedo(
                        // Crea el objeto persona (el ID se generará aleatorio en el ViewModel)
                        persona = Persona(id = 0, Nombre = nombre, ApPaterno = apPaterno, ApMaterno = apMaterno, CurpMadre = curpMadre, CurpPadre = curpPadre, fechaNacimiento = fechaSeleccionada),
                        helper = BiometricHelper(context), // Pasa el ayudante de huella
                        activity = context as androidx.fragment.app.FragmentActivity // Pasa la actividad necesaria para el prompt
                    )
                    // Limpia todos los campos después de intentar guardar
                    nombre = ""; apPaterno = ""; apMaterno = ""; curpMadre = ""; curpPadre = ""; fechaTexto = ""
                    buscarPorIdActive = false
                } else {
                    // Aviso si el campo nombre está vacío
                    Toast.makeText(context, "Escribe el nombre primero", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50), contentColor = Color.White)
        ) {
            Text("Guardar con Huella Digital")
        }

        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre secciones

        // Fila que contiene el menú de búsqueda y el botón de limpiar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Contenedor para el menú desplegable de búsqueda
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { expanded = true }, // Abre el menú al dar clic
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF673AB7), contentColor = Color.White)
                ) {
                    Text("Buscar por... 🔍")
                }

                // Definición de las opciones del menú de búsqueda
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {
                        expanded = false // Cierra menú
                        buscarPorIdActive = false // Teclado normal
                        searching = true // Activa modo búsqueda
                        viewModel.findProduct(nombre) // Busca por texto
                    }) { Text("Nombre") }

                    DropdownMenuItem(onClick = {
                        expanded = false
                        buscarPorIdActive = true // Activa teclado de números
                        searching = true
                        viewModel.findById(nombre) // Busca por ID
                    }) { Text("ID (Folio)") }

                    DropdownMenuItem(onClick = {
                        expanded = false
                        searching = true
                        // Llama a la función de búsqueda por huella
                        viewModel.identificarDedo(
                            helper = BiometricHelper(context),
                            activity = context as androidx.fragment.app.FragmentActivity
                        )
                    }) { Text("Huella Digital ☝️") }
                }
            }

            // Botón para resetear la vista y ver todos los registros
            Button(
                onClick = {
                    searching = false // Regresa a la lista completa
                    buscarPorIdActive = false // Regresa teclado a texto
                    nombre = ""; apPaterno = ""; apMaterno = ""; curpMadre = ""; curpPadre = ""; fechaTexto = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray, contentColor = Color.White)
            ) { Text("Limpiar") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Encabezado de la lista de personas
        Text("Registros Guardados:", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

        // Decide si muestra los resultados filtrados o toda la base de datos
        val listaMostrar = if (searching) searchResults else allPersonas

        // Ciclo para dibujar cada registro encontrado en una tarjeta (Card)
        listaMostrar.forEach { persona ->
            Card(
                elevation = 4.dp,
                modifier = Modifier.padding(vertical = 6.dp).fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    // Columna con los datos informativos
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Muestra el folio (ID aleatorio)
                            Text(text = "[#${persona.id}] ", color = Color.Blue, fontWeight = FontWeight.ExtraBold)
                            // Muestra nombre completo
                            Text(text = "${persona.Nombre} ${persona.ApPaterno}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            // Si tiene huella, muestra el icono del escudo
                            if (persona.aliasHuella != null) { Text(" 🛡️") }
                        }
                        Text("Madre: ${persona.CurpMadre}", fontSize = 12.sp)
                        Text("Padre: ${persona.CurpPadre}", fontSize = 12.sp)
                        Text("Nacimiento: ${persona.fechaNacimiento}", fontSize = 11.sp, color = Color.Gray)
                    }
                    // Botón para eliminar el registro de la base de datos
                    IconButton(onClick = { viewModel.deleteProduct(persona) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFB00020))
                    }
                }
            }
        }
    }
}

// Función auxiliar para crear campos de texto Outlined de forma rápida
@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = true
    )
}

// Clase necesaria para poder instanciar el ViewModel pasándole parámetros (Application)
class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que la clase solicitada sea efectivamente nuestro MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T // Retorna la instancia creada
        }
        // Lanza error si se intenta crear un ViewModel no registrado
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}