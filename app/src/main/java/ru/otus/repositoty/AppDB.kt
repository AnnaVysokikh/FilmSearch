package ru.otus.repositoty

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.otus.model.FilmModel

@Database(entities = [FilmModel::class], version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun getPublisherDao(): PublisherDao
}