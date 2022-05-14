package ru.otus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FilmsAdapter(
    private val items: List<FilmInfo>,
    private val listener: FilmsClickListener
) : RecyclerView.Adapter<FilmsViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FilmsViewHolder(inflater.inflate(R.layout.films, parent, false))
    }

    override fun onBindViewHolder(holder: FilmsViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size


    interface FilmsClickListener {
        fun onFavoriteClick(newsItem: FilmInfo, position: Int)
        fun onDetailsClick(newsItem: FilmInfo, position: Int)
    }
}