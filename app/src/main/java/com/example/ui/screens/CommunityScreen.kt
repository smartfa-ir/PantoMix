package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CommunityWordEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    communityWords: List<CommunityWordEntity>,
    onSubmitWord: (text: String, category: String, creatorName: String) -> Unit,
    onUpvote: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    var newWordText by remember { mutableStateOf("") }
    var creatorName by remember { mutableStateOf("") }

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
                        text = "انجمن کلمات و پیشنهادات کاربران 💡",
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

            // Submit Word Card
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
                            text = "افزودن کلمه یا سوژه جدید پانتومیم ✍️",
                            color = MimePartyOrange,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        OutlinedTextField(
                            value = newWordText,
                            onValueChange = { newWordText = it },
                            placeholder = { Text("عبارت یا سوژه پانتومیم...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MimePartyOrange,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = creatorName,
                            onValueChange = { creatorName = it },
                            placeholder = { Text("نام شما...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MimePartyOrange,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = {
                                if (newWordText.isNotBlank()) {
                                    onSubmitWord(newWordText, "community", creatorName.ifBlank { "کاربر ناشناس" })
                                    newWordText = ""
                                    creatorName = ""
                                }
                            },
                            enabled = newWordText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MimePartyOrange, contentColor = Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                                Text(text = "ثبت در انجمن کاربران", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // Trending List Header
            item {
                Text(
                    text = "محبوب‌ترین سوژه‌های پیشنهادی کاربران 🔥",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            items(communityWords) { item ->
                GlassCard(
                    borderColor = MimePartyTurquoise.copy(alpha = 0.4f),
                    gradientColors = listOf(
                        Color(0x2200838F),
                        Color(0x11006064)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.text,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ارسال توسط: ${item.creatorName}",
                                color = MimePartyYellowLight,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier.clickable { onUpvote(item.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFCA28)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Upvote",
                                    tint = MimePartyYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${item.votes}",
                                    color = MimePartyYellow,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
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

