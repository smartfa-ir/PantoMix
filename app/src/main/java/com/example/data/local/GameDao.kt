package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // Words
    @Query("SELECT * FROM words WHERE category = :category ORDER BY RANDOM()")
    fun getWordsByCategory(category: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getWordsByDifficulty(difficulty: String, limit: Int): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    // Categories
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    // Sessions
    @Query("SELECT * FROM game_sessions ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserProfile(profile: UserProfileEntity)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1 WHERE id = :id")
    suspend fun unlockAchievement(id: String)

    // Community Words
    @Query("SELECT * FROM community_words ORDER BY votes DESC")
    fun getCommunityWords(): Flow<List<CommunityWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityWord(word: CommunityWordEntity)

    @Query("UPDATE community_words SET votes = votes + 1 WHERE id = :id")
    suspend fun upvoteCommunityWord(id: Long)
}
