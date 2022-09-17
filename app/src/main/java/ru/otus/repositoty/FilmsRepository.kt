package ru.otus.repositoty

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.otus.presentation.SingleLiveEvent
import ru.otus.presentation.model.FilmModel
import javax.inject.Inject

class FilmsRepository @Inject constructor(private val api: FilmApi, val dao: PublisherDao) {

    lateinit var filmsSource: FilmPages
    val repoError = SingleLiveEvent<String>()

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
    {
        var films = getFilmsFromDB(pageIndex, pageSize)
        if (films.size < 20) {
            getFilmsFromAPI(pageIndex, pageSize)
            return getFilmsFromDB(pageIndex, pageSize)
        }
        return films
    }
/*
    fun getFilmsFromAPI(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
    {
        val list = arrayListOf<FilmModel>()

        api.getFilms(TYPE_TOP, pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<FilmsResponse> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(filmsResponse: FilmsResponse) {
                        filmsResponse.films.forEachIndexed { i, model ->
                            list.add(
                                FilmModel(
                                    (pageIndex - 1) * pageSize + i, model.id, model.name, model.poster)
                            )
                        }
                        dao.insert(list)
                    }

                    override fun onError(e: Throwable) {
                        repoError.postValue("Failed to get film from server: ${e.message}")
                    }
                }
            )
        return list
    }*/

    suspend fun getFilmsFromAPI(pageIndex: Int, pageSize: Int)
            = withContext(ioDispatcher) {
        val list = arrayListOf<FilmModel>()

        try {
            val response = api.getFilms(TYPE_TOP, pageIndex)
            response.films.forEachIndexed { i, model ->
                list.add(
                    FilmModel(
                        (pageIndex - 1) * pageSize + i, model.id, model.name, model.poster)
                )
            }
            dao.insert(list)
        } catch (e: Throwable){
            repoError.postValue("Failed to get films from server: ${e.message}")
            Log.d("__OTUS__", "Failed to get film from server: ${e.message}")
        }
    }

    suspend fun getFilmsFromDB(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBList = arrayListOf<FilmModel>()

        dao.getFilms(pageSize, offset)?.forEach { model ->
                DBList.add(
                    FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false
                )
                )
            }
        return@withContext DBList
    }

    suspend fun getFavoriteFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBlist = arrayListOf<FilmModel>()

        dao.getFavoriteFilms(pageSize, offset)!!
             .forEach { model ->
                DBlist.add(
                    FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false
                )
                )
            }

        return@withContext DBlist
    }

    fun getFilm(filmID: Int, onFilmReceivedListener: (FilmModel?) -> Unit): Disposable{
        onFilmReceivedListener.invoke(getFilmFromDB(filmID))

        val disposable: Disposable = api.getFilm(filmID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    updateDescriptionIntoDB(it.id, it.description)
                    onFilmReceivedListener.invoke(getFilmFromDB(filmID))
                },
                { error ->
                    repoError.postValue("Failed to get film from server: ${error.message}")
                    Log.d("__OTUS__", "Failed to get film from server: ${error.message}")
                }

            )
        return disposable
    }


    fun updateDescriptionIntoDB(filmID: Int, description: String?){
        if (description != null) {
            dao.updateFilmDescription(filmID.toString(), description)
        }
    }

    fun updateFilmInDB(filmModel: FilmModel){
        dao.update(filmModel)
    }

    fun getFilmFromDB(filmID: Int): FilmModel?{
        return dao.getFilm(filmID.toString())
    }

    companion object {
        const val PAGE_SIZE = 20
        const val TYPE_TOP = "TOP_250_BEST_FILMS"
    }
}
