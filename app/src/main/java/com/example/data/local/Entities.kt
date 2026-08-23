package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 50,
    val streakDays: Int = 1,
    val currentLevel: String = "BEGINNER",
    val activeTrackId: String = "foundation",
    val completedLessonsCsv: String = "",
    val solvedBattlesCount: Int = 0,
    val securityLabsCompletedCount: Int = 0,
    val lastActiveEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val trackId: String,
    val lessonOrder: Int,
    val title: String,
    val level: String,
    val difficulty: String,
    val conceptBn: String,
    val englishExplanation: String,
    val analogyBn: String,
    val syntaxCode: String,
    val exampleCode: String,
    val expectedOutput: String,
    val breakdownBn: String,
    val practiceTaskBn: String,
    val hintBn: String,
    val starterCode: String,
    val solutionCode: String,
    val challengeCode: String,
    val testInput: String,
    val testExpectedOutput: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "code_snippets")
data class CodeSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val code: String,
    val trackCategory: String = "Custom",
    val lastOutput: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "PYHACKER_AI"
    val messageText: String,
    val codeSnippet: String? = null,
    val messageType: String = "GENERAL", // GENERAL, DEBUG, LESSON, JUDGE, BATTLE, SECURITY
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "battle_records")
data class BattleRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val problemId: String,
    val problemTitle: String,
    val userScore: Int,
    val aiScore: Int,
    val winner: String, // "USER", "AI", "DRAW"
    val userTimeComplexity: String,
    val userSpaceComplexity: String,
    val executionTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
