package com.example.finarch.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finarch.databinding.ActivityPerfilBinding
import com.example.finarch.model.Usuario
import com.example.finarch.ui.auth.Login
import com.example.finarch.ui.auth.OlvidoContra
import com.example.finarch.ui.auth.RegistrarUsuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Perfil : AppCompatActivity() {
    //Inicializacion de variable para usar viewbinding
    private lateinit var binding: ActivityPerfilBinding
    //Inicializacion de variable para usar firebase authentification
    private lateinit var auth: FirebaseAuth
    companion object{
        private const val TAG = "PerfilUsuario"
    }

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //usuario y carga de información de este
        val usuarioUID = FirebaseAuth.getInstance().currentUser?.uid

        if(usuarioUID != null) {
            db.collection("users").document(usuarioUID).get()
                .addOnSuccessListener { documento ->
                    val usuario = documento.toObject(Usuario::class.java)

                    binding.tvFullName.text = usuario?.nombre
                    binding.tvEmail.text = usuario?.correo
                    binding.tvCurrencyValue.text = usuario?.moneda
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: Acceso Denegado", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Error: Acceso Denegado")
                    Firebase.auth.signOut()
                }
        }
        else{
            //Cerrrar la sesión y devolver a la página principal y mostrar mensaje de error
            val intent = Intent(this@Perfil, Bienvenida::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Firebase.auth.signOut()
            Toast.makeText(this, "Error: Acceso Denegado", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Error: Acceso Denegado")
        }

        //Redireccion a vista principal
        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

        //Redireccion a pantalla de bienvenida
        val button: Button = binding.btnLogout
        button.setOnClickListener {
            val intent = Intent(this@Perfil, Bienvenida::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Firebase.auth.signOut()
        }

        //Redireccion a pantalla para cambiar información del perfil
        val ivModificaInfo: ImageView = binding.ivModificarInfo
        ivModificaInfo.setOnClickListener {
            val intent = Intent(this@Perfil, RegistrarUsuario::class.java)
            startActivity(intent)
        }

        //Redireccion a pantalla para cambiar contra del perfil
        val ivcambiacontra: ImageView = binding.ivCambiarcontra
        ivcambiacontra.setOnClickListener {
            val intent = Intent(this@Perfil, OlvidoContra::class.java)
            startActivity(intent)
        }
    }
}