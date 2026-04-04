package com.example.finarch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finarch.model.AlertItem
import com.example.finarch.AlertsAdapter
import com.example.finarch.R
import com.example.finarch.model.TipoAlerta
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccionamiento a la vista del perfil
        val imProfile: ImageView = findViewById(R.id.imgProfile)
        imProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, Perfil::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a la vista de reportes
        val btnReports = findViewById<MaterialButton>(R.id.btnReportsMain)
        btnReports.setOnClickListener {
            val intent = Intent(this@MainActivity, ReportesResumen::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a la vista de ingresos
        val btnIngresos = findViewById<MaterialButton>(R.id.btnIngresoMovimiento)
        btnIngresos.setOnClickListener {
            val intent = Intent(this@MainActivity, IngresoMovimientos::class.java)
            startActivity(intent)
        }

        //Oculta la cantidad de dinero a preferencia del usuario
        val tvBalanceValue = findViewById<TextView>(R.id.tvBalanceValue)
        val tvIncomeValue = findViewById<TextView>(R.id.tvIncomeValue)
        val tvExpenseValue = findViewById<TextView>(R.id.tvExpenseValue)
        val ivToggleBalance = findViewById<ImageView>(R.id.ivToggleBalance)

        var isBalanceVisible = true
        val realBalance = "$ 1,250.00"
        val realIncome = "$ 2,000.00"
        val realExpense = "$ 750.00"

        ivToggleBalance.setOnClickListener {
            if (isBalanceVisible) {
                // Ocultar todo
                tvBalanceValue.text = "$ ••••••"
                tvIncomeValue.text = "$ ••••"
                tvExpenseValue.text = "$ ••••"
                ivToggleBalance.setImageResource(R.drawable.ic_visibility_off)
            } else {
                // Mostrar todo
                tvBalanceValue.text = realBalance
                tvIncomeValue.text = realIncome
                tvExpenseValue.text = realExpense
                ivToggleBalance.setImageResource(R.drawable.ic_visibility)
            }
            isBalanceVisible = !isBalanceVisible
        }

        //recycle view
        val rvAlerts = findViewById<RecyclerView>(R.id.rvAlerts)

        val alertas = listOf(
            AlertItem("Presupuesto de comida excedido (10%)", TipoAlerta.ADVERTENCIA),
            AlertItem("Pago próximo: Internet en 3 días", TipoAlerta.INFO)
        )

        rvAlerts.layoutManager = LinearLayoutManager(this)
        rvAlerts.adapter = AlertsAdapter(alertas)
    }
}