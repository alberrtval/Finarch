package com.example.finarch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finarch.R
import com.google.android.material.textfield.TextInputLayout
import com.example.finarch.utils.validacionSoloTexto
import com.example.finarch.utils.validacionCorreo
import com.example.finarch.utils.validacionContraseña

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

        //Validacion de campos antes de registrar al usuario
        val nombre = findViewById<TextInputLayout>(R.id.etNombre).editText?.text.toString().trim()
        val email = findViewById<TextInputLayout>(R.id.etEmail).editText?.text.toString().trim()
        val contra = findViewById<TextInputLayout>(R.id.etPassword).editText?.text.toString().trim()
        val contraConf = findViewById<TextInputLayout>(R.id.etconPassword).editText?.text.toString().trim()
        validacionDeUsusario(nombre, email, contra, contraConf)
    }

    fun validacionDeUsusario(nombre: String, email: String, contra: String, contraConf: String): Boolean
    {
        if(nombre.validacionSoloTexto() && email.validacionCorreo() && contra.validacionContraseña() && contraConf.validacionContraseña() && (contra == contraConf)){
            return true
        }
        else{
            return false
        }
    }
}