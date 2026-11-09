package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WordEntity::class,
        CategoryEntity::class,
        GameSessionEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        CommunityWordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mime_master_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database.gameDao())
                    }
                }
            }
        }

        private suspend fun seedDatabase(dao: GameDao) {
            // Default Profile
            dao.updateUserProfile(UserProfileEntity())

            // Default Categories
            val categories = listOf(
                CategoryEntity("cinema", "سینما و تلویزیون", "movie", "#9C27B0", 25),
                CategoryEntity("proverbs", "ضرب‌المثل‌های فارسی", "format_quote", "#FF9800", 20),
                CategoryEntity("jobs", "مشاغل و حرفه‌ها", "work", "#2196F3", 20),
                CategoryEntity("funny", "موقعیت‌های خنده‌دار", "sentiment_very_satisfied", "#E91E63", 20),
                CategoryEntity("animals", "حیوانات و طبیعت", "pets", "#4CAF50", 20),
                CategoryEntity("objects", "اشیاء و ابزارها", "build", "#795548", 20)
            )
            dao.insertCategories(categories)

            // Seed Words in Persian
            val seedWords = listOf(
                // Cinema
                WordEntity(text = "پایتخت ۶ و نقی معمولی", category = "cinema", difficulty = "MEDIUM", points = 20, hint = "سریال معروف نوروزی"),
                WordEntity(text = "درباره الی و شهاب حسینی", category = "cinema", difficulty = "HARD", points = 35, hint = "فیلم اصغر فرهادی"),
                WordEntity(text = "مرد دو هزار چهره", category = "cinema", difficulty = "EASY", points = 10, hint = "مهران مدیری"),
                WordEntity(text = "اخراجی‌ها ۱", category = "cinema", difficulty = "EASY", points = 10, hint = "فیلم دفاع مقدس کمدی"),
                WordEntity(text = "مارمولک و رضا مارمولک", category = "cinema", difficulty = "MEDIUM", points = 20, hint = "پیمان قاسم‌خانی"),
                
                // Proverbs
                WordEntity(text = "دیگ به دیگ میگه رویت سیاه", category = "proverbs", difficulty = "MEDIUM", points = 20, hint = "اشاره به عیب‌جویی"),
                WordEntity(text = "شاهنامه‌خوانی آخرش خوش است", category = "proverbs", difficulty = "HARD", points = 35, hint = "پایان کار مهم است"),
                WordEntity(text = "آب که از سر گذشت چه یک وجب چه صد وجب", category = "proverbs", difficulty = "MEDIUM", points = 20, hint = "بی‌تفاوتی بعد از خطای بزرگ"),
                WordEntity(text = "مرغ همسایه غازه", category = "proverbs", difficulty = "EASY", points = 10, hint = "ارزش دادن به داشته‌های دیگران"),

                // Jobs
                WordEntity(text = "جراح مغز و اعصاب در حال عمل", category = "jobs", difficulty = "HARD", points = 35, hint = "پزشکی بسیار دقیق"),
                WordEntity(text = "راننده تاکسی اینترنتی عصبانی", category = "jobs", difficulty = "EASY", points = 10, hint = "شغل روزمره شهری"),
                WordEntity(text = "آتش‌نشان در حال خاموش کردن آتش", category = "jobs", difficulty = "EASY", points = 10, hint = "فرشته نجات کلاه قرمز"),
                WordEntity(text = "باریستا در حال درست کردن اسپرسو", category = "jobs", difficulty = "MEDIUM", points = 20, hint = "کافه و قهوه"),

                // Funny
                WordEntity(text = "کسی که چای داغ خورده و زبانش سوخته", category = "funny", difficulty = "EASY", points = 10, hint = "واکنش به داغی شدید"),
                WordEntity(text = "راه رفتن روی جاده برفی یخ زده", category = "funny", difficulty = "MEDIUM", points = 20, hint = "تلاش برای سر نخوردن"),
                WordEntity(text = "گیر افتادن در آسانسور شلوغ", category = "funny", difficulty = "HARD", points = 35, hint = "کمبود فضا و گرما"),
                WordEntity(text = "تلاش برای باز کردن درب نوشابه گازدار", category = "funny", difficulty = "EASY", points = 10, hint = "ریختن نوشابه روی لباس"),

                // Animals
                WordEntity(text = "پنگوئن خوشحال روی یخ", category = "animals", difficulty = "EASY", points = 10, hint = "راه رفتن بانمک قطبی"),
                WordEntity(text = "کانگورو با بچه‌اش در کیسه", category = "animals", difficulty = "MEDIUM", points = 20, hint = "پرش‌های بلند استرالیایی"),
                WordEntity(text = "طاووس در حال باز کردن پرها", category = "animals", difficulty = "MEDIUM", points = 20, hint = "زیباترین پرنده رنگارنگ"),

                // Objects
                WordEntity(text = "جاروبرقی قدیمی پر صدا", category = "objects", difficulty = "EASY", points = 10, hint = "وسیله نظافت خانه"),
                WordEntity(text = "گوشی تلفن همراه در حال زنگ خوردن", category = "objects", difficulty = "EASY", points = 10, hint = "ویبره روی میز"),
                WordEntity(text = "چتر باز شده در باد شدید", category = "objects", difficulty = "MEDIUM", points = 20, hint = "وارونه شدن چتر")
            )
            dao.insertWords(seedWords)

            // Achievements
            val achievements = listOf(
                AchievementEntity("first_win", "اولین پیروزی", "برنده شدن در اولین بازی پانتومیم", "emoji_events", false, 50),
                AchievementEntity("speed_demon", "سلطان سرعت", "حدس ۵ کلمه در کمتر از ۶۰ ثانیه", "bolt", false, 100),
                AchievementEntity("master_mime", "استاد حرکات", "کسب ۱۰۰۰ امتیاز در بازی‌ها", "theater_comedy", false, 150),
                AchievementEntity("party_king", "سلطان مهمانی", "برگزاری ۱۰ بازی گروهی", "celebration", false, 200),
                AchievementEntity("ai_collaborator", "همکار هوش مصنوعی", "تولید کلمه توسط AI Game Master", "psychology", false, 75)
            )
            dao.insertAchievements(achievements)

            // Community words
            dao.insertCommunityWord(
                CommunityWordEntity(text = "تلاش برای زدن مگس با مگس‌کش", category = "funny", creatorName = "امیرحسین", votes = 12)
            )
            dao.insertCommunityWord(
                CommunityWordEntity(text = "گوش دادن به ویس ۵ دقیقه‌ای تلگرام", category = "funny", creatorName = "سارا", votes = 19)
            )
        }
    }
}
