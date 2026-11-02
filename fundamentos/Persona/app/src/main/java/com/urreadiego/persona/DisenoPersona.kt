package com.urreadiego.persona

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun DisenoPersona(listaPersonas: MutableList<Persona>) {
    var nombreAdd by remember { mutableStateOf("") }
    var curpAdd by remember { mutableStateOf("") }
    var edadAdd by remember { mutableStateOf("") }

    // Eliminar
    var curpEliminar by remember { mutableStateOf("") } // se elimina por el CURP
    var indiceEliminar by remember { mutableStateOf("") } // se elimina por el ÍNDICE

    var curpModificar by remember { mutableStateOf("") } // se modifica por el CURP
    var nombreNuevo by remember { mutableStateOf("") } // se le da un nuevo nombre
    var edadNueva by remember { mutableStateOf("") } // se le da una nueva edad

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // campo para añadir
        Text("Añadir Persona", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = nombreAdd, onValueChange = { nombreAdd = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = curpAdd, onValueChange = { curpAdd = it }, label = { Text("CURP") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = edadAdd, onValueChange = { edadAdd = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = {
            PersonaManager.agregar(listaPersonas, Persona(nombreAdd, curpAdd, edadAdd.toIntOrNull() ?: 0))
            nombreAdd = ""; curpAdd = ""; edadAdd = ""
        }) { Text("Añadir") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // campo para eliminar
        Text("Eliminar Persona", style = MaterialTheme.typography.titleMedium)

        // campo para eliminar por CURP
        OutlinedTextField(value = curpEliminar, onValueChange = { curpEliminar = it }, label = { Text("CURP a eliminar") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            PersonaManager.borrarPorCurp(listaPersonas, curpEliminar)
            curpEliminar = ""
        }) { Text("Eliminar") }

        // campo para eliminar por ÍNDICE
        OutlinedTextField(
            value = indiceEliminar,
            onValueChange = { indiceEliminar = it },
            label = { Text("Índice a eliminar") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            val idx = indiceEliminar.toIntOrNull()
            if (idx != null) {
                PersonaManager.borrarPorIndice(listaPersonas, idx)
            }
            indiceEliminar = ""
        }) { Text("Eliminar") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // campo para modificar
        Text("Modificar Persona", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = curpModificar, onValueChange = { curpModificar = it }, label = { Text("CURP a modificar") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = nombreNuevo, onValueChange = { nombreNuevo = it }, label = { Text("Nuevo nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = edadNueva, onValueChange = { edadNueva = it }, label = { Text("Nueva edad") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = {
            PersonaManager.editar(listaPersonas, curpModificar, Persona(nombreNuevo, curpModificar, edadNueva.toIntOrNull() ?: 0))
            curpModificar = ""; nombreNuevo = ""; edadNueva = ""
        }) { Text("Modificar") }
    }
}