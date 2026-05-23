package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.ReadingPlanDayProgress
import com.valentinerutto.divinedatagpt.ReadingPlanSummary
import com.valentinerutto.divinedatagpt.ReadingPlanTemplate
import com.valentinerutto.divinedatagpt.ReadingPlanViewModel
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPlansRoute(
    onBack: () -> Unit,
    viewModel: ReadingPlanViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReadingPlansScreen(
        isLoading = uiState.isLoading,
        templates = uiState.templates,
        summaries = uiState.summaries,
        activeSummary = uiState.activeSummary,
        onBack = onBack,
        onStartPlan = viewModel::startPlan,
        onToggleDay = viewModel::toggleDay,
        onDeletePlan = viewModel::deletePlan
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadingPlansScreen(
    isLoading: Boolean,
    templates: List<ReadingPlanTemplate>,
    summaries: List<ReadingPlanSummary>,
    activeSummary: ReadingPlanSummary?,
    onBack: () -> Unit,
    onStartPlan: (ReadingPlanTemplate) -> Unit,
    onToggleDay: (ReadingPlanDayProgress) -> Unit,
    onDeletePlan: (Long) -> Unit
) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reading Plans",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PurplePrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                ReadingPlanHero(summary = activeSummary)
            }

            item {
                Text(
                    text = "START A PLAN",
                    color = TextMuted,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(templates, key = { template -> template.id }) { template ->
                        TemplateCard(
                            template = template,
                            isStarted = summaries.any { summary ->
                                summary.plan.templateId == template.id
                            },
                            onStartPlan = { onStartPlan(template) }
                        )
                    }
                }
            }

            activeSummary?.let { summary ->
                item {
                    ProgressChart(summary = summary)
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S READING",
                            color = TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = { onDeletePlan(summary.plan.id) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete plan",
                                tint = TextMuted
                            )
                        }
                    }
                }

                items(
                    items = summary.days,
                    key = { day -> day.day.id }
                ) { day ->
                    PlanDayRow(
                        day = day,
                        onToggleDay = { onToggleDay(day) }
                    )
                }
            } ?: item {
                EmptyPlansMessage()
            }
        }
    }
}

@Composable
private fun ReadingPlanHero(summary: ReadingPlanSummary?) {
    val progress = summary?.progressFraction ?: 0f
    val accent = summary?.plan?.accentColor.toColorOrDefault(PurplePrimary)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(accent.copy(alpha = 0.95f), Color(0xFF1B1025))
                )
            )
            .padding(20.dp)
    ) {
        Text(
            text = summary?.plan?.title ?: "Build a Scripture rhythm",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Text(
            text = summary?.plan?.subtitle ?: "Choose a 7-day, 30-day, or emotion-based plan to begin tracking daily progress.",
            color = TextPrimary.copy(alpha = 0.82f),
            fontSize = 14.sp,
            lineHeight = 21.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricPill(
                label = "Streak",
                value = "${summary?.currentStreak ?: 0}d"
            )
            MetricPill(
                label = "Missed",
                value = "${summary?.missedDays ?: 0}"
            )
            MetricPill(
                label = "Goal",
                value = if (summary?.todayGoalComplete == true) "Done" else "Open"
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .padding(top = 18.dp),
            color = TextPrimary,
            trackColor = Color.White.copy(alpha = 0.18f)
        )
    }
}

@Composable
private fun MetricPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.28f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextPrimary.copy(alpha = 0.68f), fontSize = 11.sp)
    }
}

@Composable
private fun TemplateCard(
    template: ReadingPlanTemplate,
    isStarted: Boolean,
    onStartPlan: () -> Unit
) {
    val accent = template.accentColor.toColorOrDefault(PurplePrimary)
    Column(
        modifier = Modifier
            .width(236.dp)
            .heightIn(min = 180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable(enabled = !isStarted, onClick = onStartPlan)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(accent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isStarted) Icons.Default.Check else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = DarkBackground
            )
        }
        Text(
            text = template.title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 23.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = template.subtitle,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = if (isStarted) "IN PROGRESS" else "${template.durationDays} DAYS • ${template.category.uppercase()}",
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
private fun ProgressChart(summary: ReadingPlanSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 22.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progress",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${summary.completedDays}/${summary.days.size} chapters",
                color = PurpleAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            summary.days.take(30).forEach { day ->
                val color = when {
                    day.isCompleted -> summary.plan.accentColor.toColorOrDefault(PurplePrimary)
                    day.isMissed -> Color(0xFFFF6B6B)
                    day.isToday -> Color.White.copy(alpha = 0.72f)
                    else -> Color.White.copy(alpha = 0.18f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (day.isToday) 42.dp else 30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
private fun PlanDayRow(
    day: ReadingPlanDayProgress,
    onToggleDay: () -> Unit
) {
    val statusColor = when {
        day.isCompleted -> Color(0xFF74D99F)
        day.isMissed -> Color(0xFFFF6B6B)
        day.isToday -> PurpleAccent
        else -> TextMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .clickable(onClick = onToggleDay)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            if (day.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = statusColor)
            } else {
                Text(
                    text = day.day.dayNumber.toString(),
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = day.day.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${day.day.bookName} ${day.day.chapter} • ${day.day.focus}",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = LocalDate.ofEpochDay(day.scheduledEpochDay)
                .format(DateTimeFormatter.ofPattern("MMM d")),
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyPlansMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No active reading plan yet",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Start with a short plan and mark each chapter complete as you read.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun String?.toColorOrDefault(default: Color): Color {
    if (this == null) return default
    return runCatching { Color(android.graphics.Color.parseColor(this)) }
        .getOrDefault(default)
}
