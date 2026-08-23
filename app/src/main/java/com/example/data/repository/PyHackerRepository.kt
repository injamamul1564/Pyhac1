package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.AiMessageEntity
import com.example.data.local.BattleRecordEntity
import com.example.data.local.CodeSnippetEntity
import com.example.data.local.LessonEntity
import com.example.data.local.UserProgressEntity
import com.example.data.model.JudgeResult
import com.example.data.model.UserLevel
import com.example.engine.PyHackerAiEngine
import com.example.engine.PythonSandboxEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class PyHackerRepository(private val dao: AppDao) {

    fun getUserProgress(): Flow<UserProgressEntity?> = dao.getUserProgress()

    fun getLessonsByTrack(trackId: String): Flow<List<LessonEntity>> = dao.getLessonsByTrack(trackId)

    fun getAllLessons(): Flow<List<LessonEntity>> = dao.getAllLessons()

    suspend fun getLessonById(id: String): LessonEntity? = dao.getLessonById(id)

    suspend fun completeLesson(lessonId: String, xpAward: Int = 30) = withContext(Dispatchers.IO) {
        dao.setLessonCompleted(lessonId, true)
        val currentProgress = dao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        val completedList = currentProgress.completedLessonsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        completedList.add(lessonId)
        
        val newXp = currentProgress.xp + xpAward
        val newLevel = UserLevel.fromXp(newXp).name
        
        dao.updateUserProgress(
            currentProgress.copy(
                xp = newXp,
                currentLevel = newLevel,
                completedLessonsCsv = completedList.joinToString(",")
            )
        )
    }

    suspend fun addXp(amount: Int) = withContext(Dispatchers.IO) {
        val current = dao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        val newXp = current.xp + amount
        val newLevel = UserLevel.fromXp(newXp).name
        dao.updateUserProgress(current.copy(xp = newXp, currentLevel = newLevel))
    }

    fun getAllSnippets(): Flow<List<CodeSnippetEntity>> = dao.getAllSnippets()

    suspend fun saveSnippet(title: String, code: String, track: String, output: String = ""): Long = withContext(Dispatchers.IO) {
        dao.insertSnippet(
            CodeSnippetEntity(
                title = title,
                code = code,
                trackCategory = track,
                lastOutput = output
            )
        )
    }

    suspend fun deleteSnippet(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSnippet(id)
    }

    fun getAiMessages(): Flow<List<AiMessageEntity>> = dao.getAllAiMessages()

    suspend fun sendUserMessageToAi(userText: String, codeContext: String? = null): AiMessageEntity = withContext(Dispatchers.IO) {
        // 1. Save user message
        dao.insertAiMessage(
            AiMessageEntity(
                sender = "USER",
                messageText = userText,
                codeSnippet = codeContext,
                messageType = if (codeContext.isNullOrBlank()) "GENERAL" else "CODE_QUERY"
            )
        )

        // 2. Query AI engine
        val aiResponse = PyHackerAiEngine.queryAiTutor(userText, codeContext)

        // 3. Save AI reply
        val aiEntity = AiMessageEntity(
            sender = "PYHACKER_AI",
            messageText = aiResponse,
            messageType = "REPLY"
        )
        dao.insertAiMessage(aiEntity)
        return@withContext aiEntity
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        dao.clearAiMessages()
    }

    fun getAllBattleRecords(): Flow<List<BattleRecordEntity>> = dao.getAllBattleRecords()

    suspend fun recordBattleResult(
        problemId: String,
        problemTitle: String,
        userScore: Int,
        aiScore: Int,
        winner: String,
        complexity: String,
        space: String,
        execTimeMs: Long
    ) = withContext(Dispatchers.IO) {
        dao.insertBattleRecord(
            BattleRecordEntity(
                problemId = problemId,
                problemTitle = problemTitle,
                userScore = userScore,
                aiScore = aiScore,
                winner = winner,
                userTimeComplexity = complexity,
                userSpaceComplexity = space,
                executionTimeMs = execTimeMs
            )
        )
        val progress = dao.getUserProgress().firstOrNull() ?: UserProgressEntity()
        val xpGain = if (winner == "USER") 50 else 20
        val newXp = progress.xp + xpGain
        dao.updateUserProgress(
            progress.copy(
                xp = newXp,
                currentLevel = UserLevel.fromXp(newXp).name,
                solvedBattlesCount = progress.solvedBattlesCount + 1
            )
        )
    }

    fun executeSandbox(code: String, customInput: String = ""): JudgeResult {
        return PythonSandboxEvaluator.executeAndJudge(code, customInput)
    }
}
