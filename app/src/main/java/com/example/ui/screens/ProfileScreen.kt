package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AchievementEntity
import com.example.data.models.GameSessionEntity
import com.example.data.models.UserProfileEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.LevelBadgeView
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    profile: UserProfileEntity?,
    achievements: List<AchievementEntity>,
    sessions: List<GameSessionEntity>,
    onBackClick: () -> Unit
) {
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
                        text = "پروفایل و دستاوردهای شما 🏆",
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

            // Profile Header Card
            item {
                LevelBadgeView(profile = profile)
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "تعداد بازی‌ها",
                        value = "${profile?.totalGamesPlayed ?: 0}",
                        icon = Icons.Default.VideogameAsset,
                        iconColor = MimePartyTurquoise,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "حدس‌های درست",
                        value = "${profile?.totalCorrectGuesses ?: 0}",
                        icon = Icons.Default.TaskAlt,
                        iconColor = MimePartyGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Achievements Gallery Section
            item {
                Text(
                    text = "نشان‌ها و مدال‌های افتخار 🏅",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    achievements.forEach { achievement ->
                        GlassCard(
                            borderColor = if (achievement.isUnlocked) MimePartyYellow else Color.White.copy(alpha = 0.2f),
                            gradientColors = if (achievement.isUnlocked) listOf(Color(0x33FFCA28), Color(0x11FF7043)) else listOf(Color(0x22FFFFFF), Color(0x11FFFFFF))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (achievement.isUnlocked) MimePartyYellow else Color(0x33FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                        contentDescription = achievement.titleFa,
                                        tint = if (achievement.isUnlocked) MimeTextDark else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = achievement.titleFa,
                                        color = if (achievement.isUnlocked) MimePartyYellow else Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = achievement.descriptionFa,
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 12.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x3300838F)
                                ) {
                                    Text(
                                        text = "+${achievement.rewardXp} XP",
                                        color = MimePartyTurquoiseLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Games History Section
            item {
                Text(
                    text = "تاریخچه مسابقات اخیر 📜",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (sessions.isEmpty()) {
                item {
                    GlassCard {
                        Text(
                            text = "هنوز مسابقه‌ای ثبت نشده است. اولین بازی پانتومیم رو شروع کنید!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                items(sessions.take(5)) { session ->
                    GlassCard(
                        borderColor = MimePartyYellow.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${session.team1Name} (${session.team1Score}) vs ${session.team2Name} (${session.team2Score})",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "تیم برنده: ${session.winnerTeamName}",
                                    color = MimePartyYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        borderColor = iconColor.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(30.dp))
            Column {
                Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text(text = value, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

