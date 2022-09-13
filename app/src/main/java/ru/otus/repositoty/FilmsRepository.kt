package ru.otus.repositoty

import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.otus.model.FilmModel
import javax.inject.Inject

class FilmsRepository @Inject constructor(private val api: FilmApi, private val dao: PublisherDao) {

    lateinit var filmsSource: FilmPages
    val repoError = MutableLiveData<String>()

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
    {
        val films = getFilmsFromDB(pageIndex, pageSize)
        if (films.size < 20) {
            getFilmsFromAPI(pageIndex, pageSize)
            return getFilmsFromDB(pageIndex, pageSize)
        }
        return films
    }

    private fun getFilmsFromAPI(pageIndex: Int, pageSize: Int)
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
    }

    private suspend fun getFilmsFromDB(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBList = arrayListOf<FilmModel>()

        dao.getFilms(pageSize, offset)?.forEach { model ->
                DBList.add(FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false,
                    model.watchLater?: -1
                ))
            }
        return@withContext DBList
    }

    suspend fun getFavoriteFilms(pageIndex: Int, pageSize: Int): ArrayList<FilmModel>
            = withContext(ioDispatcher){
        val offset = (pageIndex - 1) * pageSize
        val DBlist = arrayListOf<FilmModel>()

        dao.getFavoriteFilms(pageSize, offset)!!
             .forEach { model ->
                DBlist.add(FilmModel(
                    model.positionID,
                    model.id,
                    model.name,
                    model.poster,
                    model.description?:"",
                    model.isFavorite?: false,
                    model.watchLater?: -1
                ))
            }

        return@withContext DBlist
    }

    fun getFilm(filmID: Int, onFilmReceivedListener: (FilmModel?) -> Unit){
        onFilmReceivedListener.invoke(getFilmFromDB(filmID))

        api.getFilm(filmID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<FilmModel> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(t: FilmModel) {
                        t.let {
                            updateDescriptionIntoDB(it.id ,it.description)
                        }
                        onFilmReceivedListener.invoke(
                            getFilmFromDB(filmID)
                        )
                    }

                    override fun onError(e: Throwable) {
                        repoError.postValue("Failed to get film from server: ${e.message}")
                    }
                }
            )
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
