package com.urreadiego.roomdemo // Define el paquete del proyecto

// Importaciones necesarias para configurar Room y los convertidores de datos
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Anotación que define la base de datos: incluye la entidad Persona, la versión 2 y desactiva la exportación de esquema
@Database(entities = [(Persona::class)], version = 2, exportSchema = false)
// Indica que use los convertidores (Converters) para manejar tipos de datos como Date
@TypeConverters(Converters::class)
// Clase abstracta que representa la base de datos y hereda de RoomDatabase
abstract class ProductRoomDatabase : RoomDatabase() {

    // Función abstracta que Room implementará para darnos acceso a los métodos del DAO
    abstract fun productDao(): ProductDao

    // Objeto compañero para manejar la base de datos como un Singleton (una sola instancia)
    companion object {
        // La anotación Volatile asegura que los cambios en la instancia sean visibles para todos los hilos
        @Volatile
        private var INSTANCE: ProductRoomDatabase? = null

        // Función para obtener la instancia de la base de datos; si no existe, la crea
        fun getInstance(context: Context): ProductRoomDatabase {
            // Si INSTANCE no es nulo, lo devuelve; si es nulo, entra al bloque sincronizado
            return INSTANCE ?: synchronized(this) {
                // Crea el constructor de la base de datos de Room
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Usa el contexto de la aplicación para evitar fugas de memoria
                    ProductRoomDatabase::class.java, // Clase de la base de datos
                    "Registro_Personas_Database" // Nombre físico del archivo de la base de datos
                )
                    // Permite recrear las tablas si cambias la versión sin hacer una migración compleja
                    .fallbackToDestructiveMigration()
                    .build() // Construye la instancia final

                INSTANCE = instance // Guarda la instancia en la variable estática
                instance // Devuelve la instancia recién creada
            }
        }
    }
}