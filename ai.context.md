# AI Context - Fright Night Android Game

## Project Overview
**Project Name**: Fright Night  
**Repository**: https://github.com/RekOUF/frightnight  
**Current Version**: v1.0  
**Last Updated**: February 20, 2026  

A horror-themed survival Android game where players must survive the night by avoiding scary monsters.

---

## Recent Changes

### 2026-02-20: Landscape Environment & Camera System - Major Visual Upgrade

#### 4. World System with Camera and Landscape
- ✅ Created `Camera.java` - Viewport management that follows the player
- ✅ Created `Landscape.java` - Full environment renderer (2400x1600 world)
- ✅ Updated all entity classes to use world coordinates
- ✅ Implemented boundary system with fence collision detection

**New Features**:
- **Large Open World**: 2400x1600 pixels (~4 football fields)
- **Grass Terrain**: Green grass background fills the entire world
- **Road System**: Horizontal road (150px wide) with yellow dashed lines
- **Scary Trees**: 30+ trees with evil glowing eyes (yellow/red)
  - Trees along road edges
  - Trees along fence perimeter
- **Fence Boundaries**: Brown wooden fence (50px margin from world edge)
  - Player cannot pass through - collision detection
  - Visual posts every 20 pixels
- **Sky Elements**:
  - Bright sun in top-right corner with glow effect
  - 8 animated clouds that drift slowly eastward
  - Sky blue gradient background
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
- Basic survival gameplay
- Monster AI with pathfinding
- Power-up system with invincibility
- Score and high score tracking
- Touch controls
- GitHub integration
- Auto-update checking system
- **Landscape environment** with grass, road, trees, fence
- **Camera system** for large world exploration (2400x1600)
- **Sky elements** (sun and animated clouds)
- **Scary trees** with glowing eyes
- **Boundary collision** (fence system)

---

*This file is maintained by AI to provide context for future development sessions.*
