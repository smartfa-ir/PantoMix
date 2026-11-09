package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiGameRecommendation
import com.example.ui.components.GlassCard
import com.example.ui.components.MimeoExpression
import com.example.ui.components.MimeoMascotView
import com.example.ui.theme.*

@Composable
fun AiMasterScreen(
    isAiLoading: Boolean,
    aiRecommendation: AiGameRecommendation?,
    onRequestRecommendation: (playerCount: Int, ageRange: String, groupType: String, minutes: Int, mood: String) -> Unit,
    onGenerateCustomTopic: (topic: String) -> Unit,
    onStartRecommendedGame: () -> Unit,
    onBackClick: () -> Unit
) {
    var playerCount by remember { mutableStateOf(4) }
    var selectedAgeRange by remember { mutableStateOf("۱۸ تا ۳۵ سال") }
    var selectedGroupType by remember { mutableStateOf("FRIENDS") } // FRIENDS, FAMILY, WORK, KIDS
    var availableMinutes by remember { mutableStateOf(30) }
    var selectedMood by remember { mutableStateOf("FUNNY") } // FUNNY, COMPETITIVE, ENERGETIC, CHILL
    var customTopicText by remember { mutableStateOf("") }

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
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "دستیار هوشمند AI Game Master 🤖",
                        color = MimePartyTurquoiseLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Icon",
                        tint = MimePartyYellow
                    )
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

            // Mascot MIMEO AI introduction
            item {
                MimeoMascotView(
                    expression = if (isAiLoading) MimeoExpression.THINKING else MimeoExpression.HAPPY,
                    dialogueText = if (isAiLoading) "میمو داره با هوش مصنوعی بهترین بازی امشب رو براتون می‌چینه..." else "اطلاعات جمع دورهمی‌تون رو بهم بده تا بهترین بازی پانتومیم رو براتون طراح کنم!",
                    size = 100.dp
                )
            }

            // AI Setup Card
            item {
                GlassCard(
                    borderColor = MimePartyTurquoise.copy(alpha = 0.7f),
                    gradientColors = listOf(
                        Color(0x3500838F),
                        Color(0x18006064)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "۱. مشخصات جمع شما 👥",
                            color = MimePartyYellow,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        // Player Count Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "تعداد بازیکنان:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$playerCount نفر", color = MimePartyTurquoiseLight, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Slider(
                                value = playerCount.toFloat(),
                                onValueChange = { playerCount = it.toInt() },
                                valueRange = 2f..16f,
                                steps = 13,
                                colors = SliderDefaults.colors(
                                    thumbColor = MimePartyYellow,
                                    activeTrackColor = MimePartyTurquoise
                                )
                            )
                        }

                        // Group Type Selection
                        Text(text = "نوع جمع دورهمی:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GroupChip("دوستانه 🥳", selectedGroupType == "FRIENDS") { selectedGroupType = "FRIENDS" }
                            GroupChip("خانوادگی 👨‍👩‍👧‍👦", selectedGroupType == "FAMILY") { selectedGroupType = "FAMILY" }
                            GroupChip("همکاران 💼", selectedGroupType == "WORK") { selectedGroupType = "WORK" }
                            GroupChip("کودکان 🎈", selectedGroupType == "KIDS") { selectedGroupType = "KIDS" }
                        }

                        // Mood Selection
                        Text(text = "فضای مدنظر جمع:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GroupChip("خنده‌دار و شاد 😂", selectedMood == "FUNNY") { selectedMood = "FUNNY" }
                            GroupChip("رقابتی و جدی 🔥", selectedMood == "COMPETITIVE") { selectedMood = "COMPETITIVE" }
                            GroupChip("پرانرژی ⚡", selectedMood == "ENERGETIC") { selectedMood = "ENERGETIC" }
                        }

                        // Analyze Button
                        Button(
                            onClick = {
                                onRequestRecommendation(playerCount, selectedAgeRange, selectedGroupType, availableMinutes, selectedMood)
                            },
                            enabled = !isAiLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MimePartyTurquoise, contentColor = MimeTextDark)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MimeTextDark)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = "AI")
                                    Text(text = "تحلیل هوشمند و پیشنهاد بازی", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }

            // AI Recommendation Output Box (If available)
            if (aiRecommendation != null) {
                item {
                    GlassCard(
                        borderColor = MimePartyYellow,
                        gradientColors = listOf(
                            Color(0x44FFCA28),
                            Color(0x22FF7043)
                        )
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Stars, contentDescription = "Star", tint = MimePartyYellow)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "پیشنهاد اختصاصی میمو:",
                                    color = MimePartyYellow,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Text(
                                text = aiRecommendation.mimeoGreeting,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "حالت پیشنهادی:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Text(text = aiRecommendation.recommendedModeTitle, color = MimePartyYellowLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "تعداد راندها:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Text(text = "${aiRecommendation.targetRounds} راند (${aiRecommendation.roundTimerSeconds} ثانیه)", color = Color.White, fontSize = 13.sp)
                            }

                            Text(
                                text = "💡 نکته میمو: ${aiRecommendation.funTip}",
                                color = MimePartyYellow,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = onStartRecommendedGame,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MimePartyYellow, contentColor = MimeTextDark)
                            ) {
                                Text(text = "شروع بازی با این تنظیمات AI", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // Custom Topic Generation Box
            item {
                GlassCard(
                    borderColor = MimePartyPink.copy(alpha = 0.6f),
                    gradientColors = listOf(
                        Color(0x35880E4F),
                        Color(0x184A148C)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "۲. ساخت کلمات پانتومیم با موضوع دلخواه شما ✍️",
                            color = MimePartyPink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "موضوع مورد نظر خودتون رو بنویسید تا هوش مصنوعی کلمات پانتومیم اختصاصی بسازه:",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )

                        OutlinedTextField(
                            value = customTopicText,
                            onValueChange = { customTopicText = it },
                            placeholder = { Text("مثلاً: فیلم‌های طنز ایرانی، نوستالژی دهه ۷۰", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MimePartyPink,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = { onGenerateCustomTopic(customTopicText) },
                            enabled = customTopicText.isNotBlank() && !isAiLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MimePartyPink, contentColor = Color.White)
                        ) {
                            Text(text = "تولید کلمات اختصاصی توسط AI", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun GroupChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MimePartyTurquoise else Color(0x33FFFFFF),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            color = if (isSelected) MimeTextDark else Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

