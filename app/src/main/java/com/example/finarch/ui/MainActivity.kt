package com.example.finarch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finarch.R
import com.example.finarch.databinding.ActivityMainBinding
import com.example.finarch.model.Categoria
import com.example.finarch.model.EventoCalendario
import com.example.finarch.model.Movimiento
import com.example.finarch.model.TipoMovimiento
import com.example.finarch.network.CalendarioApi
import com.example.finarch.ui.adapters.EventoAdapter
import com.example.finarch.utils.AlertsAdapter
import com.example.finarch.utils.calcularAlertas
import com.example.finarch.utils.calcularPorcentajeGastos
import com.example.finarch.utils.calcularResumenMes
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var isBalanceVisible = true

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarRecyclerView()
        configurarToggleBalance()
        configurarRecyclerViewNoticias()
        cargarEventos()

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, Perfil::class.java))
        }

        binding.btnReportsMain.setOnClickListener {
            verificarCategoriasYNavegar(ReportesResumen::class.java)
        }

        binding.btnIngresoMovimiento.setOnClickListener {
            verificarCategoriasYNavegar(IngresoMovimientos::class.java)
        }
    }

    override fun onStart() {
        super.onStart()
        cargarDashboard()
    }

    private fun configurarRecyclerView() {
        binding.rvAlerts.layoutManager = LinearLayoutManager(this)
        binding.rvAlerts.isNestedScrollingEnabled = false
    }

    private fun configurarToggleBalance() {
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            actualizarVisibilidadBalance()
        }
    }

    private fun actualizarVisibilidadBalance() {
        if (isBalanceVisible) {
            binding.ivToggleBalance.setImageResource(R.drawable.ic_visibility)
        } else {
            binding.ivToggleBalance.setImageResource(R.drawable.ic_visibility_off)
        }
        // Los valores se recargan desde cargarDashboard
        // solo ocultamos visualmente
        val balanceText = binding.tvBalanceValue.tag?.toString() ?: "$ 0.00"
        val incomeText = binding.tvIncomeValue.tag?.toString() ?: "$ 0.00"
        val expenseText = binding.tvExpenseValue.tag?.toString() ?: "$ 0.00"

        binding.tvBalanceValue.text = if (isBalanceVisible) balanceText else "$ ••••••"
        binding.tvIncomeValue.text = if (isBalanceVisible) incomeText else "$ ••••"
        binding.tvExpenseValue.text = if (isBalanceVisible) expenseText else "$ ••••"
    }

    private fun cargarDashboard() {
        val uid = auth.currentUser?.uid ?: return

        // Obtener mes actual
        val calendar = Calendar.getInstance()
        val mes = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val anio = calendar.get(Calendar.YEAR).toString()

        // Cargar categorías y movimientos en paralelo
        val categorias = mutableListOf<Categoria>()
        val movimientos = mutableListOf<Movimiento>()
        var categoriasListas = false
        var movimientosListos = false

        fun procesarSiTodoListo() {
            if (!categoriasListas || !movimientosListos) return

            // Filtrar movimientos del mes actual
            val movimientosMes = movimientos.filter {
                it.fecha.length == 10 &&
                        it.fecha.substring(3, 5) == mes &&
                        it.fecha.substring(6, 10) == anio
            }

            // Calcular resumen
            val (ingresos, gastos, saldo) = calcularResumenMes(movimientosMes)

            // Guardar valores reales en tag para el toggle
            val balanceStr = "$ ${String.format("%.2f", saldo)}"
            val ingresosStr = "$ ${String.format("%.2f", ingresos)}"
            val gastosStr = "$ ${String.format("%.2f", gastos)}"

            binding.tvBalanceValue.tag = balanceStr
            binding.tvIncomeValue.tag = ingresosStr
            binding.tvExpenseValue.tag = gastosStr

            // Actualizar UI con visibilidad actual
            actualizarVisibilidadBalance()
            val cantidadIngresos = movimientosMes.count { it.tipo == TipoMovimiento.INGRESO }
            binding.tvIncomeCount.text = "$cantidadIngresos ingreso${if (cantidadIngresos == 1) "" else "s"} este mes"

            // Barra de progreso gastos
            val porcentaje = calcularPorcentajeGastos(movimientosMes, categorias)
            binding.progressExpense.progress = porcentaje
            binding.tvExpenseHint.text = "$porcentaje% del presupuesto"

            // Alertas
            val alertas = calcularAlertas(movimientosMes, categorias)
            if (alertas.isEmpty()) {
                binding.cardAlerts.visibility = View.GONE
                binding.tvAlertsTitle.visibility = View.GONE
            } else {
                binding.cardAlerts.visibility = View.VISIBLE
                binding.tvAlertsTitle.visibility = View.VISIBLE
                binding.rvAlerts.adapter = AlertsAdapter(alertas)
            }
        }

        // Cargar categorías
        db.collection("users").document(uid)
            .collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->
                categorias.addAll(documentos.mapNotNull {
                    it.toObject(Categoria::class.java)
                })
                categoriasListas = true
                procesarSiTodoListo()
            }
            .addOnFailureListener {
                categoriasListas = true
                procesarSiTodoListo()
            }

        // Cargar movimientos
        db.collection("users").document(uid)
            .collection("movimientos")
            .get()
            .addOnSuccessListener { documentos ->
                movimientos.addAll(documentos.mapNotNull {
                    it.toObject(Movimiento::class.java)
                })
                movimientosListos = true
                procesarSiTodoListo()
            }
            .addOnFailureListener {
                movimientosListos = true
                procesarSiTodoListo()
            }
    }

    private fun verificarCategoriasYNavegar(destino: Class<*>) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("categorias")
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    Toast.makeText(
                        this,
                        "Primero debes crear al menos una categoría",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, ModificarCategoria::class.java))
                } else {
                    startActivity(Intent(this, destino))
                }
            }
    }

    private fun configurarRecyclerViewNoticias() {
        binding.rvNoticias.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
    }

    private fun cargarEventos() {
        CalendarioApi.create().getEventos().enqueue(object : retrofit2.Callback<List<EventoCalendario>> {
            override fun onResponse(
                call: retrofit2.Call<List<EventoCalendario>>,
                response: retrofit2.Response<List<EventoCalendario>>
            ) {
                if (!response.isSuccessful) return

                val hoy = java.util.Calendar.getInstance()
                val formatoApi = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

                val proximosEventos = response.body()
                    ?.sortedByDescending { it.fecha }
                    ?.take(5)
                    ?: emptyList()

                binding.rvNoticias.adapter = EventoAdapter(proximosEventos)
            }

            override fun onFailure(call: retrofit2.Call<List<EventoCalendario>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error al cargar eventos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}