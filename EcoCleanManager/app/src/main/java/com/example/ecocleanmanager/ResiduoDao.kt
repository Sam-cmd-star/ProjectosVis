package com.example.ecocleanmanager
import androidx.room.*

@Dao
interface ResiduoDao {
    @Insert
    suspend fun insertarRegistro(residuo: ResiduoEntity)

    @Query("SELECT * FROM registros_recoleccion ORDER BY fecha DESC")
    fun obtenerTodosLosRegistros(): List<ResiduoEntity>

    @Query("SELECT * FROM registros_recoleccion WHERE estaSincronizado = 0")
    suspend fun obtenerPendientesDeSincronizar(): List<ResiduoEntity>
}
