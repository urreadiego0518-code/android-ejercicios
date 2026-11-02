package com.urreadiego.persona

class Persona (var nombre: String, var curp: String, var edad: Int)

// usamos un objeto para poder llamar a sus funciones sin tener que instanciarlas
object PersonaManager {
    // funciones para agregar, eliminar, modificar y obtener detalles de una persona
    fun obtenerDetalles(p: Persona): String {
        return "Nombre: ${p.nombre}\nCURP: ${p.curp}\nEdad: ${p.edad} años"
    }

    fun agregar(lista: MutableList<Persona>, p: Persona) = lista.add(p)

    // modifica por su curp y actualiza los datos
    fun editar(lista: MutableList<Persona>, curpObjetivo: String, nuevaPersona: Persona) {
        val indice = lista.indexOfFirst { it.curp == curpObjetivo }
        if (indice != -1) {
            lista[indice] = nuevaPersona
        }
    }

    // eliminar por su curp
    fun borrarPorCurp(lista: MutableList<Persona>, curpObjetivo: String) {
        lista.removeAll { it.curp == curpObjetivo }
    }

    // eliminar por su índice
    fun borrarPorIndice(lista: MutableList<Persona>, indice: Int) {
        if (indice in lista.indices) {
            lista.removeAt(indice)
        }
    }
}
