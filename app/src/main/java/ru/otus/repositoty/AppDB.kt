package ru.otus.repositoty

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.otus.model.FilmModel
import java.util.concurrent.Executors

@Database(entities = [FilmModel::class], version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun getPublisherDao(): PublisherDao

    companion object {
        private var instanceDB: AppDB? = null
        fun getDB(context: Context): AppDB {
            if (instanceDB == null) {
                instanceDB = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "FILMS_DB")
                    .allowMainThreadQueries()
                    .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                        println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    .build()
            }
            return instanceDB!!
        }
    }
}