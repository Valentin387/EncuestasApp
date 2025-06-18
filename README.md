Sure! Here's the full content formatted as a single `README.md` file, ready to be copied and committed at the root of your Android project:

---

```markdown
# EncuestasApp

## Metadata
- **Project Name**: EncuestasApp  
- **Version**: 1.0.0  
- **Platform**: Android  
- **Language**: Kotlin  
- **Minimum SDK**: API 24   
- **Target SDK**: API 35 (Android 15)  
- **License**: MIT  
- **Repository**: [GitHub TBD]  
- **Web Companion**: [https://angular-kata.vercel.app/](https://angular-kata.vercel.app/)  
- **Backend**: [https://your-backend.herokuapp.com/](https://your-backend.herokuapp.com/)  

## Overview
EncuestasApp is an Android application for creating, managing, and responding to surveys. It supports user authentication, dynamic survey creation, and deep linking integration from a web application to directly open survey response forms.

## Kotlin Version
- **Kotlin**: 2.0.0  
- **Coroutines**: Used for all async API operations  
- **Kotlin DSL**: Used in Gradle configuration (`build.gradle.kts`)  

## Dependencies
Key dependencies used:
- **AndroidX**:
  - `androidx.core:core-ktx:1.13.1`
  - `androidx.appcompat:appcompat:1.7.0`
  - `androidx.constraintlayout:constraintlayout:2.1.4`
  - `androidx.navigation:navigation-fragment-ktx:2.8.3`
  - `androidx.navigation:navigation-ui-ktx:2.8.3`
- **Networking**:
  - `com.squareup.retrofit2:retrofit:2.11.0`
  - `com.squareup.retrofit2:converter-gson:2.11.0`
- **UI & Material**:
  - `com.google.android.material:material:1.12.0`
- **Testing**:
  - `junit:junit:4.13.2`
  - `androidx.test.ext:junit:1.2.1`
  - `androidx.test.espresso:espresso-core:3.6.1`
- **Others**:
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0`
  - `com.google.code.gson:gson:2.11.0`

## Use Cases
### 1. User Authentication
- JWT-based login system.
- Secure token storage with `EncryptedPrefsManager`.

### 2. Survey Creation (`CreateSurveyFragment`)
- Supports dynamic question types: Text, Age, Rating.
- Mark questions as required.
- Submits to `/api/surveys`.

### 3. Survey Management
- `MySurveysFragment`: Lists user-created surveys.
- `CompanySurveysFragment`: Lists company-wide surveys.
- View survey details and delete surveys (with or without cascade).

### 4. Survey Response (`ResponseSurveyActivity`)
- Triggered by web deep link: `myapp://survey/{surveyId}`.
- Loads survey and allows public user responses.
- **Bug**: Response submission fails (see Known Bugs).

### 5. Deep Linking
- Triggered via web at:  
  `https://angular-kata.vercel.app/survey/{surveyId}`  
- Redirects to Android if app installed; fallback to Play Store.

## Folder Structure
```

```

app/
├── src/main/java/com/kata3/encuestasapp/
│   ├── data/repositories/
│   ├── io/                   # Retrofit interfaces
│   ├── ui/
│   │   ├── main/             # Fragments: Create, List, etc.
│   │   └── response/         # ResponseSurveyActivity
│   ├── utils/
│   └── MainActivity.kt
├── res/layout/
├── res/navigation/
├── res/values/
├── AndroidManifest.xml
build.gradle.kts


````

## Setup Instructions
```bash
git clone [repository-url]
````

1. Add your `BASE_URL` in `secrets.properties`:

   ```
   BASE_URL=https://your-backend.herokuapp.com/
   ```

2. Build and run:

    * Open project in Android Studio.
    * Sync Gradle and run on a device or emulator.

3. Test deep linking:

   ```bash
   adb shell am start -W -a android.intent.action.VIEW -d "myapp://survey/657abc123" com.kata3.encuestasapp
   ```

## Known Bugs

### Survey Response Submission Failure

* **Location**: `ResponseSurveyActivity`
* **Description**: Survey response fails to submit via Retrofit.
* **Symptoms**: Form appears via deep link but response is not saved.
* **Possible Issues**:

    * Missing or invalid JWT token.
    * Network failure or wrong API path.
    * Malformed request payload.
    * Missing company or user data.
* **Debugging Tips**:

    * View logs:
      `adb logcat | grep "com.kata3.encuestasapp"`
    * Use Android Studio Network Profiler.
    * Enable Retrofit logging for more insight.

## Enhancement Opportunity

### ✅ Add Company Data to Response Payload

**Problem**: The backend may reject response payloads due to missing `companyId`.

**Proposed Fix**:

1. **Create UserService**:

   ```kotlin
   interface UserService {
       @GET("/api/users/me")
       suspend fun getUser(@Header("Authorization") token: String): UserDto
   }
   ```

2. **Fetch user on activity start**:

   ```kotlin
   val user = userService.getUser("Bearer $token")
   ```

3. **Include in response payload**:

   ```json
   {
     "surveyId": "657abc123",
     "companyId": "789def456",
     "answers": [
       { "questionId": "q1", "answer": "Good" },
       { "questionId": "q2", "answer": "30" }
     ]
   }
   ```

**Benefits**:

* Validates responses.
* Enables company-specific reporting.

**Contribute**:

* Fork and PR to \[GitHub TBD].
* Include test coverage and docs.

## Contributing

* Fork, branch, and PR via GitHub.
* Report bugs and feature requests.
* Follow Kotlin style guidelines.
* Include tests using JUnit and Espresso.

## Contact

* 📧 \[[your-email@example.com](mailto:your-email@example.com)] (replace with yours)

---

