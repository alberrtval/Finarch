package com.example.finarch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finarch.databinding.ActivityMainBinding
import com.example.finarch.model.AlertItem
import com.example.finarch.model.TipoAlerta
import com.example.finarch.utils.AlertsAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.example.finarch.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

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

        // Redireccionamiento a la vista del perfil
        binding.imgProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, Perfil::class.java)
            startActivity(intent)
        }

        // Redireccionamiento a la vista de reportes
        binding.btnReportsMain.setOnClickListener {
            verificarCategoriasYNavegar(ReportesResumen::class.java)
        }

        // Redireccionamiento a la vista de ingresos
        binding.btnIngresoMovimiento.setOnClickListener {
            verificarCategoriasYNavegar(IngresoMovimientos::class.java)
        }

        // Oculta la cantidad de dinero a preferencia del usuario
        var isBalanceVisible = true
        val realBalance = "$ 1,250.00"
        val realIncome = "$ 2,000.00"
        val realExpense = "$ 750.00"

        binding.ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                binding.tvBalanceValue.text = "$ ••••••"
                binding.tvIncomeValue.text = "$ ••••"
                binding.tvExpenseValue.text = "$ ••••"
                binding.ivToggleBalance.setImageResource(R.drawable.ic_visibility_off)
            } else {
                binding.tvBalanceValue.text = realBalance
                binding.tvIncomeValue.text = realIncome
                binding.tvExpenseValue.text = realExpense
                binding.ivToggleBalance.setImageResource(R.drawable.ic_visibility)
            }
            isBalanceVisible = !isBalanceVisible
        }

        // RecyclerView de alertas
        val alertas = listOf(
            AlertItem("Presupuesto de comida excedido (10%)", TipoAlerta.ADVERTENCIA),
            AlertItem("Pago próximo: Internet en 3 días", TipoAlerta.INFO)
        )
        binding.rvAlerts.layoutManager = LinearLayoutManager(this)
        binding.rvAlerts.adapter = AlertsAdapter(alertas)
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
}