 # DivineData 🌿

**AI-Powered Emotional Bible Companion** 

Connect your emotions to the Word.  
DivineData analyzes how you feel and suggests scripture and reflections to uplift your spirit.

```When life feels heavy, DivineData helps me hear Scripture personally, pray honestly, and remember how God has carried me.```

### ✨ Tech Stack
- **Kotlin (Native Android)**
- **MVVM + Koin + Room + Retrofit**
- **Ktor Backend Integration**

### 🌐 Backend
Backend repository: [divinedata-server](https://github.com/valentineRutto/divinedata-server)

### Architecture

DivineData is currently a native Android app built with Kotlin, Jetpack Compose,
MVVM, Koin, Room, Retrofit/OkHttp, WorkManager, and product flavors for `demo`
and `full` builds.

```text
Android App: DivineDataGPT
|
|-- App startup
|   |-- MyApplication
|   |   |-- starts Koin with AppModule, databaseModule, and networkModule
|   |   |-- seeds the local Bible database with BibleDatabaseSeeder
|   |   `-- schedules the daily reflection reminder
|   |
|   `-- MainActivity
|       |-- hosts the Compose UI
|       `-- connects navigation through NavGraph
|
|-- UI layer: Jetpack Compose screens
|   |-- HomeScreen
|   |   |-- greeting, verse of the day, emotion entry points, chat entry
|   |   `-- routes to reflection, daily reflection, reading plans, or Bible
|   |
|   |-- ReflectionScreen
|   |   `-- AI Bible companion chat for emotional/spiritual reflection
|   |
|   |-- DailyReflectionScreen
|   |   `-- daily verse, reference, and reflection content
|   |
|   |-- BibleReaderScreen
|   |   |-- local Bible reading by book/chapter
|   |   |-- verse search
|   |   `-- verse note/highlight creation
|   |
|   |-- BibleNotesScreen + NoteEditorScreen
|   |   `-- standalone notes and saved verse notes/highlights
|   |
|   `-- ReadingPlansScreen
|       `-- reading-plan templates, progress, streaks, completion, and readings
|
|-- Navigation layer
|   `-- NavGraph + Screen routes
|       |-- home
|       |-- reflection/{emotion}
|       |-- daily
|       |-- bible
|       |-- bible_notes
|       `-- reading_plans
|
|-- ViewModel layer: MVVM + StateFlow
|   |-- DivineDataViewModel
|   |   |-- home greeting and verse of the day
|   |   |-- reflection chat state
|   |   |-- recent message history
|   |   `-- AI reflection message persistence
|   |
|   |-- BibleViewModel
|   |   |-- selected translation/book/chapter state
|   |   |-- Bible search
|   |   |-- note/highlight save, update, and delete
|   |   `-- exposes BibleReaderUiState
|   |
|   `-- ReadingPlanViewModel
|       |-- built-in reading-plan templates
|       |-- active plans and day progress
|       |-- completions, missed days, and streak calculation
|       `-- selected reading content
|
|-- Repository layer
|   |-- BibleRepository
|   |   |-- reads local Bible books, chapters, verses, and search results
|   |   |-- saves and updates Bible notes
|   |   `-- manages reading plans and completions
|   |
|   `-- AiRepository
|       |-- calls Gemini/Hugging Face AI endpoints through Retrofit
|       |-- stores recent reflection messages in Room
|       `-- trims local chat history for reflection context
|
|-- Data layer
|   |-- Room: DivineDatabase
|   |   |-- VerseDao
|   |   |-- MessageDao
|   |   |-- MemorySummaryDao
|   |   `-- ReadingPlanDao
|   |
|   |-- Room entities
|   |   |-- Bible verses and full-text-search table
|   |   |-- Bible notes and bookmarks
|   |   |-- AI reflection messages and memory summaries
|   |   `-- reading plans, reading-plan days, and completions
|   |
|   `-- Network
|       |-- RetrofitClient
|       |-- ApiService for Bible/ESV requests
|       `-- AiApi for Gemini and Hugging Face chat/reflection requests
|
|-- Background and system integrations
|   |-- DailyReflectionScheduler
|   |-- DailyReflectionWorker
|   |-- DailyReflectionAlarmReceiver
|   |-- boot/package-replaced rescheduling
|   `-- daily-reflection deep link: divinedatagpt://daily-reflection
|
`-- Build configuration
    |-- namespace/applicationId: com.valentinerutto.divinedatagpt
    |-- minSdk 26, targetSdk 36, compileSdk 36
    |-- debug and release build types
    |-- demo flavor: com.valentinerutto.divinedatagpt.demo
    `-- full flavor: com.valentinerutto.divinedatagpt.full
```

### Current Data Flow

```text
Compose screen event
    -> ViewModel updates StateFlow
    -> Repository coordinates local Room data and remote Retrofit calls
    -> DAO/API result returns to Repository
    -> ViewModel emits a new UI state
    -> Compose recomposes the active screen
```

Examples:

- Reflection chat: `ReflectionScreen` -> `DivineDataViewModel` -> `AiRepository` -> `AiApi` + `MessageDao`.
- Bible reader: `BibleReaderScreen` -> `BibleViewModel` -> `BibleRepository` -> `VerseDao`.
- Notes/highlights: `BibleNotesScreen` or `NoteEditorScreen` -> `BibleViewModel` -> `BibleRepository` -> `VerseDao`.
- Reading plans: `ReadingPlansScreen` -> `ReadingPlanViewModel` -> `BibleRepository` -> `ReadingPlanDao` + `VerseDao`.
- Daily reminders: `MyApplication` schedules reminders through the notification utilities, and the alarm receiver can reopen the daily reflection flow.
