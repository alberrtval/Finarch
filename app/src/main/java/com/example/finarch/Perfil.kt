package com.example.finarch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccion a vista principal
        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        //Redireccion a pantalla de bienvenida
        val button: Button = findViewById(R.id.btnLogout)
        button.setOnClickListener {
            val intent = Intent(this@Perfil, Bienvenida::class.java)
            startActivity(intent)
        }

        //Redireccion a pantalla para cambiar información del perfil
        val ivModificaInfo: ImageView = findViewById(R.id.ivModificarInfo)
        ivModificaInfo.setOnClickListener {
            val intent = Intent(this@Perfil, RegistrarUsuario::class.java)
            startActivity(intent)
        }

        //Redireccion a pantalla para cambiar información del perfil
        val ivcambiacontra: ImageView = findViewById(R.id.ivCambiarcontra)
        ivcambiacontra.setOnClickListener {
            val intent = Intent(this@Perfil, OlvidoContra::class.java)
            startActivity(intent)
        }
    }
}