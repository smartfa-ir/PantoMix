package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.*
import com.example.ui.viewmodel.*

private val lilac = Color(0xFF6154C2)
private val tile = Color(0xFF4A3E9B)
private val ink = Color(0xFF201748)
private val yellow = Color(0xFFFFCF42)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MimeoGame(vm: GameViewModel) {
    val state by vm.uiState.collectAsState()
    val categories by vm.categories.collectAsState()
    val sessions by vm.gameSessions.collectAsState()
    val words by vm.communityWords.collectAsState()
    var settings by remember { mutableStateOf(false) }
    var topic by remember { mutableStateOf("") }
    var personal by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("FRIENDS") }
    var endConfirmation by remember { mutableStateOf(false) }
    val screen = state.currentScreen
    val home = { vm.navigateTo(ScreenState.HOME) }
    val team = if (state.activeTeamIndex == 0) state.team1Name else state.team2Name
    val word = state.wordsQueue.getOrNull(state.currentWordIndex)
    val active = state.isCardRevealed && state.isTimerRunning && word != null
    val back: () -> Unit = {
        when (screen) {
            ScreenState.GAMEPLAY -> { vm.pauseTimer(); endConfirmation = true }
            ScreenState.AI_MASTER -> vm.navigateTo(ScreenState.GAME_SETUP)
            else -> home()
        }
    }
    BackHandler(screen != ScreenState.HOME, back)
    Column(
        Modifier.fillMaxSize().background(lilac).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.mimeo_mark), null, Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)))
            Spacer(Modifier.width(10.dp))
            Text("میمو", fontWeight = FontWeight.Black, fontSize = 34.sp, modifier = Modifier.weight(1f))
            if (screen != ScreenState.HOME && screen != ScreenState.SPLASH) {
                TextButton(onClick = back) { Text("بازگشت", color = Color.White) }
            }
        }
        when (screen) {
            ScreenState.SPLASH, ScreenState.HOME -> {
                Portrait(230)
                Headline("نوبتِ پانتومیمه!")
                Primary("شروع بازی") { vm.navigateTo(ScreenState.GAME_SETUP) }
                Tile("دسته‌های کلمات", "انتخاب موضوع یا ساخت دسته") { vm.navigateTo(ScreenState.GAME_SETUP) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.weight(1f)) { Tile("کلمات من") { vm.navigateTo(ScreenState.COMMUNITY) } }
                    Box(Modifier.weight(1f)) { Tile("سابقه") { vm.navigateTo(ScreenState.PROFILE) } }
                }
            }
            ScreenState.GAME_SETUP -> {
                Headline("آمادهٔ بازی؟")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GameMode.CLASSIC to "کلاسیک", GameMode.SPEED_CHALLENGE to "سرعتی").forEach { (mode, title) ->
                        FilterChip(state.selectedMode == mode, { vm.selectGameMode(mode) }, { Text(title) })
                    }
                }
                Text(if (state.selectedMode == GameMode.SPEED_CHALLENGE) "۳۰ ثانیه برای هر تیم، امتیاز اضافه برای حدس‌های پیاپی" else "نوبتی بازی کنید و برای هر کلمه امتیاز بگیرید")
                Primary("شروع نوبت اول", !state.isAiLoading && (state.selectedCategories != listOf("personal") || words.any { it.category == "personal" })) { vm.startGame() }
                Headline("دسته‌ها")
                Tile("همهٔ موضوع‌ها", "کلمات تصادفی") { vm.chooseCategories(emptyList()) }
                if (state.selectedCategories.isEmpty()) Text("همهٔ موضوع‌ها انتخاب شده‌اند")
                (categories.map { it.id to it.titleFa } + listOf("family" to "خانوادگی", "personal" to "کلمات من"))
                    .chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            pair.forEach { (id, title) ->
                                Box(Modifier.weight(1f)) {
                                    val selected = id in state.selectedCategories
                                    Tile(if (selected) "✓  $title" else title) { vm.toggleCategorySelection(id) }
                                }
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                Field(topic, { topic = it }, "مثلاً فیلم‌های طنز ایرانی")
                Primary(if (state.isAiLoading) "در حال آماده‌سازی…" else "ساخت کلمه با این موضوع", topic.isNotBlank() && !state.isAiLoading) {
                    vm.generateAiTopicWords(topic)
                }
                Tile("تنظیمات بازی") { settings = !settings }
                if (settings) {
                    Field(state.team1Name, { vm.updateTeams(it, state.team2Name, state.totalRounds, state.roundDurationSeconds) }, "تیم اول")
                    Field(state.team2Name, { vm.updateTeams(state.team1Name, it, state.totalRounds, state.roundDurationSeconds) }, "تیم دوم")
                    Text("تعداد راند")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 3, 5).forEach { rounds ->
                            FilterChip(state.totalRounds == rounds, { vm.updateTeams(state.team1Name, state.team2Name, rounds, state.roundDurationSeconds) }, { Text("$rounds") })
                        }
                    }
                    if (state.selectedMode != GameMode.SPEED_CHALLENGE) {
                        Text("زمان هر نوبت")
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(45, 60, 90, 120).forEach { seconds ->
                                FilterChip(state.roundDurationSeconds == seconds, { vm.updateTeams(state.team1Name, state.team2Name, state.totalRounds, seconds) }, { Text("$seconds ثانیه") })
                            }
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WordDifficulty.entries.forEach { difficulty ->
                            FilterChip(state.selectedDifficulty == difficulty, { vm.setDifficulty(difficulty) }, { Text(difficulty.titleFa) })
                        }
                    }
                    Tile("پیشنهاد هوشمند بازی") { vm.navigateTo(ScreenState.AI_MASTER) }
                }
            }
            ScreenState.AI_MASTER -> {
                Headline("پیشنهاد میمو")
                Text("جمع شما چه جور دورهمی‌ای است؟")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("FRIENDS" to "دوستانه", "FAMILY" to "خانوادگی", "KIDS" to "کودکان", "WORK" to "همکاران").forEach { (id, label) ->
                        FilterChip(audience == id, { audience = id }, { Text(label) })
                    }
                }
                Primary(if (state.isAiLoading) "در حال بررسی…" else "پیشنهاد تنظیمات", !state.isAiLoading) {
                    vm.requestAiRecommendation(4, "ALL", audience, 15, "FUNNY")
                }
                state.aiRecommendation?.let { rec ->
                    Tile(rec.recommendedModeTitle, "${rec.targetRounds} راند · ${rec.roundTimerSeconds} ثانیه")
                    Text(rec.funTip)
                    Primary("برگشت به تنظیمات بازی") { vm.navigateTo(ScreenState.GAME_SETUP) }
                }
            }
            ScreenState.GAMEPLAY -> {
                Tile("${state.team1Name}  ${state.team1Score}   |   ${state.team2Name}  ${state.team2Score}", "راند ${state.currentRound} از ${state.totalRounds}")
                Portrait(84)
                Headline("نوبت $team")
                Text("${state.remainingTimerSeconds / 60}:${(state.remainingTimerSeconds % 60).toString().padStart(2, '0')}", fontSize = 52.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
                if (state.isPassingPhone) {
                    Tile("گوشی رو بده به $team")
                    Primary("آماده‌ایم") { vm.acceptPhone() }
                } else {
                    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = tile) {
                        Box(Modifier.heightIn(min = 120.dp).padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(if (state.isCardRevealed) word?.text.orEmpty() else "کارت آماده است",
                                color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    }
                    if (word == null) Text("هنوز کلمه‌ای آماده نیست. از تنظیمات دوباره شروع کن.")
                    if (!state.isCardRevealed) Primary("نمایش کلمه", word != null) { vm.toggleCardReveal() }
                    if (state.isCardRevealed && !state.isTimerRunning) Primary("ادامهٔ نوبت") { vm.startTimer() }
                    if (active) {
                        Primary("درست بود") { vm.onCorrectGuess() }
                        Tile("رد کردن") { vm.onSkipWord() }
                        if (!word?.hint.isNullOrBlank()) Tile(if (state.showHint) word?.hint.orEmpty() else "راهنما") { vm.toggleHint() }
                    }
                }
            }
            ScreenState.RESULTS -> {
                Portrait(160)
                Headline(when {
                    state.team1Score > state.team2Score -> "${state.team1Name} برنده شد!"
                    state.team2Score > state.team1Score -> "${state.team2Name} برنده شد!"
                    else -> "بازی مساوی شد"
                })
                Tile("${state.team1Score}  —  ${state.team2Score}", "${state.team1Name} و ${state.team2Name}")
                Primary("دوباره با همین تیم‌ها") { vm.startGame() }
                Tile("خانه", onClick = home)
            }
            ScreenState.PROFILE -> {
                Headline("سابقهٔ بازی")
                if (sessions.isEmpty()) Text("هنوز بازی ثبت نشده.")
                sessions.forEach { session ->
                    Tile("${session.team1Name} ${session.team1Score}  —  ${session.team2Score} ${session.team2Name}", session.winnerTeamName)
                }
            }
            ScreenState.COMMUNITY -> {
                Headline("کلمات من")
                Field(personal, { personal = it }, "یک کلمه یا عبارت")
                Primary("افزودن", personal.isNotBlank()) {
                    vm.submitCommunityWord(personal.trim(), "personal", "")
                    personal = ""
                }
                words.filter { it.category == "personal" }.forEach { Tile(it.text) }
                Primary("بازی با کلمات من", words.any { it.category == "personal" }) {
                    vm.chooseCategories(listOf("personal"))
                    vm.navigateTo(ScreenState.GAME_SETUP)
                }
            }
        }
    }
    if (endConfirmation) {
        AlertDialog(
            onDismissRequest = { endConfirmation = false },
            title = { Text("بازی تمام شود؟") },
            confirmButton = { TextButton(onClick = { endConfirmation = false; vm.endGameAndShowResults() }) { Text("نمایش نتیجه") } },
            dismissButton = { TextButton(onClick = { endConfirmation = false }) { Text("برگشت به بازی") } }
        )
    }
}

@Composable
private fun Portrait(height: Int) {
    Image(
        painterResource(R.drawable.mimeo_portrait), "میمو",
        Modifier.fillMaxWidth().height(height.dp).clip(RoundedCornerShape(24.dp)),
        contentScale = if (height < 100) ContentScale.Fit else ContentScale.Crop
    )
}

@Composable
private fun Headline(text: String) {
    Text(text, Modifier.fillMaxWidth(), fontSize = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable
private fun Primary(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 54.dp), enabled = enabled,
        shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = yellow, contentColor = ink)) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Tile(title: String, subtitle: String? = null, onClick: (() -> Unit)? = null) {
    val shape = RoundedCornerShape(17.dp)
    val base = Modifier.fillMaxWidth().clip(shape).background(tile)
    Column((if (onClick == null) base else base.clickable(onClick = onClick)).padding(15.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        subtitle?.let { Text(it, color = Color(0xFFF1ECFF), fontSize = 13.sp) }
    }
}

@Composable
private fun Field(value: String, change: (String) -> Unit, label: String) {
    OutlinedTextField(value, change, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true)
}
