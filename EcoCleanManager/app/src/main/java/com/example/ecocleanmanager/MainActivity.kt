package com.example.ecocleanmanager

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // Instancia de la base de datos local (Punto 7: Arquitectura SQLite) [cite: 7]
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inicializar la Base de Datos Room
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ecolim-db"
        ).build()

        // 2. Referencias a la UI mejorada (Punto 6: Componentes) [cite: 6]
        val spinnerTipo = findViewById<AutoCompleteTextView>(R.id.spinnerTipo)
        val editCantidad = findViewById<TextInputEditText>(R.id.editCantidad)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnVerReporte = findViewById<Button>(R.id.btnVerReporte)

        // 3. Configurar Menú Desplegable (Punto 9: Validación de tipos) [cite: 9]
        val opciones = arrayOf("Plástico", "Papel/Cartón", "Vidrio", "Residuos Peligrosos", "Orgánico")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)
        spinnerTipo.setAdapter(adapter)

        // 4. Lógica para Guardar en Tiempo Real
        btnGuardar.setOnClickListener {
            val tipo = spinnerTipo.text.toString()
            val cantidadStr = editCantidad.text.toString()

            if (tipo.isNotEmpty() && cantidadStr.isNotEmpty()) {
                val registro = ResiduoEntity(
                    tipoResiduo = tipo,
                    cantidad = cantidadStr.toDouble(),
                    ubicacion = "Instalación Industrial Alfa", // Trazabilidad por sede
                    estaSincronizado = false // Para futura sincronización con API REST [cite: 8]
                )
                guardarEnBaseDeDatos(registro, editCantidad)
            } else {
                Toast.makeText(this, "Complete todos los campos por favor", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Lógica de Reportes Automáticos con Filtrado (Punto 9) [cite: 5, 9]
        btnVerReporte.setOnClickListener {
            generarReporteConsolidado()
        }
    }

    private fun guardarEnBaseDeDatos(residuo: ResiduoEntity, campoTexto: TextInputEditText) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.residuoDao().insertarRegistro(residuo) // Registro digital en tiempo real
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "¡Registro guardado exitosamente!", Toast.LENGTH_SHORT).show()
                campoTexto.text?.clear()
            }
        }
    }

    private fun generarReporteConsolidado() {
        lifecycleScope.launch(Dispatchers.IO) {
            val lista = db.residuoDao().obtenerTodosLosRegistros()

            // Cálculo automático de cantidades (Punto 5) [cite: 5]
            val resumen = lista.groupBy { it.tipoResiduo }
                .mapValues { entry -> entry.value.sumOf { it.cantidad } }

            withContext(Dispatchers.Main) {
                if (lista.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No hay datos registrados aún", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val reporteMsg = StringBuilder("Resumen de Recolección Actual:\n\n")
                resumen.forEach { (tipo, total) ->
                    reporteMsg.append("• $tipo: ${String.format("%.2f", total)} kg/m³\n")
                }

                // Interfaz de reporte mediante Diálogo de Alerta [cite: 6]
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Reporte Automático - ECOLIM S.A.C.")
                    .setMessage(reporteMsg.toString())
                    .setPositiveButton("Cerrar", null)
                    .setIcon(android.R.drawable.ic_menu_agenda)
                    .show()
            }
        }
    }
}