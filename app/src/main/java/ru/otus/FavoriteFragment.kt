package ru.otus

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteFragment : Fragment() {

    private lateinit var recyclerViewFavorite: RecyclerView
    private var favoriteFilms = ArrayList<FilmInfo>()
    private lateinit var descriptionFragment: DescriptionFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewFavorite = view.findViewById<RecyclerView>(R.id.recyclerFavorite)
        favoriteFilms = arguments?.getSerializable(ARG_VALUE) as ArrayList<FilmInfo>
        initRecycler(savedInstanceState, view)
    }
    override fun onPause()
    {
        val result = Bundle()
        result.putSerializable(FAVORITE_CHANGE, favoriteFilms)
        parentFragmentManager.setFragmentResult("favorite_result", result)
        super.onPause()
    }

    companion object {
        private const val ARG_VALUE = "films_favorite"
        private const val FAVORITE_CHANGE = "films_Favorite_change"
        private const val IS_CHANGE = "isFavoriteChange"

        fun create(films: ArrayList<FilmInfo>): FavoriteFragment {
            val fragment = FavoriteFragment()
            val arguments = Bundle()
            arguments.putSerializable(ARG_VALUE, films)
            fragment.arguments = arguments
            return fragment
        }
    }

    private fun initRecycler(savedInstanceState: Bundle?, view: View) {
        val layoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> GridLayoutManager(context, 2)
            else -> LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerViewFavorite.layoutManager = layoutManager
        var list = favoriteFilms.filter { i -> i.isFavorite} as ArrayList<FilmInfo>
        recyclerViewFavorite.adapter = FilmsAdapter(list, object : FilmsAdapter.FilmsClickListener {
            override fun onFavoriteClick(film: FilmInfo, position: Int) {
                list.removeAt(position)
                recyclerViewFavorite.adapter?.notifyItemRemoved(position)
                favoriteFilms.filter { it == film }.forEach { it.isFavorite = false }
                Toast.makeText(view.context, R.string.del_favorite, Toast.LENGTH_SHORT).show()
            }

            override fun onDetailsClick(film: FilmInfo, position: Int) {
                descriptionFragment = DescriptionFragment()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DescriptionFragment.create(film))
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}