package ru.otus.repositoty

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

private const val DATABASE_NAME = "FILMS_DB"
object DB {
    private var instance: AppDB? = null

    fun getDB(context: Context): AppDB? {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDB::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                    println("SQL Query: $sqlQuery SQL Args: $bindArgs")
                }, Executors.newSingleThreadExecutor())
                .build()
        }
        return instance
    }

    fun destroyDB(){
        instance?.close()
        instance = null
    }
}