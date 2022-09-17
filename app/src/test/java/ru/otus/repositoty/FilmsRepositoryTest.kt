package ru.otus.repositoty

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import ru.otus.RxImmediateSchedulerRule
import ru.otus.presentation.model.FilmModel

class FilmsRepositoryTest{

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var api: FilmApi

    @Mock
    lateinit var dao: PublisherDao

    lateinit var filmsRepository: FilmsRepository
    private var filmsRepositoryTest = ArrayList<FilmModel>()
    private var response = FilmsResponse(1, filmsRepositoryTest)

    @Before
    fun setUp() {
        filmsRepository = FilmsRepository(api, dao)
        filmsRepositoryTest.clear()
        filmsRepositoryTest.add(FilmModel(1, 1, name = "Зеленая миля", poster = ".img", description = "description", false))
        filmsRepositoryTest.add(FilmModel(1, 2, name = "Зеленая миля2", poster = ".img", description = "description2", true))
        filmsRepositoryTest.add(FilmModel(1, 3, name = "Зеленая миля3", poster = ".img", description = "description3", false))
        response = FilmsResponse(1, filmsRepositoryTest)
    }

    @Test
    fun getFilmFromDB() {
        val film = FilmModel(1, 2, name = "Зеленая миля2", poster = ".img", description = "description2", true)

        Mockito.`when`(dao.getFilm(Mockito.anyString()))
            .thenReturn(film)

        val resp = filmsRepository.getFilmFromDB(2)
        Assert.assertEquals(resp, film)
    }

    @Test
    fun getFilmsFromDB() = runTest {
        Mockito.`when`(dao.getFilms(Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(filmsRepositoryTest)
        val resp = filmsRepository.getFilmsFromDB(2, 1)
        Assert.assertEquals(resp, filmsRepositoryTest)
    }

    @Test
    fun getFavoriteFilms() = runTest {
        Mockito.`when`(dao.getFavoriteFilms(Mockito.anyInt(),Mockito.anyInt())).thenReturn(filmsRepositoryTest)

        val resp = filmsRepository.getFavoriteFilms(2, 1)
        Assert.assertEquals(resp, filmsRepositoryTest)
    }
}