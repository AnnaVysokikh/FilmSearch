package ru.otus.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import ru.otus.R
import ru.otus.presentation.model.FilmModel

class FilmsAdapter(
    private val onDetailsClick: (id: Int) -> Unit,
    private val onFavoriteClick: (id: Int) -> Unit,
    private val highlightedFilms: MutableLiveData<Int>

) : PagingDataAdapter<FilmModel, FilmsViewHolder>(DiffItemCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val viewHolder = FilmsViewHolder(inflater.inflate(R.layout.films, parent, false))
        highlightedFilms.observe(viewHolder.itemView.context as LifecycleOwner) {
            highlightFilm(viewHolder)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FilmsViewHolder, position: Int) {
        when (holder) {
            is FilmsViewHolder -> {
                getItem(position)?.let { holder.bind(it, onDetailsClick, onFavoriteClick) }
                highlightFilm(holder)
            }
        }
    }

    private fun highlightFilm(viewHolder: FilmsViewHolder) {
        if (viewHolder.filmID == highlightedFilms.value) {
            viewHolder.itemView.findViewById<TextView>(R.id.name_film).setTextColor(Color.RED)
        }
        else viewHolder.itemView.findViewById<TextView>(R.id.name_film).setTextColor(Color.BLACK)
    }

}