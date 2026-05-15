package com.example.finarch.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.finarch.R
import com.example.finarch.databinding.ItemEventoBinding
import com.example.finarch.model.EventoCalendario

class EventoAdapter(
    private val eventos: List<EventoCalendario>
) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    inner class EventoViewHolder(val binding: ItemEventoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = ItemEventoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvNombreEvento.text = evento.titulo
            tvTipoEvento.text = evento.categoria
            tvFechaEvento.text = formatearFecha(evento.fecha)

            val color = when (evento.categoria) {
                "mercados" -> ContextCompat.getColor(context, R.color.cl_ingresos)
                "economía" -> ContextCompat.getColor(context, R.color.cl_divisor)
                "bolsa" -> ContextCompat.getColor(context, R.color.cl_ingresos)
                "cripto" -> ContextCompat.getColor(context, R.color.cl_error)
                "inversión" -> ContextCompat.getColor(context, R.color.cl_gastos)
                "banca" -> ContextCompat.getColor(context, R.color.cl_text_ter)
                else -> ContextCompat.getColor(context, R.color.cl_texto_princ)
            }
            viewTipoIndicador.setBackgroundColor(color)
        }
    }

    override fun getItemCount() = eventos.size

    private fun formatearFecha(fecha: String): String {
        return try {
            val meses = listOf("", "ene", "feb", "mar", "abr", "may",
                "jun", "jul", "ago", "sep", "oct", "nov", "dic")
            val partes = fecha.split("-")
            val dia = partes[2].toInt()
            val mes = meses[partes[1].toInt()]
            val anio = partes[0]
            "$dia $mes $anio"
        } catch (e: Exception) {
            fecha
        }
    }
}