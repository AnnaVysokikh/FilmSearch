package ru.otus.view

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import kotlinx.coroutines.launch
import ru.otus.FilmsAdapter
import ru.otus.MainActivity.Companion.FAVORITE_FILMS
import ru.otus.model.FilmModel

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