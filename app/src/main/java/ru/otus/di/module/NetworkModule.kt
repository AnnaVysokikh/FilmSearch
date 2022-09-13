package ru.otus.di.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.repositoty.FilmApi
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {

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
        return client
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KINOPOISK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): FilmApi{
        return retrofit.create(FilmApi::class.java)
    }

    companion object {
        const val KINOPOISK_URL = "https://kinopoiskapiunofficial.tech"
        const val TOKEN = "8c954462-41eb-4ae2-b22d-d48728ec769a"
    }
}