package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onStartReflection: () -> Unit,
    onDailyReflection: () -> Unit,
    onChatClick: () -> Unit,
    onJournal: () -> Unit,
    viewModel: DivineDataViewModel = koinViewModel()
) {

    val uiState by viewModel.homeuiState.collectAsState()


    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {


            Spacer(Modifier.height(8.dp))

            // ── Hero Text ────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Welcome.",
                    color = TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "May your time here bring you peace and clarity.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(PurplePrimary, Color(0xFF9B59B6)))
                    )
                    .clickable { onStartReflection() }
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Start a Deep Reflection",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Chat with our AI Bible Companion",
                            color = Color.White.copy(0.8f),
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Chat, contentDescription = null,
                            tint = TextPrimary, modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CardBackground)
                    .clickable { onJournal() }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Divine Journal",
                            color = TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Document your spiritual Journey.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Verse of the Day ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Verse of the Day",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Share",
                    color = PurpleAccent,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onDailyReflection() }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            } else {
                uiState.verseOfDay?.let { verse ->
                    VerseCard(
                        verse = verse.verse,
                        reference = verse.reference,
                        onClick = { onDailyReflection() }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }


@Composable
fun VerseCard(verse: String, reference: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A3A2A), Color(0xFF1A1A30))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "❝❝",
                    color = PurpleAccent,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = verse,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = reference.uppercase(),
                    color = PurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
