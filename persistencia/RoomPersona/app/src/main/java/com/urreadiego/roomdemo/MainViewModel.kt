package com.urreadiego.roomdemo // Define el paquete del proyecto

// Importaciones para manejar la lógica de la aplicación, ciclos de vida y corrutinas
import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

// Clase ViewModel que conecta la interfaz de usuario con la base de datos (Repository)
class MainViewModel(application: Application) : ViewModel() {

    // Lista observable que contiene a todas las personas en la base de datos
    val allProducts: LiveData<List<Persona>>
    // Lista observable para los resultados de las búsquedas (Nombre, ID o Huella)
    val searchResults: MutableLiveData<List<Persona>>
    // Referencia al repositorio que gestiona las operaciones de datos
    private val repository: ProductRepository
    // Referencia directa al DAO para realizar consultas específicas
    private val productDao: ProductDao

    // Bloque de inicialización que se ejecuta al crear el ViewModel
    init {
        // Obtiene la instancia única de la base de datos Room
        val productDb = ProductRoomDatabase.getInstance(application)
        // Inicializa el DAO a partir de la base de datos
        productDao = productDb.productDao()
        // Inicializa el repositorio pasándole el DAO
        repository = ProductRepository(productDao)

        // Vincula las variables locales con las que vienen del repositorio
        allProducts = repository.allProducts
        searchResults = repository.searchResults
    }

    // Función para insertar una persona en la base de datos a través del repositorio
    fun insertProduct(persona: Persona) {
        repository.insertProduct(persona)
    }

    // Función para eliminar un registro de la base de datos
    fun deleteProduct(persona: Persona) {
        repository.deleteProduct(persona)
    }

    // Función para buscar personas por coincidencia de nombre
    fun findProduct(name: String) {
        repository.findProduct(name)
    }

    // Función principal para registrar una persona nueva usando biometría
    fun registrarPersonaConDedo(persona: Persona, helper: BiometricHelper, activity: FragmentActivity) {
        // Genera un número aleatorio de 6 dígitos para usarlo como folio (ID único)
        val numeroAleatorio = (100000..999999).random()
        // Crea un nombre único para la llave de seguridad basado en ese número
        val aliasUnico = "HUELLA_$numeroAleatorio"

        // Muestra el prompt de la huella digital
        helper.mostrarPrompt(
            activity = activity,
            titulo = "Registro Biométrico",
            subtitulo = "Folio: $numeroAleatorio", // Muestra el folio en pantalla
            onSuccess = {
                // Si la huella es válida, crea la llave física en el chip del teléfono
                helper.crearLlaveParaPersona(aliasUnico)
                // Crea una copia de la persona asignándole el folio aleatorio y su alias de huella
                val personaFinal = persona.copy(id = numeroAleatorio, aliasHuella = aliasUnico)
                // Guarda la persona final con todos sus datos en Room
                insertProduct(personaFinal)
            },
            onError = { /* Espacio para manejar errores de registro */ }
        )
    }

    // Función para identificar a alguien ya registrado mediante su huella
    fun identificarDedo(helper: BiometricHelper, activity: FragmentActivity) {
        // Solicita el escaneo del dedo
        helper.mostrarPrompt(
            activity = activity,
            titulo = "Modo Identificación",
            subtitulo = "Coloque su dedo para buscar su registro",
            onSuccess = {
                // Inicia una corrutina en el hilo principal para manejar la respuesta
                viewModelScope.launch(Dispatchers.Main) {
                    // Cambia al hilo de Entrada/Salida (IO) para consultar la base de datos
                    val registrados = withContext(Dispatchers.IO) {
                        // Trae de la DB solo a las personas que tienen una huella vinculada
                        productDao.getPersonasConHuella()
                    }

                    // Si hay personas con huella registradas
                    if (registrados.isNotEmpty()) {
                        // Actualiza la lista de resultados de búsqueda para mostrar solo a esos usuarios
                        searchResults.value = registrados
                        // Muestra un aviso de éxito en la interfaz
                        Toast.makeText(activity, "Identidad Verificada: Accediendo a registros", Toast.LENGTH_SHORT).show()
                    } else {
                        // Avisa si no se encontró ninguna huella en la base de datos
                        Toast.makeText(activity, "No hay huellas registradas en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onError = { error ->
                // Muestra un aviso si el sensor falla o el usuario cancela
                Toast.makeText(activity, "Error de identificación: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Función para buscar un registro específico usando el ID (folio de 6 dígitos)
    fun findById(id: String) {
        // Intenta convertir el texto ingresado a un número entero
        val idInt = id.toIntOrNull()
        // Si la conversión fue exitosa (es un número válido)
        if (idInt != null) {
            // Inicia una corrutina ligada al ciclo de vida del ViewModel
            viewModelScope.launch(Dispatchers.Main) {
                // Realiza la consulta en un hilo secundario para no congelar la pantalla
                val result = withContext(Dispatchers.IO) {
                    productDao.findById(idInt) // Llama al DAO para buscar el ID exacto
                }
                // Actualiza los resultados de búsqueda con la persona encontrada
                searchResults.value = result
            }
        }
    }
}