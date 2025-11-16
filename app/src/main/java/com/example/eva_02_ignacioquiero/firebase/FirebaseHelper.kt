package com.example.eva_02_ignacioquiero.firebase

import com.example.eva_02_ignacioquiero.models.Noticia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseHelper {

    // Instancias de Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Colección de noticias en Firestore
    private val noticiasCollection = firestore.collection("noticias")

    // ==================== AUTHENTICATION ====================

    /**
     * Registrar un nuevo usuario
     */
    fun registerUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let { user ->
                    onSuccess(user)
                } ?: onFailure("Error: Usuario nulo")
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error desconocido al registrar")
            }
    }

    /**
     * Iniciar sesión
     */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let { user ->
                    onSuccess(user)
                } ?: onFailure("Error: Usuario nulo")
            }
            .addOnFailureListener { exception ->
                onFailure(getErrorMessage(exception))
            }
    }

    /**
     * Recuperar contraseña
     */
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al enviar correo de recuperación")
            }
    }

    /**
     * Cerrar sesión
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Obtener usuario actual
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Verificar si hay un usuario logueado
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // ==================== FIRESTORE - NOTICIAS ====================

    /**
     * Guardar una noticia en Firestore
     */
    fun saveNoticia(
        noticia: Noticia,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Crear un documento con ID automático si no tiene
        val documentId = noticia.id.ifEmpty { noticiasCollection.document().id }
        val noticiaConId = noticia.copy(id = documentId)

        noticiasCollection.document(documentId)
            .set(noticiaConId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al guardar noticia")
            }
    }

    /**
     * Obtener todas las noticias ordenadas por fecha (más recientes primero)
     */
    fun getNoticias(
        onSuccess: (List<Noticia>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        noticiasCollection
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val noticias = querySnapshot.documents.mapNotNull { document ->
                    try {
                        document.toObject(Noticia::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(noticias)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al obtener noticias")
            }
    }

    /**
     * Obtener una noticia específica por ID
     */
    fun getNoticiaById(
        id: String,
        onSuccess: (Noticia?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        noticiasCollection.document(id)
            .get()
            .addOnSuccessListener { document ->
                val noticia = document.toObject(Noticia::class.java)
                onSuccess(noticia)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al obtener noticia")
            }
    }

    /**
     * Eliminar una noticia
     */
    fun deleteNoticia(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        noticiasCollection.document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al eliminar noticia")
            }
    }

    /**
     * Actualizar una noticia existente
     */
    fun updateNoticia(
        noticia: Noticia,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        noticiasCollection.document(noticia.id)
            .set(noticia)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al actualizar noticia")
            }
    }

    // ==================== UTILIDADES ====================

    /**
     * Traducir mensajes de error de Firebase al español
     */
    private fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("no user record") == true ||
                    exception.message?.contains("user not found") == true ->
                "No existe una cuenta con este correo electrónico"

            exception.message?.contains("wrong-password") == true ||
                    exception.message?.contains("invalid-credential") == true ->
                "Contraseña incorrecta"

            exception.message?.contains("invalid-email") == true ->
                "Formato de correo electrónico inválido"

            exception.message?.contains("email-already-in-use") == true ->
                "Ya existe una cuenta con este correo electrónico"

            exception.message?.contains("weak-password") == true ->
                "La contraseña es muy débil. Debe tener al menos 6 caracteres"

            exception.message?.contains("network") == true ->
                "Error de conexión. Verifica tu internet"

            else -> exception.message ?: "Error desconocido"
        }
    }
}