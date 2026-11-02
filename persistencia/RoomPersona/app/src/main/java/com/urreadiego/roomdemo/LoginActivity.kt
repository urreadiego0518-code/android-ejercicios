package com.urreadiego.roomdemo // Define el paquete del proyecto

// Importaciones necesarias para Compose, Componentes de Android y Biometría
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Clase de la pantalla de Login que hereda de AppCompatActivity para usar fragmentos de biometría
class LoginActivity : AppCompatActivity() {

    // Instancia del ayudante de biometría
    private lateinit var biometricHelper: BiometricHelper

    // Método principal que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama a la lógica base de Android
        biometricHelper = BiometricHelper(this) // Inicializa el ayudante con el contexto actual

        // Accede al archivo de preferencias "seguridad_app" para leer datos guardados localmente
        val sharedPop = getSharedPreferences("seguridad_app", Context.MODE_PRIVATE)

        // Define el contenido visual de la pantalla usando Jetpack Compose
        setContent {
            // Variable de estado que recuerda si la huella ya fue registrada anteriormente
            var isHuellaRegistrada by remember {
                mutableStateOf(sharedPop.getBoolean("huella_registrada", false))
            }

            // Aplica el tema visual de Material Design
            MaterialTheme {
                // Estructura base de la pantalla que ocupa todo el tamaño disponible
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    // Llama a la función que dibuja la interfaz de usuario
                    LoginScreen(
                        isHuellaRegistrada = isHuellaRegistrada, // Pasa el estado de registro
                        modifier = Modifier.padding(paddingValues), // Respeta los espacios del Scaffold
                        onRegisterClick = { registrarHuellaInicial { isHuellaRegistrada = true } }, // Acción al registrar
                        onLoginClick = { autenticarConHuella() } // Acción al querer entrar
                    )
                }
            }
        }
    }

    // Función para el primer registro de huella cuando la app es nueva
    private fun registrarHuellaInicial(onSuccessRegistry: () -> Unit) {
        // Verifica si el hardware del teléfono permite usar biometría
        val support = biometricHelper.checkBiometricSupport()

        // Si el resultado es exitoso (el código de éxito es 0)
        if (support == BiometricManager.BIOMETRIC_SUCCESS) {
            // Muestra el cuadro de diálogo para escanear el dedo
            biometricHelper.mostrarPrompt(
                activity = this, // Actividad actual
                titulo = "Registro de Huella", // Título de la ventana
                subtitulo = "Configura tu acceso biométrico", // Descripción
                onSuccess = {
                    // Crea la llave criptográfica inicial llamada "UsuarioPrincipal"
                    biometricHelper.crearLlaveParaPersona("UsuarioPrincipal")

                    // Guarda en preferencias que el usuario ya registró su huella
                    val sharedPop = getSharedPreferences("seguridad_app", Context.MODE_PRIVATE)
                    sharedPop.edit().putBoolean("huella_registrada", true).apply()

                    onSuccessRegistry() // Actualiza el estado en la interfaz
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show() // Aviso visual
                    irAMainActivity() // Salta a la pantalla principal
                },
                onError = { error ->
                    // Muestra un mensaje si hubo un error durante el escaneo
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Aviso si el dispositivo no tiene sensor o no es compatible
            Toast.makeText(this, "Hardware biométrico no disponible en tu Cubot", Toast.LENGTH_LONG).show()
        }
    }

    // Función para validar la huella una vez que ya ha sido registrada
    private fun autenticarConHuella() {
        // Muestra el lector de huellas para verificar identidad
        biometricHelper.mostrarPrompt(
            activity = this,
            titulo = "Desbloquear",
            subtitulo = "Usa tu huella para entrar",
            onSuccess = { irAMainActivity() }, // Si es correcto, entra a la app
            onError = { error ->
                // Aviso si el dedo no coincide o se cancela el proceso
                Toast.makeText(this, "Acceso denegado: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Función para navegar de la pantalla de Login a la de Registro Civil
    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java) // Crea la orden de navegación
        // Configura banderas para que el usuario no pueda regresar al Login con el botón atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent) // Inicia el cambio de pantalla
        finish() // Destruye la actividad de Login para liberar memoria
    }
}

// Componente visual de la pantalla de bienvenida/login
@Composable
fun LoginScreen(
    isHuellaRegistrada: Boolean, // Variable para saber qué botones mostrar
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit, // Acción para el botón de registro
    onLoginClick: () -> Unit // Acción para el botón de ingreso
) {
    // Columna central que organiza los elementos verticalmente
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la sección
        Text("Seguridad", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        // Espacio vertical de 24dp
        Spacer(modifier = Modifier.height(24.dp))

        // Si la huella NO está registrada, muestra la instrucción de registro
        if (!isHuellaRegistrada) {
            Text("Es necesario registrar tu huella para continuar.", modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = onRegisterClick) {
                Text("Registrar Huella")
            }
        } else {
            // Si ya está registrada, muestra el estado protegido y el botón de ingreso
            Text("App Protegida", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLoginClick) {
                Text("Ingresar con Huella")
            }

            // Intento automático de abrir el lector de huellas al cargar la pantalla
            LaunchedEffect(Unit) {
                onLoginClick()
            }
        }
    }
}