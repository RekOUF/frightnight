# Fright Night - Android Game Copilot Instructions

## Architecture Overview
This is a native Android game using **SurfaceView with a custom game loop** (not a game engine). Key pattern: `GameView` implements `Runnable` with `update()→draw()→sleep()` cycle at ~60 FPS (17ms sleep). Thread management is critical - always handle `pause()`/`resume()` lifecycle properly.

## Core Game Loop (GameView.java)
- **Entity Management**: `ArrayList<Enemy>` and `ArrayList<PowerUp>` updated backwards (`i--`) for safe removal during iteration
- **Spawning**: Time-based spawning (2s enemies, 5s power-ups). Enemies spawn off-screen from random edge (see `spawnEnemy()` switch statement)
- **Collision Detection**: Uses `Rect.intersects()` - each entity has `getBounds()` method returning `Rect`
- **Score System**: +1/frame survival, +10 per escaped enemy, +50 per power-up collected

## Entity Classes Pattern
All entities (Player, Enemy, PowerUp) follow consistent interface:
```java
void update()             // Movement/state logic
void draw(Canvas, Paint)  // Rendering with Paint color changes
boolean collidesWith(Player)
Rect getBounds()          // For collision detection
```

## State Persistence
Uses `SharedPreferences` (not Room/SQLite). Key: `"FrightNightPrefs"`, stores only `highScore` as int. Always use `editor.apply()` not `commit()`.

## Version Management System
- **Current version**: Update `VersionChecker.CURRENT_VERSION` constant when releasing
- **Gradle versions**: Sync `app/build.gradle` (`versionCode`/`versionName`) with VersionChecker
- **GitHub workflow**: `gh release create vX.Y` triggers update notifications
- Auto-update check runs on `MainActivity.onCreate()` via GitHub API, shows `AlertDialog` if newer version exists

## Activity Lifecycle & Threading
- `GameActivity`: Fullscreen container, **must** call `gameView.pause()/resume()` in lifecycle methods
- `MainActivity`: Menu + update checker, returns here on game over via `finish()`
- Game thread created in `resume()`, joined in `pause()` - missing this causes crashes or ANRs

## Build & Test Commands
```bash
./gradlew assembleDebug          # Build APK
./gradlew installDebug            # Install to device/emulator
adb logcat -s GameView:D          # Debug game loop issues
```

## Project-Specific Conventions
- **Color scheme**: Dark (#1A1A1A bg), Green player, Red enemies, Yellow power-ups/eyes
- **Coordinates**: Touch events use screen pixels directly, no virtual coordinates
- **No external dependencies**: Pure Android SDK (no game engines, no Gson/Retrofit)
- **Landscape-only**: Set in manifest, design assumes width > height

## Common Modifications
- **Adjust difficulty**: Change `ENEMY_SPAWN_INTERVAL`, `Enemy.speed`, or `Player.INVINCIBILITY_DURATION`
- **New entity type**: Extend entity pattern above, add to GameView ArrayList, update in game loop
- **New power-up**: Modify `PowerUp` class and `Player.activateInvincibility()` method

## Integration Points
- **GitHub API**: `VersionChecker` uses `HttpURLConnection` (no OkHttp), parses JSON manually
- **No backend**: Fully offline except version checking
- **Internet permissions**: Required in manifest for update checking only

See `ai.context.md` for detailed release workflow and version history.
