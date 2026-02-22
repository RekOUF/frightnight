# AI Context - Fright Night Android Game

## Project Overview
**Project Name**: Fright Night  
**Repository**: https://github.com/RekOUF/frightnight  
**Current Version**: v2.6  
**Last Updated**: February 22, 2026  

A horror-themed survival Android game where players must survive the night by avoiding scary monsters.

---

## Recent Changes

### 2026-02-22: Volumetric Clouds, Lightning/Thunder System & Flying Birds

#### 9. Realistic Atmospheric Effects
- ✅ Created `VolumetricCloud.java` - Realistic 3D clouds using multiple overlapping spheres
- ✅ Created `LightningSystem.java` - Dramatic lightning with thunder sound effects
- ✅ Created `FlyingBird.java` - Animated birds with wing-flapping animation
- ✅ Integrated all systems into `FrightNightGame3D.java`
- ✅ Moved sound files to LibGDX assets directory

**Volumetric Clouds**:
- **12 clouds** floating in the sky at varying heights (30-55m)
- Each cloud made of **8-14 spherical puffs** with random sizes and positions
- Semi-transparent purple-tinted dusk appearance (alpha 0.5-0.8)
- **Drifting animation**: Clouds slowly drift across sky at random speeds/directions
- Wrap-around: Clouds reappear on opposite side when drifting off-screen
- Irregular puffy shapes create realistic volumetric effect
- Total: **~100 cloud puff instances** added to scene

**Lightning & Thunder System**:
- **Random lightning strikes** every 5-15 seconds in the distance
- Lightning appears 80-130 units away from player at random angles
- **Screen flash effect**: White overlay fades out over 0.2 seconds
- **Thunder rumble sound** (thunder.ogg) plays with realistic delay (0.3-0.8s) based on distance
- **Lightning crack sound** (lightning.ogg) plays instantly at strike
- Creates jump scare potential and enhances horror atmosphere
- Lightning bolt rendered as jagged line from sky (40-60m) to ground

**Flying Birds**:
- **6 birds** (crows/ravens) flying through the sky at various heights (25-55m)
- Each bird has **3 model parts**: body (ellipsoid) + left wing + right wing
- **Wing-flapping animation**: Smooth sinusoidal wing movement at 3 flaps/second
- **Flight patterns**:
  - 50% circle in the air (like birds of prey)
  - 50% fly straight toward distant targets
  - Undulating flight paths (bob up/down)
- 70% dark crows (almost black) for horror effect
- Birds face direction of movement and rotate properly
- Wrap-around: Birds reappear if they fly too far

**Technical Implementation**:
```java
// VolumetricCloud
- 8-14 random spheres per cloud with varying sizes (2-5 units)
- BlendingAttribute for semi-transparency
- Drift speed: 0.5-2.0 units/second
- Purple-tinted material (0.7, 0.6, 0.7)

// LightningSystem  
- Screen flash using ShapeRenderer overlay
- Asymmetric audio: crack (instant) + thunder (delayed)
- Random strike positions around player
- Flash intensity fades exponentially

// FlyingBird
- Wing flap: sin(time * 3 * PI * 2) * 45° angle
- Circular flight: radius 20-50 units
- Straight flight: picks random distant targets
- Body: 0.4x0.8x0.3 ellipsoid, Wings: 2.5x0.1x0.8 flat boxes
```

**Performance Impact**:
- Instance count: 98 → **234 instances** (+136)
  - Clouds: ~100 puff instances
  - Birds: 18 instances (6 birds × 3 parts)
  - Rest: original trees, fence, etc.
- Sound files: +2.5MB (thunder.ogg + lightning.ogg)
- Rendering: Transparent objects require depth sorting (handled by LibGDX)

**Gameplay Impact**:
- **Immersive atmosphere**: Realistic sky with moving elements
- **Jump scares**: Sudden lightning flashes startle players
- **Environmental storytelling**: Ominous crows circling overhead
- **Dynamic world**: Sky no longer static, feels alive
- **Audio cues**: Thunder warns of incoming weather threats
- **Visual variety**: Each playthrough has different cloud/bird patterns

---

### 2026-02-21: First-Person Camera System & LibGDX 3D Engine

#### 8. First-Person Camera Implementation
- ✅ Created `FirstPersonController.java` - Full first-person camera system
- ✅ Touch drag camera rotation (right half of screen)
- ✅ Pitch and yaw rotation with constraints (-89° to +89° pitch)
- ✅ Movement via joystick (left half of screen)
- ✅ Smooth camera look controls for mobile
- ✅ Updated `FrightNightGame3D.java` to use FPS controller
- ✅ Updated `GameInputProcessor.java` for FPS input handling
- ✅ Removed old button-based camera rotation system

**First-Person Features**:
- **Camera Look**: Touch and drag on right side of screen to look around
- **Movement**: Virtual joystick on left side for forward/backward/strafe
- **Pitch Constraints**: Prevents camera flipping (limited to -89° to +89°)
- **Yaw Rotation**: Full 360° horizontal rotation
- **Configurable Sensitivity**: Adjustable look sensitivity (default 0.15 for mobile)

**Technical Implementation**:
```java
// FirstPersonController handles:
- touchDown() - Detects camera look area (right 60% of screen)
- touchDragged() - Calculates delta and updates yaw/pitch
- update() - Updates camera position and direction
- move() - Handles player movement in 3D space
- clampPosition() - Keeps player within world boundaries
```

**Input System**:
- Left side: Virtual joystick for movement
- Right side: Touch drag for camera rotation
- Multi-touch support (simultaneous movement + look)
- Back button: Exit to MainActivity

**Game Design Impact**:
- **Immersive Horror**: First-person view creates tension and fear
- **Limited Visibility**: Player can only see what they're looking at
- **Jump Scares**: Enemies can appear from any direction
- **Exploration**: Natural way to explore the scary 3D environment

**LibGDX 3D Integration**:
- Uses LibGDX native libraries (libgdx.so, libgdx-bullet.so, libgdx-freetype.so)
- Bullet Physics 3D ready for collision detection
- gdx-ai ready for enemy pathfinding and behavior trees
- Box2DLights for atmospheric lighting effects
- FreeType for custom horror fonts

---

### 2026-02-20: Night Scene Transformation & Version Management

#### 6. Dark Horror Atmosphere with Lightning & Thunder
- ✅ Transformed day scene to eerie night scene
- ✅ Replaced sun with full moon (detailed with craters, glowing aura)
- ✅ Changed sky from bright blue to dark night (#0A0A1E)
- ✅ Dark ominous clouds replace white fluffy clouds
- ✅ Dark grass (#0F2F0F) instead of bright green
- ✅ **Lightning System**: Periodic lightning strikes (5-10s intervals)
  - Lightning forms "FRIGHTNIGHT" text in the sky
  - Electric glow effect (blue/white/yellow layered)
  - White flash covers entire screen during strike
  - Synchronized with thunder sound
- ✅ **Thunder Sound Effects**: 
  - Generated ambient thunder sound (3s duration)
  - Low frequency rumble for atmospheric scare
  - Plays during splash screen boot
  - Stored as `res/raw/thunder.ogg`

**Visual Details**:
- Night sky: `#0A0A1E` (very dark blue/purple)
- Dark grass: `#0F2F0F` (almost black green)  
- Moon: `#E0E0E0` with `#505070` glow
- Dark clouds: `#1A1A2E` with darker bottom edges
- Lightning flash: Fading white overlay (alpha 80→0)
- Lightning text: Layered glow effect (blue→white→yellow core)

**Atmosphere Impact**:
- Dramatically increases horror tension
- Lightning illuminates world periodically
- Thunder creates jump scare potential
- "FRIGHTNIGHT" text reinforces theme
- Perfect setup for future horror mechanics

#### 7. Splash Screen with Version Checking
- ✅ Version check moved from MainActivity to SplashActivity
- ✅ Automatic GitHub API check during app boot
- ✅ Thunder sound plays during 3-second splash
- ✅ Update dialog shows if newer version available
- ✅ Non-blocking: waits for both splash duration AND version check
- ✅ Updated version system to v1.1:
  - `build.gradle`: `versionCode 2`, `versionName "1.1"`
  - `VersionChecker.CURRENT_VERSION`: "1.1"

**Version Management Workflow**:
1. Update `VersionChecker.CURRENT_VERSION` constant
2. Update `build.gradle` versionCode and versionName
3. Build and test APK
4. Create GitHub release: `gh release create v1.1 app-debug.apk`
5. Next app launch will detect update and prompt user

**Technical Implementation**:
- `SplashActivity` runs version check asynchronously
- Uses boolean flags `versionChecked` and `splashComplete`
- Only proceeds to MainActivity when both complete
- Shows localized Dutch update dialog
- MediaPlayer for thunder sound with low volume (0.3)
- Proper resource cleanup in `onDestroy()`

### 2026-02-20: Scary Level System - Transform to Schrik-Spel (Scare Game)

#### 5. Scary Level Selector (0-10)
- ✅ Added SeekBar in MainActivity for horror intensity selection
- ✅ 11 levels from "Vrij Wandelen" (0) to "Pure Horror" (10)
- ✅ Descriptive Dutch labels with emojis for each level
- ✅ Saved in SharedPreferences, persists between sessions
- ✅ GameView respects scary level:
  - **Level 0**: Peaceful exploration mode (no enemies, no power-ups)
  - **Level 1-10**: Progressive horror with enemies and dangers

**Game Philosophy Change**:
- NOT a kill game - it's a **SCHRIK-spel** (scare game)
- Focus on psychological horror, jump scares, tension
- Level 0 allows players to explore the beautiful but eerie landscape
- Higher levels introduce increasing horror elements

**Scary Level Labels**:
```
0 - Vrij Wandelen 🌳      (Free exploration)
1 - Beetje Spooky 👻      (A bit spooky)
2 - Licht Onrustig 😰     (Slightly uneasy)
3 - Verdacht 🤨           (Suspicious)
4 - Eng Geluid 🔊         (Scary sounds)
5 - Schaduwen 🌑          (Shadows)
6 - Spanning 😨           (Tension)
7 - Gevaarlijk 💀         (Dangerous)
8 - Terrificerend 😱      (Terrifying)
9 - Nachtmerrie 🔥        (Nightmare)
10 - Pure Horror 💀🔥     (Pure horror)
```

**Technical Implementation**:
- SeekBar with 11 positions (0-10)
- Real-time label updates as user drags
- Automatic save on release
- GameView checks `scaryLevel` before spawning enemies
- UI shows "Exploration" instead of score in level 0
- Score mechanics disabled in exploration mode

**Future Horror Features** (Planned):
- **Jump Scares**: Zombies suddenly appearing from behind trees
- **Sound Effects**: Ambient horror sounds based on level
- **Dynamic Spawning**: More frequent/aggressive enemies at higher levels
- **Visual Effects**: Fog, darkness, shadows increase with level
- **Random Events**: Creepy occurrences based on scary level
- **Player Fear Mechanic**: Screen shake, heartbeat sounds
- **Time-based Scares**: Things get scarier at "night" in-game

### 2026-02-20: Landscape Environment & Camera System - Major Visual Upgrade

#### 4. World System with Camera and Landscape
- ✅ Created `Camera.java` - Viewport management that follows the player
- ✅ Created `Landscape.java` - Full environment renderer (2400x1600 world)
- ✅ Updated all entity classes to use world coordinates
- ✅ Implemented boundary system with fence collision detection
- ✅ **Updated to Night Scene** (see section 6 above for details)

**New Features**:
- **Large Open World**: 2400x1600 pixels (~4 football fields)
- **Dark Terrain**: Dark grass (#0F2F0F) creates ominous atmosphere
- **Road System**: Horizontal road (150px wide) with yellow dashed lines
- **Scary Trees**: 30+ trees with evil glowing eyes (yellow/red)
  - Trees along road edges
  - Trees along fence perimeter
- **Fence Boundaries**: Brown wooden fence (50px margin from world edge)
  - Player cannot pass through - collision detection
  - Visual posts every 20 pixels
- **Night Sky Elements**:
  - Full moon in top-right with glow and crater details
  - 12 dark ominous clouds that drift slowly
  - Dark night sky background (#0A0A1E)
  - Periodic lightning forming "FRIGHTNIGHT" text
- **Camera System**: Smooth following of player, clamped to world bounds

**Technical Implementation**:
- `Camera` class converts between world and screen coordinates
- `Landscape.isInBounds()` validates player movement
- Touch events translated from screen to world coordinates
- Enemies spawn around player (400px away), not at screen edges
- Entities only rendered when visible in viewport (performance optimization)

**Gameplay Impact**:
- Players can freely roam large open area
- Enemies chase player anywhere in world
- Power-ups spawn near player position
- Camera creates cinematic feeling as player explores

### 2026-02-20: GitHub Integration, Auto-Update System & Copilot Instructions

#### 3. AI Copilot Instructions
- ✅ Created `.github/copilot-instructions.md` for AI coding agents
- Concise, actionable guide (~58 lines) covering architecture, patterns, and workflows
- Focuses on unique aspects: SurfaceView game loop, entity management, version system
- Includes build commands, conventions, and integration points

### 2026-02-20: GitHub Integration & Auto-Update System

#### 1. Git & GitHub Repository Setup
- ✅ Initialized Git repository
- ✅ Created initial commit with all game files
- ✅ Created public GitHub repository: `RekOUF/frightnight`
- ✅ Pushed code to GitHub using gh CLI
- ✅ Created initial release: v1.0

**Commands Used**:
```bash
git init
git add .
git commit -m "Initial commit: Fright Night Android game"
gh repo create frightnight --public --source=. --description="A horror-themed survival Android game" --push
gh release create v1.0 --title "Fright Night v1.0" --notes "Initial release..."
```

#### 2. Auto-Update System Implementation

**New Files Created**:
- `VersionChecker.java` - Handles version checking via GitHub API

**Modified Files**:
- `AndroidManifest.xml` - Added internet permissions
- `MainActivity.java` - Added update checking and dialog

**Features Implemented**:
- ✅ Automatic version checking on app startup
- ✅ Compares local version with latest GitHub release
- ✅ Shows update dialog when new version is available
- ✅ Opens GitHub release page in browser for download
- ✅ Graceful error handling (silent fail if network unavailable)

**Technical Details**:
1. **Internet Permissions**: Added to AndroidManifest.xml
   - `INTERNET` - For GitHub API calls
   - `ACCESS_NETWORK_STATE` - For network status checking

2. **VersionChecker Class**:
   - Uses GitHub API endpoint: `https://api.github.com/repos/RekOUF/frightnight/releases/latest`
   - Async task implementation for non-blocking network calls
   - Semantic version comparison (e.g., 1.0 vs 1.1, 2.0)
   - Returns: update availability, latest version, and download URL

3. **Update Dialog**:
   - Shown automatically on MainActivity startup
   - Two options: "Update" (opens browser) or "Later" (dismisses)
   - Non-intrusive - allows app usage even if update is skipped

---

## Project Structure

```
frightnight/
├── app/
│   ├── build.gradle               # App-level Gradle config
│   ├── src/main/
│   │   ├── AndroidManifest.xml    # App manifest with permissions
│   │   ├── java/com/frightnight/game/
│   │   │   ├── MainActivity.java      # Main menu + update checker
│   │   │   ├── GameActivity.java      # Game screen container
│   │   │   ├── GameView.java          # Game loop and logic
│   │   │   ├── Camera.java            # Viewport/camera system [NEW]
│   │   │   ├── Landscape.java         # World renderer (terrain, trees, sky) [NEW]
│   │   │   ├── Player.java            # Player character (world coords)
│   │   │   ├── Enemy.java             # Monster enemy (world coords)
│   │   │   ├── PowerUp.java           # Power-up collectible (world coords)
│   │   │   └── VersionChecker.java    # GitHub version checking
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_main.xml  # Main menu layout
│   │       │   └── activity_game.xml  # Game layout
│   │       └── values/
│   │           ├── strings.xml
│   │           ├── colors.xml
│   │           └── themes.xml
├── build.gradle               # Project-level Gradle config
├── settings.gradle            # Gradle settings
├── gradle.properties          # Gradle properties
├── .github/
│   └── copilot-instructions.md # AI coding agent instructions
├── README.md                  # Project documentation
└── ai.context.md             # This file - AI context
```

---

## How to Create a New Release

When you want to release a new version:

1. **Update Version in Code**:
   - Edit `VersionChecker.java` line 14: `CURRENT_VERSION = "X.Y"`
   - Edit `app/build.gradle`:
     - Increment `versionCode` (e.g., 1 → 2)
     - Update `versionName` (e.g., "1.0" → "1.1")

2. **Commit Changes**:
   ```bash
   git add .
   git commit -m "Release vX.Y: Description of changes"
   git push
   ```

3. **Create GitHub Release**:
   ```bash
   gh release create vX.Y --title "Fright Night vX.Y" --notes "Release notes here"
   ```

4. **Users Get Notified**: Next time users open the app, they'll see the update dialog!

---

## GitHub CLI Commands Reference

### Repository Management
```bash
# Check authentication
gh auth status

# Create repository
gh repo create <name> --public --source=. --description="..." --push

# View repository
gh repo view
```

### Release Management
```bash
# Create release
gh release create vX.Y --title "Title" --notes "Notes"

# Create release with file
gh release create vX.Y app/release/app-release.apk --title "Title" --notes "Notes"

# List releases
gh release list

# View latest release
gh release view --json tag_name,html_url
```

### Other Useful Commands
```bash
# View issues
gh issue list

# Create issue
gh issue create --title "Bug report" --body "Description"

# View pull requests
gh pr list
```

---

## Version Checking Flow

```
App Startup (MainActivity.onCreate)
         ↓
    checkForUpdates()
         ↓
VersionChecker.checkForUpdate()
         ↓
GitHub API Call (async)
api.github.com/repos/RekOUF/frightnight/releases/latest
         ↓
    Compare Versions
    (current vs latest)
         ↓
   Update Available?
    /           \
  YES            NO
   ↓              ↓
Show Dialog    Continue
   ↓           (silent)
User Choice
 /      \
Update  Later
 ↓       ↓
Browser Dismiss
```

---

## API Endpoints Used

### GitHub Releases API
- **Endpoint**: `https://api.github.com/repos/RekOUF/frightnight/releases/latest`
- **Method**: GET
- **Headers**: `Accept: application/vnd.github.v3+json`
- **Response**: JSON with release info
  ```json
  {
    "tag_name": "v1.0",
    "html_url": "https://github.com/RekOUF/frightnight/releases/tag/v1.0",
    "name": "Fright Night v1.0",
    "body": "Release notes...",
    ...
  }
  ```

---

## Testing the Update System

### Test Scenario 1: No Update Available
1. Current version: 1.0
2. Latest release: v1.0
3. Expected: No dialog, app continues normally

### Test Scenario 2: Update Available
1. Current version: 1.0
2. Create new release: v1.1 (using `gh release create v1.1 ...`)
3. Restart app
4. Expected: Update dialog appears with "v1.1"
5. Click "Update": Browser opens to GitHub release page
6. Click "Later": Dialog dismisses, app continues

### Test Scenario 3: Network Unavailable
1. Disable internet connection
2. Start app
3. Expected: No dialog, silent fail, app continues normally

---

## Future Enhancements

### Potential Features
- [ ] **Auto-download APK**: Download and install APK directly via app
- [ ] **In-app changelog**: Show release notes in the update dialog
- [ ] **Update frequency control**: Check once per day instead of every launch
- [ ] **Skip version**: Allow users to skip specific versions
- [ ] **Background update check**: Check after 5-10 minutes instead of startup
- [ ] **Beta releases**: Support for beta channel with alpha/beta releases

### Technical Improvements
- [ ] **Migrate to GitHub Actions**: Auto-build APK on release
- [ ] **Code signing**: Sign APK releases for distribution
- [ ] **Crash reporting**: Integrate Firebase Crashlytics
- [ ] **Analytics**: Track update adoption rates

---

## Dependencies

### Current Dependencies (app/build.gradle)
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

### No External Libraries Needed for Update System
The version checking system uses only Android built-in classes:
- `AsyncTask` - Background network operations
- `HttpURLConnection` - HTTP requests
- `JSONObject` - JSON parsing
- `AlertDialog` - Update dialog

---

## Security Considerations

### Permissions
- `INTERNET`: Required for GitHub API calls
- `ACCESS_NETWORK_STATE`: Check if network is available before making requests

### API Security
- Uses HTTPS for all GitHub API calls
- GitHub API is public and requires no authentication for public repos
- No sensitive data transmitted

### Update Security
- Opens official GitHub release page in browser
- Users manually download from trusted source (GitHub)
- Future: Consider APK signature verification if implementing auto-download

---

## Troubleshooting

### Update Dialog Not Appearing
1. Check internet connection
2. Verify GitHub release exists: `gh release list`
3. Check logcat for errors: `adb logcat -s MainActivity:D VersionChecker:E`
4. Ensure `INTERNET` permission is in AndroidManifest.xml

### Version Comparison Issues
- Version format must be semantic: `X.Y` or `X.Y.Z`
- GitHub release tag should be: `vX.Y` (e.g., `v1.0`, `v2.1`)
- Code automatically strips the 'v' prefix for comparison

### GitHub CLI Issues
```bash
# Re-authenticate if needed
gh auth login

# Check repository access
gh repo view

# Verify release creation
gh release list
```

---

## Git Workflow

### Daily Development
```bash
# Make changes to code
git add .
git commit -m "Descriptive message"
git push
```

### Creating a Release
```bash
# Update version in code first!
# Then:
git add .
git commit -m "Bump version to X.Y"
git push
gh release create vX.Y --title "Fright Night vX.Y" --notes "Changes..."
```

### Branch Strategy
Current: Single `master` branch
Future: Consider `develop` + `main` with feature branches

---

## Notes for AI Assistant

### When User Requests Updates
- Always update `CURRENT_VERSION` in VersionChecker.java
- Update `versionCode` and `versionName` in app/build.gradle
- Commit and create GitHub release using `gh release create`
- Update this ai.context.md file with changes

### Code Style
- Java 8 compatibility
- Android API 24+ (Android 7.0+)
- Use AsyncTask (deprecated but works for simple cases)
- Material Design components

### Testing
- Test on emulator or physical device
- Verify internet permission works
- Test update flow with actual GitHub releases

---

## Contact & Repository Info

- **GitHub Repo**: https://github.com/RekOUF/frightnight
- **Owner**: RekOUF
- **License**: Not specified
- **Android Target**: API 34 (Android 14)
- **Min Android**: API 24 (Android 7.0)

---

## Changelog

### v1.0 (2026-02-20)
- Initial release
- Survival and exploration gameplay
- Monster AI with pathfinding (enabled via Scary Level)
- Power-up system with invincibility
- **Scary Level system (0-10)**: Choose your horror intensity
- **Exploration Mode (Level 0)**: Peaceful walking, no enemies
- Score and high score tracking (survival mode only)
- Touch controls for movement
- GitHub integration with auto-update checking
- **Landscape environment** with grass, road, scary trees, fence
- **Camera system** for large world exploration (2400x1600)
- **Sky elements** (sun and animated clouds)
- **Scary trees** with glowing eyes
- **Boundary collision** (fence system)
- **Schrik-spel philosophy**: Focus on scares, not kills

---

*This file is maintained by AI to provide context for future development sessions.*
