package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AiMessageEntity
import com.example.data.local.BattleRecordEntity
import com.example.data.local.CodeSnippetEntity
import com.example.data.local.CurriculumData
import com.example.data.local.LessonEntity
import com.example.data.local.PyHackerDatabase
import com.example.data.local.UserProgressEntity
import com.example.data.model.AiBattleProblem
import com.example.data.model.JudgeResult
import com.example.data.model.TestCase
import com.example.data.repository.PyHackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PyHackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PyHackerRepository

    init {
        val database = PyHackerDatabase.getDatabase(application, viewModelScope)
        repository = PyHackerRepository(database.appDao())
    }

    val userProgress: StateFlow<UserProgressEntity?> = repository.getUserProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _activeTrackId = MutableStateFlow("foundation")
    val activeTrackId: StateFlow<String> = _activeTrackId.asStateFlow()

    val allLessons: StateFlow<List<LessonEntity>> = repository.getAllLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTrackLessons: StateFlow<List<LessonEntity>> = combine(allLessons, _activeTrackId) { lessons, trackId ->
        lessons.filter { it.trackId == trackId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLesson = MutableStateFlow<LessonEntity?>(null)
    val selectedLesson: StateFlow<LessonEntity?> = _selectedLesson.asStateFlow()

    // Sandbox & Judge State
    private val _sandboxCode = MutableStateFlow("# Type Python code here\nprint(\"Hello, PyHacker!\")\n")
    val sandboxCode: StateFlow<String> = _sandboxCode.asStateFlow()

    private val _sandboxOutput = MutableStateFlow("")
    val sandboxOutput: StateFlow<String> = _sandboxOutput.asStateFlow()

    private val _judgeResult = MutableStateFlow<JudgeResult?>(null)
    val judgeResult: StateFlow<JudgeResult?> = _judgeResult.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    // AI Tutor & Chat State
    val aiMessages: StateFlow<List<AiMessageEntity>> = repository.getAiMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Saved Snippets
    val savedSnippets: StateFlow<List<CodeSnippetEntity>> = repository.getAllSnippets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Battle State
    val battleRecords: StateFlow<List<BattleRecordEntity>> = repository.getAllBattleRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBattleProblem = MutableStateFlow<AiBattleProblem?>(null)
    val selectedBattleProblem: StateFlow<AiBattleProblem?> = _selectedBattleProblem.asStateFlow()

    private val _battleResult = MutableStateFlow<JudgeResult?>(null)
    val battleResult: StateFlow<JudgeResult?> = _battleResult.asStateFlow()

    private val _isBattleJudging = MutableStateFlow(false)
    val isBattleJudging: StateFlow<Boolean> = _isBattleJudging.asStateFlow()

    // Tab Navigation
    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setActiveTrack(trackId: String) {
        _activeTrackId.value = trackId
    }

    fun selectLesson(lesson: LessonEntity?) {
        _selectedLesson.value = lesson
        if (lesson != null) {
            _sandboxCode.value = if (lesson.starterCode.isNotBlank()) lesson.starterCode else lesson.exampleCode
            _judgeResult.value = null
            _sandboxOutput.value = ""
        }
    }

    fun updateSandboxCode(code: String) {
        _sandboxCode.value = code
    }

    fun runSandboxCode() {
        viewModelScope.launch {
            _isExecuting.value = true
            val result = repository.executeSandbox(_sandboxCode.value)
            _judgeResult.value = result
            _sandboxOutput.value = result.outputLog
            _isExecuting.value = false
        }
    }

    fun submitJudgeForCurrentLesson() {
        val current = _selectedLesson.value ?: return
        viewModelScope.launch {
            _isExecuting.value = true
            val result = repository.executeSandbox(_sandboxCode.value, current.testInput)
            _judgeResult.value = result
            _sandboxOutput.value = result.outputLog

            val outputMatches = result.outputLog.trim() == current.testExpectedOutput.trim() ||
                    result.outputLog.trim().contains(current.testExpectedOutput.trim())

            if (result.isPassed && outputMatches) {
                repository.completeLesson(current.id, 40)
            }
            _isExecuting.value = false
        }
    }

    fun applyFixToSandbox(correctedCode: String) {
        _sandboxCode.value = correctedCode
    }

    fun sendChatMessage(userText: String, includeCurrentSandbox: Boolean = false) {
        if (userText.isBlank()) return
        val codeContext = if (includeCurrentSandbox) _sandboxCode.value else null
        viewModelScope.launch {
            _isAiThinking.value = true
            repository.sendUserMessageToAi(userText, codeContext)
            _isAiThinking.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun saveCurrentSnippet(title: String) {
        viewModelScope.launch {
            repository.saveSnippet(
                title = title.ifBlank { "Python Script" },
                code = _sandboxCode.value,
                track = _activeTrackId.value,
                output = _sandboxOutput.value
            )
        }
    }

    fun deleteSnippet(id: Long) {
        viewModelScope.launch {
            repository.deleteSnippet(id)
        }
    }

    fun loadSnippetToSandbox(snippet: CodeSnippetEntity) {
        _sandboxCode.value = snippet.code
        _sandboxOutput.value = snippet.lastOutput
        _activeTab.value = 1 // Switch to Sandbox Tab
    }

    fun selectBattleProblem(problem: AiBattleProblem) {
        _selectedBattleProblem.value = problem
        _sandboxCode.value = problem.starterCode
        _battleResult.value = null
    }

    fun submitBattleSolution(code: String) {
        val problem = _selectedBattleProblem.value ?: return
        viewModelScope.launch {
            _isBattleJudging.value = true
            val startTime = System.currentTimeMillis()
            val result = repository.executeSandbox(code)
            val elapsedMs = (System.currentTimeMillis() - startTime).coerceAtLeast(8)
            _battleResult.value = result

            val isCorrect = result.isPassed && !result.outputLog.contains("Error")
            val userScore = if (isCorrect) 95 else 30
            val winner = if (isCorrect) "USER" else "AI"

            repository.recordBattleResult(
                problemId = problem.id,
                problemTitle = problem.title,
                userScore = userScore,
                aiScore = problem.aiScoreTarget,
                winner = winner,
                complexity = result.timeComplexity,
                space = result.spaceComplexity,
                execTimeMs = elapsedMs
            )
            _isBattleJudging.value = false
        }
    }
}
