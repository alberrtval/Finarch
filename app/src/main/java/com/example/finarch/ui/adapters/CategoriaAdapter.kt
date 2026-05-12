package com.example.finarch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finarch.R
import com.example.finarch.model.Categoria

class CategoriaAdapter(
    private val categorias: MutableList<Categoria>,
    private val onEditar: (Categoria) -> Unit,
    private val onEliminar: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCategoria)
        val tvPresupuesto: TextView = itemView.findViewById(R.id.tvPresupuestoCategoria)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarPresupuesto)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]

        holder.tvNombre.text = categoria.nombre
        holder.tvPresupuesto.text = if (categoria.presupuesto > 0)
            "$ ${categoria.presupuesto} / mes"
        else
            "Sin presupuesto"

        holder.btnEditar.setOnClickListener { onEditar(categoria) }
        holder.btnEliminar.setOnClickListener { onEliminar(categoria) }
    }

    override fun getItemCount() = categorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        categorias.clear()
        categorias.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}