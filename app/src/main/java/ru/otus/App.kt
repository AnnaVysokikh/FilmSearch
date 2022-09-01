package ru.otus

import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.repositoty.DB
import ru.otus.repositoty.FilmApi

class App : Application() {

    lateinit var api: FilmApi

    override fun onCreate() {
        super.onCreate()
        instance = this

        initRetrofit()

        DB.getDB(this)
    }

    private fun initRetrofit() {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val response = chain.proceed(
                    chain.request()
                        .newBuilder()
                        .addHeader("accept", "application/json")
                        .addHeader("X-API-KEY", TOKEN)
                        .build()
                )
                return@addInterceptor response
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(KINOPOISK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(FilmApi::class.java)
    }

    companion object {
        const val KINOPOISK_URL = "https://kinopoiskapiunofficial.tech"
        const val TOKEN = "8c954462-41eb-4ae2-b22d-d48728ec769a"

        lateinit var instance: App
            private set
    }
}