package ru.otus.repositoty

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import ru.otus.presentation.model.FilmModel

interface FilmApi {

    @GET ("/api/v2.2/films/top")
    suspend fun getFilms(@Query("type") type: String,@Query("page") page: Int): FilmsResponse

    @GET ("/api/v2.2/films/{filmID}")
    fun getFilm(@Path("filmID") filmID: Int): Single<FilmModel>
}