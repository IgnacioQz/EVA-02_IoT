package com.example.eva_02_ignacioquiero

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    // Variables para elementos de la pantalla
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var checkBluetoothButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var registerTextView: TextView

    // Variable para el adaptador de Bluetooth
    private var bluetoothAdapter: BluetoothAdapter? = null

    // Código de solicitud de permisos
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        // Inicializar Bluetooth
        initializeBluetooth()

        // Inicializar vistas
        initializeViews()

        // Configurar eventos
        setupListeners()
    }

    private fun initializeBluetooth() {
        // Para Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
        } else {
            // Para versiones anteriores
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        checkBluetoothButton = findViewById(R.id.checkBluetoothButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        registerTextView = findViewById(R.id.registerTextView)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            handleLogin()
        }

        // botón de verificar Bluetooth
        checkBluetoothButton.setOnClickListener {
            checkBluetoothStatus()
        }

        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        when {
            email.isEmpty() -> {
                showAlert("Error", "Por favor ingresa tu correo electrónico")
            }
            password.isEmpty() -> {
                showAlert("Error", "Por favor ingresa tu contraseña")
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showAlert("Error", "Por favor ingresa un correo válido")
            }
            else -> {
                showAlert("Éxito", "Inicio de sesión exitoso\n\nBienvenido: $email")
            }
        }
    }

    /**
     * FUNCIÓN BONUS: Verificar el estado del Bluetooth
     */
    private fun checkBluetoothStatus() {
        // Verificar si el dispositivo soporta Bluetooth
        if (bluetoothAdapter == null) {
            showAlert(
                getString(R.string.bluetooth_status),
                getString(R.string.bluetooth_not_supported)
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasBluetoothPermissions()) {
                requestBluetoothPermissions()
                return
            }
        }

        // Obtener el estado del Bluetooth
        val isEnabled = try {
            bluetoothAdapter?.isEnabled ?: false
        } catch (e: SecurityException) {
            showAlert(
                getString(R.string.bluetooth_status),
                getString(R.string.bluetooth_permission_denied)
            )
            return
        }

        val message = if (isEnabled) {
            buildString {
                append(getString(R.string.bluetooth_enabled))
                append("\n\n")
                append("📱 Dispositivo: ${Build.MODEL}\n")
                append("🔧 Fabricante: ${Build.MANUFACTURER}\n")
                append("📡 Estado: CONECTIVIDAD ACTIVA")
            }
        } else {
            buildString {
                append(getString(R.string.bluetooth_disabled))
                append("\n\n")
                append("Para activar el Bluetooth:\n")
                append("1. Ve a Configuración\n")
                append("2. Busca 'Bluetooth'\n")
                append("3. Activa el interruptor")
            }
        }

        showAlert(getString(R.string.bluetooth_status), message)
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, verificar Bluetooth de nuevo
                    checkBluetoothStatus()
                } else {
                    // Permiso denegado
                    showAlert(
                        "Permiso Denegado",
                        "Se necesitan permisos de Bluetooth para verificar el estado.\n\n" +
                                "Puedes activarlos en Configuración → Aplicaciones → Permisos"
                    )
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}