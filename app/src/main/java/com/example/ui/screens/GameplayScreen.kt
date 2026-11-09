package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WordEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun GameplayScreen(
    team1Name: String,
    team2Name: String,
    team1Score: Int,
    team2Score: Int,
    activeTeamIndex: Int,
    currentRound: Int,
    totalRounds: Int,
    remainingSeconds: Int,
    totalSeconds: Int,
    isTimerRunning: Boolean,
    isCardRevealed: Boolean,
    currentWord: WordEntity?,
    comboCount: Int,
    mimeoReaction: String,
    showHint: Boolean,
    onToggleReveal: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onCorrectGuess: () -> Unit,
    onSkipWord: () -> Unit,
    onToggleHint: () -> Unit,
    onEndGameEarly: () -> Unit
) {
    val context = LocalContext.current
    var showPauseDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MimePurpleDark,
        topBar = {
            Surface(color = Color(0xFF160A2A)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showPauseDialog = true }) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "راند $currentRound از $totalRounds 🎮",
                            color = MimePartyYellow,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (activeTeamIndex == 0) "نوبت $team1Name" else "نوبت $team2Name",
                            color = MimePartyTurquoiseLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x33FFCA28),
                        border = BorderStroke(1.dp, MimePartyYellow.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "امتیاز: ${if (activeTeamIndex == 0) team1Score else team2Score}",
                            color = MimePartyYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Teams Scoreboard Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TeamScoreBox(
                    teamName = team1Name,
                    score = team1Score,
                    isActive = activeTeamIndex == 0
                )
                TeamScoreBox(
                    teamName = team2Name,
                    score = team2Score,
                    isActive = activeTeamIndex == 1
                )
            }

            // Large Word Card Container (Glassmorphism Game Card)
            GlassCard(
                onClick = onToggleReveal,
                borderColor = if (isCardRevealed) MimePartyYellow else MimePartyPink.copy(alpha = 0.5f),
                gradientColors = if (isCardRevealed) listOf(Color(0x554A148C), Color(0x33311B92)) else listOf(Color(0x332A1448), Color(0x11160A2A)),
                padding = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isCardRevealed) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MimePartyYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Reveal",
                                    tint = MimeTextDark,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "برای مشاهده کلمه لمس کنید 👈",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "تایمر به‌صورت خودکار شروع می‌شود",
                                color = MimePartyYellowLight,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MimePartyYellow.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, MimePartyYellow)
                                ) {
                                    Text(
                                        text = "+${currentWord?.points ?: 10} امتیاز 🎯",
                                        color = MimePartyYellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (comboCount > 1) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MimePartyPink
                                    ) {
                                        Text(
                                            text = "کامبو x$comboCount 🔥",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // The Word
                            Text(
                                text = currentWord?.text ?: "کلمه پانتومیم",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Hint Box
                            if (showHint && !currentWord?.hint.isNullOrBlank()) {
                                Text(
                                    text = "💡 راهنمایی: ${currentWord?.hint}",
                                    color = MimePartyYellow,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onToggleHint() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Hint",
                                        tint = MimePartyYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "مشاهده راهنمایی کلمه",
                                        color = MimePartyYellow,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Animated Timer Ring & MIMEO Mascot Reactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedTimerRing(
                    remainingSeconds = remainingSeconds,
                    totalSeconds = totalSeconds,
                    size = 110.dp
                )

                Box(modifier = Modifier.width(220.dp)) {
                    MimeoMascotView(
                        expression = if (remainingSeconds < 10) MimeoExpression.SURPRISED else MimeoExpression.HAPPY,
                        dialogueText = mimeoReaction,
                        size = 80.dp
                    )
                }
            }

            // Game Action Buttons (Correct & Skip)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Skip Button
                Button(
                    onClick = {
                        SoundFeedbackManager.vibrateSkip(context)
                        onSkipWord()
                    },
                    enabled = isCardRevealed,
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MimePartyRed, contentColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Skip")
                        Text(text = "رد کردن", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Success Button
                Button(
                    onClick = {
                        SoundFeedbackManager.vibrateSuccess(context)
                        onCorrectGuess()
                    },
                    enabled = isCardRevealed,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MimePartyGreen, contentColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Correct")
                        Text(text = "درست (کسب امتیاز)", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }

    // Pause Dialog
    if (showPauseDialog) {
        AlertDialog(
            onDismissRequest = { showPauseDialog = false },
            containerColor = Color(0xFF2A1448),
            title = {
                Text(text = "توقف بازی پانتومیم ⏸️", color = MimePartyYellow, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Text(
                    text = "آیا می‌خواهید بازی ادامه پیدا کنه یا بازی رو تموم کنید؟",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPauseDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MimePartyYellow, contentColor = MimeTextDark)
                ) {
                    Text("ادامه بازی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPauseDialog = false
                        onEndGameEarly()
                    }
                ) {
                    Text("خروج و دیدن نتایج", color = MimePartyRed, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun TeamScoreBox(
    teamName: String,
    score: Int,
    isActive: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) Color(0x554A148C) else Color(0x22160A2A),
        border = BorderStroke(
            if (isActive) 2.dp else 1.dp,
            if (isActive) MimePartyYellow else Color.White.copy(alpha = 0.2f)
        ),
        modifier = Modifier.width(155.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = teamName,
                color = if (isActive) MimePartyYellow else Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = "$score امتیاز",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

