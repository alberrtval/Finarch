package com.example.finarch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.finarch.databinding.ActivityLoginBinding
import com.example.finarch.ui.MainActivity
import com.example.finarch.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class Login : AppCompatActivity() {
    //Inicializacion de variable para usar viewbinding
    private lateinit var binding: ActivityLoginBinding
    //Inicializacion de variable para usar firebase authentification
    private lateinit var auth: FirebaseAuth
    companion object{
        private const val TAG = "LoginUsuario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //Redireccionamiento a vista de olvide la contraseña
        val tvOlvidocontra = binding.tvOlvidar
        tvOlvidocontra.setOnClickListener {
            val intent = Intent(this@Login, OlvidoContra::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a vista de registro
        val tvRegistrar = binding.tvRegistrar
        tvRegistrar.setOnClickListener {
            val intent = Intent(this@Login, RegistrarUsuario::class.java)
            startActivity(intent)
        }

        //Variables a utilizar
        val email = binding.tilEmail
        val contra = binding.tilPassword
        val btnLogin = binding.btnLogin


        //Limpieza de mensajes de error para evitar confusión del usuario
        email.editText?.addTextChangedListener {
            email.error = null
            email.isErrorEnabled = false
        }

        contra.editText?.addTextChangedListener {
            contra.error = null
            contra.isErrorEnabled = false
        }

        //Redireccionamiento a vista principal
        btnLogin.setOnClickListener {

            val resultadoValidacion = validacionDeUsusarioLogin(
                email.editText?.text.toString(),
                contra.editText?.text.toString()
            )

            if (resultadoValidacion == null) {

                auth.signInWithEmailAndPassword(
                    email.editText?.text.toString(),
                    contra.editText?.text.toString()
                )
                    .addOnSuccessListener { resultado ->
                        Toast.makeText(this, "!Gracias por usar Finarch¡", Toast.LENGTH_SHORT)
                            .show()
                        Log.w(TAG, "Inicio de sesion correctamente")
                        //Redireccionamiento a ventana Main
                        val intent = Intent(this@Login, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        val mensaje = if (e is FirebaseAuthException) {
                            when (e.errorCode) {
                                "ERROR_INVALID_CREDENTIAL" -> "Usuario o contraseña incorrectos"
                                "ERROR_USER_DISABLED" -> "Error usuario desactivado"
                                "ERROR_TOO_MANY_REQUESTS" -> "Demasiadas peticiones"
                                else -> e.message.toString()
                            }
                        } else {
                            "Error!"
                        }
                        Toast.makeText(this, "Error: ${mensaje}", Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Error: ${e.message}")
                    }

            } else {
                val mensaje = resultadoValidacion.mensaje
                when (resultadoValidacion.campo) {
                    CampoError.EMAIL -> email.error = mensaje
                    CampoError.CONTRASENA -> contra.error = mensaje
                    else -> email.error = "ERROR"
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser == null) {

        }
        else{
            val intent = Intent(this@Login, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}