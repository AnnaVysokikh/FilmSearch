package ru.otus.repositoty

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.otus.presentation.model.FilmModel
import java.lang.Exception

typealias FilmsPageLoader = suspend (pageIndex: Int, pageSize: Int) -> List<FilmModel>

class FilmPages (
    private val loader: FilmsPageLoader,
    private val pageSize: Int
) : PagingSource<Int, FilmModel>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmModel> {
        val pageIndex = params.key ?: 1

        return try {
            val films = loader.invoke(pageIndex, pageSize)
            return LoadResult.Page(
                data = films,
                prevKey = if (pageIndex == 1) null else pageIndex - 1,
                nextKey = if (films.size < pageSize) null else pageIndex + 1,
                itemsBefore = pageIndex * pageSize
            )
        } catch (e: Exception){
            LoadResult.Error(
                throwable =  e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FilmModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}