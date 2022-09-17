package ru.otus.di

import dagger.Component
import javax.inject.Singleton
import ru.otus.App
import ru.otus.di.module.AppModule
import ru.otus.di.module.NetworkModule
import ru.otus.di.module.RepositoryModule
import ru.otus.presentation.MainActivity
import ru.otus.presentation.view.FilmsFragment

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(context: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: FilmsFragment)

}