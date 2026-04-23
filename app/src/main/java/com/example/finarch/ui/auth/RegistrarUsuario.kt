package com.example.finarch.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.finarch.databinding.ActivityRegistrarUsuarioBinding
import com.example.finarch.utils.CampoError
import com.example.finarch.utils.validacionDeUsusario

class RegistrarUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccion a login por medio de textView
        val tvRegistrar = binding.tvRegistrar
        tvRegistrar.setOnClickListener {
            val intent = Intent(this@RegistrarUsuario, Login::class.java)
            startActivity(intent)
        }

        //Variables a utilizar
        val nombre = binding.tilNombre
        val email = binding.tilEmail
        val contra = binding.tilPassword
        val contraConf = binding.tilConPassword
        val button = binding.btnRegistrarse
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
        button.setOnClickListener {
            //Validacion de campos antes de registrar al usuario
            val resultadoValidacion = validacionDeUsusario(nombre.editText?.text.toString().trim(), email.editText?.text.toString().trim(), contra.editText?.text.toString().trim(), contraConf.editText?.text.toString().trim())

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