package com.urreadiego.roomdemo // Paquete del proyecto

// Importaciones de librerías de Android para seguridad, biometría
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.security.KeyStore
import javax.crypto.KeyGenerator

// Clase encargada de gestionar el sensor de huella y llaves de seguridad
class BiometricHelper(val context: Context) {

    // Muestra la ventana de escaneo de huella
    fun mostrarPrompt(
        activity: androidx.fragment.app.FragmentActivity, // muestra el diálogo
        titulo: String, // Título de la ventana
        subtitulo: String, // Descripción de la ventana
        onSuccess: () -> Unit, // Función a ejecutar si la huella es válida
        onError: (String) -> Unit // Función a ejecutar si hay error
    ) {
        // Obtiene el ejecutor para correr tareas en el hilo principal
        val executor = ContextCompat.getMainExecutor(activity)

        // Crea la instancia que gestiona la respuesta del sensor
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() { // Inicia el proceso de autenticación
                // Se ejecuta cuando la autenticación es exitosa
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess() // Llama a la función de éxito
                }
                // Se ejecuta cuando hay un error (po rejmplo un dedo mal puesto)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString()) // Devuelve el mensaje de error como texto
                }
            })

        // Configura los detalles de la autenticación
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo) // Asigna el título
            .setSubtitle(subtitulo) // Asigna el subtítulo
            .setNegativeButtonText("Cancelar") // Texto del botón para abortar
            .build() // Construye el objeto final

        // Ordena al sistema que active el lector y muestre el diálogo
        biometricPrompt.authenticate(promptInfo)
    }

    // Función para crear una llave de seguridad única en el chip del teléfono
    fun crearLlaveParaPersona(alias: String) {
        try { // Bloque para capturar posibles errores de seguridad
            // Accede al almacén de llaves seguro del sistema Android
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null) // Carga el almacén (necesario inicializarlo)

            // Configura un generador de llaves bajo el estándar AES
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, // Algoritmo de cifrado
                "AndroidKeyStore" // Ubicación segura
            )

            // Define las reglas de uso de esta llave específica
            val builder = KeyGenParameterSpec.Builder(
                alias, // El identificador único (tu folio aleatorio)
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT // Sirve para cifrar y descifrar
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // Define cómo se agrupan los datos
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7) // Define el relleno de datos
                .setUserAuthenticationRequired(true) // ¡CLAVE!: Solo se usa si el usuario pone su huella
                .setInvalidatedByBiometricEnrollment(true) // Si el dueño cambia su huella en ajustes, la llave muere

            // Inicializa el generador con las reglas creadas
            keyGenerator.init(builder.build())
            // Crea la llave físicamente dentro del hardware seguro
            keyGenerator.generateKey()

        } catch (e: Exception) { // Si algo falla en el hardware o permisos
            e.printStackTrace() // Imprime el error en la consola de depuración
        }
    }

    // Función para checar si el dispositivo tiene sensor y huellas configuradas
    fun checkBiometricSupport(): Int {
        // Consulta al sistema si se puede usar biometría fuerte (Strong)
        return BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    }
}