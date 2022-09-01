package ru.otus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.otus.repositoty.FilmsRepository
import ru.otus.view.DescriptionFragment
import ru.otus.view.FavoriteFragment
import ru.otus.view.FilmsFragment
import ru.otus.viewmodel.FilmsViewModel
import ru.otus.viewmodel.FilmsViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, FilmsViewModelFactory(FilmsRepository()))[FilmsViewModel::class.java]

        val navigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.all_films -> {
                    openAllFilms(savedInstanceState)
                }
                R.id.favorites -> {
                    openFavoriteFilms(savedInstanceState)
                }
                R.id.exit -> {
                    openExitDialog()
                }
            }
            true
        }
        navigation.setOnNavigationItemReselectedListener { true }

        when (viewModel.fragmentName) {
            FILMS -> openAllFilms(savedInstanceState)
            FAVORITE_FILMS -> openFavoriteFilms(savedInstanceState)
            DETAILS -> viewModel.selectedFilm.value?.id?.let { openDetails() }
        }
    }

    fun openAllFilms(savedInstanceState: Bundle?) {
        viewModel.fragmentName = FILMS
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FilmsFragment(), FILMS)
                .commit()
        } else {
            val filmsFragment = supportFragmentManager.findFragmentByTag(FILMS)?: FilmsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, filmsFragment)
                .commit()
        }
    }

    private fun openFavoriteFilms(savedInstanceState: Bundle?) {
        viewModel.fragmentName = FAVORITE_FILMS
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavoriteFragment(), FAVORITE_FILMS)
                .addToBackStack(null)
                .commit()
        }
        else {
            val favoritesFragment = supportFragmentManager.findFragmentByTag(FAVORITE_FILMS)?: FavoriteFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, favoritesFragment)
                .commit()
        }
    }

    fun openDetails() {
        viewModel.fragmentName = DETAILS
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, DescriptionFragment(), DETAILS)
            .addToBackStack(DETAILS)
            .commit()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            openExitDialog()
        }
    }

    private fun openExitDialog()
    {
        CloseDialog().show(supportFragmentManager, "closeDialog")
    }

    companion object {
        const val FAVORITE_FILMS = "films_favorite"
        const val FILMS = "fragment_films"
        const val DETAILS = "fragment_details"
    }

}