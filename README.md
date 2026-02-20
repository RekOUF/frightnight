# Fright Night - Android Game

A horror-themed survival game for Android where you must survive the night by avoiding scary monsters!

## 🎮 Game Features

- **Survive the Night**: Avoid the red monsters that hunt you down
- **Power-Ups**: Collect yellow power-ups for temporary invincibility (flashing cyan/white effect)
- **Score System**: Earn points for surviving and collecting power-ups
- **High Score**: Your best score is saved and displayed on the main menu
- **Touch Controls**: Simply tap or drag to move your character (green circle)

## 🎯 How to Play

1. Launch the game and tap "Start Game"
2. Control the green player by tapping or dragging on the screen
3. Avoid the red monsters with evil yellow eyes - they chase you!
4. Collect yellow pulsating power-ups for temporary invincibility
5. Survive as long as possible to increase your score
6. When you die, tap the screen to return to the main menu

## 📱 Technical Details

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Language**: Java
- **Orientation**: Landscape mode
- **Framework**: Native Android (SurfaceView-based game loop)

## 🏗️ Project Structure

```
frightnight/
├── app/
│   ├── src/main/
│   │   ├── java/com/frightnight/game/
│   │   │   ├── MainActivity.java      # Main menu screen
│   │   │   ├── GameActivity.java      # Game screen container
│   │   │   ├── GameView.java          # Main game loop and logic
│   │   │   ├── Player.java            # Player character
│   │   │   ├── Enemy.java             # Monster enemy
│   │   │   └── PowerUp.java           # Power-up collectible
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml  # Main menu layout
│   │   │   │   └── activity_game.xml  # Game layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml        # String resources
│   │   │   │   ├── colors.xml         # Color definitions
│   │   │   │   └── themes.xml         # App theme
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 🚀 Build Instructions

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator
4. Click "Run" or press Shift + F10

Alternatively, build from command line:
```bash
./gradlew assembleDebug
```

## 🎨 Game Mechanics

- **Player**: Green circle with eyes, moves at 8 units/frame toward touch position
- **Enemies**: Red circles with evil eyes, spawn every 2 seconds from screen edges
- **Power-Ups**: Yellow pulsating circles, spawn every 5 seconds randomly
- **Invincibility**: Lasts 3 seconds when activated
- **Score**: +1 per frame survived, +10 per enemy avoided, +50 per power-up collected
- **Game Loop**: Runs at ~60 FPS

## 🎭 Theme

Dark horror atmosphere with:
- Midnight blue and dark gray color scheme
- Blood red accents
- Ghost white text
- Fullscreen immersive experience

## 📝 Future Enhancements

- Add sound effects and background music
- Multiple enemy types with different behaviors
- Boss battles
- Multiple levels/difficulty progression
- Particle effects
- More power-up types
- Leaderboard integration
- Achievement system

## 📄 License

Free to use and modify for educational purposes.

---

**Survive the night if you dare! 👻**
