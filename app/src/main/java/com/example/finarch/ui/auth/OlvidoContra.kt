package com.example.finarch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import com.example.finarch.databinding.ActivityOlvidoContraBinding
import com.google.firebase.auth.FirebaseAuth

class OlvidoContra : AppCompatActivity() {

    private lateinit var binding: ActivityOlvidoContraBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOlvidoContraBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuarioActual = auth.currentUser

        if (usuarioActual != null) {
            // Viene desde el perfil, ocultar campos innecesarios
            binding.tilEmail.visibility = View.GONE
            binding.tvLoginVol.visibility = View.GONE
        } else {
            // Viene desde login, mostrar todo
            binding.tilEmail.visibility = View.VISIBLE
            binding.tvLoginVol.visibility = View.VISIBLE
        }

        binding.tvLoginVol.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnRecuperaContra.setOnClickListener {
            val correo = if (usuarioActual != null) {
                usuarioActual.email ?: ""
            } else {
                binding.etEmail.text.toString().trim()
            }

            if (correo.isEmpty()) {
                binding.tilEmail.error = "El correo es obligatorio"
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(correo)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Correo de recuperación enviado a $correo",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}