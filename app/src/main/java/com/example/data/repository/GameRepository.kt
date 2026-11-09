package com.example.data.repository

import com.example.ai.AiGameRecommendation
import com.example.ai.GeminiGameMaster
import com.example.data.local.GameDao
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val dao: GameDao) {

    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val achievements: Flow<List<AchievementEntity>> = dao.getAllAchievements()
    val gameSessions: Flow<List<GameSessionEntity>> = dao.getAllSessions()
    val communityWords: Flow<List<CommunityWordEntity>> = dao.getCommunityWords()

    suspend fun getWordsForGame(
        categoryIds: List<String>,
        difficulty: WordDifficulty? = null,
        count: Int = 10
    ): List<WordEntity> {
        val wordList = mutableListOf<WordEntity>()
        for (cat in categoryIds) {
            val words = when (cat) {
                "personal" -> dao.getCommunityWords().firstOrNull().orEmpty()
                    .filter { it.category == "personal" }
                    .map { WordEntity(text = it.text, category = cat, difficulty = (difficulty ?: WordDifficulty.MEDIUM).name, points = (difficulty ?: WordDifficulty.MEDIUM).points) }
                "family" -> listOf("animals", "objects").flatMap { dao.getWordsByCategory(it).firstOrNull().orEmpty() }
                else -> dao.getWordsByCategory(cat).firstOrNull().orEmpty()
            }
            wordList.addAll(words)
        }
        
        if (wordList.isEmpty()) {
            if (categoryIds == listOf("personal")) return emptyList()
            wordList.addAll(dao.getRandomWords(count))
        }

        var filtered = if (difficulty != null) {
            wordList.filter { it.difficulty == difficulty.name }
        } else wordList

        if (filtered.isEmpty()) filtered = wordList

        return filtered.shuffled().take(count)
    }

    suspend fun getAiGameRecommendation(
        playerCount: Int,
        ageRange: String,
        groupType: String,
        availableMinutes: Int,
        mood: String
    ): AiGameRecommendation {
        return GeminiGameMaster.analyzeGroupAndRecommend(
            playerCount = playerCount,
            ageRange = ageRange,
            groupType = groupType,
            availableMinutes = availableMinutes,
            mood = mood
        )
    }

    suspend fun generateCustomAiWords(
        topic: String,
        count: Int = 5,
        difficulty: WordDifficulty = WordDifficulty.MEDIUM
    ): List<WordEntity> {
        val words = GeminiGameMaster.generateCustomAiWords(topic, count, difficulty)
        dao.insertWords(words)
        return words
    }

    suspend fun saveGameSession(
        mode: GameMode,
        team1Name: String,
        team2Name: String,
        team1Score: Int,
        team2Score: Int,
        durationSeconds: Int,
        correctGuessesCount: Int
    ) {
        val winner = when {
            team1Score > team2Score -> team1Name
            team2Score > team1Score -> team2Name
            else -> "مساوی"
        }

        val session = GameSessionEntity(
            mode = mode.name,
            team1Name = team1Name,
            team2Name = team2Name,
            team1Score = team1Score,
            team2Score = team2Score,
            winnerTeamName = winner,
            durationSeconds = durationSeconds
        )
        dao.insertSession(session)

        // Award XP and Coins
        val earnedXp = (team1Score + team2Score) * 5 + 50
        val earnedCoins = (team1Score + team2Score) + 10
        addXpAndCoins(earnedXp, earnedCoins, correctGuessesCount)
    }

    suspend fun addXpAndCoins(earnedXp: Int, earnedCoins: Int, newGuesses: Int = 0) {
        val currentProfile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        var newXp = currentProfile.xp + earnedXp
        var newLevel = currentProfile.level
        var xpForNext = newLevel * 100

        while (newXp >= xpForNext) {
            newXp -= xpForNext
            newLevel++
            xpForNext = newLevel * 100
        }

        val updatedProfile = currentProfile.copy(
            level = newLevel,
            xp = newXp,
            coins = currentProfile.coins + earnedCoins,
            totalGamesPlayed = currentProfile.totalGamesPlayed + 1,
            totalCorrectGuesses = currentProfile.totalCorrectGuesses + newGuesses
        )
        dao.updateUserProfile(updatedProfile)

        checkAndUnlockAchievements(updatedProfile)
    }

    private suspend fun checkAndUnlockAchievements(profile: UserProfileEntity) {
        if (profile.totalGamesPlayed >= 1) dao.unlockAchievement("first_win")
        if (profile.totalGamesPlayed >= 10) dao.unlockAchievement("party_king")
        if (profile.xp >= 1000) dao.unlockAchievement("master_mime")
    }

    suspend fun submitCommunityWord(text: String, category: String, creatorName: String) {
        dao.insertCommunityWord(
            CommunityWordEntity(
                text = text,
                category = category,
                creatorName = creatorName
            )
        )
    }

    suspend fun upvoteCommunityWord(id: Long) {
        dao.upvoteCommunityWord(id)
    }
}
