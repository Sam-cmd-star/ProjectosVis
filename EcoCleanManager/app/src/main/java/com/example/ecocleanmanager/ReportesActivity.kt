package com.example.ecocleanmanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocleanmanager.data.AppDatabase
import com.example.ecocleanmanager.data.ResiduoAdapter
import com.example.ecocleanmanager.data.ResiduoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReportesActivity : AppCompatActivity() {

    private lateinit var adapter: ResiduoAdapter
    private lateinit var recyclerView: RecyclerView
    private var listaActual: List<ResiduoEntity> = emptyList()
    private var listaFiltrada: List<ResiduoEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        // Configuración de la lista (RecyclerView)
        recyclerView = findViewById(R.id.rvResiduos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ResiduoAdapter(emptyList())
        recyclerView.adapter = adapter

        // Referencias a los botones principales
        val btnFiltrarMaestro = findViewById<Button>(R.id.btnFiltrarMaestro)
        val btnGenerarWeb = findViewById<Button>(R.id.btnGenerarWeb)

        // Acción del Menú Unificado (Estilo Steam/Premium)
        btnFiltrarMaestro.setOnClickListener { mostrarMenuPremium(it as Button) }

        // Generar el reporte web basado en la vista actual
        btnGenerarWeb.setOnClickListener {
            val aExportar = if (listaFiltrada.isNotEmpty()) listaFiltrada else listaActual
            if (aExportar.isNotEmpty()) {
                generarReporteWeb(aExportar)
            } else {
                Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            }
        }

        cargarDatos()
    }

    private fun mostrarMenuPremium(boton: Button) {
        val popup = PopupMenu(this, boton)

        // SECCIÓN 1: VISTA GENERAL
        popup.menu.add(1, 1, 1, "♻️ Ver Todo el Historial")

        // SECCIÓN 2: CRONOLOGÍA (SUBMENÚ)
        val menuFecha = popup.menu.addSubMenu(2, 2, 2, "📅 Filtrar por Fecha >")
        menuFecha.add(2, 21, 1, "Más Recientes")
        menuFecha.add(2, 22, 2, "Más Antiguos")
        menuFecha.add(2, 23, 3, "Registros de Ayer")
        menuFecha.add(2, 24, 4, "Seleccionar en Calendario...")

        // SECCIÓN 3: CATEGORIZACIÓN (SUBMENÚ)
        val menuTipo = popup.menu.addSubMenu(3, 3, 3, "📦 Filtrar por Tipo >")
        val categorias = arrayOf("Plástico", "Papel/Cartón", "Vidrio", "Orgánico", "Peligroso")
        categorias.forEach { menuTipo.add(3, 0, 0, it) }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> { // Resetear filtros
                    listaFiltrada = listaActual
                    adapter.actualizarLista(listaFiltrada)
                    boton.text = "Filtro: Todos"
                }
                21 -> { // Ordenar por más nuevos
                    listaFiltrada = (if(listaFiltrada.isEmpty()) listaActual else listaFiltrada).sortedByDescending { it.fecha }
                    adapter.actualizarLista(listaFiltrada)
                    boton.text = "Orden: Recientes"
                }
                22 -> { // Ordenar por más antiguos
                    listaFiltrada = (if(listaFiltrada.isEmpty()) listaActual else listaFiltrada).sortedBy { it.fecha }
                    adapter.actualizarLista(listaFiltrada)
                    boton.text = "Orden: Antiguos"
                }
                23 -> filtrarAyer(boton)
                24 -> abrirCalendario(boton)
                else -> { // Filtrado por categoría de residuo
                    listaFiltrada = listaActual.filter { it.tipo == item.title }
                    adapter.actualizarLista(listaFiltrada)
                    boton.text = "Tipo: ${item.title}"
                }
            }
            true
        }
        popup.show()
    }

    private fun filtrarAyer(boton: Button) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val ayerStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)

        listaFiltrada = listaActual.filter {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.fecha)) == ayerStr
        }

        adapter.actualizarLista(listaFiltrada)
        boton.text = "Fecha: Ayer ($ayerStr)"
        if(listaFiltrada.isEmpty()) Toast.makeText(this, "Sin datos registrados ayer", Toast.LENGTH_SHORT).show()
    }

    private fun abrirCalendario(boton: Button) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            val fechaSel = String.format("%02d/%02d/%d", d, m + 1, y)
            listaFiltrada = listaActual.filter {
                val fechaReg = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.fecha))
                fechaReg == fechaSel
            }
            adapter.actualizarLista(listaFiltrada)
            boton.text = "Fecha: $fechaSel"
            if(listaFiltrada.isEmpty()) Toast.makeText(this, "No hay registros en esta fecha", Toast.LENGTH_SHORT).show()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun cargarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@ReportesActivity)
            val lista = db.residuoDao().obtenerTodosDirecto()
            listaActual = lista
            withContext(Dispatchers.Main) {
                adapter.actualizarLista(lista)
            }
        }
    }

    private fun generarReporteWeb(lista: List<ResiduoEntity>) {
        val webView = findViewById<WebView>(R.id.webViewReporte)
        webView.visibility = android.view.View.VISIBLE
        recyclerView.visibility = android.view.View.GONE

        val htmlBuilder = StringBuilder()
        htmlBuilder.append("""
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; margin: 20px; color: #333; }
                    .header { text-align: center; border-bottom: 3px solid #2E7D32; padding-bottom: 10px; }
                    h2 { color: #2E7D32; margin-bottom: 5px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th { background-color: #2E7D32; color: white; padding: 12px; text-align: left; }
                    td { border: 1px solid #ddd; padding: 10px; font-size: 14px; }
                    tr:nth-child(even) { background-color: #f2f2f2; }
                    .footer { margin-top: 40px; text-align: center; font-size: 11px; color: #777; border-top: 1px solid #eee; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>ECOLIM S.A.C.</h2>
                    <p>REPORTE OFICIAL DE TRAZABILIDAD AMBIENTAL</p>
                </div>
                <p><strong>Generado el:</strong> ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}</p>
                <table>
                    <tr>
                        <th>Categoría</th>
                        <th>Cantidad</th>
                        <th>Área de Origen</th>
                    </tr>
        """.trimIndent())

        for (res in lista) {
            htmlBuilder.append("""
                <tr>
                    <td>${res.tipo}</td>
                    <td>${res.cantidad} ${res.unidad}</td>
                    <td>${res.area}</td>
                </tr>
            """.trimIndent())
        }

        htmlBuilder.append("""
                </table>
                <div class="footer">
                    © 2026 ECOLIM S.A.C. | Control de Residuos Industriales<br>
                    Este documento tiene validez legal para auditorías ambientales.
                </div>
            </body>
            </html>
        """.trimIndent())

        webView.loadDataWithBaseURL(null, htmlBuilder.toString(), "text/html", "UTF-8", null)
    }
}