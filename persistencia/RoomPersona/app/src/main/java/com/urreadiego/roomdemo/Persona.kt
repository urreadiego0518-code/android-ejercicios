package com.urreadiego.roomdemo // Define el paquete donde se encuentra este archivo

// Importaciones de Room para definir tablas y columnas, y la clase Date para fechas
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// Define que esta clase es una Entidad de Room, mapeada a una tabla llamada "Personas"
@Entity(tableName = "Personas")
data class Persona(
    // Marca el siguiente campo como la Llave Primaria (identificador único) de la tabla
    @PrimaryKey // Quitamos el autoGenerate para que el sistema use el ID aleatorio de 6 dígitos
    // Define el nombre físico de la columna en la base de datos como "CurpId"
    @ColumnInfo(name = "CurpId")
    val id: Int, // Esta variable almacenará el folio generado aleatoriamente

    // Define la columna para el Nombre, con un valor inicial de texto vacío
    @ColumnInfo(name = "Nombre") var Nombre: String = "",

    // Define la columna para el Apellido Paterno
    @ColumnInfo(name = "ApPaterno") var ApPaterno: String = "",

    // Define la columna para el Apellido Materno
    @ColumnInfo(name = "ApMaterno") var ApMaterno: String = "",

    // Define la columna para almacenar la CURP de la madre como referencia
    @ColumnInfo(name = "CurpMadre") var CurpMadre: String = "",

    // Define la columna para almacenar la CURP del padre como referencia
    @ColumnInfo(name = "CurpPadre") var CurpPadre: String = "",

    // Define la columna para la fecha de nacimiento (usa los Converters para procesar el objeto Date)
    @ColumnInfo(name = "fechaNacimiento") var fechaNacimiento: Date = Date(),

    // Columna opcional para guardar el alias de la huella digital (puede ser nulo si no tiene huella)
    @ColumnInfo(name = "aliasHuella") var aliasHuella: String? = null
)