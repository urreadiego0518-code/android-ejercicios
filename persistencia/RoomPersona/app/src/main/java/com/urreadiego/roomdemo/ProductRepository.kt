package com.urreadiego.roomdemo // Define el paquete del proyecto

// Importaciones para manejar datos observables (LiveData) y tareas en segundo plano (Corrutinas)
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

// Clase Repositorio: sirve como intermediaria entre el ViewModel y el DAO (la fuente de datos)
class ProductRepository(private val productDao: ProductDao) {

    // Variable que contiene a todas las personas; se sincroniza automáticamente con el DAO
    val allProducts: LiveData<List<Persona>> = productDao.getAllProducts()
    // Variable para guardar los resultados de las búsquedas y avisar a la interfaz
    val searchResults = MutableLiveData<List<Persona>>()
    // Define el alcance de las corrutinas, indicando que por defecto inicien en el hilo principal (Main)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Función para insertar una nueva persona en la base de datos
    fun insertProduct(newproduct: Persona) {
        // Inicia una tarea en segundo plano usando el hilo de Entrada/Salida (IO) para no trabar la app
        coroutineScope.launch(Dispatchers.IO) {
            productDao.insertProduct(newproduct) // Llama al DAO para realizar la inserción
        }
    }

    // Función para eliminar un registro de la base de datos
    fun deleteProduct(persona: Persona) {
        // Ejecuta la eliminación en un hilo secundario (IO)
        coroutineScope.launch(Dispatchers.IO) {
            productDao.deleteProduct(persona) // Llama al DAO para borrar la fila
        }
    }

    // Función para buscar personas por nombre y actualizar los resultados
    fun findProduct(name: String) {
        // Se ejecuta en el hilo principal porque vamos a actualizar la interfaz (searchResults)
        coroutineScope.launch(Dispatchers.Main) {
            // Llama a la función asíncrona y espera el resultado sin bloquear el hilo principal
            val result = asyncFind(name).await()
            // Asigna el resultado encontrado o una lista vacía si no hay coincidencias
            searchResults.value = result ?: listOf()
        }
    }

    // Función privada que realiza la búsqueda de forma asíncrona (en paralelo)
    private fun asyncFind(name: String): Deferred<List<Persona>?> =
        // Crea una tarea que promete devolver un resultado (Deferred) en el hilo IO
        coroutineScope.async(Dispatchers.IO) {
            // Ejecuta la consulta en el DAO y devuelve el resultado de la búsqueda
            return@async productDao.findPersona(name)
        }
}