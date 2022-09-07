package ru.otus.view


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.otus.*
import ru.otus.model.FilmModel
import ru.otus.viewmodel.FilmsViewModel
import ru.otus.databinding.FragmentFilmsBinding
import ru.otus.repositoty.FilmsRepository
import ru.otus.viewmodel.FilmsViewModelFactory
import ru.otus.MainActivity.Companion.FILMS

open class FilmsFragment : Fragment() {

    lateinit var viewModel: FilmsViewModel
    private lateinit var binding : FragmentFilmsBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var loadStateHolder: LoadAdapter.Holder
    private lateinit var filmAdapter: FilmsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), FilmsViewModelFactory(FilmsRepository()))[FilmsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,savedInstanceState: Bundle?
    ) {
        initRecycler()

        viewModel.error.observe(viewLifecycleOwner, Observer<String> { error ->
            Snackbar.make(binding.root,error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again) { filmAdapter.refresh() }
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .show()
        } )
    }

    override fun onResume() {
        viewModel.fragmentName = FILMS
        super.onResume()
    }

    private fun initRecycler() {
        recyclerView = binding.recycler

        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(context, 2)
            else -> LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView.layoutManager = layoutManager

        val horizontalItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_drawable)
            ?.let { horizontalItemDecoration.setDrawable(it) }
        recyclerView.addItemDecoration(horizontalItemDecoration)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val verticalItemDecoration =
                DividerItemDecoration(requireContext(), RecyclerView.HORIZONTAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.divider_drawable)
                ?.let { verticalItemDecoration.setDrawable(it) }
            recyclerView.addItemDecoration(verticalItemDecoration)
        }

        filmAdapter = FilmsAdapter(
            {id -> onFilmDetailsClick(id)},
            {id -> onFavoriteClick(id)},
            viewModel.highlightedFilms
        )

        observePagedFilms(filmAdapter)

        val tryAgainAction: TryAgainAction = { filmAdapter.refresh()}
        val loadAdapter = LoadAdapter(tryAgainAction)
        val adapterWithLoadState = filmAdapter.withLoadStateFooter(loadAdapter)
        recyclerView.adapter = adapterWithLoadState

        loadStateHolder = LoadAdapter.Holder(
            binding.loadStateView,
            binding.swipeRefreshLayout,
            tryAgainAction
        )

        observeLoadState(filmAdapter)
    }

    private fun observeLoadState(adapter: FilmsAdapter) {
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                loadStateHolder.bind(state.refresh)
            }
        }
    }

    open fun observePagedFilms(filmAdapter: FilmsAdapter) {
        viewModel.pagedFilms.observe(viewLifecycleOwner, Observer<PagingData<FilmModel>> {
            lifecycleScope.launch { filmAdapter.submitData(it) }
        })
    }

    open fun onFilmDetailsClick(id: Int) {
        viewModel.onFilmClick(id)
        (activity as MainActivity).openDetails()
    }

    open fun onFavoriteClick(id: Int){
        viewModel.onFavoriteChanged(id)
        var message = if (viewModel.selectedFilm.value?.isFavorite ==true) R.string.add_favorite
            else R.string.del_favorite
        Snackbar.make(binding.root,message, Snackbar.LENGTH_SHORT)
            .setAction(R.string.cancel) { viewModel.onFavoriteChanged(id) }
            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
            .show()
    }
}
