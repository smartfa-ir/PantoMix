package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

enum class MimeoExpression {
    HAPPY, CELEBRATING, THINKING, SURPRISED, ENCOURAGING, LOSING_ENCOURAGEMENT
}

@Composable
fun MimeoMascotView(
    modifier: Modifier = Modifier,
    expression: MimeoExpression = MimeoExpression.HAPPY,
    dialogueText: String? = null,
    size: Dp = 100.dp,
    onMascotClick: (() -> Unit)? = null
) {
    var tapTrigger by remember { mutableStateOf(0) }
    val tapScale by animateFloatAsState(
        targetValue = if (tapTrigger % 2 == 1) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { tapTrigger = 0 },
        label = "mascot_tap_bounce"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "mascot_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    val (emojiBadge, badgeBgColor, defaultMessage) = when (expression) {
        MimeoExpression.HAPPY -> Triple("😊", MimePartyYellow, "سلام! من میمو هستم! آماده‌ای؟")
        MimeoExpression.CELEBRATING -> Triple("🥳", MimePartyPink, "عالی بود! شما فوق‌العاده‌اید!")
        MimeoExpression.THINKING -> Triple("🧐", MimePartyTurquoise, "دارم سخت‌ترین کلمه رو براتون می‌چینم...")
        MimeoExpression.SURPRISED -> Triple("😲", MimePartyOrange, "واای! وقت داره تموم میشه! سریع‌تر!")
        MimeoExpression.ENCOURAGING -> Triple("💪", MimePartyGreen, "اصلاً نگران نباش! نوبت بعدی جبران می‌کنی!")
        MimeoExpression.LOSING_ENCOURAGEMENT -> Triple("💛", MimePartyYellow, "اشکالی نداره، بازی بعدی ماله شماست!")
    }

    val displayedDialogue = dialogueText ?: defaultMessage

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!displayedDialogue.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .widthIn(max = 280.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xCC2A1448),
                shadowElevation = 8.dp,
                border = BorderStroke(1.5.dp, badgeBgColor.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = emojiBadge, fontSize = 16.sp)
                    Text(
                        text = displayedDialogue,
                        color = MimeTextCream,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(y = floatOffset.dp)
                .scale(tapScale)
                .size(size)
                .clickable {
                    tapTrigger++
                    onMascotClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            // Glowing Backdrop Radial Halo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.15f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                badgeBgColor.copy(alpha = haloPulse * 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Main Mascot Image Frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(3.dp, Brush.horizontalGradient(listOf(MimePartyYellow, MimePartyPink, MimePartyTurquoise)), CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mimeo_mascot_1785751995877),
                    contentDescription = "Mascot MIMEO",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Small Emotion Badge Pill in Corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(badgeBgColor)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emojiBadge, fontSize = 14.sp)
            }
        }
    }
}

