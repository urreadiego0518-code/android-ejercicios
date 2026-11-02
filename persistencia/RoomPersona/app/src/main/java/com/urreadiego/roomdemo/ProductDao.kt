package com.urreadiego.roomdemo // Define el paquete del proyecto

// Importaciones de Room para manejar la base de datos y LiveData para observar cambios
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

// Indica que esta interfaz es un DAO, encargada de las consultas a la base de datos
@Dao
interface ProductDao {

    // Define una operación de inserción; si el ID ya existe, reemplaza los datos antiguos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(persona: Persona)

    // Define una operación para borrar un registro específico de la tabla
    @Delete
    fun deleteProduct(persona: Persona)

    // Consulta SQL para obtener todos los registros de la tabla "Personas"
    // Devuelve un LiveData para que la interfaz se actualice sola si algo cambia
    @Query("SELECT * FROM Personas")
    fun getAllProducts(): LiveData<List<Persona>>

    // Busca personas cuyo nombre contenga el texto enviado (usando el operador LIKE)
    // El símbolo '||' sirve para concatenar el texto con los comodines '%'
    @Query("SELECT * FROM Personas WHERE Nombre LIKE '%' || :name || '%'")
    fun findPersona(name: String): List<Persona>

    // Obtiene únicamente a los usuarios que tienen un alias de huella guardado
    // Esto sirve para el modo de identificación biométrica
    @Query("SELECT * FROM Personas WHERE aliasHuella IS NOT NULL")
    fun getPersonasConHuella(): List<Persona>

    // Busca un registro exacto comparando el "CurpId" con el folio de 6 dígitos
    // Aunque sea un ID único, Room lo entrega dentro de una lista por seguridad
    @Query("SELECT * FROM Personas WHERE CurpId = :id")
    fun findById(id: Int): List<Persona>
}