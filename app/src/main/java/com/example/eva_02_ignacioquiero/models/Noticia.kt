package com.example.eva_02_ignacioquiero.models

data class Noticia(
    val id: String,
    val titulo: String,
    val bajada: String,
    val imagenUrl: String = "",
    val cuerpo: String,
    val fecha: String
){
    // Constructor
    constructor() : this("", "", "", "", "", "")
}