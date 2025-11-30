# 잇다

**잇다** is an AI-powered welfare policy recommendation app designed specifically for elderly citizens and their families. By leveraging ReAct-based LLM technology, we help seniors easily discover and access government support benefits tailored to their needs.

A significant portion of government support policy budgets often fails to reach the elderly population due to complex application procedures and fragmented information across various government sites. Senior users frequently miss out on available benefits because of low digital literacy and difficulty understanding complex administrative procedures.

To solve this problem, **'잇다'** provides personalized policy recommendations, AI-generated summaries, and an intuitive interface designed specifically for elderly users.


## What demo demonstrates - Iteration 5

### Showcasing demo video

[demo video](https://youtube.com/shorts/iPXSoZVXLwI?feature=share)

### Features implemented

**1. Frontend Implementation**
* **UI/UX Enhancements:** Implemented splash screen, improved page design consistency, and optimized overall UX.
* **Accessibility:** Complemented color themes and implemented variable font size support for better accessibility.
* **Profile & Search Features:** Developed profile page design and implemented bookmark functionality within the search screen.
* **Authentication:** Connected refresh token logic and fixed logout functionality.
* **Interaction Logic:** Implemented "Like/Dislike" features and resolved logic errors in bookmarking/liking.
* **Refactoring & Architecture:** Adopted design patterns, refactored authentication modules, optimized program-fetching codes, and performed overall code refactoring (including profile).

**2. Backend Implementation**
* **Recommendation Engine:** Incorporated "likes/unlikes" and "bookmarks" into the recommendation algorithm and added recommendation reasons to the API response.
* **Performance:** Implemented User Feed Cache for improved performance.
* **Authentication:** Implemented refresh token handling.
* **Logic Fixes:** Fixed address matching logic.

**3. Testing**
* **Test Coverage:** Completed test code to achieve coverage goals and implemented Repository tests.
* **Test Maintenance:** Fixed `fakeRepository` implementation in Android integration tests.
* **User Acceptance:** Completed User Acceptance Testing (UAT) Documents.

### Goals achieved

* **Testing:** Achieved test coverage goals and validated system stability through Repository testing.
* **UI/UX Implementation:** Completed consistent page designs (including Profile and Splash screens) and enhanced accessibility with color themes and variable font sizes.
* **Recommendation Logic Refinement:** Enhanced the recommendation engine by incorporating user interactions (Likes/Bookmarks) and implementing recommendation reasons.
* **Architecture & Refactoring:** Successfully adopted design patterns and refactored core modules (Auth, Program fetching) to improve code maintainability.

## Getting Started

### Execution Instruction

1. Open the project directory (./frontend/ or the root of the repo) with Android Studio.
2. Allow the project to sync and install any necessary dependencies (Gradle sync).
3. Ensure you have an appropriate Android Emulator or a connected physical device configured.
4. Run the 'app' configuration on your device/emulator.
