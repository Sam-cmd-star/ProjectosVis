package com.example.ecocleanmanager.data // Asegúrate que coincida con tu paquete

import com.example.ecocleanmanager.data.ResiduoEntity
import android.annotation.SuppressLint


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocleanmanager.R
import java.text.SimpleDateFormat
import java.util.*

class ResiduoAdapter(private var lista: List<ResiduoEntity>) :
    RecyclerView.Adapter<ResiduoAdapter.ResiduoViewHolder>() {

    class ResiduoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.tvTipoItem)
        val cantidad: TextView = view.findViewById(R.id.tvCantidadItem)
        val fecha: TextView = view.findViewById(R.id.tvFechaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResiduoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_residuo, parent, false)
        return ResiduoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ResiduoViewHolder, position: Int) {
        val residuo = lista[position]
        holder.tipo.text = residuo.tipo
        holder.cantidad.text = "${residuo.cantidad} ${residuo.unidad}"

        // Convertir milisegundos a fecha legible para el reporte
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.fecha.text = "Fecha: ${sdf.format(Date(residuo.fecha))}"
    }

    override fun getItemCount(): Int = lista.size

    // Función para actualizar la lista cuando usemos filtros (por tipo o fecha)
    fun actualizarLista(nuevaLista: List<ResiduoEntity>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}