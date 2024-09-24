package com.nassican.splashcalculatorapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nassican.splashcalculatorapp.database.dao.IMCRecordDao
import com.nassican.splashcalculatorapp.database.dao.UserDao
import com.nassican.splashcalculatorapp.database.model.IMCRecord
import com.nassican.splashcalculatorapp.database.model.User

@Database(entities = [User::class, IMCRecord::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun imcRecordDao(): IMCRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}