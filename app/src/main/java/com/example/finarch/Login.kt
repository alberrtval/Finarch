package com.example.finarch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccionamiento a vista de olvide la contraseña
        val tvOlvidocontra: TextView = findViewById(R.id.tvOlvidar)
        tvOlvidocontra.setOnClickListener {
            val intent = Intent(this@Login, OlvidoContra::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a vista principal
        val btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a vista de registro
        val tvRegistrar: TextView = findViewById(R.id.tvRegistrar)
        tvRegistrar.setOnClickListener {
            val intent = Intent(this@Login, RegistrarUsuario::class.java)
            startActivity(intent)
        }
    }
}