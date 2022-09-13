package ru.otus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.otus.repositoty.FilmsRepository
import javax.inject.Inject

class FilmsViewModelFactory @Inject constructor(
    private val filmsRepository: FilmsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilmsViewModel(filmsRepository) as T
    }
}