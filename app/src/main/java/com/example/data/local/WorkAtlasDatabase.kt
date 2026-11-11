package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkedJobEntity::class, CompletedLessonEntity::class, RecentSearchEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WorkAtlasDatabase : RoomDatabase() {
    abstract fun bookmarkedJobDao(): BookmarkedJobDao
    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        @Volatile
        private var INSTANCE: WorkAtlasDatabase? = null

        fun getDatabase(context: Context): WorkAtlasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkAtlasDatabase::class.java,
                    "workatlas_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
