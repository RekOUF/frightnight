# Fright Night - Android Game Copilot Instructions

## Architecture Overview
This is a native Android game using **SurfaceView with a custom game loop** (not a game engine). Key pattern: `GameView` implements `Runnable` with `update()→draw()→sleep()` cycle at ~60 FPS (17ms sleep). Thread management is critical - always handle `pause()`/`resume()` lifecycle properly.

**World System**: Large 2400x1600 pixel world (~4 football fields) with camera following player. Touch coordinates converted from screen space to world space.

**Game Philosophy**: This is a **SCHRIK-spel** (scare game), NOT a kill game. Focus is on psychological horror, jump scares, and tension building. Players can choose horror intensity (0-10).

## Core Game Loop (GameView.java)
- **Camera System**: `Camera` tracks player, converts world↔screen coordinates. Camera clamped to world boundaries.
- **Scary Level System**: `scaryLevel` (0-10) controls enemy spawning and game intensity. Level 0 = exploration mode (no enemies).
- **Entity Management**: `ArrayList<Enemy>` and `ArrayList<PowerUp>` updated backwards (`i--`) for safe removal during iteration
- **Conditional Spawning**: Enemies/power-ups only spawn when `scaryLevel > 0`. Time-based spawning (2s enemies, 5s power-ups).
- **Smart Spawning**: Enemies spawn around player (400px radius), not screen edges
- **Collision Detection**: Uses `Rect.intersects()` - each entity has `getBounds()` method returning `Rect`
- **Score System**: Only active in survival mode (level > 0). +1/frame survival, +10 per escaped enemy, +50 per power-up
- **Landscape Rendering**: `Landscape.draw()` renders sky, grass, road, trees, fence using camera viewport

## World & Camera Pattern
```java
// Camera follows player
camera.centerOn(player.getX(), player.getY());

// Drawing with world coordinates
int screenX = camera.worldToScreenX(entity.x);
canvas.drawCircle(screenX, screenY, size, paint);

// Touch input conversion
float worldX = camera.screenToWorldX(event.getX());
player.moveTo(worldX, worldY);
```

## Entity Classes Pattern
All entities (Player, Enemy, PowerUp) follow consistent interface:
```java
void update()             // Movement/state logic (Player: update(Landscape))
void draw(Canvas, Paint, Camera)  // Rendering with camera transformation
boolean collidesWith(Player)
Rect getBounds()          // For collision detection
```

## Landscape System (Landscape.java)
- **Static world size**: `WORLD_WIDTH = 2400`, `WORLD_HEIGHT = 1600`
- **Fence boundaries**: 50px margin, enforced via `isInBounds(x, y, margin)`
- **Night Scene**: Dark sky (#0A0A1E), full moon with glow, dark grass (#0F2F0F), ominous dark clouds
- **Lightning System**: Periodic lightning (5-10s intervals) forming "FRIGHTNIGHT" text in sky, with white flash effect
- **Elements**: Dark terrain, horizontal road (150px wide), 30+ trees with glowing evil eyes, fence posts, moon, animated clouds
- **Player boundaries**: `Player.update(landscape)` checks `landscape.isInBounds()` before moving
- **Tree generation**: Random placement avoiding road, perimeter trees along fence
- **Atmosphere**: Psychological horror ambiance with lightning flashes and thunder sounds

## State Persistence
Uses `SharedPreferences` (not Room/SQLite). Key: `"FrightNightPrefs"`, stores: 
- `highScore` (int) - Best survival score
- `scaryLevel` (int 0-10) - Horror intensity preference
Always use `editor.apply()` not `commit()`.

## Version Management System
- **Current version**: Update `VersionChecker.CURRENT_VERSION` constant when releasing
- **Gradle versions**: Sync `app/build.gradle` (`versionCode`/`versionName`) with VersionChecker
- **GitHub workflow**: `gh release create vX.Y` triggers update notifications
- Auto-update check runs on `SplashActivity.onCreate()` during boot screen via GitHub API
- Shows `AlertDialog` if newer version exists, allows download or skip
- Version check completes asynchronously, waits for both splash duration and check before proceeding

## Activity Lifecycle & Threading
- `SplashActivity`: Boot screen with thunder sound, version checking, 3-second display before MainActivity
- `GameActivity`: Fullscreen container, **must** call `gameView.pause()/resume()` in lifecycle methods
- `MainActivity`: Menu with scary level selector, returns here on game over via `finish()`
- Game thread created in `resume()`, joined in `pause()` - missing this causes crashes or ANRs

## Build & Test Commands
```bash
./gradlew assembleDebug          # Build APK
./gradlew installDebug            # Install to device/emulator
adb logcat -s GameView:D          # Debug game loop issues
```

## Project-Specific Conventions
- **Color scheme**: Night sky (#0A0A1E), dark grass (#0F2F0F), road gray (#505050), dark trees (#0A3D0A), moon (#E0E0E0)
- **Coordinates**: World coordinates (0-2400, 0-1600), camera translates to screen space
- **No external dependencies**: Pure Android SDK (no game engines, no Gson/Retrofit)
- **Landscape-only**: Set in manifest, design assumes width > height
- **Horror effects**: Lightning strikes every 5-10s forming "FRIGHTNIGHT" text, thunder sound (res/raw/thunder.ogg)
- **New scare mechanics**: Implement jump scares, sound effects, visual effects based on `scaryLevel` value
## Common Modifications
- **Adjust difficulty**: Change `ENEMY_SPAWN_INTERVAL`, `Enemy.speed`, or `Player.INVINCIBILITY_DURATION`
- **New entity type**: Extend entity pattern above, add to GameView ArrayList, update in game loop, add camera param to draw()
- **New power-up**: Modify `PowerUp` class and `Player.activateInvincibility()` method
- **World size**: Update `Landscape.WORLD_WIDTH/HEIGHT` constants, regenerate tree positions
- **New landscape element**: Add to `Landscape.draw()` with camera coordinate conversion

## Integration Points
- **GitHub API**: `VersionChecker` uses `HttpURLConnection` (no OkHttp), parses JSON manually
- **No backend**: Fully offline except version checking
- **Internet permissions**: Required in manifest for update checking only

See `ai.context.md` for detailed release workflow and version history.
