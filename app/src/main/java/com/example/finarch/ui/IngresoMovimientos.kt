package com.example.finarch.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finarch.R
import com.example.finarch.databinding.ActivityIngresoMovimientosBinding
import com.example.finarch.model.Movimiento
import com.example.finarch.model.TipoMovimiento
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IngresoMovimientos : AppCompatActivity() {

    private lateinit var binding: ActivityIngresoMovimientosBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val etiquetas = mutableListOf<String>()
    private var movimientoId: String? = null
    private var modoEdicion = false

    companion object {
        private const val TAG = "IngresoMovimientos"
        private const val LIMITE_ETIQUETAS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIngresoMovimientosBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        movimientoId = intent.getStringExtra("movimientoId")
        modoEdicion = movimientoId != null

        binding.tilPayment.visibility = View.GONE
        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            val esIngreso = checkedIds.contains(R.id.chipIncome)
            binding.tilPayment.visibility = if (esIngreso) View.GONE else View.VISIBLE
        }

        cargarFechaHoy()
        cargarCategorias()
        configurarDatePicker()
        configurarEtiquetas()
        cargarMetodosPago()

        if (modoEdicion) {
            binding.btnDelete.visibility = View.VISIBLE
            binding.tvTitle.text = "Editar movimiento"
            cargarMovimiento()
        }

        binding.btnBackIngresoMovimiento.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { guardarMovimiento() }
        binding.btnDelete.setOnClickListener { confirmarEliminar() }
    }

    private fun cargarMovimiento() {
        val uid = auth.currentUser?.uid ?: return
        val id = movimientoId ?: return

        db.collection("users").document(uid)
            .collection("movimientos").document(id)
            .get()
            .addOnSuccessListener { documento ->
                val movimiento = documento.toObject(Movimiento::class.java) ?: return@addOnSuccessListener

                binding.etAmount.setText(movimiento.monto.toString())
                binding.etDate.setText(movimiento.fecha)
                binding.etDescription.setText(movimiento.descripcion)
                binding.acCategory.setText(movimiento.categoria, false)
                binding.acPayment.setText(movimiento.metodoPago, false)

                if (movimiento.tipo == TipoMovimiento.GASTO) {
                    binding.chipExpense.isChecked = true
                    binding.tilPayment.visibility = View.VISIBLE
                } else {
                    binding.chipIncome.isChecked = true
                }

                movimiento.etiquetas.forEach { etiqueta ->
                    etiquetas.add(etiqueta)
                    agregarChip(etiqueta)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar movimiento", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al cargar movimiento", e)
            }
    }

    private fun confirmarEliminar() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Estás seguro que deseas eliminar este movimiento? Esta acción no se puede deshacer.")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Eliminar") { _, _ -> eliminarMovimiento() }
            .show()
    }

    private fun eliminarMovimiento() {
        val uid = auth.currentUser?.uid ?: return
        val id = movimientoId ?: return

        db.collection("users").document(uid)
            .collection("movimientos").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar movimiento", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al eliminar movimiento", e)
            }
    }

    private fun guardarMovimiento() {
        val uid = auth.currentUser?.uid ?: return

        val tipo = if (binding.chipIncome.isChecked) TipoMovimiento.INGRESO else TipoMovimiento.GASTO
        val montoTexto = binding.etAmount.text.toString().trim()
        val categoria = binding.acCategory.text.toString().trim()
        val fecha = binding.etDate.text.toString().trim()
        val metodoPago = binding.acPayment.text.toString().trim()
        val descripcion = binding.etDescription.text.toString().trim()

        if (montoTexto.isEmpty()) {
            binding.tilAmount.error = "El monto es obligatorio"
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            binding.tilAmount.error = "Ingresa un monto válido"
            return
        }

        if (categoria.isEmpty()) {
            binding.tilCategory.error = "La categoría es obligatoria"
            return
        }

        if (fecha.isEmpty()) {
            binding.tilDate.error = "La fecha es obligatoria"
            return
        }

        val ref = if (modoEdicion) {
            db.collection("users").document(uid)
                .collection("movimientos").document(movimientoId!!)
        } else {
            db.collection("users").document(uid)
                .collection("movimientos").document()
        }

        val movimiento = Movimiento(
            id = ref.id,
            uid = uid,
            tipo = tipo,
            monto = monto,
            categoria = categoria,
            fecha = fecha,
            metodoPago = metodoPago,
            descripcion = descripcion,
            etiquetas = etiquetas.toList()
        )

        ref.set(movimiento)
            .addOnSuccessListener {
                val mensaje = if (modoEdicion) "Movimiento actualizado" else "Movimiento guardado"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar movimiento", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al guardar movimiento", e)
            }
    }

    private fun cargarFechaHoy() {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(formato.format(Date()))
    }

    private fun cargarCategorias() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->
                val nombres = documentos.mapNotNull { it.getString("nombre") }
                val adapter = android.widget.ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombres
                )
                binding.acCategory.setAdapter(adapter)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al cargar categorías", e)
            }
    }

    private fun configurarDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona una fecha")
            .build()

        binding.etDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "DatePicker")
        }
        binding.tilDate.setEndIconOnClickListener {
            datePicker.show(supportFragmentManager, "DatePicker")
        }
        datePicker.addOnPositiveButtonClickListener { seleccion ->
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etDate.setText(formato.format(Date(seleccion)))
        }
    }

    private fun configurarEtiquetas() {
        binding.tilTag.setEndIconOnClickListener {
            val texto = binding.etTag.text.toString().trim()
            if (texto.isEmpty()) return@setEndIconOnClickListener
            if (etiquetas.size >= LIMITE_ETIQUETAS) {
                Toast.makeText(this, "Límite de $LIMITE_ETIQUETAS etiquetas alcanzado", Toast.LENGTH_SHORT).show()
                return@setEndIconOnClickListener
            }
            if (etiquetas.contains(texto)) {
                Toast.makeText(this, "Esa etiqueta ya existe", Toast.LENGTH_SHORT).show()
                return@setEndIconOnClickListener
            }
            etiquetas.add(texto)
            agregarChip(texto)
            binding.etTag.text?.clear()
        }
    }

    private fun agregarChip(texto: String) {
        val chip = Chip(this)
        chip.text = texto
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroupTags.removeView(chip)
            etiquetas.remove(texto)
        }
        binding.chipGroupTags.addView(chip)
    }

    private fun cargarMetodosPago() {
        val metodos = resources.getStringArray(R.array.payment_methods_list)
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            metodos
        )
        binding.acPayment.setAdapter(adapter)
    }
}