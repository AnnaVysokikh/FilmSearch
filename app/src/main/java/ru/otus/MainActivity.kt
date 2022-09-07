package ru.otus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import ru.otus.repositoty.FilmsRepository
import ru.otus.view.DescriptionFragment
import ru.otus.view.FavoriteFragment
import ru.otus.view.FilmsFragment
import ru.otus.viewmodel.FilmsViewModel
import ru.otus.viewmodel.FilmsViewModelFactory

open class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, FilmsViewModelFactory(FilmsRepository()))[FilmsViewModel::class.java]

        initFirebaseToken()

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

        if ((intent.action == NOTIFICATION_RECEIVER_ACTION ||
                intent.getStringExtra("action") == "startDetails")
        && savedInstanceState == null)    {
            openAllFilms(savedInstanceState)
            viewModel.fragmentName = DETAILS
            var id = intent.getIntExtra(DescriptionFragment.EXTRA_FILM_ID, 0)
            if (id == 0)
                id = intent.getStringExtra(DescriptionFragment.EXTRA_FILM_ID)?.toInt()!!
            viewModel.setSelectedFilm(id)
            //viewModel.setSelectedFilm(intent.getStringExtra(DescriptionFragment.EXTRA_FILM_ID)?.toInt())
        }

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