package ru.otus.di

import dagger.Component
import ru.otus.App
import ru.otus.MainActivity
import ru.otus.di.module.AppModule
import ru.otus.di.module.NetworkModule
import ru.otus.di.module.RepositoryModule
import ru.otus.view.FilmsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(context: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: FilmsFragment)

}