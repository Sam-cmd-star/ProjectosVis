package com.example.ecocleanmanager

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
// IMPORTANTE: Verifica que estas rutas coincidan con tus carpetas

import com.example.ecocleanmanager.data.AppDatabase
import com.example.ecocleanmanager.data.ResiduoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<Spinner>(R.id.spinnerCategorias)
        val etCantidad = findViewById<EditText>(R.id.etCantidad)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnVerReportes = findViewById<Button>(R.id.btnVerReportes)

        // Validación por menús desplegables para evitar errores de clasificación [cite: 2, 9]
        val opciones = arrayOf("Plástico", "Papel/Cartón", "Vidrio", "Orgánico", "Peligroso")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinner.adapter = adapter

        // Registro digital en tiempo real [cite: 4]
        btnRegistrar.setOnClickListener {
            val tipo = spinner.selectedItem.toString()
            val cantidadStr = etCantidad.text.toString()

            if (cantidadStr.isNotEmpty()) {
                val cantidad = cantidadStr.toDouble()
                guardarEnBaseDeDatos(tipo, cantidad)
            } else {
                Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            }
        }

        // Comunicación entre pantallas para consolidación de información [cite: 3, 7]
        btnVerReportes.setOnClickListener {
            val intent = Intent(this, ReportesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun guardarEnBaseDeDatos(tipo: String, cant: Double) {
        // Objeto con trazabilidad (fecha y área) [cite: 4, 7]
        val nuevoRegistro = ResiduoEntity(
            tipo = tipo,
            cantidad = cant,
            unidad = "Kg",
            fecha = System.currentTimeMillis(),
            area = "Planta Industrial",
            sincronizado = false
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(this@MainActivity)
                db.residuoDao().insertarRegistro(nuevoRegistro)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "¡Registrado con éxito!", Toast.LENGTH_SHORT).show()
                    findViewById<EditText>(R.id.etCantidad).text.clear()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Muestra el error real en el logcat para debuguear
                    Toast.makeText(this@MainActivity, "Error en SQLite: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}