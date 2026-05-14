package com.example.finarch.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finarch.R
import com.example.finarch.databinding.ActivityReportesResumenBinding
import com.example.finarch.model.Movimiento
import com.example.finarch.model.TipoMovimiento
import com.example.finarch.ui.adapters.MovimientoAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportesResumen : AppCompatActivity() {

    private lateinit var binding: ActivityReportesResumenBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var todosLosMovimientos = listOf<Movimiento>()
    private lateinit var adapter: MovimientoAdapter

    companion object {
        private const val TAG = "ReportesResumen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportesResumenBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarRecyclerView()
        cargarCategorias()
        cargarOrden()
        configurarDatePickers()
        configurarBusqueda()
        cargarMovimientos()

        binding.btnBackreport.setOnClickListener { finish() }

        binding.btnApplyFilters.setOnClickListener { aplicarFiltros() }

        binding.btnResetFilters.setOnClickListener { reiniciarFiltros() }

        binding.tvClearFilters.setOnClickListener { reiniciarFiltros() }
    }

    private fun configurarRecyclerView() {
        adapter = MovimientoAdapter(
            movimientos = emptyList(),
            onClick = { movimiento -> abrirEdicion(movimiento) }
        )
        binding.rvMovements.layoutManager = LinearLayoutManager(this)
        binding.rvMovements.adapter = adapter
    }

    private fun cargarCategorias() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->
                val nombres = mutableListOf("Todas")
                nombres.addAll(documentos.mapNotNull { it.getString("nombre") })
                val adapter = android.widget.ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nombres
                )
                binding.acCategory.setAdapter(adapter)
                binding.acCategory.setText("Todas", false)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al cargar categorías", e)
            }
    }

    private fun cargarOrden() {
        val opciones = resources.getStringArray(R.array.sort_list)
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            opciones
        )
        binding.acSort.setAdapter(adapter)
    }

    private fun configurarDatePickers() {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val datePickerDesde = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha desde")
            .build()

        val datePickerHasta = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha hasta")
            .build()

        binding.etDateFrom.setOnClickListener {
            datePickerDesde.show(supportFragmentManager, "DatePickerDesde")
        }
        binding.tilDateFrom.setEndIconOnClickListener {
            datePickerDesde.show(supportFragmentManager, "DatePickerDesde")
        }

        binding.etDateTo.setOnClickListener {
            datePickerHasta.show(supportFragmentManager, "DatePickerHasta")
        }
        binding.tilDateTo.setEndIconOnClickListener {
            datePickerHasta.show(supportFragmentManager, "DatePickerHasta")
        }

        datePickerDesde.addOnPositiveButtonClickListener { seleccion ->
            binding.etDateFrom.setText(formato.format(Date(seleccion)))
        }

        datePickerHasta.addOnPositiveButtonClickListener { seleccion ->
            binding.etDateTo.setText(formato.format(Date(seleccion)))
        }
    }

    private fun configurarBusqueda() {
        binding.etSearch.addTextChangedListener {
            aplicarFiltros()
        }
    }

    private fun cargarMovimientos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("movimientos")
            .get()
            .addOnSuccessListener { documentos ->
                todosLosMovimientos = documentos.mapNotNull {
                    it.toObject(Movimiento::class.java)
                }
                aplicarFiltros()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar movimientos", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al cargar movimientos", e)
            }
    }

    private fun aplicarFiltros() {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val busqueda = binding.etSearch.text.toString().trim().lowercase()
        val categoriaFiltro = binding.acCategory.text.toString().trim()
        val fechaDesde = binding.etDateFrom.text.toString().trim()
        val fechaHasta = binding.etDateTo.text.toString().trim()
        val montoMin = binding.etMinAmount.text.toString().toDoubleOrNull()
        val montoMax = binding.etMaxAmount.text.toString().toDoubleOrNull()
        val tipoSeleccionado = binding.chipGroupType.checkedChipId

        var resultado = todosLosMovimientos

        if (busqueda.isNotEmpty()) {
            resultado = resultado.filter {
                it.descripcion.lowercase().contains(busqueda) ||
                        it.categoria.lowercase().contains(busqueda) ||
                        it.etiquetas.any { etiqueta -> etiqueta.lowercase().contains(busqueda) }
            }
        }

        if (categoriaFiltro.isNotEmpty() && categoriaFiltro != "Todas") {
            resultado = resultado.filter { it.categoria == categoriaFiltro }
        }

        if (fechaDesde.isNotEmpty()) {
            val desde = formato.parse(fechaDesde)
            resultado = resultado.filter {
                val fechaMov = formato.parse(it.fecha)
                fechaMov != null && desde != null && !fechaMov.before(desde)
            }
        }

        if (fechaHasta.isNotEmpty()) {
            val hasta = formato.parse(fechaHasta)
            resultado = resultado.filter {
                val fechaMov = formato.parse(it.fecha)
                fechaMov != null && hasta != null && !fechaMov.after(hasta)
            }
        }

        if (montoMin != null) {
            resultado = resultado.filter { it.monto >= montoMin }
        }

        if (montoMax != null) {
            resultado = resultado.filter { it.monto <= montoMax }
        }

        when (tipoSeleccionado) {
            R.id.chipIncome -> resultado = resultado.filter {
                it.tipo == TipoMovimiento.INGRESO
            }
            R.id.chipExpense -> resultado = resultado.filter {
                it.tipo == TipoMovimiento.GASTO
            }
        }

        val ordenSeleccionado = binding.acSort.text.toString()
        resultado = when (ordenSeleccionado) {
            "Más reciente" -> resultado.sortedByDescending { formato.parse(it.fecha) }
            "Más antiguo" -> resultado.sortedBy { formato.parse(it.fecha) }
            "Mayor monto" -> resultado.sortedByDescending { it.monto }
            "Menor monto" -> resultado.sortedBy { it.monto }
            else -> resultado.sortedByDescending { formato.parse(it.fecha) }
        }

        actualizarResumen(resultado)
        adapter.actualizarLista(resultado)
    }

    private fun actualizarResumen(lista: List<Movimiento>) {
        val total = lista.sumOf {
            if (it.tipo == TipoMovimiento.INGRESO) it.monto else -it.monto
        }
        binding.tvResultsCount.text = "Mostrando ${lista.size} movimientos"
        binding.tvResultsTotal.text = "Total: $${String.format("%.2f", total)}"
    }

    private fun reiniciarFiltros() {
        binding.etSearch.text?.clear()
        binding.etDateFrom.text?.clear()
        binding.etDateTo.text?.clear()
        binding.etMinAmount.text?.clear()
        binding.etMaxAmount.text?.clear()
        binding.acCategory.setText("Todas", false)
        binding.acSort.text?.clear()
        binding.chipAll.isChecked = true
        aplicarFiltros()
    }

    private fun abrirEdicion(movimiento: Movimiento) {
        val intent = Intent(this, IngresoMovimientos::class.java)
        intent.putExtra("movimientoId", movimiento.id)
        startActivity(intent)
    }
}