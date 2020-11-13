package com.android.homework.savestatehomework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.homework.savestatehomework.db.model.Contact

@Database(entities = [Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao

    companion object {
        private var instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                )
                    .build()
            }
            return instance!!
        }
    }
}