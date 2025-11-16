package com.example.eva_02_ignacioquiero

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalleNoticiaActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var userButton: ImageView
    private lateinit var imagenNoticia: ImageView
    private lateinit var fechaTextView: TextView
    private lateinit var tituloTextView: TextView
    private lateinit var bajadaTextView: TextView
    private lateinit var cuerpoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_noticia)

        supportActionBar?.hide()

        initializeViews()
        setupListeners()
        loadNoticiaData()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        userButton = findViewById(R.id.userButton)
        imagenNoticia = findViewById(R.id.imagenNoticia)
        fechaTextView = findViewById(R.id.fechaTextView)
        tituloTextView = findViewById(R.id.tituloTextView)
        bajadaTextView = findViewById(R.id.bajadaTextView)
        cuerpoTextView = findViewById(R.id.cuerpoTextView)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish() // Volver a MainActivity
        }

        // Botón de usuario - Cerrar sesión
        userButton.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadNoticiaData() {
        // Obtener datos pasados desde MainActivity
        val titulo = intent.getStringExtra("TITULO") ?: ""
        val bajada = intent.getStringExtra("BAJADA") ?: ""
        val cuerpo = intent.getStringExtra("CUERPO") ?: ""
        val fecha = intent.getStringExtra("FECHA") ?: ""
        val imagenUrl = intent.getStringExtra("IMAGEN_URL") ?: ""

        // Validar que se recibieron datos
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Error: No se recibieron datos de la noticia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Asignar datos a las vistas
        tituloTextView.text = titulo
        bajadaTextView.text = bajada
        cuerpoTextView.text = cuerpo
        fechaTextView.text = fecha

        // Por ahora dejamos la imagen por defecto
        // Cuando conectemos Firebase, cargaremos la imagen real desde imagenUrl
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí, cerrar sesión") { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // TODO: Aquí limpiaremos la sesión de Firebase cuando lo conectemos

        // Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        // Navegar al Login y limpiar el stack de Activities
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Método para manejar el botón "atrás" del sistema
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}