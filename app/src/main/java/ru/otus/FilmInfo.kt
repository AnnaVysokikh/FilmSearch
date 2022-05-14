package ru.otus

import java.io.Serializable

data class FilmInfo(val name: String
                   ,val description: String
                   ,val resId: Int
                   ,var isFavorite: Boolean
                   ) : Serializable