package ru.otus

import android.app.Application
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.di.AppComponent
import ru.otus.di.DaggerAppComponent
import ru.otus.di.module.AppModule
import ru.otus.repositoty.FilmApi

class App : Application() {

    companion object {
    lateinit var instance: App
           private set
    lateinit var appComponent: AppComponent
        private set
   }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}