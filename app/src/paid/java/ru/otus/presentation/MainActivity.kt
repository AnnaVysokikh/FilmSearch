package ru.otus.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import ru.otus.App
import ru.otus.R
import ru.otus.presentation.viewModel.FilmsViewModel
import ru.otus.presentation.viewModel.FilmsViewModelFactory
import ru.otus.presentation.view.DescriptionFragment
import ru.otus.presentation.view.FavoriteFragment
import ru.otus.presentation.view.FilmsFragment
import javax.inject.Inject

open class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: FilmsViewModelFactory
    private lateinit var viewModel: FilmsViewModel
    lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.appComponent.inject(this)
        viewModel = ViewModelProvider(this, factory)[FilmsViewModel::class.java]

        initFirebaseToken()

        navigation = findViewById(R.id.bottomNavigation)

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
        navigation.setOnItemReselectedListener {  }

        if (intent.action == NOTIFICATION_RECEIVER_ACTION && savedInstanceState == null)
        {
            Log.d("__OTUS__","notification:  ${intent.action}")

            openAllFilms(savedInstanceState)
            viewModel.fragmentName = DETAILS
            var id = intent.getIntExtra(DescriptionFragment.EXTRA_FILM_ID, 0)
            if (id == 0)
                id = intent.getStringExtra(DescriptionFragment.EXTRA_FILM_ID)?.toInt()!!
            viewModel.setSelectedFilm(id)
        }

        when (viewModel.fragmentName) {
            FILMS -> openAllFilms(savedInstanceState)
            FAVORITE_FILMS -> openFavoriteFilms(savedInstanceState)
            DETAILS -> viewModel.selectedFilm.value?.id?.let { openDetails() }
        }
    }

    private fun openAllFilms(savedInstanceState: Bundle?) {
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

    private fun initFirebaseToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("__OTUS__", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("__OTUS__", "token = $token")
        })
    }

    companion object {
        const val FAVORITE_FILMS = "films_favorite"
        const val FILMS = "fragment_films"
        const val DETAILS = "fragment_details"
        const val NOTIFICATION_RECEIVER_ACTION = "NotificationReceiverAction"
    }

}