# 잇다

A significant portion of government support policy budgets often fails to reach the public due to complex application procedures and fragmented information across various government sites. Individuals frequently miss out on available benefits because existing platforms lack true personalized recommendations and offer poor accessibility to the original, crucial policy documents.

To solve this problem, '잇다' is developing a service that helps individuals easily and conveniently find and utilize support policies tailored specifically to them. We use a ReAct-based LLM approach to unify and automate policy data crawling across all levels of government, creating a single, comprehensive source. Based on user-provided data (age, income, residency, etc.), the system provides personalized policy recommendations, supports keyword search, and simplifies complex government announcements. This includes clear guidance on application procedures and a direct link to the official application page, ultimately maximizing citizen convenience and utilization.


## What demo demonstrates - Iteration 1

### Showcasing demo video

https://youtu.be/Z_AnJLYR0aw

### Features implemented

In iteration 1, We focuses on establishing a robust architectural foundation using the modern Android development stack (Kotlin and Jetpack Compose) to prepare the further development.

1. State-Driven UI with Jetpack Compose (Modern UI Structure)
   - MVVM & State Management: Implemented the officially recommended MVVM architecture utilizing Kotlin StateFlow to manage and react to the app's state (MainViewState).
   - Declarative UI: The screen is built using Jetpack Compose, ensuring a responsive and modern UI structure.

2. Core Application State Flow Demonstration
    - Loading: Shows a CircularProgressIndicator during data fetching.
    - Content: Renders the main HomeView after loading.
    - Error: Includes a logic path to display an error message.

3. Basic User Interaction (Refresh Action)
   - The "Policy Refresh" button triggers the state transition, proving the basic data re-fetching mechanism is functional and properly integrated with the ViewModel.

### Goals achieved
- Completion of the initial setup for a modern Android app based on Jetpack Compose.
- Verification of the basic implementation of Android's officially recommended architecture (MVVM + Coroutines/Flow).
- Successful demonstration of screen transition logic between the data loading state (Loading) and the content display state (HomeContent).
- Implementation of a basic interaction flow where user input (button click) triggers an app state change and UI refresh.


## Getting Started

### Execution Instruction

1. Open the project directory (./frontend/ or the root of the repo) with Android Studio.
2. Allow the project to sync and install any necessary dependencies (Gradle sync).
3. Ensure you have an appropriate Android Emulator or a connected physical device configured.
4. Run the 'app' configuration on your device/emulator.
