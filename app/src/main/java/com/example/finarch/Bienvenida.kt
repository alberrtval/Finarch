package com.example.finarch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Bienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccion al login
        val button: Button = findViewById(R.id.btnInicioSesion)
        button.setOnClickListener {
            val intent = Intent(this@Bienvenida, Login::class.java)
            startActivity(intent)
        }

        //redireccion a pantalla de registro
        val textview: TextView = findViewById(R.id.tvRegistrar)
        textview.setOnClickListener {
            val intent = Intent(this@Bienvenida, RegistrarUsuario::class.java)
            startActivity(intent)
        }
    }
}