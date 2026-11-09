package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.models.WordDifficulty
import com.example.data.models.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiGameRecommendation(
    val recommendedModeTitle: String,
    val recommendedModeId: String, // CLASSIC, SPEED_CHALLENGE, TEAM_BATTLE, AI_CHALLENGE, FAMILY, PARTY_EXTREME
    val mimeoGreeting: String,
    val suggestedCategoryIds: List<String>,
    val targetRounds: Int,
    val roundTimerSeconds: Int,
    val funTip: String
)

object GeminiGameMaster {

    private const val TAG = "GeminiGameMaster"
    private const val MODEL = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
    }

    /**
     * AI Game Master analyzes group metadata and recommends custom game configuration.
     */
    suspend fun analyzeGroupAndRecommend(
        playerCount: Int,
        ageRange: String,
        groupType: String, // "FRIENDS", "FAMILY", "WORK", "KIDS"
        availableMinutes: Int,
        mood: String // "ENERGETIC", "CHILL", "COMPETITIVE", "FUNNY"
    ): AiGameRecommendation = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext fallbackRecommendation(groupType, playerCount)
        }

        val prompt = """
            You are "MIMEO", the funny and energetic AI Game Master for "MIME MASTER AI" (Persian Pantomime / Charades Party Game).
            Analyze this party group setup:
            - Player Count: $playerCount
            - Age Range: $ageRange
            - Group Type: $groupType
            - Available Time: $availableMinutes minutes
            - Group Mood: $mood

            Return ONLY a valid JSON object without markdown fences with these exact keys:
            {
              "recommendedModeTitle": "عنوان فارسی مد پیشنهادی",
              "recommendedModeId": "TEAM_BATTLE", // choose one of: CLASSIC, SPEED_CHALLENGE, TEAM_BATTLE, AI_CHALLENGE, FAMILY, PARTY_EXTREME
              "mimeoGreeting": "پیام پرانرژی و خنده‌دار میمو به زبان فارسی به بازی‌کنندگان",
              "suggestedCategoryIds": ["cinema", "funny", "proverbs"],
              "targetRounds": 4,
              "roundTimerSeconds": 90,
              "funTip": "یک نکته طنز یا توصیه بازی به فارسی"
            }
        """.trimIndent()

        try {
            val jsonResponse = callGeminiApi(prompt, apiKey)
            if (jsonResponse != null) {
                val cleanJson = cleanJsonResponse(jsonResponse)
                val obj = JSONObject(cleanJson)
                val catArray = obj.optJSONArray("suggestedCategoryIds")
                val catList = mutableListOf<String>()
                if (catArray != null) {
                    for (i in 0 until catArray.length()) {
                        catList.add(catArray.getString(i))
                    }
                }
                return@withContext AiGameRecommendation(
                    recommendedModeTitle = obj.optString("recommendedModeTitle", "نبرد تیم‌های پانتومیم"),
                    recommendedModeId = obj.optString("recommendedModeId", "TEAM_BATTLE"),
                    mimeoGreeting = obj.optString("mimeoGreeting", "سلام به همه! میمو اینجاست تا بهترین بازی پانتومیم امشب رو براتون بچینه!"),
                    suggestedCategoryIds = if (catList.isNotEmpty()) catList else listOf("cinema", "funny"),
                    targetRounds = obj.optInt("targetRounds", 3),
                    roundTimerSeconds = obj.optInt("roundTimerSeconds", 90),
                    funTip = obj.optString("funTip", "قبل از شروع حتماً حرکات دستتون رو گرم کنید!")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI recommendation", e)
        }

        return@withContext fallbackRecommendation(groupType, playerCount)
    }

    /**
     * AI generates dynamic Persian words and scenarios with difficulty and hints.
     */
    suspend fun generateCustomAiWords(
        topic: String,
        count: Int = 5,
        difficulty: WordDifficulty = WordDifficulty.MEDIUM
    ): List<WordEntity> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext fallbackAiWords(topic, difficulty)
        }

        val prompt = """
            You are MIMEO, the AI Game Master for Persian Pantomime.
            Generate $count funny and creative Persian Charades / Pantomime words or situation phrases for topic "$topic" with difficulty "${difficulty.name}".
            Return ONLY a valid JSON array without markdown code blocks containing objects with format:
            [
              {
                "text": "عبارت یا کلمه فارسی پانتومیم",
                "category": "ai_generated",
                "difficulty": "${difficulty.name}",
                "points": ${difficulty.points},
                "hint": "راهنمایی کوتاه طنز برای بازیگر"
              }
            ]
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt, apiKey)
            if (responseText != null) {
                val cleanJson = cleanJsonResponse(responseText)
                val array = JSONArray(cleanJson)
                val resultList = mutableListOf<WordEntity>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    resultList.add(
                        WordEntity(
                            text = item.optString("text", "کلمه پانتومیم"),
                            category = "ai_generated",
                            difficulty = difficulty.name,
                            points = difficulty.points,
                            hint = item.optString("hint", "راهنمایی میمو"),
                            isCustomAi = true
                        )
                    )
                }
                if (resultList.isNotEmpty()) return@withContext resultList
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating custom AI words", e)
        }

        return@withContext fallbackAiWords(topic, difficulty)
    }

    /**
     * AI generates Mascot MIMEO commentary on win, loss, or combo!
     */
    suspend fun getMimeoReaction(
        isSuccess: Boolean,
        score: Int,
        combo: Int = 0
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext if (isSuccess) "عالی بود! میمو حسابی کیف کرد! 🎭🔥" else "فدای سرت! کلمه بعدی رو با قدرت بزن! 💪"
        }

        val prompt = """
            You are MIMEO, the mascot of MIME MASTER AI.
            Give a 1-sentence hilarious Persian encouraging or celebratory reaction for a player who just ${if (isSuccess) "GUESSED CORRECTLY" else "SKIPPED/FAILED"} (Score: $score, Combo: $combo).
            Keep it very short (max 12 words) and energetic. Output raw text only.
        """.trimIndent()

        try {
            val text = callGeminiApi(prompt, apiKey)
            if (!text.isNullOrBlank()) {
                return@withContext text.trim().replace("\"", "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in MIMEO reaction", e)
        }

        return@withContext if (isSuccess) "فوق‌العاده بود! امتیاز درخشان! 🎉" else "اشکالی نداره، بعدی رو طوفانی اجرا کن! ⭐"
    }

    private fun callGeminiApi(prompt: String, apiKey: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"
        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val bodyString = response.body?.string() ?: return null
            val root = JSONObject(bodyString)
            val candidates = root.optJSONArray("candidates") ?: return null
            val firstCandidate = candidates.optJSONObject(0) ?: return null
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            val firstPart = parts.optJSONObject(0) ?: return null
            return firstPart.optString("text")
        }
    }

    private fun cleanJsonResponse(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    private fun fallbackRecommendation(groupType: String, playerCount: Int): AiGameRecommendation {
        return when (groupType) {
            "FAMILY", "KIDS" -> AiGameRecommendation(
                recommendedModeTitle = "بازی خانوادگی صمیمی",
                recommendedModeId = "FAMILY",
                mimeoGreeting = "سلام به خانواده دوست‌داشتنی! میمو بهترین کلمات شاد و خانوادگی رو براتون اماده کرده!",
                suggestedCategoryIds = listOf("animals", "objects", "funny"),
                targetRounds = 3,
                roundTimerSeconds = 120,
                funTip = "بچه‌ها هم می‌تونن تو داوری و تشویق شرکت کنن!"
            )
            "WORK" -> AiGameRecommendation(
                recommendedModeTitle = "رقابت تیمی همکاران",
                recommendedModeId = "TEAM_BATTLE",
                mimeoGreeting = "وقتش رسیده یخ جلسه رو بشکنیم و همکاران رو تو پانتومیم به چالش بکشیم!",
                suggestedCategoryIds = listOf("jobs", "cinema", "proverbs"),
                targetRounds = 4,
                roundTimerSeconds = 90,
                funTip = "تیم برنده کاپ مجازی میمو رو می‌بره!"
            )
            else -> AiGameRecommendation(
                recommendedModeTitle = "نبرد پانتومیم دوستانه",
                recommendedModeId = "PARTY_EXTREME",
                mimeoGreeting = "سلام رفقا! میمو آماده‌ست تا امشب کل‌کل پانتومیم رو به اوج برسونه!",
                suggestedCategoryIds = listOf("cinema", "funny", "proverbs"),
                targetRounds = 4,
                roundTimerSeconds = 90,
                funTip = "کلمات سخت رو با خنده و حرکت اجرا کنید!"
            )
        }
    }

    private fun fallbackAiWords(topic: String, difficulty: WordDifficulty): List<WordEntity> {
        return listOf(
            WordEntity(
                text = "ربات هوش مصنوعی در حال رقصیدن ($topic)",
                category = "ai_generated",
                difficulty = difficulty.name,
                points = difficulty.points,
                hint = "حرکات مکانیکی دست و پا",
                isCustomAi = true
            ),
            WordEntity(
                text = "فضانورد گم شده روی کره مریخ",
                category = "ai_generated",
                difficulty = difficulty.name,
                points = difficulty.points,
                hint = "نگاه کردن به اطراف با کلاه ایمنی",
                isCustomAi = true
            ),
            WordEntity(
                text = "آشپز بین‌المللی در حال چشیدن غذای تند",
                category = "ai_generated",
                difficulty = difficulty.name,
                points = difficulty.points,
                hint = "آتش گرفتن زبان و فوت کردن",
                isCustomAi = true
            )
        )
    }
}
