package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun LevelBadgeView(
    profile: UserProfileEntity?,
    modifier: Modifier = Modifier
) {
    val level = profile?.level ?: 1
    val xp = profile?.xp ?: 0
    val xpNext = level * 100
    val progress = (xp.toFloat() / xpNext.toFloat()).coerceIn(0f, 1f)
    val coins = profile?.coins ?: 150
    val streak = profile?.streak ?: 3

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = MimePartyYellow.copy(alpha = 0.5f),
        padding = 12.dp,
        gradientColors = listOf(
            Color(0x403A125E),
            Color(0x281B0933)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Level Badge & XP Progress
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MimePartyYellow, MimePartyOrange)
                            )
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LEVEL",
                            color = MimeTextDark,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$level",
                            color = MimeTextDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = profile?.name ?: "قهرمان پانتومیم",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = "Badge",
                            tint = MimePartyYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .width(90.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MimePartyYellow,
                            trackColor = Color(0x40FFFFFF)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$xp/$xpNext XP",
                            color = MimePartyYellowLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Streak & Coins Pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Streak Fire
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33FF7043),
                    border = BorderStroke(1.dp, MimePartyOrange.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = MimePartyOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$streak روز 🔥",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coins
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33FFCA28),
                    border = BorderStroke(1.dp, MimePartyYellow.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = MimePartyYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$coins 🪙",
                            color = MimePartyYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

