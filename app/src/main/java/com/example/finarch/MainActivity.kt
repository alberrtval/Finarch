package com.example.finarch

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Redireccionamiento a la vista del perfil
        val imProfile: ImageView = findViewById(R.id.imgProfile)
        imProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, Perfil::class.java)
            startActivity(intent)
        }

        //Redireccionamiento a la vista de reportes
        val btnReports = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReportsMain)
        btnReports.setOnClickListener {
            val intent = Intent(this@MainActivity, ReportesResumen::class.java)
            startActivity(intent)
        }
    }
}