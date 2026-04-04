package com.example.finarch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.finarch.model.AlertItem
import com.example.finarch.model.TipoAlerta

class AlertsAdapter(private val alertas: List<AlertItem>) :
    RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    inner class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivAlertIcon)
        val texto: TextView = view.findViewById(R.id.tvAlertText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alerta = alertas[position]
        holder.texto.text = alerta.mensaje

        when (alerta.tipo) {
            TipoAlerta.ADVERTENCIA -> {
                holder.icon.setImageResource(R.drawable.ic_warning)
                holder.icon.imageTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.cl_error)
            }
            TipoAlerta.INFO -> {
                holder.icon.setImageResource(R.drawable.ic_info)
                holder.icon.imageTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, android.R.color.holo_blue_light)
            }
        }
    }

    override fun getItemCount() = alertas.size
}