# Voice Reader App

Voice Reader is a native Android application that allows users to read EPUB, PDF, and plain text documents with text-to-speech functionality. It features male/female voice options, bookmarking, and table of contents navigation.

## Features

*   **Document Support**: Reads EPUB, PDF, and TXT files.
*   **Text-to-Speech**: Utilizes Android's built-in TTS engine with customizable voice (male/female), speed, and pitch.
*   **Bookmarks**: Automatically saves reading progress and allows manual bookmark creation.
*   **Table of Contents (TOC)**: Navigates through document chapters or sections.
*   **Mobile-First UI**: Responsive design built with Jetpack Compose for a seamless experience on various Android devices.
*   **Offline Reading**: All processing happens on-device, no internet connection required after initial setup.

## Technologies Used

*   **Kotlin**: Primary programming language for Android development.
*   **Jetpack Compose**: Modern Android UI toolkit for building native UIs.
*   **Room Persistence Library**: For local database storage (bookmarks, document history).
*   **EPUBLib**: For parsing EPUB documents.
*   **PDFBox Android**: For extracting text from PDF documents.
*   **Android TextToSpeech API**: For text-to-speech functionality.
*   **MVVM Architecture**: Clean and maintainable code structure.

## Installation and Setup

Follow these steps to set up and run the Voice Reader app on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK)**: Version 11 or higher.
*   **Android Studio**: The official IDE for Android development. Download it from [developer.android.com/studio](https://developer.android.com/studio).
*   **Android SDK**: Android API Level 24 (Android 7.0 Nougat) or higher. Android Studio will help you install this.

### Step-by-Step Installation

1.  **Clone the Repository (or ensure you have the project files)**:
    If you received this project as a ZIP file, extract it to your desired directory. If this were a Git repository, you would clone it using:
    ```bash
    git clone https://github.com/your-username/voicereader.git
    cd voicereader
    ```
    *(Note: This is a placeholder command. You already have the project files in `/home/akamalov/projects/voicereader`)*

2.  **Open the Project in Android Studio**:
    *   Launch Android Studio.
    *   From the Welcome screen, select "Open an existing Android Studio project" or go to `File` > `Open...` from the menu bar.
    *   Navigate to the root directory of the `voicereader` project (e.g., `/home/akamalov/projects/voicereader`) and click `OK`.
    *   Android Studio will import the project and sync Gradle files. This may take some time depending on your internet connection and system performance.

3.  **Install Required SDK Components (if prompted)**:
    *   Android Studio might prompt you to install missing SDK components or update Gradle. Follow the instructions provided by Android Studio.

4.  **Build the Project**:
    *   Once Gradle sync is complete, build the project by going to `Build` > `Make Project` in the Android Studio menu bar, or click the hammer icon in the toolbar.

5.  **Run the App on an Emulator or Physical Device**:

    *   **Using an Android Emulator**:
        *   In Android Studio, click on the AVD Manager icon (a small Android phone with a blue arrow) in the toolbar, or go to `Tools` > `Device Manager`.
        *   Create a new Virtual Device if you don't have one. Choose a device definition and a system image (API Level 24 or higher is recommended).
        *   Start the emulator.
        *   Once the emulator is running, select it from the device dropdown in the Android Studio toolbar and click the `Run 'app'` button (green play icon).

    *   **Using a Physical Android Device**:
        *   Enable Developer Options and USB Debugging on your Android device. (Go to `Settings` > `About phone`, then tap `Build number` seven times. Then go to `Settings` > `System` > `Developer options` and enable `USB debugging`).
        *   Connect your device to your computer via a USB cable.
        *   If prompted, allow USB debugging on your device.
        *   Select your connected device from the device dropdown in the Android Studio toolbar.
        *   Click the `Run 'app'` button (green play icon).

The app will now be installed and launched on your selected emulator or physical device.

## Usage

1.  **Add Documents**: On the main screen, click the "Add Document" button (floating action button with a plus icon) to open your device's file picker. Select an EPUB, PDF, or TXT file.
2.  **Read Document**: The selected document will appear in your library. Tap on a document to open it in the reader.
3.  **Text-to-Speech**: Use the audio controls at the bottom of the reader screen to play, pause, rewind, or fast-forward the narration.
4.  **Voice Options**: Access voice settings from the settings screen to change the voice gender, speech rate, and pitch.
5.  **Bookmarks**: The app automatically saves your last reading position. You can also manually add bookmarks with notes from the reader screen.
6.  **Table of Contents**: Navigate chapters or sections using the Table of Contents button in the reader's top app bar.

## Contributing

*(This section is a placeholder. If this were an open-source project, you would include guidelines for contributions here.)*

## License

*(This section is a placeholder. You would typically specify the license under which the project is distributed here.)*
