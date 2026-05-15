package com.example.finarch.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finarch.R
import com.example.finarch.databinding.ActivityModificarInformacionBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ModificarInformacion : AppCompatActivity() {

    private lateinit var binding: ActivityModificarInformacionBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "ModificarInformacion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityModificarInformacionBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarMonedas()
        cargarDatosUsuario()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnGuardar.setOnClickListener { guardarCambios() }
    }

    private fun cargarMonedas() {
        val monedas = resources.getStringArray(R.array.currency_list)
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            monedas
        )
        binding.acMoneda.setAdapter(adapter)
    }

    private fun cargarDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return

        binding.etCorreo.setText(auth.currentUser?.email ?: "")

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { documento ->
                binding.etNombre.setText(documento.getString("nombre") ?: "")
                binding.acMoneda.setText(
                    documento.getString("moneda") ?: "Dolar Estadounidense", false
                )
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al cargar datos", e)
            }
    }

    private fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val nombre = binding.etNombre.text.toString().trim()
        val moneda = binding.acMoneda.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.tilNombre.error = "El nombre es obligatorio"
            return
        }

        if (moneda.isEmpty()) {
            binding.tilMoneda.error = "La moneda es obligatoria"
            return
        }

        val cambios = mapOf(
            "nombre" to nombre,
            "moneda" to moneda
        )

        db.collection("users").document(uid)
            .update(cambios)
            .addOnSuccessListener {
                Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al guardar cambios", e)
            }
    }
}