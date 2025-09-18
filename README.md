# AiChat
This is a chat application that uses the OpenAI API to generate AI-based responses.

### Configuration
To use this application, you need to configure an OpenAI API key:

1. Create or edit the local.properties file in the root of the project.
2. Add the following line with your API key:


    openai.api.key=your_api_key_here


### Obtaining an OpenAI API Key
If you don't have an OpenAI API key, you can get one by following these steps:

    1. Sign up or log in to OpenAI.
    2. Go to the API Keys section on the platform.
    3. Create a new API key.
    4. Copy the key and add it to your local.properties file.

### Features
    - Real-time chat with OpenAI's AI
    - Intuitive user interface with Material Design 3
    - Conversation management:
        - Side menu with a list of all conversations
        - Create new conversations
        - Select and switch between conversations
        - Delete any conversation
        - AI-generated titles for conversations based on first messages
    - Support for long messages
    - Loading indicator during response generation

## Technologies used
- Kotlin
- Jetpack Compose
- Material 3
- Hilt (Dependency Injection)
- MVVM (Architecture Components)
- Room Database (for local storage)
- OpenAI API (library to connect with OpenAi)
- Ktor (HTTP Client)
- Clean Architecture
