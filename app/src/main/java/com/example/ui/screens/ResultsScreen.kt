package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.GlassCard
import com.example.ui.components.MimeoExpression
import com.example.ui.components.MimeoMascotView
import com.example.ui.theme.*

@Composable
fun ResultsScreen(
    team1Name: String,
    team2Name: String,
    team1Score: Int,
    team2Score: Int,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val winnerName = when {
        team1Score > team2Score -> team1Name
        team2Score > team1Score -> team2Name
        else -> "هر دو تیم (مساوی!) 🎉"
    }

    val earnedXp = (team1Score + team2Score) * 5 + 50
    val earnedCoins = (team1Score + team2Score) + 10

    Scaffold(
        containerColor = MimePurpleDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            // Winner Mascot Animation
            item {
                MimeoMascotView(
                    expression = MimeoExpression.CELEBRATING,
                    dialogueText = "تبریک میگم! بازی فوق‌العاده‌ای بود! $winnerName عالی درخشید!",
                    size = 120.dp
                )
            }

            // Winner Trophy Banner
            item {
                GlassCard(
                    borderColor = MimePartyYellow,
                    gradientColors = listOf(
                        Color(0x55FFCA28),
                        Color(0x33FF7043)
                    ),
                    padding = 20.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(MimePartyYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = MimeTextDark,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "👑 قهرمان مسابقه پانتومیم 🏆",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = winnerName,
                            color = MimePartyYellow,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Scores Comparison Table
            item {
                GlassCard(
                    borderColor = MimePartyTurquoise.copy(alpha = 0.5f),
                    gradientColors = listOf(
                        Color(0x3300838F),
                        Color(0x18006064)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "جدول نتایج نهایی 📊",
                            color = MimePartyTurquoiseLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        ScoreRow(teamName = team1Name, score = team1Score, isWinner = team1Score >= team2Score)
                        Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                        ScoreRow(teamName = team2Name, score = team2Score, isWinner = team2Score >= team1Score)
                    }
                }
            }

            // Earned Rewards (XP & Coins)
            item {
                GlassCard(
                    borderColor = MimePartyYellow.copy(alpha = 0.7f),
                    gradientColors = listOf(
                        Color(0x333F51B5),
                        Color(0x187E57C2)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RewardBadge(icon = Icons.Default.Star, value = "+$earnedXp XP", color = MimePartyYellow)
                        RewardBadge(icon = Icons.Default.MonetizationOn, value = "+$earnedCoins سکه", color = MimePartyOrange)
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = onPlayAgainClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MimePartyYellow, contentColor = MimeTextDark)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Replay, contentDescription = "Replay")
                            Text(text = "بازی مجدد", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Button(
                        onClick = onHomeClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF), contentColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                            Text(text = "صفحه اصلی", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun ScoreRow(teamName: String, score: Int, isWinner: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = teamName,
                color = if (isWinner) MimePartyYellow else Color.White,
                fontSize = 16.sp,
                fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.Medium
            )
            if (isWinner) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "👑", fontSize = 16.sp)
            }
        }
        Text(
            text = "$score امتیاز",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun RewardBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = value, tint = color, modifier = Modifier.size(26.dp))
        Text(text = value, color = color, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    }
}

