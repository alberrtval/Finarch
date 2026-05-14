package com.example.finarch.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.finarch.R
import com.example.finarch.databinding.ItemMovementBinding
import com.example.finarch.model.Movimiento
import com.example.finarch.model.TipoMovimiento

class MovimientoAdapter(
    private var movimientos: List<Movimiento>,
    private val onClick: (Movimiento) -> Unit
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    inner class MovimientoViewHolder(val binding: ItemMovementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val binding = ItemMovementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovimientoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        val movimiento = movimientos[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvTitle.text = if (movimiento.descripcion.isNotEmpty())
                movimiento.descripcion else movimiento.categoria

            tvSubtitle.text = "${movimiento.categoria} • ${movimiento.fecha}"

            tvNote.text = if (movimiento.etiquetas.isNotEmpty())
                movimiento.etiquetas.joinToString(", ") else ""

            val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
            val color = if (esIngreso)
                ContextCompat.getColor(context, R.color.cl_ingresos)
            else
                ContextCompat.getColor(context, R.color.cl_gastos)

            viewIndicator.setBackgroundColor(color)

            tvAmount.text = if (esIngreso)
                "+$ ${movimiento.monto}" else "-$ ${movimiento.monto}"
            tvAmount.setTextColor(color)

            tvType.text = if (esIngreso) "Ingreso" else "Gasto"

            root.setOnClickListener { onClick(movimiento) }
        }
    }

    override fun getItemCount() = movimientos.size

    fun actualizarLista(nuevaLista: List<Movimiento>) {
        movimientos = nuevaLista
        notifyDataSetChanged()
    }
}