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

``` 
Android App (Kotlin + MVVM)
│
├── UI Layer (Jetpack Compose)
│     ├─ EmotionInputScreen
│     │     ├─ User enters emotion & message
│     │     └─ Sends event to ViewModel
│     ├─ VerseScreen
│     │     ├─ Displays verse + AI reflection
│     │     └─ Observes LiveData/StateFlow
│
├── ViewModel Layer
│     ├─ Handles UI logic & state
│     ├─ Calls Repository to fetch verse + AI response
│     ├─ Exposes results as LiveData/StateFlow
│
├── Repository Layer
│     ├─ Coordinates data from:
│     │     ├─ RemoteDataSource (Ktor/Retrofit API)
│     │     └─ LocalDataSource (Room Database, Cache)
│     ├─ Combines results and handles errors
│
├── Data Layer
│     ├─ RemoteDataSource
│     │     ├─ Retrofit client calls DivineDataServer Ktor 
│     │     └─ e.g. `/api/v1/chat`, `/api/v1/verse`
│     ├─ LocalDataSource
│     │     ├─ Room entities + DAO
│     │     └─ Caches last verses & AI messages
│
├── Models (Data Classes)
│     ├─ EmotionRequest
│     ├─ ChatResponse
│     ├─ Verse
│
└── Core / Utils
      ├─ NetworkModule (Retrofit, OkHttp, Json/Kotlinx)
      ├─ DI (Koin)
      ├─ Resource Wrappers (Success/Error/Loading)
      └─ PreferencesManager (theme, settings)

```
