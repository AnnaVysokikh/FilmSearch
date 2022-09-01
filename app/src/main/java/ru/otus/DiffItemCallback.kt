package ru.otus

import androidx.recyclerview.widget.DiffUtil
import ru.otus.model.FilmModel

class DiffItemCallback : DiffUtil.ItemCallback<FilmModel>() {

    override fun areItemsTheSame(oldItem: FilmModel, newItem: FilmModel): Boolean {
        return newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: FilmModel, newItem: FilmModel): Boolean {
        return (newItem.id == oldItem.id &&
                newItem.isFavorite == oldItem.isFavorite)
    }
}