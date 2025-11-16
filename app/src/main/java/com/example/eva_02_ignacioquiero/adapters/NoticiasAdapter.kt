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
    private val noticias: MutableList<Noticia>,  // Cambiar a MutableList
    private val onNoticiaClick: (Noticia) -> Unit
) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imagenNoticia)
        val titulo: TextView = itemView.findViewById(R.id.tituloNoticia)
        val bajada: TextView = itemView.findViewById(R.id.bajadaNoticia)
        val fecha: TextView = itemView.findViewById(R.id.fechaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]

        holder.titulo.text = noticia.titulo
        holder.bajada.text = noticia.bajada
        holder.fecha.text = noticia.fecha

        // Por ahora dejamos la imagen por defecto
        // Cuando conectemos Firebase Storage, cargaremos imágenes reales

        holder.itemView.setOnClickListener {
            onNoticiaClick(noticia)
        }
    }

    override fun getItemCount(): Int = noticias.size

    // Métodos para actualizar la lista
    fun updateNoticias(newNoticias: List<Noticia>) {
        noticias.clear()
        noticias.addAll(newNoticias)
        notifyDataSetChanged()
    }

    fun addNoticia(noticia: Noticia) {
        noticias.add(0, noticia) // Agregar al inicio
        notifyItemInserted(0)
    }
}