package com.example.ecocleanmanager.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResiduoDao {
    @Insert
    suspend fun insertarRegistro(residuo: ResiduoEntity)

    @Query("SELECT * FROM recoleccion_residuos ORDER BY fecha DESC")
    suspend fun obtenerTodosDirecto(): List<ResiduoEntity>

    @Query("SELECT * FROM recoleccion_residuos WHERE tipo = :tipoResiduo")
    fun filtrarPorTipo(tipoResiduo: String): List<ResiduoEntity>

    @Query("SELECT * FROM recoleccion_residuos WHERE sincronizado = 0")
    fun obtenerPendientesSincronizar(): List<ResiduoEntity>
}