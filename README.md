# 잇다

**잇다** is an AI-powered welfare policy recommendation app designed specifically for elderly citizens and their families. By leveraging ReAct-based LLM technology, we help seniors easily discover and access government support benefits tailored to their needs.

A significant portion of government support policy budgets often fails to reach the elderly population due to complex application procedures and fragmented information across various government sites. Senior users frequently miss out on available benefits because of low digital literacy and difficulty understanding complex administrative procedures.

To solve this problem, **'잇다'** provides personalized policy recommendations, AI-generated summaries, and an intuitive interface designed specifically for elderly users.


## What demo demonstrates - Iteration 4

### Showcasing demo video

[https://youtu.be/Z_AnJLYR0aw](https://www.youtube.com/watch?v=RFButGVnY_g&feature=youtu.be)

### Features implemented

**1. Frontend Implementation**

- **Onboarding Page**: Interactive tutorial guiding elderly users through app features with swipe navigation
- **Personal Information Input**: Enabled combined personal info input with text fields for easier data entry
- **Preference Survey**: Replaced sliders with radio buttons for better usability
- **Profile Page**: Improved profile page design and added comprehensive error handling
- **Search Functionality**: Implemented keyword search with API integration, category filtering, and result sorting
- **Accessibility Settings**:
    - Font Size Adjustment: Users can adjust text size app-wide
    - Dark Mode: Toggle dark theme for better visibility
- **"Not Interested" Feature**: Added option to filter out irrelevant policies
- **Error Handling**: Comprehensive network and general error handling with user-friendly messages

**2. Backend Implementation**

- **Keyword Search API**: Implemented search endpoint with keyword matching
- **User Preference Collection API**: API for collecting and managing user preferences

**3. Testing**

- **Frontend Testing**: Implemented unit tests and integration tests for ViewModels and Repository interactions
- **Backend Testing**: Added integration tests for API endpoints and service layer

### Goals achieved
- Completed elderly-friendly UI/UX with large fonts, intuitive navigation, and simplified workflows
- Implemented comprehensive search and recommendation features
- Added accessibility features (font size adjustment, dark mode)
- Improved recommendation algorithm with user feedback integration
- Comprehensive error handling across the app
- Established testing infrastructure for both frontend and backend


## Getting Started

### Execution Instruction

1. Open the project directory (./frontend/ or the root of the repo) with Android Studio.
2. Allow the project to sync and install any necessary dependencies (Gradle sync).
3. Ensure you have an appropriate Android Emulator or a connected physical device configured.
4. Run the 'app' configuration on your device/emulator.
