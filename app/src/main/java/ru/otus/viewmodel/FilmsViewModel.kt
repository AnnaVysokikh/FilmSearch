package ru.otus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.google.android.material.snackbar.Snackbar
import ru.otus.repositoty.FilmsRepository
import ru.otus.MainActivity.Companion.FILMS
import ru.otus.model.FilmModel
import ru.otus.repositoty.FilmPages
import ru.otus.repositoty.FilmsPageLoader

class FilmsViewModel (
    private val filmsRepository: FilmsRepository
    ) : ViewModel(){

    var fragmentName: String = FILMS
    private val mError = filmsRepository.repoError
    private val mSelectedFilm = MutableLiveData<FilmModel>()
    var highlightedFilms = MutableLiveData<Int>()

    val pagedFilms: LiveData<PagingData<FilmModel>>
        get() {
            val loader: FilmsPageLoader = { pageIndex, pageSize ->
                filmsRepository.getFilms(pageIndex, pageSize)
            }
            return Pager(
                config = PagingConfig(
                    pageSize = FilmsRepository.PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = FilmsRepository.PAGE_SIZE
                ),
                pagingSourceFactory = {
                    filmsRepository.filmsSource = FilmPages(loader, FilmsRepository.PAGE_SIZE)
                    filmsRepository.filmsSource
                }
            ).liveData.cachedIn(viewModelScope)
        }

    val pagedFavoriteFilms: LiveData<PagingData<FilmModel>>
        get() {
            val loader: FilmsPageLoader = { pageIndex, pageSize ->
                filmsRepository.getFavoriteFilms(pageIndex, pageSize)
            }
            return Pager(
                config = PagingConfig(
                    pageSize = FilmsRepository.PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = FilmsRepository.PAGE_SIZE
                ),
                pagingSourceFactory = {
                    filmsRepository.filmsSource = FilmPages(loader, FilmsRepository.PAGE_SIZE)
                    filmsRepository.filmsSource
                }
            ).liveData.cachedIn(viewModelScope)
        }

    init {
        highlightedFilms.value = -1
    }
    val selectedFilm: LiveData<FilmModel> = mSelectedFilm
    val error: LiveData<String> = mError

    fun onFilmClick(id: Int){
        highlightedFilms.value = id
        filmsRepository.getFilm(id) { mSelectedFilm.value = it }
    }

    fun onFavoriteChanged(id: Int){
        val changingFilm = filmsRepository.getFilmFromDB(id)
        if (changingFilm?.isFavorite != null) {
            changingFilm.isFavorite = !changingFilm.isFavorite!!
            filmsRepository.updateFilmInDB(changingFilm)
            filmsRepository.filmsSource.invalidate()
        }
    }
}