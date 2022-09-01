package ru.otus.repositoty

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.otus.App
import ru.otus.model.FilmModel

class FilmsRepository() {

    lateinit var filmsSource: FilmPages
    val repoError = MutableLiveData<String>()

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher) {
        val films = getFilmsFromDB(pageIndex, pageSize)
        if (films.size < 20) {
            getFilmsFromAPI(pageIndex, pageSize)
            return@withContext getFilmsFromDB(pageIndex, pageSize)
        }
        else {
            return@withContext films}
    }

    private suspend fun getFilmsFromAPI(pageIndex: Int, pageSize: Int)
            = withContext(ioDispatcher) {
        val list = arrayListOf<FilmModel>()

        try {
            val response = App.instance.api.getFilms(TYPE_TOP, pageIndex)
            response.films.forEachIndexed { i, model ->
                list.add(
                    FilmModel(
                        (pageIndex - 1) * pageSize + i, model.id, model.name, model.poster)
                )
            }
            DB.getDB(App.instance.applicationContext)?.getPublisherDao()?.insert(list)
        } catch (e: Throwable){
            repoError.postValue("Failed to get films from server: ${e.message}")
        }
    }

    private suspend fun getFilmsFromDB(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBList = arrayListOf<FilmModel>()
        DB.getDB(App.instance.applicationContext)?.getPublisherDao()
            ?.getFilms(pageSize, offset)?.forEach { model ->
                DBList.add(FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false
                ))
            }
        return@withContext DBList
    }

    suspend fun getFavoriteFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBlist = arrayListOf<FilmModel>()
        DB.getDB(App.instance.applicationContext)?.getPublisherDao()
            ?.getFavoriteFilms(pageSize, offset)?.forEach { model ->
                DBlist.add(FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false
                ))
            }

        return@withContext DBlist
    }

    fun getFilm(filmID: Int, onFilmReceivedListener: (FilmModel?) -> Unit){
        onFilmReceivedListener.invoke(
            getFilmFromDB(filmID)
        )
        App.instance.api.getFilm(filmID).enqueue(
            object: Callback<FilmModel> {
                override fun onResponse(call: Call<FilmModel>, response: Response<FilmModel>) {
                    response.body()?.let {
                        updateDescriptionIntoDB(it.id ,it.description)
                    }
                    onFilmReceivedListener.invoke(
                        getFilmFromDB(filmID)
                    )
                }
                override fun onFailure(call: Call<FilmModel>, t: Throwable) {
                    repoError.postValue("Failed to get film from server: ${t.message}")
                }
            }
        )
    }

    fun updateDescriptionIntoDB(filmID: Int, description: String?){
        if (description != null) {
            DB.getDB(App.instance.applicationContext)?.getPublisherDao()
                ?.updateFilmDescription(filmID.toString(), description)
        }
    }

    fun updateFilmInDB(filmModel: FilmModel){
        DB.getDB(App.instance.applicationContext)?.getPublisherDao()
            ?.update(filmModel)
    }

    fun getFilmFromDB(filmID: Int): FilmModel?{
        val filmModel = DB.getDB(App.instance.applicationContext)?.getPublisherDao()
            ?.getFilm(filmID.toString())
        return filmModel
    }

    companion object {
        const val PAGE_SIZE = 20
        const val TYPE_TOP = "TOP_250_BEST_FILMS"
    }
}
