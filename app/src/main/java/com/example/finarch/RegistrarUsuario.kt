package com.example.finarch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccion a login por medio de boton
        val button: Button = findViewById(R.id.btnRegistrarse)
        button.setOnClickListener {
            val intent = Intent(this@RegistrarUsuario, Login::class.java)
            startActivity(intent)
        }

        //Redireccion a login por medio de textView
        val tvRegistrar: TextView = findViewById(R.id.tvRegistrar)
        tvRegistrar.setOnClickListener {
            val intent = Intent(this@RegistrarUsuario, Login::class.java)
            startActivity(intent)
        }
    }
}