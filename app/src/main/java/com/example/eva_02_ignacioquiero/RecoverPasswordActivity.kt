package com.example.eva_02_ignacioquiero

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.eva_02_ignacioquiero.firebase.FirebaseHelper
import com.google.android.material.textfield.TextInputEditText

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var backToLoginTextView: TextView

    private val firebaseHelper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        supportActionBar?.hide()

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        sendButton = findViewById(R.id.sendButton)
        backToLoginTextView = findViewById(R.id.backToLoginTextView)
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            handleRecoverPassword()
        }

        backToLoginTextView.setOnClickListener {
            finish()
        }
    }

    private fun handleRecoverPassword() {
        val email = emailEditText.text.toString().trim()

        when {
            email.isEmpty() -> {
                showAlert("Error", "Por favor ingresa tu correo electrónico")
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showAlert("Error", "Por favor ingresa un correo válido")
            }
            else -> {
                // Enviar correo de recuperación con Firebase
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        // Mostrar loading
        setLoading(true)

        firebaseHelper.resetPassword(
            email = email,
            onSuccess = {
                setLoading(false)

                showSuccessDialog(
                    "Correo Enviado",
                    "Se ha enviado un enlace de recuperación a:\n\n$email\n\n" +
                            "Por favor revisa tu bandeja de entrada y sigue las instrucciones."
                )
            },
            onFailure = { errorMessage ->
                setLoading(false)
                showAlert("Error", errorMessage)
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            sendButton.isEnabled = false
            sendButton.text = "Enviando..."
            emailEditText.isEnabled = false
        } else {
            sendButton.isEnabled = true
            sendButton.text = getString(R.string.send_button)
            emailEditText.isEnabled = true
        }
    }

    private fun showSuccessDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish() // Volver al login
            }
            .setCancelable(false)
            .show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}