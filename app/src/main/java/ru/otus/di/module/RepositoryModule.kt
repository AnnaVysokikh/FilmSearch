package ru.otus.di.module

import dagger.Module
import dagger.Provides
import ru.otus.repositoty.FilmsRepository
import ru.otus.viewmodel.FilmsViewModelFactory

@Module
class RepositoryModule {

    @Provides
    fun provideViewModelFilms(filmsRepository: FilmsRepository): FilmsViewModelFactory{
        return FilmsViewModelFactory(filmsRepository)
    }

}