package ru.otus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Favorite : AppCompatActivity() {

    private val recyclerViewFavorite by lazy { findViewById<RecyclerView>(R.id.recyclerFavorite) }
    private var favoriteFilms = ArrayList<FilmInfo>()
    private var noFavoriteFilms = ArrayList<FilmInfo>()
    private var isFavoriteChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        favoriteFilms = intent.getSerializableExtra("favorite") as ArrayList<FilmInfo>

        initRecycler()
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("refreshFavoriteFilms", noFavoriteFilms)
        data.putExtra("isFavoriteChange", isFavoriteChange)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewFavorite.layoutManager = layoutManager
        recyclerViewFavorite.adapter = FilmsAdapter(favoriteFilms, object : FilmsAdapter.FilmsClickListener {
            override fun onFavoriteClick(film: FilmInfo, position: Int) {
                isFavoriteChange = true
                noFavoriteFilms.add(film)
                favoriteFilms.removeAt(position)
                recyclerViewFavorite.adapter?.notifyItemRemoved(position)
            }

            override fun onDetailsClick(film: FilmInfo, position: Int) {
                val intent = Intent(this@Favorite, DescriptionActivity::class.java)
                intent.putExtra("film", film)
                startActivity(intent)
            }
        })
    }
}