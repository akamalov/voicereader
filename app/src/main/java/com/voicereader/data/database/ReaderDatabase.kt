package com.voicereader.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [DocumentEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ReaderDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun bookmarkDao(): BookmarkDao
    
    companion object {
        @Volatile
        private var INSTANCE: ReaderDatabase? = null
        
        fun getDatabase(context: Context): ReaderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReaderDatabase::class.java,
                    "reader_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
