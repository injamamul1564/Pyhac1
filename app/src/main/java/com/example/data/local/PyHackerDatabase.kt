package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProgressEntity::class,
        LessonEntity::class,
        CodeSnippetEntity::class,
        AiMessageEntity::class,
        BattleRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PyHackerDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: PyHackerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PyHackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PyHackerDatabase::class.java,
                    "pyhacker_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AppDao) {
                dao.insertUserProgress(
                    UserProgressEntity(
                        id = 1,
                        xp = 120,
                        streakDays = 3,
                        currentLevel = "BEGINNER",
                        activeTrackId = "foundation"
                    )
                )
                dao.insertLessons(CurriculumData.getAllInitialLessons())
                
                // Pre-seed an initial friendly AI message in Bangla + English
                dao.insertAiMessage(
                    AiMessageEntity(
                        sender = "PYHACKER_AI",
                        messageText = "হ্যালো কোডার! আমি **PyHacker AI** — তোমার সার্বক্ষণিক Python শিক্ষক, কোড জাজ, ডিবাগার এবং এথিক্যাল হ্যাকিং মেন্টর।\n\nযেকোনো বিষয়ে প্রশ্ন করতে পারো অথবা কোনো কোড দিয়ে বলতে পারো: **\"আমার কোড চেক করো\"**। আমি বাংলায় সহজ উপমাসহ প্রতিটি লাইন বুঝিয়ে দেব!",
                        messageType = "GENERAL"
                    )
                )
            }
        }
    }
}
