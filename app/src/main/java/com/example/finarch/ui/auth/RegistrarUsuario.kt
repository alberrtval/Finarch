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
import com.example.finarch.databinding.ActivityRegistrarUsuarioBinding
import com.example.finarch.model.Usuario
import com.example.finarch.utils.CampoError
import com.example.finarch.utils.validacionDeUsusario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class RegistrarUsuario : AppCompatActivity() {

    //Inicializacion de variable para usar viewbinding
    private lateinit var binding: ActivityRegistrarUsuarioBinding
    //Inicializacion de variable para usar firebase authentification
    private lateinit var auth: FirebaseAuth
    companion object{
        private const val TAG = "ResigtrarUSuario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
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
                //Inicializacion de variable para acceder a firestore
                val db = Firebase.firestore

                //Creacion de credenciales de usuario para ralizar login
                auth.createUserWithEmailAndPassword(email.editText?.text.toString(), contra.editText?.text.toString())
                    .addOnSuccessListener { resultado ->
                        //Agregando data del usuario a Firestore
                        val usuario = Usuario(
                            nombre = nombre.editText?.text.toString(),
                            correo =email.editText?.text.toString(),
                            uid = resultado.user?.uid ?: ""
                        )

                        db.collection("users")
                            .document(resultado.user?.uid!!).set(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario Creado correctamente", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "Usuario agregado correctamente")

                                //Redireccion a login por medio de boton
                                val intent = Intent(this@RegistrarUsuario, Login::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show()
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
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