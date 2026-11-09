package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GameMode(val titleFa: String, val descriptionFa: String, val iconName: String) {
    CLASSIC("کلاسیک", "پانتومیم سنتی - بازی نوبتی، حدس تیم و محاسبه امتیاز", "theater_comedy"),
    SPEED_CHALLENGE("چالش سرعت", "حداکثر حدس درست در زمان محدود با کامبو امتیاز!", "bolt"),
    TEAM_BATTLE("نبرد تیم‌ها", "رقابت ۲ یا چند تیم با کل‌کل و کل‌کل پانتومیم!", "groups"),
    AI_CHALLENGE("چالش هوش مصنوعی", "تولید کلمات و سناریوهای هوشمند توسط AI", "psychology"),
    FAMILY("خانوادگی", "مناسب تمامی سنین، کودکان و بزرگسالان صمیمی", "family_restroom"),
    PARTY_EXTREME("پارتی اکستریم", "مخصوص جمع‌های دوستانه، سناریوهای خنده‌دار و سخت", "celebration")
}

enum class WordDifficulty(val titleFa: String, val points: Int, val colorHex: String) {
    EASY("ساده", 10, "#4CAF50"),
    MEDIUM("متوسط", 20, "#FF9800"),
    HARD("سخت", 35, "#F44336"),
    EXTREME("فوق سخت", 50, "#9C27B0")
}

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val category: String,
    val difficulty: String, // WordDifficulty.name
    val points: Int,
    val hint: String = "",
    val ageGroup: String = "ALL", // KIDS, TEENS, ADULTS, ALL
    val usageCount: Int = 0,
    val isCustomAi: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val titleFa: String,
    val iconName: String,
    val colorHex: String,
    val itemCount: Int = 0
)

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val team1Name: String,
    val team2Name: String,
    val team1Score: Int,
    val team2Score: Int,
    val winnerTeamName: String,
    val durationSeconds: Int,
    val dateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "استاد پانتومیم",
    val avatarId: String = "mimeo_1",
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val streak: Int = 1,
    val totalGamesPlayed: Int = 0,
    val totalCorrectGuesses: Int = 0
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val titleFa: String,
    val descriptionFa: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val rewardXp: Int = 50
)

@Entity(tableName = "community_words")
data class CommunityWordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val category: String,
    val creatorName: String,
    val votes: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
