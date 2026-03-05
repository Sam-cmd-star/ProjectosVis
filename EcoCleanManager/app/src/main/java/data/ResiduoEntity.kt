package com.example.ecocleanmanager.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recoleccion_residuos")
data class ResiduoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,          // Ej: Plástico, Vidrio 
    val cantidad: Double,      // Cantidad recolectada 
    val unidad: String,        // Kg, m3, etc.
    val fecha: Long,           // Para filtrado por fecha 
    val area: String,          // Oficina, Planta Industrial [cite: 1]
    val sincronizado: Boolean = false // Para la API RESTful 
)