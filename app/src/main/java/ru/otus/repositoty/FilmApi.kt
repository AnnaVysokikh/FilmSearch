package ru.otus.repositoty

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import ru.otus.model.FilmModel

interface FilmApi {

    @GET ("/api/v2.2/films/top")
    fun getFilms(@Query("type") type: String,@Query("page") page: Int): Single<FilmsResponse>

    @GET ("/api/v2.2/films/{filmID}")
    fun getFilm(@Path("filmID") filmID: Int): Single<FilmModel>
}