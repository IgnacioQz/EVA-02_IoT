package com.example.eva_02_ignacioquiero.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eva_02_ignacioquiero.R
import com.example.eva_02_ignacioquiero.models.Noticia

class NoticiasAdapter(
    private val noticias: List<Noticia>,
    private val onNoticiaClick: (Noticia) -> Unit
) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    // ViewHolder: representa cada item individual del RecyclerView
    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imagenNoticia)
        val titulo: TextView = itemView.findViewById(R.id.tituloNoticia)
        val bajada: TextView = itemView.findViewById(R.id.bajadaNoticia)
        val fecha: TextView = itemView.findViewById(R.id.fechaNoticia)
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    // Reemplaza el contenido de una vista (invocado por el layout manager)
    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]

        // Asignar datos a las vistas
        holder.titulo.text = noticia.titulo
        holder.bajada.text = noticia.bajada
        holder.fecha.text = noticia.fecha

        // Click listener para cada item
        holder.itemView.setOnClickListener {
            onNoticiaClick(noticia)
        }
    }

    // Retorna el tamaño de la lista (invocado por el layout manager)
    override fun getItemCount(): Int = noticias.size
}