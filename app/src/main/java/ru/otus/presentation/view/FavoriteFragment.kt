package ru.otus.presentation.view

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import kotlinx.coroutines.launch
import ru.otus.presentation.FilmsAdapter
import ru.otus.presentation.MainActivity.Companion.FAVORITE_FILMS
import ru.otus.presentation.model.FilmModel

class FavoriteFragment : FilmsFragment() {

    override fun observePagedFilms(filmAdapter: FilmsAdapter) {
        viewModel.pagedFavoriteFilms.observe(viewLifecycleOwner, Observer<PagingData<FilmModel>> {
            lifecycleScope.launch { filmAdapter.submitData(it) }
        })
    }

    override fun onResume(){
        super.onResume()
        viewModel.fragmentName = FAVORITE_FILMS
    }
}