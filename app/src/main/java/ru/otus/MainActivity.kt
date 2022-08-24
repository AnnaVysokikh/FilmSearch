package ru.otus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var filmsFragment: FilmsFragment
    private lateinit var favoritesFragment: FavoriteFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openAllFilms(savedInstanceState)

        val navigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navigation.setOnItemSelectedListener {
             when (it.itemId) {
                 R.id.all_films -> {
                     Toast.makeText(this, "all_films", Toast.LENGTH_SHORT).show()
                    openAllFilms(savedInstanceState)
                 }
                 R.id.favorites -> {
                     Toast.makeText(this, "favorites", Toast.LENGTH_SHORT).show()
                    openFavoriteFilms(savedInstanceState)
                 }
                 R.id.exit -> {
                     openExitDialog()
                 }
             }
             true
         }
        navigation.setOnNavigationItemReselectedListener { true }
    }

    private fun openAllFilms(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            filmsFragment = FilmsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, filmsFragment, FILMS)
                .commit()
        } else {
            filmsFragment = supportFragmentManager.findFragmentByTag(FILMS) as FilmsFragment
        }
     }

    private fun openFavoriteFilms(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            if (filmsFragment.favoriteFilms != null) {
                favoritesFragment = FavoriteFragment.create(filmsFragment.films as ArrayList<FilmInfo>)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, favoritesFragment, FAVORITE_FILMS)
                    .addToBackStack(null)
                    .commit()
            }
        }
        else {
            favoritesFragment = supportFragmentManager.findFragmentByTag(FAVORITE_FILMS) as FavoriteFragment
        }
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
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_question)
            .setNegativeButton(R.string.no) { dialog, which -> }
            .setNeutralButton(R.string.later) { dialog, which -> }
            .setPositiveButton(R.string.yes) { dialog, which -> super.onBackPressed() }
            .create()
            .show()
    }

    companion object {
         const val FAVORITE_FILMS = "films_favorite"
         const val FILMS = "fragment_films"
    }
}