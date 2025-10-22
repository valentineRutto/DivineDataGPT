# DivineDataGPT is a Faith-emotional companion consuming  [DivineDataServer Ktor Server](https://github.com/valentineRutto/divinedata-server/tree/main) 

 This is the ESV + AI-integrated Bible Companion app architecture:

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
