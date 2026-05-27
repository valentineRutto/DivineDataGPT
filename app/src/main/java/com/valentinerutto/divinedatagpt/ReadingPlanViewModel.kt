package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanCompletionEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanDayEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ReadingPlanTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationDays: Int,
    val category: String,
    val accentColor: String,
    val chapters: List<ReadingPlanChapter>
)

data class ReadingPlanChapter(
    val title: String,
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val focus: String
)

data class ReadingPlanDayProgress(
    val day: ReadingPlanDayEntity,
    val scheduledEpochDay: Long,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val isMissed: Boolean
)

data class ReadingPlanSummary(
    val plan: ReadingPlanEntity,
    val days: List<ReadingPlanDayProgress>,
    val completedDays: Int,
    val missedDays: Int,
    val todayGoalComplete: Boolean,
    val currentStreak: Int,
    val progressFraction: Float
)

data class ReadingPlansUiState(
    val templates: List<ReadingPlanTemplate> = ReadingPlanTemplates.all,
    val summaries: List<ReadingPlanSummary> = emptyList(),
    val selectedPlanId: Long? = null,
    val isLoading: Boolean = true
) {
    val activeSummary: ReadingPlanSummary?
        get() = summaries.firstOrNull { it.plan.id == selectedPlanId } ?: summaries.firstOrNull()
}

data class ReadingPlanReading(
    val day: ReadingPlanDayEntity,
    val verses: List<VerseEntity>
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingPlanViewModel(
    private val repository: BibleRepository
) : ViewModel() {
    private val selectedReadingDay = MutableStateFlow<ReadingPlanDayEntity?>(null)
    private val today: Long
        get() = LocalDate.now().toEpochDay()

    val uiState: StateFlow<ReadingPlansUiState> = combine(
        repository.observeReadingPlans(),
        repository.observeReadingPlanDays(),
        repository.observeReadingPlanCompletions()
    ) { plans, days, completions ->
        val completionIds = completions.map { completion -> completion.planDayId }.toSet()
        val completionEpochDays = completions.map { completion -> completion.completedEpochDay }.toSet()
        val summaries = plans.map { plan ->
            val planDays = days
                .filter { day -> day.planId == plan.id }
                .sortedBy { day -> day.dayNumber }
                .map { day ->
                    val scheduledEpochDay = plan.startEpochDay + day.dayNumber - 1
                    val completed = day.id in completionIds
                    ReadingPlanDayProgress(
                        day = day,
                        scheduledEpochDay = scheduledEpochDay,
                        isCompleted = completed,
                        isToday = scheduledEpochDay == today,
                        isMissed = scheduledEpochDay < today && !completed
                    )
                }

            ReadingPlanSummary(
                plan = plan,
                days = planDays,
                completedDays = planDays.count { day -> day.isCompleted },
                missedDays = planDays.count { day -> day.isMissed },
                todayGoalComplete = planDays.any { day -> day.isToday && day.isCompleted },
                currentStreak = calculateCurrentStreak(completionEpochDays),
                progressFraction = if (planDays.isEmpty()) {
                    0f
                } else {
                    planDays.count { day -> day.isCompleted }.toFloat() / planDays.size
                }
            )
        }

        ReadingPlansUiState(
            summaries = summaries,
            selectedPlanId = summaries.firstOrNull()?.plan?.id,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReadingPlansUiState()
    )

    val selectedReading: StateFlow<ReadingPlanReading?> = selectedReadingDay
        .flatMapLatest { day ->
            if (day == null) {
                flowOf(null)
            } else {
                repository.observeChapter(
                    translation = "shortname",
                    book = day.book,
                    chapter = day.chapter
                ).map { verses ->
                    ReadingPlanReading(day = day, verses = verses)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun startPlan(template: ReadingPlanTemplate) {
        viewModelScope.launch {
            val plan = ReadingPlanEntity(
                templateId = template.id,
                title = template.title,
                subtitle = template.subtitle,
                durationDays = template.durationDays,
                category = template.category,
                accentColor = template.accentColor,
                startEpochDay = today
            )
            val days = template.chapters.mapIndexed { index, chapter ->
                ReadingPlanDayEntity(
                    planId = 0,
                    dayNumber = index + 1,
                    title = chapter.title,
                    bookName = chapter.bookName,
                    book = chapter.book,
                    chapter = chapter.chapter,
                    focus = chapter.focus
                )
            }
            repository.startReadingPlan(plan, days)
        }
    }

    fun toggleDay(day: ReadingPlanDayProgress) {
        viewModelScope.launch {
            if (day.isCompleted) {
                repository.uncompleteReadingPlanDay(day.day.id)
            } else {
                repository.completeReadingPlanDay(day.day.id, today)
            }
        }
    }

    fun openReading(day: ReadingPlanDayProgress) {
        selectedReadingDay.value = day.day
    }

    fun closeReadingAndComplete() {
        val day = selectedReadingDay.value ?: return
        selectedReadingDay.value = null
        viewModelScope.launch {
            repository.completeReadingPlanDay(day.id, today)
        }
    }

    fun deletePlan(planId: Long) {
        viewModelScope.launch {
            repository.deleteReadingPlan(planId)
        }
    }

    private fun calculateCurrentStreak(completedEpochDays: Set<Long>): Int {
        if (completedEpochDays.isEmpty()) return 0

        var cursor = today
        if (cursor !in completedEpochDays) {
            cursor -= 1
        }

        var streak = 0
        while (cursor in completedEpochDays) {
            streak += 1
            cursor -= 1
        }
        return streak
    }
}

private object ReadingPlanTemplates {
    val all = listOf(
        sevenDayFoundation(),
        thirtyDayJourney(),
        emotionPlan(
            id = "emotion-peace",
            title = "Peace for Anxiety",
            subtitle = "Seven readings for calm, trust, and steadiness.",
            accentColor = "#74D99F",
            chapters = listOf(
                chapter("Still Waters", "Psalms", 19, 23, "Rest in God's nearness."),
                chapter("Do Not Fear", "Isaiah", 23, 41, "Practice courage with God beside you."),
                chapter("A Quiet Heart", "Matthew", 40, 6, "Release tomorrow's weight."),
                chapter("Peace I Give", "John", 43, 14, "Receive peace that is not fragile."),
                chapter("Pray First", "Philippians", 50, 4, "Turn anxiety into prayer."),
                chapter("A Sound Mind", "2 Timothy", 55, 1, "Name fear without letting it lead."),
                chapter("Cast Your Cares", "1 Peter", 60, 5, "Let God carry what you cannot.")
            )
        ),
        emotionPlan(
            id = "emotion-strength",
            title = "Strength When Tired",
            subtitle = "Seven readings for resilience and renewed courage.",
            accentColor = "#7AB7FF",
            chapters = listOf(
                chapter("Renewed Strength", "Isaiah", 23, 40, "Wait with hope, not passivity."),
                chapter("The Lord Is Light", "Psalms", 19, 27, "Choose courage in pressure."),
                chapter("Enough Grace", "2 Corinthians", 47, 12, "Find strength in weakness."),
                chapter("Press On", "Philippians", 50, 3, "Keep moving with purpose."),
                chapter("Armor for Today", "Ephesians", 49, 6, "Prepare your mind and heart."),
                chapter("Run With Endurance", "Hebrews", 58, 12, "Stay faithful over time."),
                chapter("Joy in Trials", "James", 59, 1, "Let endurance mature you.")
            )
        ),
        emotionPlan(
            id = "emotion-grief",
            title = "Hope in Grief",
            subtitle = "Seven readings for sorrow, comfort, and honest hope.",
            accentColor = "#FF8FC7",
            chapters = listOf(
                chapter("Near the Brokenhearted", "Psalms", 19, 34, "Let sorrow be seen by God."),
                chapter("Lament With Faith", "Lamentations", 25, 3, "Hold grief and hope together."),
                chapter("Comfort My People", "Isaiah", 23, 40, "Receive tenderness in pain."),
                chapter("Blessed Are Those Who Mourn", "Matthew", 40, 5, "Let comfort meet honesty."),
                chapter("Jesus Wept", "John", 43, 11, "See Christ present in loss."),
                chapter("God of All Comfort", "2 Corinthians", 47, 1, "Be comforted and strengthened."),
                chapter("No More Tears", "Revelation", 66, 21, "Look toward restoration.")
            )
        )
    )

    private fun sevenDayFoundation(): ReadingPlanTemplate {
        return ReadingPlanTemplate(
            id = "foundation-7",
            title = "7-Day Foundation",
            subtitle = "A focused first week through creation, wisdom, Jesus, and faith.",
            durationDays = 7,
            category = "Starter",
            accentColor = "#C15CFF",
            chapters = listOf(
                chapter("Beginning With God", "Genesis", 1, 1, "Start with identity and creation."),
                chapter("The Shepherd Psalm", "Psalms", 19, 23, "Notice God's care and guidance."),
                chapter("Wisdom's Invitation", "Proverbs", 20, 3, "Trust God with the path ahead."),
                chapter("The Kingdom Way", "Matthew", 40, 5, "Sit with Jesus' vision of blessing."),
                chapter("Love Made Visible", "John", 43, 3, "Return to the heart of the gospel."),
                chapter("Life in the Spirit", "Romans", 45, 8, "Remember freedom over condemnation."),
                chapter("Faith That Moves", "James", 59, 2, "Let belief become practice.")
            )
        )
    }

    private fun thirtyDayJourney(): ReadingPlanTemplate {
        val chapters = listOf(
            chapter("Creation", "Genesis", 1, 1, "Begin with God's good world."),
            chapter("Calling", "Genesis", 1, 12, "Notice trust in the unknown."),
            chapter("Deliverance", "Exodus", 2, 14, "Remember that rescue is possible."),
            chapter("Presence", "Exodus", 2, 33, "Pay attention to God's nearness."),
            chapter("Blessed Life", "Psalms", 19, 1, "Choose rootedness."),
            chapter("Shepherd", "Psalms", 19, 23, "Practice rest."),
            chapter("Confession", "Psalms", 19, 51, "Let honesty lead to renewal."),
            chapter("Wisdom", "Proverbs", 20, 4, "Seek wisdom in ordinary choices."),
            chapter("Hope", "Isaiah", 23, 40, "Wait with strength."),
            chapter("Courage", "Isaiah", 23, 41, "Refuse fear as your ruler."),
            chapter("Sermon", "Matthew", 40, 5, "Learn the shape of the kingdom."),
            chapter("Prayer", "Matthew", 40, 6, "Pray with simplicity."),
            chapter("Rest", "Matthew", 40, 11, "Come honestly to Christ."),
            chapter("Sower", "Mark", 41, 4, "Examine the soil of your heart."),
            chapter("Compassion", "Luke", 42, 10, "Love your neighbor with action."),
            chapter("Lost and Found", "Luke", 42, 15, "Receive mercy."),
            chapter("Word Made Flesh", "John", 43, 1, "See light entering darkness."),
            chapter("New Birth", "John", 43, 3, "Return to grace."),
            chapter("Abiding", "John", 43, 15, "Stay connected to Christ."),
            chapter("Resurrection", "John", 43, 20, "Let hope become concrete."),
            chapter("No Condemnation", "Romans", 45, 8, "Live from freedom."),
            chapter("Love", "1 Corinthians", 46, 13, "Measure maturity by love."),
            chapter("Comfort", "2 Corinthians", 47, 1, "Receive comfort to give comfort."),
            chapter("Grace", "Galatians", 48, 5, "Walk by the Spirit."),
            chapter("Armor", "Ephesians", 49, 6, "Stand with intention."),
            chapter("Joy", "Philippians", 50, 4, "Practice gratitude."),
            chapter("Mindset", "Colossians", 51, 3, "Set your mind on what lasts."),
            chapter("Endurance", "Hebrews", 58, 12, "Run with patience."),
            chapter("Faith Works", "James", 59, 2, "Make faith visible."),
            chapter("Restoration", "Revelation", 66, 21, "End with hope.")
        )
        return ReadingPlanTemplate(
            id = "journey-30",
            title = "30-Day Scripture Journey",
            subtitle = "A month-long path through story, wisdom, gospel, and formation.",
            durationDays = 30,
            category = "Journey",
            accentColor = "#FFD166",
            chapters = chapters
        )
    }

    private fun emotionPlan(
        id: String,
        title: String,
        subtitle: String,
        accentColor: String,
        chapters: List<ReadingPlanChapter>
    ): ReadingPlanTemplate {
        return ReadingPlanTemplate(
            id = id,
            title = title,
            subtitle = subtitle,
            durationDays = chapters.size,
            category = "Emotion",
            accentColor = accentColor,
            chapters = chapters
        )
    }

    private fun chapter(
        title: String,
        bookName: String,
        book: Int,
        chapter: Int,
        focus: String
    ): ReadingPlanChapter {
        return ReadingPlanChapter(
            title = title,
            bookName = bookName,
            book = book,
            chapter = chapter,
            focus = focus
        )
    }
}
