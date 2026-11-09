package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.GameMode
import com.example.data.models.UserProfileEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.LevelBadgeView
import com.example.ui.components.MimeoExpression
import com.example.ui.components.MimeoMascotView
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: UserProfileEntity?,
    onQuickStartClick: () -> Unit,
    onModeSelect: (GameMode) -> Unit,
    onAiMasterClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var mascotDialogueIndex by remember { mutableStateOf(0) }
    val mimeoMessages = listOf(
        "من «میمو» هستم! آماده‌ای یک بازی خنده‌دار و پر انرژی شروع کنیم؟ 🥳",
        "روی من بزن تا بهت انرژی بدم! امشب کی قهرمان پانتومیم میشه؟ 🔥",
        "به دستیار AI سر زدی؟ می‌تونه بازی اختصاصی برات بچینه! 🧐",
        "چالش روزانه رو تموم کردی؟ ۱۰۰ XP جایزه منتظرته! 🏆"
    )

    Scaffold(
        containerColor = MimePurpleDark,
        bottomBar = {
            Surface(
                color = Color(0xFF160A2A),
                shadowElevation = 16.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        icon = Icons.Default.Home,
                        label = "خانه",
                        isSelected = true,
                        activeColor = MimePartyYellow,
                        onClick = {}
                    )
                    BottomNavItem(
                        icon = Icons.Default.AutoAwesome,
                        label = "AI Game Master",
                        isSelected = false,
                        activeColor = MimePartyTurquoise,
                        onClick = onAiMasterClick
                    )
                    BottomNavItem(
                        icon = Icons.Default.GroupAdd,
                        label = "کلمات انجمن",
                        isSelected = false,
                        activeColor = MimePartyPink,
                        onClick = onCommunityClick
                    )
                    BottomNavItem(
                        icon = Icons.Default.Person,
                        label = "پروفایل",
                        isSelected = false,
                        activeColor = MimePartyOrange,
                        onClick = onProfileClick
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Level Badge & Daily Retention Header
            item {
                LevelBadgeView(profile = profile)
            }

            // Hero Section: MIMEO Mascot & Interactive Voice Greeting
            item {
                GlassCard(
                    borderColor = MimePartyYellow.copy(alpha = 0.6f),
                    padding = 16.dp,
                    gradientColors = listOf(
                        Color(0x554A148C),
                        Color(0x33311B92),
                        Color(0x221A237E)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MimePartyYellow,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = "MIME MASTER AI 🎉",
                                    color = MimeTextDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "دورهمی پانتومیم نینتاندو!",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "لمس کن رو میمو تا جمله‌های بعدی رو بگه!",
                                color = MimePartyYellowLight,
                                fontSize = 12.sp
                            )
                        }

                        MimeoMascotView(
                            expression = MimeoExpression.HAPPY,
                            dialogueText = mimeoMessages[mascotDialogueIndex % mimeoMessages.size],
                            size = 110.dp,
                            onMascotClick = {
                                mascotDialogueIndex++
                            }
                        )
                    }
                }
            }

            // Quick Start CTA Button (Big Bouncy Party Play Button)
            item {
                Button(
                    onClick = onQuickStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MimePartyYellow,
                        contentColor = MimeTextDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MimeTextDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Quick Play",
                                tint = MimePartyYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "شروع سریع بازی (کلاسیک)",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // AI Game Master Major Feature Card
            item {
                GlassCard(
                    onClick = onAiMasterClick,
                    borderColor = MimePartyTurquoise.copy(alpha = 0.8f),
                    padding = 16.dp,
                    gradientColors = listOf(
                        Color(0x4000838F),
                        Color(0x25006064)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(MimePartyTurquoise, MimePartyTurquoiseLight)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Master",
                                tint = MimeTextDark,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "دستیار هوشمند AI Game Master",
                                    color = MimePartyTurquoiseLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MimePartyPink
                                ) {
                                    Text(
                                        text = "ویژه ✨",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "تحلیل تعداد نفرات، سن و جمع دورهمی برای پیشنهاد بهترین بازی",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 16.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Arrow",
                            tint = MimePartyTurquoiseLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Upgraded Game Modes Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حالت‌های هیجان‌انگیز بازی 🎮",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "۶ حالت فعال",
                        color = MimePartyYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(GameMode.values()) { mode ->
                        GameModeCard(
                            mode = mode,
                            onClick = { onModeSelect(mode) }
                        )
                    }
                }
            }

            // Daily Challenge & Retention Rewards Section
            item {
                GlassCard(
                    borderColor = MimePartyPink.copy(alpha = 0.6f),
                    gradientColors = listOf(
                        Color(0x40880E4F),
                        Color(0x224A148C)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MimePartyPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Daily Challenge",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "چالش روزانه پانتومیم 🎁",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "۵ کلمه حدس بزن + ۱۰۰ XP جایزه بگیر!",
                                        color = MimePartyPinkLight,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Button(
                                onClick = onQuickStartClick,
                                colors = ButtonDefaults.buttonColors(containerColor = MimePartyPink),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("دریافت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

                        // Achievements & Rewards Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AchievementPill(title = "چالش هفتگی", subtitle = "۳/۵ نبرد", color = MimePartyOrange)
                            AchievementPill(title = "دستاوردهای بازی", subtitle = "۱۲ باز شده", color = MimePartyTurquoise)
                            AchievementPill(title = "رتبه امشب", subtitle = "نفر اول 👑", color = MimePartyYellow)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AchievementPill(
    title: String,
    subtitle: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun GameModeCard(
    mode: GameMode,
    onClick: () -> Unit
) {
    val (color, difficultyText, bgGradient) = when (mode) {
        GameMode.CLASSIC -> Triple(MimePartyYellow, "⭐ آسان", listOf(Color(0x33FFCA28), Color(0x15FF7043)))
        GameMode.SPEED_CHALLENGE -> Triple(MimePartyOrange, "🔥 شدید", listOf(Color(0x33FF7043), Color(0x15E91E63)))
        GameMode.TEAM_BATTLE -> Triple(MimePartyTurquoise, "⭐ متوسط", listOf(Color(0x3326C6DA), Color(0x1500ACC1)))
        GameMode.AI_CHALLENGE -> Triple(MimePartyPink, "⭐⭐ سخت", listOf(Color(0x33EC407A), Color(0x158E24AA)))
        GameMode.FAMILY -> Triple(MimePartyGreen, "⭐ آسان", listOf(Color(0x3366BB6A), Color(0x15009688)))
        GameMode.PARTY_EXTREME -> Triple(MimePartyPurple, "🔥 شدید", listOf(Color(0x337E57C2), Color(0x153F51B5)))
    }

    GlassCard(
        onClick = onClick,
        borderColor = color.copy(alpha = 0.7f),
        gradientColors = bgGradient,
        padding = 14.dp,
        modifier = Modifier
            .width(170.dp)
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.25f))
                        .border(1.5.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getModeIcon(mode),
                        contentDescription = mode.titleFa,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = difficultyText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Column {
                Text(
                    text = mode.titleFa,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = mode.descriptionFa,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    maxLines = 3,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun getModeIcon(mode: GameMode): ImageVector {
    return when (mode) {
        GameMode.CLASSIC -> Icons.Default.TheaterComedy
        GameMode.SPEED_CHALLENGE -> Icons.Default.Bolt
        GameMode.TEAM_BATTLE -> Icons.Default.Groups
        GameMode.AI_CHALLENGE -> Icons.Default.Psychology
        GameMode.FAMILY -> Icons.Default.FamilyRestroom
        GameMode.PARTY_EXTREME -> Icons.Default.Celebration
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    activeColor: Color = MimePartyYellow,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = if (isSelected) activeColor else Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

