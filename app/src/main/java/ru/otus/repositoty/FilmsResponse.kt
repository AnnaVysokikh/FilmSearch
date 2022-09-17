package ru.otus.repositoty

import com.google.gson.annotations.Expose
import ru.otus.presentation.model.FilmModel

data class FilmsResponse (
    @Expose
    var pagesCount: Int,
    @Expose
    var films: ArrayList<FilmModel>
    )