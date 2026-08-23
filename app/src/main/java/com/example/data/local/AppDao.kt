package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgressEntity)

    @Update
    suspend fun updateUserProgress(progress: UserProgressEntity)

    // Lessons
    @Query("SELECT * FROM lessons ORDER BY trackId, lessonOrder ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE trackId = :trackId ORDER BY lessonOrder ASC")
    fun getLessonsByTrack(trackId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :lessonId")
    suspend fun setLessonCompleted(lessonId: String, isCompleted: Boolean)

    // Code Snippets
    @Query("SELECT * FROM code_snippets ORDER BY updatedAt DESC")
    fun getAllSnippets(): Flow<List<CodeSnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: CodeSnippetEntity): Long

    @Query("DELETE FROM code_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Long)

    // AI Messages
    @Query("SELECT * FROM ai_messages ORDER BY timestamp ASC")
    fun getAllAiMessages(): Flow<List<AiMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(message: AiMessageEntity): Long

    @Query("DELETE FROM ai_messages")
    suspend fun clearAiMessages()

    // Battle Records
    @Query("SELECT * FROM battle_records ORDER BY timestamp DESC")
    fun getAllBattleRecords(): Flow<List<BattleRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBattleRecord(record: BattleRecordEntity): Long
}
