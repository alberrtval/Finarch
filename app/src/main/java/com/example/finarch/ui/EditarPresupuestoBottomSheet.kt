package com.example.finarch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finarch.databinding.BottomSheetEditarPresupuestoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditarPresupuestoBottomSheet(
    private val categoriaId: String,
    private val presupuestoActual: Double,
    private val onGuardar: (Double) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditarPresupuestoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetEditarPresupuestoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (presupuestoActual > 0) {
            binding.etNuevoPresupuesto.setText(presupuestoActual.toString())
        }

        binding.btnGuardarPresupuesto.setOnClickListener {
            val nuevo = binding.etNuevoPresupuesto.text.toString().toDoubleOrNull() ?: 0.0
            onGuardar(nuevo)
            dismiss()
        }

        binding.btnCancelarPresupuesto.setOnClickListener { dismiss() }
    }
}