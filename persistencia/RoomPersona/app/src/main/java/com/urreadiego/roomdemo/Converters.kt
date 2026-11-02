package com.urreadiego.roomdemo // Define el paquete al que pertenece esta clase

// Importa la anotación de Room para convertir datos y la clase Date de Java
import androidx.room.TypeConverter
import java.util.Date

// Clase que contiene los métodos para transformar tipos de datos complejos
class Converters {

    // Indica a Room que esta función sirve para convertir un dato de la base de datos a código
    @TypeConverter
    // Convierte un número Long (milisegundos) que viene de la base de datos a un objeto Date
    // Si el valor es nulo, devuelve nulo; si no, crea el objeto Date con esos milisegundos
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    // Indica a Room que esta función sirve para convertir un dato de código a la base de datos
    @TypeConverter
    // Toma un objeto Date ylo transforma en un número Long (milisegundos) para guardarlo en SQLite
    // Si el objeto Date es nulo, devuelve nulo; de lo contrario, extrae el tiempo en milisegundos
    fun dateToTimestamp(date: Date?): Long? = date?.time
}