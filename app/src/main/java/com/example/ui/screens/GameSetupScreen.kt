package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CategoryEntity
import com.example.data.models.GameMode
import com.example.data.models.WordDifficulty
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun GameSetupScreen(
    selectedMode: GameMode,
    categories: List<CategoryEntity>,
    selectedCategories: List<String>,
    selectedDifficulty: WordDifficulty,
    onToggleCategory: (String) -> Unit,
    onSelectDifficulty: (WordDifficulty) -> Unit,
    onStartGame: (team1: String, team2: String, rounds: Int, duration: Int) -> Unit,
    onBackClick: () -> Unit
) {
    var team1Name by remember { mutableStateOf("تیم ستاره‌ها 🌟") }
    var team2Name by remember { mutableStateOf("تیم قهرمانان 🏆") }
    var roundsCount by remember { mutableStateOf(3) }
    var timerSeconds by remember { mutableStateOf(90) }

    Scaffold(
        containerColor = MimePurpleDark,
        topBar = {
            Surface(color = Color(0xFF160A2A)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "تنظیمات بازی ${selectedMode.titleFa}",
                        color = MimePartyYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Team Names Card
            item {
                GlassCard(
                    borderColor = MimePartyTurquoise.copy(alpha = 0.6f),
                    gradientColors = listOf(
                        Color(0x3500838F),
                        Color(0x18006064)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "نام تیم‌ها 👥",
                            color = MimePartyYellow,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        OutlinedTextField(
                            value = team1Name,
                            onValueChange = { team1Name = it },
                            label = { Text("نام تیم اول", color = Color.White.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MimePartyTurquoise,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = team2Name,
                            onValueChange = { team2Name = it },
                            label = { Text("نام تیم دوم", color = Color.White.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MimePartyTurquoise,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }
            }

            // Game Options (Rounds & Timer)
            item {
                GlassCard(
                    borderColor = MimePartyYellow.copy(alpha = 0.6f),
                    gradientColors = listOf(
                        Color(0x33FFCA28),
                        Color(0x18FF7043)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "زمان و تعداد راندها ⏱️",
                            color = MimePartyYellow,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        // Timer Selection
                        Text(text = "زمان اجرا برای هر نوبت:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TimerOptionChip("۶۰ ثانیه", timerSeconds == 60) { timerSeconds = 60 }
                            TimerOptionChip("۹۰ ثانیه", timerSeconds == 90) { timerSeconds = 90 }
                            TimerOptionChip("۱۲۰ ثانیه", timerSeconds == 120) { timerSeconds = 120 }
                        }

                        // Rounds Selection
                        Text(text = "تعداد راندهای بازی:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TimerOptionChip("۲ راند", roundsCount == 2) { roundsCount = 2 }
                            TimerOptionChip("۳ راند", roundsCount == 3) { roundsCount = 3 }
                            TimerOptionChip("۵ راند", roundsCount == 5) { roundsCount = 5 }
                        }
                    }
                }
            }

            // Difficulty Selection
            item {
                GlassCard(
                    borderColor = MimePartyOrange.copy(alpha = 0.6f),
                    gradientColors = listOf(
                        Color(0x33FF7043),
                        Color(0x18E64A19)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "درجه سختی کلمات 🌶️",
                            color = MimePartyOrange,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WordDifficulty.values().forEach { diff ->
                                DifficultyChip(
                                    difficulty = diff,
                                    isSelected = selectedDifficulty == diff,
                                    onClick = { onSelectDifficulty(diff) }
                                )
                            }
                        }
                    }
                }
            }

            // Category Selection
            item {
                Text(
                    text = "دسته‌بندی موضوعات پانتومیم 🎯",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        val isSelected = selectedCategories.contains(category.id)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleCategory(category.id) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0x554A148C) else Color(0x22FFFFFF),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) MimePartyYellow else Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MimePartyYellow else Color.Transparent)
                                            .border(2.dp, MimePartyYellow, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MimeTextDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = category.titleFa,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${category.itemCount} کلمه",
                                    color = MimePartyYellowLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Start Game Button
            item {
                Button(
                    onClick = { onStartGame(team1Name, team2Name, roundsCount, timerSeconds) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MimePartyYellow, contentColor = MimeTextDark)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(28.dp))
                        Text(text = "شروع پانتومیم هیجان‌انگیز", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TimerOptionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MimePartyYellow else Color(0x33FFFFFF),
        border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            color = if (isSelected) MimeTextDark else Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DifficultyChip(
    difficulty: WordDifficulty,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(android.graphics.Color.parseColor(difficulty.colorHex)) else Color(0x33FFFFFF),
        border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            text = difficulty.titleFa,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

