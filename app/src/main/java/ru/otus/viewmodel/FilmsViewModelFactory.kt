package ru.otus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.otus.repositoty.FilmsRepository

class FilmsViewModelFactory(val filmsRepository: FilmsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilmsViewModel(filmsRepository) as T
    }
}