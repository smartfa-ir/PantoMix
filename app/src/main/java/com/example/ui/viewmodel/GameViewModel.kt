package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiGameRecommendation
import com.example.data.local.AppDatabase
import com.example.data.models.*
import com.example.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenState {
    SPLASH, HOME, AI_MASTER, GAME_SETUP, GAMEPLAY, RESULTS, PROFILE, COMMUNITY
}

data class GameUiState(
    val currentScreen: ScreenState = ScreenState.HOME,
    val selectedMode: GameMode = GameMode.CLASSIC,
    val team1Name: String = "تیم آبی",
    val team2Name: String = "تیم قرمز",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val activeTeamIndex: Int = 0, // 0 for team1, 1 for team2
    val currentRound: Int = 1,
    val totalRounds: Int = 3,
    val roundDurationSeconds: Int = 90,
    val remainingTimerSeconds: Int = 90,
    val isTimerRunning: Boolean = false,
    val isCardRevealed: Boolean = false,
    val wordsQueue: List<WordEntity> = emptyList(),
    val currentWordIndex: Int = 0,
    val comboCount: Int = 0,
    val mimeoReactionText: String = "آماده‌ای؟ کارت رو لمس کن تا کلمه رو ببینی!",
    val selectedCategories: List<String> = emptyList(),
    val selectedDifficulty: WordDifficulty = WordDifficulty.MEDIUM,
    val aiRecommendation: AiGameRecommendation? = null,
    val isAiLoading: Boolean = false,
    val customAiTopic: String = "",
    val showHint: Boolean = false,
    val isPassingPhone: Boolean = false,
    val correctGuessesInRound: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val userProfile: StateFlow<UserProfileEntity?>
    val categories: StateFlow<List<CategoryEntity>>
    val achievements: StateFlow<List<AchievementEntity>>
    val communityWords: StateFlow<List<CommunityWordEntity>>
    val gameSessions: StateFlow<List<GameSessionEntity>>

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())

        userProfile = repository.userProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity()
        )
        categories = repository.allCategories.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        achievements = repository.achievements.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        communityWords = repository.communityWords.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        gameSessions = repository.gameSessions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    fun navigateTo(screen: ScreenState) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectGameMode(mode: GameMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun updateTeams(team1: String, team2: String, rounds: Int, durationSeconds: Int) {
        _uiState.update {
            it.copy(
                team1Name = team1,
                team2Name = team2,
                totalRounds = rounds,
                roundDurationSeconds = durationSeconds,
                remainingTimerSeconds = durationSeconds
            )
        }
    }

    fun chooseCategories(ids: List<String>) {
        _uiState.update { it.copy(selectedCategories = ids, customAiTopic = "") }
    }

    fun acceptPhone() {
        _uiState.update { it.copy(isPassingPhone = false) }
    }

    fun toggleCategorySelection(categoryId: String) {
        _uiState.update { state ->
            val list = state.selectedCategories.toMutableList()
            if (list.contains(categoryId)) {
                list.remove(categoryId)
            } else {
                list.add(categoryId)
            }
            state.copy(selectedCategories = list, customAiTopic = "")
        }
    }

    fun setDifficulty(difficulty: WordDifficulty) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun requestAiRecommendation(
        playerCount: Int,
        ageRange: String,
        groupType: String,
        availableMinutes: Int,
        mood: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val rec = repository.getAiGameRecommendation(
                playerCount, ageRange, groupType, availableMinutes, mood
            )
            _uiState.update { state ->
                state.copy(
                    isAiLoading = false,
                    aiRecommendation = rec,
                    selectedCategories = rec.suggestedCategoryIds,
                    selectedMode = if (rec.recommendedModeId == "SPEED_CHALLENGE") GameMode.SPEED_CHALLENGE else GameMode.CLASSIC,
                    customAiTopic = "",
                    roundDurationSeconds = rec.roundTimerSeconds,
                    totalRounds = rec.targetRounds
                )
            }
        }
    }

    fun generateAiTopicWords(topic: String) {
        if (topic.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val words = repository.generateCustomAiWords(topic, 8, _uiState.value.selectedDifficulty)
            _uiState.update { state ->
                state.copy(
                    isAiLoading = false,
                    wordsQueue = words,
                    currentWordIndex = 0,
                    customAiTopic = topic
                )
            }
            startGame(words)
        }
    }

    fun startGame(preparedWords: List<WordEntity>? = null) {
        viewModelScope.launch {
            val state = _uiState.value
            val words = preparedWords ?: if (state.customAiTopic.isNotBlank() && state.wordsQueue.isNotEmpty()) {
                state.wordsQueue.shuffled()
            } else {
                repository.getWordsForGame(state.selectedCategories, state.selectedDifficulty, 30)
            }
            val freshWords = words.filterNot { word ->
                state.wordsQueue.take(state.currentWordIndex + 1).any { it.text == word.text }
            }
            val deck = if (preparedWords == null && freshWords.isNotEmpty()) freshWords + (words - freshWords.toSet()) else words
            _uiState.update {
                it.copy(
                    currentScreen = ScreenState.GAMEPLAY,
                    wordsQueue = deck,
                    team1Name = it.team1Name.trim().ifBlank { "تیم آبی" },
                    team2Name = it.team2Name.trim().ifBlank { "تیم قرمز" },
                    isPassingPhone = false,
                    team1Score = 0,
                    team2Score = 0,
                    activeTeamIndex = 0,
                    currentRound = 1,
                    currentWordIndex = 0,
                    comboCount = 0,
                    isCardRevealed = false,
                    remainingTimerSeconds = if (it.selectedMode == GameMode.SPEED_CHALLENGE) 30 else it.roundDurationSeconds,
                    isTimerRunning = false,
                    showHint = false,
                    correctGuessesInRound = 0,
                    mimeoReactionText = "نوبت ${it.team1Name}! روی کارت کلیک کن و تایمر رو بزن!"
                )
            }
        }
    }

    fun toggleCardReveal() {
        _uiState.update { it.copy(isCardRevealed = !it.isCardRevealed) }
        if (_uiState.value.isCardRevealed && !_uiState.value.isTimerRunning) {
            startTimer()
        }
    }

    fun startTimer() {
        val state = _uiState.value
        if (state.isTimerRunning || state.isPassingPhone || !state.isCardRevealed ||
            state.wordsQueue.getOrNull(state.currentWordIndex) == null) return
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimerSeconds > 0 && _uiState.value.isTimerRunning) {
                delay(1000)
                _uiState.update { it.copy(remainingTimerSeconds = it.remainingTimerSeconds - 1) }
            }
            if (_uiState.value.remainingTimerSeconds <= 0) {
                onTurnTimerExpired()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun toggleHint() {
        _uiState.update { it.copy(showHint = !it.showHint) }
    }

    fun onCorrectGuess() {
        val currentWord = _uiState.value.wordsQueue.getOrNull(_uiState.value.currentWordIndex)
        if (!_uiState.value.isTimerRunning || !_uiState.value.isCardRevealed || currentWord == null) return
        val pts = currentWord.points
        val newCombo = _uiState.value.comboCount + 1
        val bonus = if (_uiState.value.selectedMode == GameMode.SPEED_CHALLENGE) newCombo * 2 else 0

        _uiState.update { state ->
            val updatedTeam1 = if (state.activeTeamIndex == 0) state.team1Score + pts + bonus else state.team1Score
            val updatedTeam2 = if (state.activeTeamIndex == 1) state.team2Score + pts + bonus else state.team2Score
            state.copy(
                team1Score = updatedTeam1,
                team2Score = updatedTeam2,
                comboCount = newCombo,
                correctGuessesInRound = state.correctGuessesInRound + 1,
                showHint = false
            )
        }


        advanceToNextWord()
    }

    fun onSkipWord() {
        if (!_uiState.value.isTimerRunning || !_uiState.value.isCardRevealed) return
        _uiState.update { state ->
            state.copy(
                comboCount = 0,
                showHint = false
            )
        }
        viewModelScope.launch {
            val reaction = com.example.ai.GeminiGameMaster.getMimeoReaction(
                false, 
                if (_uiState.value.activeTeamIndex == 0) _uiState.value.team1Score else _uiState.value.team2Score
            )
            _uiState.update { it.copy(mimeoReactionText = reaction) }
        }
        advanceToNextWord()
    }

    private fun advanceToNextWord() {
        _uiState.update { state ->
            val nextIndex = state.currentWordIndex + 1
            if (nextIndex < state.wordsQueue.size) {
                state.copy(currentWordIndex = nextIndex, isCardRevealed = false)
            } else {
                state.copy(wordsQueue = state.wordsQueue.shuffled(), currentWordIndex = 0, isCardRevealed = false)
            }
        }
    }

    private fun nextTurnWordIndex(state: GameUiState): Int {
        if (!state.isCardRevealed || state.wordsQueue.isEmpty()) return state.currentWordIndex
        return (state.currentWordIndex + 1) % state.wordsQueue.size
    }

    private fun onTurnTimerExpired() {
        pauseTimer()
        val isTeam1 = _uiState.value.activeTeamIndex == 0
        if (isTeam1) {
            // Switch to Team 2
            _uiState.update {
                it.copy(
                    activeTeamIndex = 1,
                    isPassingPhone = true,
                    comboCount = 0,
                    currentWordIndex = nextTurnWordIndex(it),
                    remainingTimerSeconds = if (it.selectedMode == GameMode.SPEED_CHALLENGE) 30 else it.roundDurationSeconds,
                    isCardRevealed = false,
                    showHint = false,
                    mimeoReactionText = "زمان تموم شد! نوبت ${it.team2Name} هست! کلمه رو باز کن!"
                )
            }
        } else {
            // Round Complete!
            val nextRound = _uiState.value.currentRound + 1
            if (nextRound > _uiState.value.totalRounds) {
                endGameAndShowResults()
            } else {
                _uiState.update {
                    it.copy(
                        currentRound = nextRound,
                        isPassingPhone = true,
                        comboCount = 0,
                        currentWordIndex = nextTurnWordIndex(it),
                        activeTeamIndex = 0,
                        remainingTimerSeconds = if (it.selectedMode == GameMode.SPEED_CHALLENGE) 30 else it.roundDurationSeconds,
                        isCardRevealed = false,
                        showHint = false,
                        mimeoReactionText = "راند $nextRound شروع شد! نوبت ${it.team1Name}!"
                    )
                }
            }
        }
    }

    fun endGameAndShowResults() {
        pauseTimer()
        val state = _uiState.value
        viewModelScope.launch {
            repository.saveGameSession(
                mode = state.selectedMode,
                team1Name = state.team1Name,
                team2Name = state.team2Name,
                team1Score = state.team1Score,
                team2Score = state.team2Score,
                durationSeconds = state.totalRounds * (if (state.selectedMode == GameMode.SPEED_CHALLENGE) 30 else state.roundDurationSeconds) * 2,
                correctGuessesCount = state.correctGuessesInRound
            )
            _uiState.update { it.copy(currentScreen = ScreenState.RESULTS) }
        }
    }

    fun submitCommunityWord(text: String, category: String, creatorName: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.submitCommunityWord(text, category, creatorName)
        }
    }

    fun upvoteCommunityWord(id: Long) {
        viewModelScope.launch {
            repository.upvoteCommunityWord(id)
        }
    }
}
