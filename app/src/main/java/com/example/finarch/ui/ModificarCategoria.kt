package com.example.finarch.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finarch.databinding.ActivityModificarCategoriaBinding
import com.example.finarch.model.Categoria
import com.example.finarch.ui.adapters.CategoriaAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ModificarCategoria : AppCompatActivity() {

    private lateinit var binding: ActivityModificarCategoriaBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapter: CategoriaAdapter

    companion object {
        private const val TAG = "ModificarCategoria"
        private const val LIMITE = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityModificarCategoriaBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarRecyclerView()
        cargarCategorias()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAgregarCategoria.setOnClickListener {
            agregarCategoria()
        }
    }

    private fun configurarRecyclerView() {
        adapter = CategoriaAdapter(
            categorias = listaCategorias,
            onEditar = { categoria -> mostrarBottomSheet(categoria) },
            onEliminar = { categoria -> eliminarCategoria(categoria) }
        )
        binding.rvCategorias.layoutManager = LinearLayoutManager(this)
        binding.rvCategorias.adapter = adapter
    }

    private fun cargarCategorias() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->
                val lista = documentos.mapNotNull { it.toObject(Categoria::class.java) }
                adapter.actualizarLista(lista)
                actualizarUI()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al cargar categorías", e)
            }
    }

    private fun agregarCategoria() {
        val nombre = binding.etNombreCategoria.text.toString().trim()
        val presupuesto = binding.etPresupuesto.text.toString().toDoubleOrNull() ?: 0.0
        val uid = auth.currentUser?.uid ?: return

        if (nombre.isEmpty()) {
            binding.tilNombreCategoria.error = "El nombre es obligatorio"
            return
        }

        if (listaCategorias.size >= LIMITE) return

        val ref = db.collection("users").document(uid).collection("categorias").document()
        val categoria = Categoria(
            id = ref.id,
            uid = uid,
            nombre = nombre,
            presupuesto = presupuesto
        )

        ref.set(categoria)
            .addOnSuccessListener {
                listaCategorias.add(categoria)
                adapter.notifyItemInserted(listaCategorias.size - 1)
                binding.etNombreCategoria.text?.clear()
                binding.etPresupuesto.text?.clear()
                actualizarUI()
                Toast.makeText(this, "Categoría agregada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al agregar categoría", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al agregar categoría", e)
            }
    }

    private fun eliminarCategoria(categoria: Categoria) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("categorias").document(categoria.id)
            .delete()
            .addOnSuccessListener {
                val index = listaCategorias.indexOf(categoria)
                listaCategorias.remove(categoria)
                adapter.notifyItemRemoved(index)
                actualizarUI()
                Toast.makeText(this, "Categoría eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar categoría", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al eliminar categoría", e)
            }
    }

    private fun mostrarBottomSheet(categoria: Categoria) {
        val bottomSheet = EditarPresupuestoBottomSheet(
            categoriaId = categoria.id,
            presupuestoActual = categoria.presupuesto,
            onGuardar = { nuevoPresupuesto ->
                actualizarPresupuesto(categoria, nuevoPresupuesto)
            }
        )
        bottomSheet.show(supportFragmentManager, "EditarPresupuesto")
    }

    private fun actualizarPresupuesto(categoria: Categoria, nuevoPresupuesto: Double) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("categorias").document(categoria.id)
            .update("presupuesto", nuevoPresupuesto)
            .addOnSuccessListener {
                val index = listaCategorias.indexOf(categoria)
                listaCategorias[index] = categoria.copy(presupuesto = nuevoPresupuesto)
                adapter.notifyItemChanged(index)
                Toast.makeText(this, "Presupuesto actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar presupuesto", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error al actualizar presupuesto", e)
            }
    }

    private fun actualizarUI() {
        val cantidad = listaCategorias.size
        binding.tvContador.text = "$cantidad / $LIMITE categorías"
        binding.tvSinCategorias.visibility = if (cantidad == 0) View.VISIBLE else View.GONE
        binding.btnAgregarCategoria.isEnabled = cantidad < LIMITE
        binding.tvLimiteAlcanzado.visibility = if (cantidad >= LIMITE) View.VISIBLE else View.GONE
    }
}