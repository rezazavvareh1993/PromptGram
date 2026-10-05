package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PromptEntity::class], version = 2, exportSchema = false)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao

    companion object {
        @Volatile
        private var INSTANCE: PromptDatabase? = null

        fun getInstance(context: Context): PromptDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PromptDatabase::class.java,
                    "prompt_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).promptDao().insertAll(DefaultPrompts.getInitialPrompts())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
