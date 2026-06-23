# Level Up Bot — Android Project

**Package:** `com.zuyceo.levelupbot`  
**Creator:** ZuyCEO  
**Version:** 1.0  

---

## How to Build APK

### Requirements
- Android Studio (latest)
- JDK 17+
- Android SDK 34

### Steps

1. Open **Android Studio**
2. `File → Open` → Select this `LevelUpBot` folder
3. Wait for Gradle sync to finish
4. `Build → Generate Signed Bundle / APK → APK`
5. Or for debug: `Build → Build Bundle(s) / APK(s) → Build APK(s)`

---

## App Flow

```
Launch → Splash Screen (5 sec, LEVEL UP BOT in Blue)
       → Auth Screen (Enter Username & Password)
       → Main Screen (START / STOP button)
```

---

## Authentication Credentials

Change these in `AuthActivity.kt`:
```kotlin
private val VALID_USERNAME = "admin"
private val VALID_PASSWORD = "levelup2024"
```

---

## SOCKS5 Proxy Config

**IP and Port are hidden** — set in `ProxyConfig.kt` (encoded, not visible in UI).

Current hardcoded values:
- **Host:** 127.0.0.1  
- **Port:** 1080  

**To change IP/Port:**  
Edit `ProxyConfig.kt`:
```kotlin
internal val H = intArrayOf(49,50,55,46,48,46,48,46,49)  // <- IP as ASCII codes
internal val P = 1080                                       // <- Port number
```

**To convert your IP to ASCII array:**
```
IP: 192.168.1.100
→ '1'=49, '9'=57, '2'=50, '.'=46, '1'=49, '6'=54, '8'=56 ...
```

---

## SOCKS5 Proxy Auth

Proxy-level auth (for connecting clients) is in `Socks5Server.kt`:
```kotlin
private val AUTH_USERNAME = "proxy_user"
private val AUTH_PASSWORD = "secure_pass_2024"
```

---

## Features

- Blue animated splash screen (5 seconds)
- App authentication screen
- SOCKS5 proxy server with client authentication
- Start / Stop with live status
- Foreground service (keeps running in background)
- IP/Port hidden from UI
- About screen (Creator: ZuyCEO)

---

## Files Structure

```
app/src/main/
├── java/com/zuyceo/levelupbot/
│   ├── ProxyConfig.kt      ← Hardcoded IP/Port (hidden)
│   ├── SplashActivity.kt   ← 5-sec blue splash
│   ├── AuthActivity.kt     ← Login screen
│   ├── MainActivity.kt     ← Main screen (Start/Stop)
│   ├── AboutActivity.kt    ← About ZuyCEO
│   ├── ProxyService.kt     ← Foreground service
│   └── Socks5Server.kt     ← SOCKS5 implementation
├── res/layout/             ← UI XML files
├── res/drawable/           ← Shapes & gradients
├── res/values/             ← Colors, strings, themes
└── res/anim/               ← Animations
```
# Sock5-LEVEL-UP
