package com.example.eva_02_ignacioquiero

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eva_02_ignacioquiero.adapters.NoticiasAdapter
import com.example.eva_02_ignacioquiero.firebase.FirebaseHelper
import com.example.eva_02_ignacioquiero.models.Noticia
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var userIconImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: NoticiasAdapter

    private val firebaseHelper = FirebaseHelper()
    private var noticiasList = mutableListOf<Noticia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        initializeViews()
        setupRecyclerView()
        setupListeners()

        // Cargar noticias desde Firebase
        loadNoticiasFromFirebase()
    }

    override fun onResume() {
        super.onResume()
        // Recargar noticias cada vez que volvemos a esta pantalla
        loadNoticiasFromFirebase()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.noticiasRecyclerView)
        addButton = findViewById(R.id.addButton)
        userIconImageView = findViewById(R.id.userIconImageView)
        // progressBar = findViewById(R.id.progressBar) // Lo agregaremos al layout
        // emptyTextView = findViewById(R.id.emptyTextView) // Lo agregaremos al layout
    }

    private fun setupRecyclerView() {
        adapter = NoticiasAdapter(noticiasList) { noticia ->
            val intent = Intent(this, DetalleNoticiaActivity::class.java).apply {
                putExtra("TITULO", noticia.titulo)
                putExtra("BAJADA", noticia.bajada)
                putExtra("CUERPO", noticia.cuerpo)
                putExtra("FECHA", noticia.fecha)
                putExtra("IMAGEN_URL", noticia.imagenUrl)
                putExtra("ID", noticia.id)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            val intent = Intent(this, AgregarNoticiaActivity::class.java)
            startActivity(intent)
        }

        userIconImageView.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadNoticiasFromFirebase() {
        // Mostrar loading
        setLoading(true)

        firebaseHelper.getNoticias(
            onSuccess = { noticias ->
                setLoading(false)

                // Actualizar la lista
                noticiasList.clear()
                noticiasList.addAll(noticias)
                adapter.notifyDataSetChanged()

                // Mostrar mensaje si no hay noticias
                if (noticias.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }
            },
            onFailure = { errorMessage ->
                setLoading(false)
                Toast.makeText(
                    this,
                    "Error al cargar noticias: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
                showEmptyState()
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            // progressBar?.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // progressBar?.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showEmptyState() {
        // emptyTextView?.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        // emptyTextView?.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
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
        // Cerrar sesión en Firebase
        firebaseHelper.logout()

        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Deseas salir de la aplicación?")
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}