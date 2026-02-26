package com.example.ecocleanmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_recoleccion")
data class ResiduoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipoResiduo: String, // Ejemplo: Plástico, Papel, Peligroso [cite: 5, 9]
    val cantidad: Double,    // Volumen o peso [cite: 5]
    val ubicacion: String,   // Oficina o Planta [cite: 1]
    val fecha: Long = System.currentTimeMillis(), // Trazabilidad
    val estaSincronizado: Boolean = false // Para control de API REST
)