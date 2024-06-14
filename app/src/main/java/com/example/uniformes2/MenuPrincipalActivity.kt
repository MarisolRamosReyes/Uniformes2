package com.example.uniformes2

import com.example.uniformes2.ApiService
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        val btn: Button = findViewById(R.id.EscuelasBoton)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, MostrarEscuela::class.java)
            startActivity(intent)
        }

        // Botón Tallas
        val btnSize: Button = findViewById(R.id.tallasBoton)
        btnSize.setOnClickListener {
            val intent = Intent(this, MostrarTallas::class.java)
            startActivity(intent)
        }

        // Botón Prendas
        val btnGarment: Button = findViewById(R.id.PrendasBoton)
        btnGarment.setOnClickListener {
            val intent = Intent(this, MostrarPrendas::class.java)
            startActivity(intent)
        }





    }


}