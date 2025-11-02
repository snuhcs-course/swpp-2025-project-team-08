# 잇다

A significant portion of government support policy budgets often fails to reach the public due to complex application procedures and fragmented information across various government sites. Individuals frequently miss out on available benefits because existing platforms lack true personalized recommendations and offer poor accessibility to the original, crucial policy documents.

To solve this problem, '잇다' is developing a service that helps individuals easily and conveniently find and utilize support policies tailored specifically to them. We use a ReAct-based LLM approach to unify and automate policy data crawling across all levels of government, creating a single, comprehensive source. Based on user-provided data (age, income, residency, etc.), the system provides personalized policy recommendations, supports keyword search, and simplifies complex government announcements. This includes clear guidance on application procedures and a direct link to the official application page, ultimately maximizing citizen convenience and utilization.


## What demo demonstrates - Iteration 3

### Showcasing demo video

[https://youtu.be/Z_AnJLYR0aw](https://youtube.com/shorts/3aYDKGdWCdQ?feature=share)

### Features implemented

In iteration 3, we focused on implementing most of the application and on testing.

**1. Frontend Implement**
- **Profile pages**: On the profile pages, you can update or view your personal information.
- **Auth**: We added a logout feature and improved the login and sign-up functionality.
- **HomeView**: We enhanced the Home view screen.

**2. Testing**
- **Frontend Unit Test**: We implement in frontend unit tests. (not achieved coverage goal yet)
- **Backend Test**: We achieved meaningful results in backend tests. (>=80%)

**3. Backend Implement**
- **Data loader completion**: Program details vectorize and save in vectorDB
- **Confirmation of recommendation algorithms**: Generate a user preference profile vector by incorporating satisfaction scores (1–5) for the five user policies. Recommend by computing the similarity between the user’s preferenceVector and the program’s detailsVector.


### Goals achieved
- **Testing**: Implement Testing code and achieve meaningful result in backend
- **Ready-to-Present UI/UX**: Implement most of UI/UX
- **Data loader completion**: Program details vectorize and save in vectorDB
- **Confirmation of recommendation algorithms**: Generate a user preference profile vector by incorporating satisfaction scores (1–5) for the five user policies. Recommend by computing the similarity between the user’s preferenceVector and the program’s detailsVector.


## Getting Started

### Execution Instruction

1. Open the project directory (./frontend/ or the root of the repo) with Android Studio.
2. Allow the project to sync and install any necessary dependencies (Gradle sync).
3. Ensure you have an appropriate Android Emulator or a connected physical device configured.
4. Run the 'app' configuration on your device/emulator.