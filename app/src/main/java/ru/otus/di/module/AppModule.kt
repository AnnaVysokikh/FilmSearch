package ru.otus.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.otus.repositoty.AppDB
import ru.otus.repositoty.PublisherDao
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Singleton
    @Provides
    fun getDao(database: AppDB): PublisherDao{
        return database.getPublisherDao()
    }

    @Singleton
    @Provides
    fun getRoomDbInstance(): AppDB {
        return AppDB.getDB(provideAppContext())
    }
    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return application.applicationContext
    }
}