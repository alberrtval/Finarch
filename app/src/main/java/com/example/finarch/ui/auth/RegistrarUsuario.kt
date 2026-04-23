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
import com.example.finarch.utils.*
import androidx.core.widget.addTextChangedListener

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

        //Redireccion a login por medio de textView
        val tvRegistrar: TextView = findViewById(R.id.tvRegistrar)
        tvRegistrar.setOnClickListener {
            val intent = Intent(this@RegistrarUsuario, Login::class.java)
            startActivity(intent)
        }


        val button: Button = findViewById(R.id.btnRegistrarse)
        button.setOnClickListener {
            //Validacion de campos antes de registrar al usuario
            val nombre = findViewById<TextInputLayout>(R.id.tilNombre)
            val email = findViewById<TextInputLayout>(R.id.tilEmail)
            val contra = findViewById<TextInputLayout>(R.id.tilPassword)
            val contraConf = findViewById<TextInputLayout>(R.id.tilConPassword)
            val resultadoValidacion = validacionDeUsusario(nombre.editText?.text.toString().trim(), email.editText?.text.toString().trim(), contra.editText?.text.toString().trim(), contraConf.editText?.text.toString().trim())

            //Limpieza de mensajes de error para evitar confusion del usuario
            nombre.editText?.addTextChangedListener {
                nombre.error = null
                nombre.isErrorEnabled = false
            }
            email.editText?.addTextChangedListener {
                email.error = null
                email.isErrorEnabled = false
            }
            contra.editText?.addTextChangedListener {
                contra.error = null
                contra.isErrorEnabled = false
            }
            contraConf.editText?.addTextChangedListener {
                contraConf.error = null
                contraConf.isErrorEnabled = false
            }

            if(resultadoValidacion == null){
                //Redireccion a login por medio de boton
                val intent = Intent(this@RegistrarUsuario, Login::class.java)
                startActivity(intent)
            }
            else{
                var campo = resultadoValidacion.campo
                var mensaje = resultadoValidacion.mensaje

                when(campo){
                    CampoError.NOMBRE -> nombre.error = mensaje
                    CampoError.EMAIL -> email.error = mensaje
                    CampoError.CONTRASENA -> contra.error = mensaje
                    CampoError.CONFIRMAR -> contraConf.error = mensaje
                    CampoError.CONTRASENAYCONF -> {
                        contra.error = "Las contraseñas no coinciden"
                        contraConf.error = "Las contraseñas no coinciden"
                    }
                }

            }
        }
    }
}