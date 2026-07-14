# ZeroW App — Super-Detailed Technical Implementation Plan
## Part 2 of 2: AI Layer, Frontend Apps, Offline Mode, Security, Deployment, Testing, Roadmap

> **Continuation from Part 1.** Part 1 covers: Architecture, Tech Stack, Phase Timeline, Database Schema, Backend API, Maps & Geospatial, QR System, Auth/RBAC, Real-Time Communication, Revised Budget, EPR/Legal summary.
>
> **Architecture Rev 2 (July 2026):** Tech stack updated to match actual project implementation:
> - **Mobile:** Kotlin Multiplatform (Compose Multiplatform) — Android, iOS, Web from single codebase
> - **Backend:** Ktor (Kotlin) — coroutines, typed routes, native JSON
> - **Database:** Supabase (PostgreSQL + PostGIS + Auth + Storage + Realtime)
> - **AI:** Multi-provider LLM gateway (Cerebras, Groq, SambaNova, Mistral, OpenRouter, NVIDIA NIM)
> - **Web:** Next.js 14 (App Router) — Vercel deployment
> - **Hosting:** Render (backend), Vercel (web), Supabase (DB)
> - **Offline:** Room (SQLite) via KMP
> - **Maps:** Leaflet (web + KMP)
> - **Push:** Firebase Cloud Messaging
> - **SMS:** Fast2SMS, MSG91, Twilio (fallback chain)

---

## Table of Contents — Part 2

12. [AI Layer — Segregation Verification (LLM Vision)](#12-ai-layer--segregation-verification)
13. [AI Layer — Ticket Triage (LLM NLP)](#13-ai-layer--ticket-triage-llm-nlp)
14. [AI Layer — Predictive Overflow & Route Optimization](#14-ai-layer--predictive-overflow--route-optimization)
15. [AI Layer — Anomaly Detection](#15-ai-layer--anomaly-detection)
16. [AI Service Architecture & Multi-Provider Gateway](#16-ai-service-architecture--multi-provider-gateway)
17. [Mobile App — Kotlin Multiplatform Screen Architecture](#17-mobile-app--kotlin-multiplatform-screen-architecture)
18. [Web Dashboard — Next.js Page Architecture](#18-web-dashboard--nextjs-page-architecture)
19. [Offline-First Architecture (Room)](#19-offline-first-architecture-room)
20. [Security Hardening](#20-security-hardening)
21. [Deployment & DevOps (Render + Vercel + Supabase)](#21-deployment--devops)
22. [Testing Strategy](#22-testing-strategy)
23. [Monitoring & Observability](#23-monitoring--observability)
24. [Build Roadmap — Sprint-by-Sprint](#24-build-roadmap--sprint-by-sprint)
25. [Third-Party Service Integration Details](#25-third-party-service-integration-details)

---

## 12. AI Layer — Segregation Verification (LLM Vision)

### 12.1 Problem Statement

When a sweeper scans a bin QR and takes a photo, the system needs to verify whether the waste in the bin is correctly segregated. This is the **single highest-value AI feature** — it transforms "verified cleaning" into "verified correct segregation," which is what compliance-focused buyers pay for.

### 12.2 Classification Categories

| Class | Description | Visual Cues |
|-------|-------------|-------------|
| `wet` | Organic/food waste | Food scraps, peels, leaves, green/brown organic matter |
| `dry` | Recyclable dry waste | Plastic bottles, paper, cardboard, metal cans, glass |
| `sanitary` | Sanitary waste | Wrappers, tissues, diapers, masks (non-hospital) |
| `mixed` | Missegregation detected | Multiple waste types in same bin |
| `empty` | Bin is empty or near-empty | No visible waste |

### 12.3 Approach — Multi-Provider LLM Vision

**No custom model training required.** Instead, we use vision-capable LLMs via API to classify bin photos. This eliminates the need for data collection, model training, ONNX serving, and GPU infrastructure.

```
Input: Bin photo (URL from Supabase Storage)
  ↓
LLM Vision Gateway (Ktor AI route):
  1. Build structured prompt with image URL
  2. Send to provider (Cerebras → Groq → SambaNova → fallback chain)
  3. Parse structured JSON response
  ↓
Output: { classification, confidence, reasoning, verified }
  ↓
Decision: classification field → predicted class
Confidence: parsed from LLM response (0.0–1.0)
If confidence < 0.60 → "uncertain" flag → manual review
```

**Prompt Template:**
```
You are a waste segregation auditor. Analyze this bin photo and classify the waste.

Respond in JSON format:
{
  "classification": "wet" | "dry" | "sanitary" | "mixed" | "empty",
  "confidence": 0.0-1.0,
  "reasoning": "brief explanation",
  "contaminants_detected": ["list of non-compliant items"]
}

Expected waste type for this bin: {bin.waste_type}
```

### 12.4 Provider Fallback Chain

```
Primary: Cerebras (fastest inference, ~200ms)
    ↓ (if rate-limited or timeout)
Secondary: Groq (fast, ~300ms)
    ↓
Tertiary: SambaNova (~400ms)
    ↓
Quaternary: Mistral (~500ms)
    ↓
Fallback: OpenRouter (routes to best available)
    ↓
Last resort: NVIDIA NIM
```

Each provider has free tiers sufficient for pilot (1,000–10,000 requests/day). Circuit breaker pattern prevents cascading failures.

### 12.5 Inference Flow

```
Sweeper scans bin → photo uploaded to Supabase Storage → URL sent to Ktor API
  ↓
Ktor AI route publishes job to internal job queue (PewsJobQueue pattern)
  ↓
AI Worker (Ktor coroutine) consumes job:
  1. Download image from Supabase Storage URL
  2. Build prompt with image URL + expected waste type
  3. Call LLM vision provider (with fallback chain)
  4. Parse JSON response
  5. Return: { classification, confidence, verified, reasoning }
  ↓
API updates bin.ai_result + bin.ai_verified in Supabase
  ↓
Supabase Realtime emit 'bin_updated' with AI result
  ↓
Cleaner app shows result (if still on screen)
Dashboard updates bin marker
```

### 12.6 Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| LLM inference time | <500ms (including image download) | p95 latency |
| Accuracy (5-class) | >85% | LLM vision models are pre-trained on diverse images |
| False negative (mixed → classified as correct) | <10% | Critical metric — better to over-flag than miss |
| Uncertain rate | <15% | % of predictions below 0.60 confidence |
| Provider uptime | 99.5% | Achieved via 6-provider fallback chain |

### 12.7 Edge Cases & Handling

| Edge Case | Handling |
|-----------|----------|
| Dark/blurry photo | LLM returns low confidence → prompt retake |
| Bin lid closed (can't see contents) | LLM returns "uncertain" → prompt sweeper to open lid |
| Photo of wrong thing (not a bin) | LLM detects non-bin content → return "invalid_photo" |
| Multiple bins in one photo | LLM can describe multiple bins → classify primary one |
| All providers rate-limited | Queue job for retry with exponential backoff (max 3 retries) |

---

## 13. AI Layer — Ticket Triage (LLM NLP)

### 13.1 Problem Statement

Citizens raise tickets with free-text descriptions. The system needs to:
1. **Classify** the ticket type (overflow, misplacement, missegregration, damaged_bin, hygiene, pothole, other)
2. **Assign priority** (low, medium, high, critical)
3. **Route** to the correct zone/sweeper

### 13.2 Approach — LLM-Based Triage

**No custom NLP model training required.** Use LLM providers for zero-shot classification with structured prompts.

```
Input: ticket.description (free text, e.g., "Bin near hostel 3 mess is overflowing with food waste, smells terrible")

LLM Triage (Ktor AI route):
  1. Build structured prompt with ticket text
  2. Send to LLM provider (Cerebras → Groq → fallback chain)
  3. Parse structured JSON response

Step 1 — Classification (type + priority):
  LLM returns: { category, priority, urgency_reason }

Step 2 — Zone routing:
  Use ticket.geo_location → Supabase PostGIS query → find containing zone
  → Find assigned sweeper for that zone
  → Auto-assign if sweeper is checked in, else queue for admin dispatch
```

**Prompt Template:**
```
You are a waste management ticket triage assistant. Classify this citizen complaint.

Ticket: "{description}"
Location: {zone_name}

Respond in JSON:
{
  "category": "overflow" | "misplacement" | "missegregation" | "damaged_bin" | "hygiene" | "pothole" | "other",
  "priority": "low" | "medium" | "high" | "critical",
  "urgency_reason": "brief explanation",
  "suggested_action": "what the sweeper should do"
}
```

### 13.3 Inference Flow

```
Citizen raises ticket → POST /api/v1/tickets
  ↓
Ktor API saves ticket to Supabase → publishes to internal job queue
  ↓
AI Worker (Ktor coroutine) consumes:
  1. Extract description text
  2. LLM triage → (type, priority, suggested_action)
  3. Supabase PostGIS geo-lookup → zone → assigned sweeper
  4. Return: { ai_category, ai_priority, suggested_zone, suggested_sweeper }
  ↓
API updates ticket.ai_category + ai_priority in Supabase
  ↓
If auto-assign enabled: assign to suggested sweeper
  ↓
Supabase Realtime: 'ticket_assigned' event
  ↓
Sweeper app: FCM push notification "New ticket: Overflow at Hostel 3 Mess"
```

---

## 14. AI Layer — Predictive Overflow & Route Optimization

### 14.1 Predictive Overflow — Rule-Based + LLM

**Goal:** Predict which bins will overflow in the next X hours based on historical fill patterns.

**Data Source:** `bin_scans` table in Supabase — fill level over time per bin.

**Phase 1 (Pilot): Rule-Based Thresholds**
```
Rules:
  - If current_fill > 80% AND time_since_last_scan > 6h → flag "overflow_predicted"
  - If current_fill > 90% → flag "overflow_imminent"
  - Zone waste_intensity (low/medium/high/critical) adjusts thresholds

Action:
  → Supabase Realtime: 'bin_overflow' event
  → Dashboard: bin marker pulses red
  → FCM notification to assigned sweeper: "Bin X predicted to overflow in 3 hours"
```

**Phase 2: LLM-Enhanced Prediction**
```
Feed historical scan data to LLM with structured prompt:
  "Given this bin's fill history: [scan_data], current fill: 75%, 
   time of day: 14:00, day: Tuesday, zone_intensity: high.
   Predict hours_to_overflow and confidence."

LLM returns: { hours_to_overflow: 3.5, confidence: 0.82, reasoning: "..." }
```

### 14.2 Route Optimization

**Goal:** Given a set of bins that need attention, generate an efficient route for the sweeper.

```
Input: 
  - List of bins needing attention (overflow predicted, ticket assigned, scheduled cleaning)
  - Sweeper's current location
  - Walking constraints (campus paths, not straight-line)

Algorithm: 
  1. Calculate walking distances between all bin pairs (Leaflet + OSRM)
  2. Solve TSP approximation:
     - Nearest neighbor heuristic for small sets (<15 bins)
     - Priority order: critical → high → medium → low
  3. Output: ordered list of bins to visit

Output:
  - Route as GeoJSON LineString on Leaflet map
  - Estimated time per bin (2-3 min for scanning + photo)
  - Total route time
```

### 14.3 Campus Path Graph

For accurate walking routes, build a path graph of the campus:

```
Nodes: intersections, building entrances, bin locations
Edges: walkable paths with distance (meters) and time (seconds)

Sources:
  - OpenStreetMap (OSM) data for campus area
  - Manual annotation of paths not in OSM
  - Satellite imagery for verification

Storage: PostGIS adjacency table in Supabase
Query: pgRouting Dijkstra's algorithm for shortest path
```

---

## 15. AI Layer — Anomaly Detection

### 15.1 Goal

Detect fraudulent or erroneous scan patterns to substantiate the "tamper-resistant attendance" claim.

### 15.2 Anomaly Types

| Anomaly | Detection Method | Action |
|---------|-----------------|--------|
| GPS spoofing | Compare scan GPS with bin registered GPS. If always identical → suspicious | Flag for review |
| Impossible travel | Two scans by same sweeper at different bins within time impossible for distance | Flag + notify admin |
| Scan clustering | Many scans at same timestamp from same device | Flag batch |
| Photo mismatch | LLM detects photo doesn't match bin's waste type consistently | Flag sweeper |
| Off-route scan | Scan outside sweeper's assigned zone | Warning + flag |
| Ghost scans | Scans on inactive/moved bins | Block + alert |

### 15.3 Implementation (Ktor Scheduled Job)

```kotlin
// Anomaly detection job (runs every hour via Ktor scheduled coroutine)
suspend fun detectAnomalies(sweeperId: String, timeWindowHours: Int = 1) {
    val scans = scanRepository.getScans(sweeperId, timeWindowHours)
    
    for (i in 1 until scans.size) {
        val prev = scans[i - 1]
        val curr = scans[i]
        
        // Impossible travel
        val distance = GeoUtils.haversine(prev.geo, curr.geo) // meters
        val timeDiffMin = ChronoUnit.MINUTES.between(prev.createdAt, curr.createdAt)
        val requiredSpeed = distance / (timeDiffMin * 60.0) // m/s
        if (requiredSpeed > 8.0) { // faster than running
            anomalyRepository.flag(curr.id, AnomalyType.IMPOSSIBLE_TRAVEL, mapOf(
                "distance_m" to distance, "time_min" to timeDiffMin, "speed_mps" to requiredSpeed
            ))
        }
        
        // GPS spoofing (exact same coordinates for different bins)
        if (prev.geo == curr.geo && prev.binId != curr.binId) {
            anomalyRepository.flag(curr.id, AnomalyType.GPS_SPOOFING, mapOf(
                "identical_coords" to true, "different_bins" to true
            ))
        }
    }
    
    // Off-route check
    val assignedZones = sweeperRepository.getAssignedZones(sweeperId)
    scans.filter { it.zoneId !in assignedZones }.forEach { scan ->
        anomalyRepository.flag(scan.id, AnomalyType.OFF_ROUTE, mapOf(
            "scan_zone" to scan.zoneId, "assigned_zones" to assignedZones
        ))
    }
}
```

### 15.4 Anomaly Dashboard

Admin dashboard shows:
- Flagged scans with reason + evidence (Leaflet map showing impossible travel path)
- Sweeper anomaly score (accumulated flags)
- Action buttons: dismiss, investigate, suspend sweeper

---

## 16. AI Service Architecture & Multi-Provider Gateway

### 16.1 Service Design

```
┌──────────────────────────────────────────────┐
│      Ktor AI Gateway (Kotlin coroutines)     │
│                                              │
│  ┌──────────────┐  ┌─────────────────────┐  │
│  │ Segregation   │  │ Ticket Triage       │  │
│  │ Vision Route  │  │ NLP Route           │  │
│  │ /ai/segregate │  │ /ai/triage          │  │
│  └──────┬───────┘  └─────────┬───────────┘  │
│         │                     │              │
│  ┌──────┴─────────────────────┴──────────┐  │
│  │     Multi-Provider LLM Gateway        │  │
│  │                                       │  │
│  │  ┌──────────┐  ┌──────────┐          │  │
│  │  │ Cerebras │  │  Groq    │          │  │
│  │  │ (primary)│  │(fallback)│          │  │
│  │  └──────────┘  └──────────┘          │  │
│  │  ┌──────────┐  ┌──────────┐          │  │
│  │  │SambaNova │  │ Mistral  │          │  │
│  │  └──────────┘  └──────────┘          │  │
│  │  ┌──────────┐  ┌──────────┐          │  │
│  │  │OpenRouter│  │ NVIDIA   │          │  │
│  │  │(router)  │  │ NIM      │          │  │
│  │  └──────────┘  └──────────┘          │  │
│  └───────────────────────────────────────┘  │
│                                              │
│  ┌───────────────────────────────────────┐  │
│  │  Internal Job Queue (PewsJobQueue)    │  │
│  │  - ai_segregation_queue               │  │
│  │  - ai_triage_queue                    │  │
│  │  - ai_overflow_queue                  │  │
│  └───────────────────────────────────────┘  │
│                                              │
│  ┌───────────────────────────────────────┐  │
│  │  Scheduled Coroutines                 │  │
│  │  - Hourly anomaly detection           │  │
│  │  - Daily overflow prediction          │  │
│  │  - Circuit breaker health checks      │  │
│  └───────────────────────────────────────┘  │
│                                              │
└──────────────────┬───────────────────────────┘
                   │
           ┌───────┴───────┐
           │  Supabase     │
           │  Storage      │
           │  (bin photos) │
           └───────────────┘
```

### 16.2 AI Routes (Ktor)

```
POST   /api/v1/ai/segregate     Body: { image_url, bin_id } → { class, confidence, verified, reasoning }
POST   /api/v1/ai/triage        Body: { text, geo_location } → { category, priority, zone_id, action }
POST   /api/v1/ai/overflow      Body: { bin_id } → { hours_to_overflow, confidence }
GET    /api/v1/ai/health        → { providers_status, queue_depth, circuit_breakers }
GET    /api/v1/ai/providers     → { active_provider, fallback_chain, rate_limits }
```
### 16.3 Provider Management & Circuit Breakers

```kotlin
// Circuit breaker per provider — prevents cascading failures
class ProviderCircuitBreaker(
    private val failureThreshold: Int = 5,
    private val resetTimeoutMs: Long = 60_000
) {
    private var failureCount = 0
    private var lastFailureTime = 0L
    private var state = CircuitState.CLOSED
    
    suspend fun <T> execute(block: suspend () -> T): T {
        if (state == CircuitState.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
                state = CircuitState.HALF_OPEN
            } else {
                throw CircuitOpenException("Provider circuit open")
            }
        }
        return try {
            val result = block()
            if (state == CircuitState.HALF_OPEN) state = CircuitState.CLOSED
            failureCount = 0
            result
        } catch (e: Exception) {
            failureCount++
            lastFailureTime = System.currentTimeMillis()
            if (failureCount >= failureThreshold) state = CircuitState.OPEN
            throw e
        }
    }
}

// Provider fallback chain
val providerChain = listOf(
    CerebrasProvider(circuitBreaker = ProviderCircuitBreaker()),
    GroqProvider(circuitBreaker = ProviderCircuitBreaker()),
    SambaNovaProvider(circuitBreaker = ProviderCircuitBreaker()),
    MistralProvider(circuitBreaker = ProviderCircuitBreaker()),
    OpenRouterProvider(circuitBreaker = ProviderCircuitBreaker()),
    NvidiaNimProvider(circuitBreaker = ProviderCircuitBreaker())
)

suspend fun callWithFallback(prompt: String, image: String? = null): LLMResponse {
    for (provider in providerChain) {
        try {
            return provider.complete(prompt, image)
        } catch (e: CircuitOpenException) {
            continue // Skip to next provider
        } catch (e: Exception) {
            log.warn("Provider ${provider.name} failed: ${e.message}")
            continue
        }
    }
    throw AllProvidersFailedException("All LLM providers exhausted")
}
```

### 16.4 Resource Usage

| Component | RAM | CPU | Notes |
|-----------|-----|-----|-------|
| Ktor AI Gateway | ~100MB | 0.5 core | Coroutine-based, lightweight |
| LLM API calls | 0 (external) | 0 (external) | No local model inference |
| Job Queue | ~50MB | 0.1 core | In-memory queue with persistence |
| **Total** | ~150MB | 0.6 core | Fits within Render free/starter tier |

**No GPU required.** All inference is done by external LLM providers via API.

---

## 17. Mobile App — Kotlin Multiplatform Screen Architecture

### 17.1 App Structure

```
composeApp/
├── src/
│   ├── commonMain/                    # Shared code (Android, iOS, Web)
│   │   └── kotlin/
│   │       └── com/zerow/app/
│   │           ├── App.kt             # Compose root
│   │           ├── core/
│   │           │   ├── network/
│   │           │   │   ├── ApiClient.kt         # Ktor HTTP client
│   │           │   │   ├── AuthInterceptor.kt   # JWT injection
│   │           │   │   ├── OfflineQueue.kt      # Queue requests when offline
│   │           │   │   └── SyncService.kt       # Background sync
│   │           │   ├── storage/
│   │           │   │   ├── SecureStorage.kt     # JWT storage (platform-specific)
│   │           │   │   └── LocalDatabase.kt     # Room (SQLite) setup
│   │           │   ├── auth/
│   │           │   │   ├── AuthViewModel.kt     # Auth state (StateFlow)
│   │           │   │   └── AuthRepository.kt
│   │           │   ├── theme/
│   │           │   │   ├── VColors.kt           # Color tokens
│   │           │   │   ├── VTypography.kt       # Text styles
│   │           │   │   └── VShapes.kt           # Corner radii
│   │           │   └── utils/
│   │           │       ├── GeoUtils.kt          # GPS helpers
│   │           │       └── QrUtils.kt           # QR parsing
│   │           ├── features/
│   │           │   ├── auth/
│   │           │   │   ├── LoginScreen.kt       # Phone input → OTP
│   │           │   │   ├── OtpScreen.kt         # OTP verification
│   │           │   │   ├── RegisterScreen.kt    # First-time registration
│   │           │   │   └── AuthViewModel.kt
│   │           │   ├── citizen/                  # Student/Citizen role
│   │           │   │   ├── CitizenHomeScreen.kt  # Bottom nav: Map, Tickets, Leaderboard, Profile
│   │           │   │   ├── CitizenMapScreen.kt   # Campus map with bins + tickets
│   │           │   │   ├── RaiseTicketScreen.kt  # Ticket creation form
│   │           │   │   ├── TicketListScreen.kt   # Citizen's own tickets
│   │           │   │   ├── LeaderboardScreen.kt  # Rankings
│   │           │   │   └── CitizenProfileScreen.kt
│   │           │   ├── sweeper/                   # Sweeper/Cleaner role
│   │           │   │   ├── SweeperHomeScreen.kt   # Bottom nav: Scan, Tasks, Attendance, Profile
│   │           │   │   ├── ScanScreen.kt          # QR scanner + photo capture
│   │           │   │   ├── ScanResultScreen.kt    # AI result display
│   │           │   │   ├── TaskListScreen.kt      # Assigned tickets
│   │           │   │   ├── AttendanceScreen.kt    # Check-in/out + history
│   │           │   │   └── SweeperProfileScreen.kt
│   │           │   ├── admin/                     # Admin/Committee role
│   │           │   │   ├── AdminHomeScreen.kt     # Dashboard with sidebar
│   │           │   │   ├── ZoneManagementScreen.kt
│   │           │   │   ├── StaffAssignmentScreen.kt
│   │           │   │   ├── TicketDispatchScreen.kt
│   │           │   │   └── ReportsScreen.kt
│   │           │   └── shared/
│   │           │       ├── TicketDetailScreen.kt
│   │           │       ├── BinDetailScreen.kt
│   │           │       └── NotificationListScreen.kt
│   │           └── widgets/
│   │               ├── BinMarker.kt
│   │               ├── TicketMarker.kt
│   │               ├── ZonePolygon.kt
│   │               ├── StatusBadge.kt
│   │               ├── LoadingIndicator.kt
│   │               └── ErrorWidget.kt
│   ├── androidMain/                  # Android-specific
│   │   └── kotlin/
│   │       └── com/zerow/app/
│   │           ├── platform/
│   │           │   ├── BarcodeScanner.kt         # CameraX QR scanning
│   │           │   ├── BiometricAuthenticator.kt
│   │           │   ├── HapticFeedback.kt
│   │           │   ├── MediaPicker.kt            # Camera + gallery
│   │           │   ├── NotificationPermissionLauncher.kt
│   │           │   └── ShareHelper.kt
│   │           └── MainActivity.kt
│   ├── iosMain/                      # iOS-specific
│   │   └── kotlin/
│   │       └── com/zerow/app/
│   │           ├── platform/
│   │           │   ├── BarcodeScanner.kt         # AVFoundation QR
│   │           │   ├── BiometricAuthenticator.kt  # LocalAuthentication
│   │           │   ├── HapticFeedback.kt
│   │           │   ├── MediaPicker.kt            # PHPickerViewController
│   │           │   └── ShareHelper.kt
│   │           └── iOSApp.kt
│   └── wasmJsMain/                   # Web (Kotlin/Wasm)
│       └── kotlin/
│           └── com/zerow/app/
│               └── platform/
│                   └── Platform.kt               # Web implementations
├── build.gradle.kts
└── google-services.json              # Firebase config (Android)
```

### 17.2 Key Screen Specs

#### Citizen Home (Bottom Nav — 4 tabs)

```
┌─────────────────────────────┐
│  ZeroW          🔔 (badge)  │
├─────────────────────────────┤
│                             │
│   [Leaflet Map — full]      │
│    Bins as colored dots     │
│    Tickets as alert icons   │
│    Zone boundaries          │
│                             │
│         ┌──────┐            │
│         │Raise │            │
│         │Ticket│            │
│         └──────┘            │
│                             │
├─────────────────────────────┤
│  Map  | Tickets | Board | Me│
└─────────────────────────────┘
```

- **Map tab**: Full campus map (Leaflet). Tap bin → bottom sheet with bin details. FAB "Raise Ticket" → opens form pre-filled with nearest zone.
- **Tickets tab**: List of citizen's own tickets with status badges. Tap → detail screen.
- **Leaderboard tab**: Weekly/monthly rankings. Citizen's rank highlighted. Zone ranking below.
- **Profile tab**: Name, points, badges, settings, logout.

#### Sweeper Home (Bottom Nav — 4 tabs)

```
┌─────────────────────────────┐
│  ZeroW          🔔 (badge)  │
├─────────────────────────────┤
│                             │
│  Today's Tasks: 3           │
│  ┌─────────────────────┐    │
│  │ ⚠ Overflow - H3     │    │
│  │   Hostel 3 Mess      │    │
│  │   SLA: 2h remaining  │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ 🔄 Cleaning - AB1    │    │
│  │   Academic Block 1   │    │
│  │   Scheduled          │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │   [SCAN BIN QR]     │    │
│  │   Large button      │    │
│  └─────────────────────┘    │
│                             │
├─────────────────────────────┤
│  Scan | Tasks | Attend | Me │
└─────────────────────────────┘
```

- **Scan tab**: Large "Scan Bin QR" button → opens camera (BarcodeScanner platform). After scan: photo prompt → upload → result.
- **Tasks tab**: List of assigned tickets + scheduled cleaning rounds. Tap → navigate to bin on map.
- **Attendance tab**: Check-in/out buttons. Today's hours + scan count. Weekly history.
- **Profile tab**: Name, zone assignment, scan count, attendance history, settings.

#### Scan Flow (Sweeper)

```
Screen 1: Scan tab
  → Tap "SCAN BIN QR"
  → Camera opens (BarcodeScanner.kt — CameraX on Android, AVFoundation on iOS)

Screen 2: Camera view
  → QR detected → HapticFeedback.kt vibration
  → Auto-navigate to photo capture

Screen 3: Photo capture
  → "Take photo of bin contents"
  → Camera opens (MediaPicker.kt)
  → Photo captured → preview
  → "Retake" or "Submit"

Screen 4: Submitting
  → GPS captured (GeoUtils.kt)
  → Distance check vs bin location
  → If online: upload to Supabase Storage + API call → loading spinner
  → If offline: save to Room (SQLite) → "Saved offline, will sync"

Screen 5: Result
  → ✅ "Bin scanned successfully"
  → AI result (if available): "Segregation: WET (87%)" or "⚠️ Mixed waste detected"
  → Points earned: +10
  → "Scan Next" or "Done"
```

### 17.3 State Management (StateFlow + ViewModel)

```kotlin
// Auth state
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    fun login(phone: String) { viewModelScope.launch { ... } }
    fun verifyOtp(otp: String) { viewModelScope.launch { ... } }
    fun logout() { viewModelScope.launch { ... } }
}

sealed class AuthState {
    object Idle : AuthState()
    data class Loading(val message: String) : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

// Bins state (with offline support)
class BinsViewModel(
    private val apiClient: ApiClient,
    private val localDb: LocalDatabase,
    private val connectivity: ConnectivityObserver
) : ViewModel() {
    val bins: StateFlow<List<Bin>> = _bins.asStateFlow()
    val isOffline: StateFlow<Boolean> = connectivity.isOnline.map { !it }.stateIn(...)
    
    fun loadBins(zoneId: String) { viewModelScope.launch { ... } }
}

// Scan state (one-time flow)
class ScanViewModel(
    private val apiClient: ApiClient,
    private val localDb: LocalDatabase,
    private val geoUtils: GeoUtils
) : ViewModel() {
    val phase: StateFlow<ScanPhase> = _phase.asStateFlow()
    
    fun startScan() { _phase.value = ScanPhase.Scanning }
    fun onQrDetected(binId: String) { ... }
    fun onPhotoCaptured(photo: String) { ... }
    fun submit() { viewModelScope.launch { ... } }
}

sealed class ScanPhase {
    object Idle : ScanPhase()
    object Scanning : ScanPhase()
    data class PhotoCapture(val bin: Bin) : ScanPhase()
    object Uploading : ScanPhase()
    data class Result(val scanResult: ScanResult) : ScanPhase()
}
```

### 17.4 Deep Linking

```
zerow://ticket/{id}        → Open ticket detail
zerow://bin/{id}           → Open bin detail
zerow://scan               → Open scanner
zerow://leaderboard        → Open leaderboard
zerow://zone/{id}          → Open zone on map
```

Used in FCM push notifications → tapping notification opens relevant screen via Compose Navigation.

---

## 18. Web Dashboard — Next.js Page Architecture

### 18.1 Page Structure

```
website/
├── src/
│   ├── app/                        # Next.js 14 App Router
│   │   ├── layout.tsx              # Root layout (fonts, metadata, providers)
│   │   ├── page.tsx                # Landing / role selector
│   │   ├── (auth)/
│   │   │   ├── login/page.tsx      # Phone + OTP login
│   │   │   └── layout.tsx          # Auth layout (centered card)
│   │   ├── (dashboard)/
│   │   │   ├── layout.tsx          # Dashboard layout (sidebar + topbar)
│   │   │   ├── dashboard/page.tsx  # Main dashboard (admin/committee)
│   │   │   ├── map/page.tsx        # Full campus map view
│   │   │   ├── bins/page.tsx       # Bin management table
│   │   │   ├── tickets/page.tsx    # Ticket management
│   │   │   ├── attendance/page.tsx # Attendance reports
│   │   │   ├── zones/page.tsx      # Zone management
│   │   │   ├── staff/page.tsx      # Cleaning staff management
│   │   │   ├── leaderboard/page.tsx # Rankings view
│   │   │   ├── reports/page.tsx    # Analytics & compliance reports
│   │   │   ├── marketplace/page.tsx # Waste marketplace
│   │   │   ├── settings/page.tsx   # Institution settings
│   │   │   └── anomalies/page.tsx  # Anomaly review (admin)
│   │   └── api/                    # Route handlers (if needed)
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Sidebar.tsx         # Role-based navigation
│   │   │   ├── TopBar.tsx          # Notifications, user menu
│   │   │   └── DashboardLayout.tsx # Layout wrapper
│   │   ├── map/
│   │   │   ├── CampusMap.tsx       # Leaflet map component (dynamic import)
│   │   │   ├── BinMarker.tsx
│   │   │   ├── TicketMarker.tsx
│   │   │   ├── ZoneOverlay.tsx
│   │   │   ├── HeatmapLayer.tsx
│   │   │   └── SweeperRoute.tsx
│   │   ├── dashboard/
│   │   │   ├── OverviewCards.tsx   # KPI cards
│   │   │   ├── WasteTrendChart.tsx
│   │   │   ├── TicketStatsChart.tsx
│   │   │   ├── ComplianceScore.tsx
│   │   │   └── ZoneSummary.tsx
│   │   ├── tickets/
│   │   │   ├── TicketTable.tsx
│   │   │   ├── TicketDetail.tsx
│   │   │   └── TicketStatusBadge.tsx
│   │   ├── bins/
│   │   │   ├── BinTable.tsx
│   │   │   ├── BinDetail.tsx
│   │   │   └── BinStatusBadge.tsx
│   │   └── ui/                     # shadcn/ui components
│   │       ├── button.tsx
│   │       ├── card.tsx
│   │       ├── table.tsx
│   │       └── ...
│   ├── hooks/
│   │   ├── useRealtime.ts          # Supabase Realtime subscription
│   │   ├── useAuth.ts              # Supabase Auth state
│   │   ├── useBins.ts              # TanStack Query hooks for bins
│   │   ├── useTickets.ts           # TanStack Query hooks for tickets
│   │   └── useDashboard.ts         # Dashboard data hooks
│   ├── lib/
│   │   ├── supabase.ts             # Supabase client setup
│   │   ├── api.ts                  # Ktor API client (fetch wrapper)
│   │   ├── utils.ts                # Helpers (formatting, colors)
│   │   └── constants.ts
│   ├── middleware.ts               # Auth guard middleware
│   └── types/
│       └── index.ts                # Shared TypeScript types
├── public/
│   ├── manifest.json
│   └── sw.js                       # Service worker (PWA)
├── next.config.mjs
├── tailwind.config.ts
└── package.json
```

### 18.2 Dashboard Layout

```
┌────────────────────────────────────────────────────────────┐
│  ZeroW Dashboard          [🔔 3]  [Admin User ▾]          │
├──────────┬─────────────────────────────────────────────────┤
│          │                                                 │
│  Sidebar │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌───────┐│
│          │  │ Waste   │ │ Bins    │ │ Tickets │ │Compl. ││
│  📊 Dash │  │ 22 kg   │ │ 45/60   │ │ 3 open  │ │ 87%   ││
│  🗺 Map  │  │ today   │ │ active  │ │ 1 crit  │ │       ││
│  🗑 Bins │  └─────────┘ └─────────┘ └─────────┘ └───────┘│
│  🎫 Tix  │                                                 │
│  ⏱ Atten │  ┌──────────────────────────────────────────┐  │
│  🏷 Zones│  │           Campus Map                       │  │
│  👥 Staff│  │     [Interactive Leaflet map with          │  │
│  🏆 Board│  │      bins, zones, tickets, heatmap]        │  │
│  📊 Rpts │  │                                            │  │
│  🛒 Mkt  │  └──────────────────────────────────────────┘  │
│  ⚠ Anom │                                                 │
│  ⚙ Sett  │  ┌──────────────┐ ┌────────────────────────┐  │
│          │  │ Waste Trends  │ │ Ticket Resolution Time │  │
│          │  │ [Line chart]  │ │ [Bar chart]            │  │
│          │  └──────────────┘ └────────────────────────┘  │
│          │                                                 │
└──────────┴─────────────────────────────────────────────────┘
```

### 18.3 Real-Time Updates (Supabase Realtime)

```typescript
// useRealtime hook — Supabase Realtime subscriptions
import { supabase } from '@/lib/supabase'
import { useQueryClient } from '@tanstack/react-query'

function useRealtime() {
  const queryClient = useQueryClient()

  useEffect(() => {
    // Bin updates
    const binChannel = supabase
      .channel('bin_updates')
      .on('postgres_changes', 
        { event: '*', schema: 'public', table: 'bins' },
        (payload) => {
          queryClient.setQueryData(['bins'], (old: any) =>
            updateBinInCache(old, payload.new)
          )
        }
      )
      .subscribe()

    // Ticket updates
    const ticketChannel = supabase
      .channel('ticket_updates')
      .on('postgres_changes',
        { event: 'INSERT', schema: 'public', table: 'tickets' },
        (payload) => {
          queryClient.invalidateQueries({ queryKey: ['tickets'] })
          showToast(`New ${payload.new.type} ticket in ${payload.new.zone_name}`)
        }
      )
      .on('postgres_changes',
        { event: 'UPDATE', schema: 'public', table: 'tickets' },
        (payload) => {
          queryClient.invalidateQueries({ queryKey: ['tickets'] })
          if (payload.new.status === 'assigned') {
            showToast(`Ticket ${payload.new.ticket_number} assigned`)
          }
        }
      )
      .subscribe()

    // SLA breach alerts
    const slaChannel = supabase
      .channel('sla_breaches')
      .on('postgres_changes',
        { event: '*', schema: 'public', table: 'sla_breaches' },
        (payload) => {
          showToast(`SLA breached: Ticket ${payload.new.ticket_number}`, 'error')
        }
      )
      .subscribe()

    return () => {
      supabase.removeChannel(binChannel)
      supabase.removeChannel(ticketChannel)
      supabase.removeChannel(slaChannel)
    }
  }, [])
}
```

### 18.4 Role-Based Sidebar

| Menu Item | Admin | Committee | Superadmin |
|-----------|-------|-----------|------------|
| Dashboard | ✅ | ✅ | ✅ |
| Map | ✅ | ✅ | ✅ |
| Bins | ✅ | view only | ✅ |
| Tickets | ✅ | ✅ (zone-level) | ✅ |
| Attendance | ✅ | ✅ (zone-level) | ✅ |
| Zones | ✅ | ❌ | ✅ |
| Staff | ✅ | ✅ (assign only) | ✅ |
| Leaderboard | ✅ | ✅ | ✅ |
| Reports | ✅ | ✅ | ✅ |
| Marketplace | ✅ | ❌ | ✅ |
| Anomalies | ✅ | ❌ | ✅ |
| Settings | ✅ | ❌ | ✅ |

---

## 19. Offline-First Architecture (Room)

### 19.1 Why Offline-First?

Sweepers may not have reliable smartphone data access across the campus (especially in basements, remote academic blocks, gardens). The app must function fully offline and sync when connectivity returns.

### 19.2 Local Database (Room / SQLite via KMP)

```kotlin
// Room entities mirroring server data
@Entity(tableName = "local_bins")
data class LocalBinEntity(
    @PrimaryKey val id: String,
    val qrCode: String,
    val zoneId: String,
    val wasteType: String,
    val lat: Double,
    val lng: Double,
    val lastFillPercent: Int,
    val cachedAt: Long
)

@Entity(tableName = "local_tickets")
data class LocalTicketEntity(
    @PrimaryKey val id: String,
    val type: String,
    val description: String,
    val zoneId: String,
    val status: String,
    val isOffline: Boolean,
    val createdAt: Long
)

@Entity(tableName = "local_scans")
data class LocalScanEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val binId: String,
    val photoPath: String,
    val lat: Double,
    val lng: Double,
    val synced: Boolean = false,
    val createdAt: Long
)

@Entity(tableName = "local_attendance")
data class LocalAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val sweeperId: String,
    val type: String, // "check_in" | "check_out"
    val lat: Double,
    val lng: Double,
    val synced: Boolean = false,
    val timestamp: Long
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String, // "scan" | "ticket" | "attendance"
    val entityId: String,
    val payload: String, // JSON
    val retryCount: Int = 0,
    val createdAt: Long
)

@Database(
    entities = [LocalBinEntity::class, LocalTicketEntity::class, 
                LocalScanEntity::class, LocalAttendanceEntity::class, SyncQueueEntity::class],
    version = 1
)
abstract class ZeroWDatabase : RoomDatabase() {
    abstract fun binDao(): LocalBinDao
    abstract fun ticketDao(): LocalTicketDao
    abstract fun scanDao(): LocalScanDao
    abstract fun attendanceDao(): LocalAttendanceDao
    abstract fun syncQueueDao(): SyncQueueDao
}
```

### 19.3 Sync Strategy

```
┌──────────────────────────────────────────────┐
│              Online State                     │
│  All operations → Ktor API directly           │
│  Supabase Realtime active for live updates    │
│  Periodic cache refresh (every 5 min)         │
└──────────────┬───────────────────────────────┘
               │ ConnectivityObserver detects offline
               ▼
┌──────────────────────────────────────────────┐
│             Offline State                     │
│  All operations → Room (SQLite)               │
│  Scans: save photo path locally, queue upload │
│  Tickets: save with isOffline=true            │
│  Attendance: save locally                     │
│  Map: use cached tiles + local zone data      │
│  Show "Offline" banner in UI (Compose)        │
└──────────────┬───────────────────────────────┘
               │ ConnectivityObserver detects online
               ▼
┌──────────────────────────────────────────────┐
│            Sync State                         │
│  1. POST /api/v1/sync/batch with queued data  │
│  2. Upload queued photos to Supabase Storage  │
│  3. Pull latest bin/ticket updates            │
│  4. Update local Room cache                   │
│  5. Clear sync queue                          │
│  6. Resume Supabase Realtime subscription     │
│  7. Show "Synced" toast (Compose Snackbar)    │
└──────────────────────────────────────────────┘
```

### 19.4 Conflict Resolution

| Conflict | Resolution |
|----------|------------|
| Same bin scanned by two sweepers offline | Both scans accepted — server keeps both with timestamps. Admin reviews if needed. |
| Ticket raised offline, same issue already raised by another citizen | Server detects duplicate (same zone + type within 1h) → marks second as "duplicate" → notifies citizen |
| Bin deactivated while sweeper was offline | Sync fails for that scan → sweeper notified "Bin no longer active" |
| Attendance check-in offline, but sweeper was already checked in by admin | Server keeps earliest check-in time |

### 19.5 SMS Fallback

For sweepers without smartphones or in zero-connectivity areas:

```
1. Sweeper sends SMS to ZeroW number: SCAN <bin_qr_code>
   → Server processes → records scan with timestamp
   → No photo/GPS (lower confidence scan)

2. Citizen sends SMS: TICKET <zone_code> <type> <description>
   → Server creates ticket
   → Auto-assigns to zone sweeper

3. Server sends SMS to sweeper: TASK <ticket_number> <zone_name> <type>
   → Sweeper goes to location

4. Sweeper sends SMS: DONE <ticket_number>
   → Server marks ticket as resolved
```

**SMS Gateway**: Fast2SMS (primary, ₹0.11-0.20/SMS) → MSG91 (fallback, ₹0.15/SMS) → Twilio (international). Used only for sweepers without app access.

---

## 20. Security Hardening

### 20.1 API Security

| Measure | Implementation |
|---------|---------------|
| HTTPS only | Render auto-TLS + HSTS header, Vercel auto-TLS |
| JWT validation | Supabase Auth JWT verification on every Ktor request |
| Rate limiting | Ktor rate-limiting plugin (100 req/min per user) |
| Input validation | Kotlinx.serialization + custom validators on all routes |
| SQL injection | Supabase RLS + parameterized queries via Ktor client |
| XSS | Next.js auto-escapes; Content-Security-Policy header via middleware |
| CSRF | SameSite cookies for web; JWT in Authorization header for API |
| File upload | Max 5MB, image-only (magic byte check), Supabase Storage |
| CORS | Ktor CORS plugin — whitelist specific origins |
| Security headers | Ktor DefaultHeaders plugin — HSTS, X-Content-Type-Options, X-Frame-Options |

### 20.2 Data Security

| Measure | Implementation |
|---------|---------------|
| Auth | Supabase Auth (phone OTP, JWT, refresh tokens) |
| OTP security | 6-digit, 10-min expiry, max 3 attempts, rate-limited per phone |
| PII encryption | Phone numbers encrypted at rest (AES-256-GCM via Supabase) |
| Geo data anonymization | GPS coordinates rounded to 5 decimal places (~1m) for analytics |
| Photo access | Supabase Storage signed URLs with 24h expiry |
| DB backups | Supabase automated daily backups (Pro plan), 7-day retention |
| RLS | Row-Level Security policies on all tables (per-role access) |
| Secrets | Environment variables on Render/Vercel, never in code |

### 20.3 Privacy-by-Design

1. **Consent**: Sweepers must explicitly consent to location tracking during work hours. Can opt out (manual SMS fallback).
2. **Transparency**: App shows "Your location is being recorded for scan verification" during active sessions.
3. **Retention**: Attendance data deleted after 1 year. Photos after 6 months. OTP codes after 10 minutes.
4. **Access control**: Only admin can view full attendance records. Committee sees zone-level only.
5. **Data export**: Users can request their data (DPDP Act 2023 compliance).
6. **Data deletion**: Users can request account deletion → soft delete → hard delete after 90 days.

---

## 21. Deployment & DevOps (Render + Vercel + Supabase)

### 21.1 Architecture

```
┌──────────────────────────────────────────────────────┐
│                    USER                               │
│                                                       │
│  Mobile App (KMP)     Web Dashboard (Next.js)        │
│  Android / iOS / Wasm  Vercel (auto-deploy)          │
└───────────┬──────────────────┬───────────────────────┘
            │                  │
            ▼                  ▼
┌───────────────────┐  ┌──────────────────┐
│   Render (Ktor)   │  │   Vercel         │
│   API Server      │  │   Next.js SSR    │
│   - REST API      │  │   - Dashboard    │
│   - AI Gateway    │  │   - Auth pages   │
│   - Job Scheduler │  │   - Reports      │
│   - WebSocket     │  │                  │
└────────┬──────────┘  └────────┬─────────┘
         │                      │
         ▼                      ▼
┌──────────────────────────────────────────┐
│           Supabase (PostgreSQL)           │
│           - PostGIS extension             │
│           - Auth (OTP, JWT)               │
│           - Storage (bin photos)          │
│           - Realtime (WebSocket events)   │
│           - RLS (Row-Level Security)      │
└──────────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│         External LLM Providers            │
│  Cerebras → Groq → SambaNova → Mistral   │
│  → OpenRouter → NVIDIA NIM               │
└──────────────────────────────────────────┘
```

### 21.2 Render Configuration

```yaml
# render.yaml
services:
  - type: web
    name: zerow-api
    runtime: docker
    dockerfilePath: ./Dockerfile
    healthCheckPath: /health
    envVars:
      - key: SUPABASE_URL
        sync: false
      - key: SUPABASE_SERVICE_KEY
        sync: false
      - key: JWT_SECRET
        sync: false
      - key: FCM_SERVER_KEY
        sync: false
      - key: SMS_API_KEY
        sync: false
      - key: CEREBRAS_API_KEY
        sync: false
      - key: GROQ_API_KEY
        sync: false
      - key: SAMBANOVA_API_KEY
        sync: false
      - key: MISTRAL_API_KEY
        sync: false
      - key: OPENROUTER_API_KEY
        sync: false
      - key: NVIDIA_NIM_API_KEY
        sync: false
    plan: starter  # $7/mo — upgrade to pro ($85/mo) for production
```

### 21.3 Vercel Configuration

```json
// vercel.json
{
  "framework": "nextjs",
  "buildCommand": "next build",
  "outputDirectory": ".next",
  "env": {
    "NEXT_PUBLIC_SUPABASE_URL": "@supabase_url",
    "NEXT_PUBLIC_SUPABASE_ANON_KEY": "@supabase_anon_key",
    "NEXT_PUBLIC_API_URL": "@api_url"
  }
}
```

### 21.4 CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - run: ./gradlew :server:test
      - run: ./gradlew :shared:test

  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: cd website && npm ci && npm run lint && npm run build

  # Render auto-deploys on push to main
  # Vercel auto-deploys on push to main
  # Supabase migrations run manually via CLI
```

### 21.5 Database Migrations (Supabase)

```bash
# Create migration
supabase migration new add_marketplace_tables

# Apply locally
supabase db push

# Apply to production
supabase db push --db-url $PROD_DB_URL

# Seed data (zones, departments, initial admin)
supabase db seed
```

### 21.6 Backup Strategy

| Data | Method | Frequency | Retention |
|------|--------|-----------|-----------|
| PostgreSQL | Supabase automated backups (Pro) | Daily | 7 days |
| Supabase Storage | Supabase redundancy | Continuous | Permanent |
| Code | Git (GitHub) | Every push | Permanent |
| LLM prompts | Versioned in code | Every release | Permanent |

### 21.7 Keep-Alive (Free Tier)

```yaml
# .github/workflows/keep-render-awake.yml
name: Keep Render Awake
on:
  schedule:
    - cron: '*/5 * * * *'  # Every 5 minutes
jobs:
  ping:
    runs-on: ubuntu-latest
    steps:
      - run: curl -s https://zerow-api.onrender.com/health
```

**Alternative:** UptimeRobot (free, 5-min intervals) — saves GitHub Actions minutes.

---

## 22. Testing Strategy

### 22.1 Backend Tests (JUnit 5 + Kotest)

```
server/src/test/
├── kotlin/
│   └── com/zerow/server/
│       ├── unit/
│       │   ├── AuthTest.kt           # OTP generation, JWT, password hashing
│       │   ├── QrTest.kt             # QR generation, parsing
│       │   ├── SlaTest.kt            # SLA deadline calculation
│       │   └── GeoTest.kt            # Distance calculations
│       ├── integration/
│       │   ├── BinsTest.kt           # Bin CRUD + QR lookup
│       │   ├── TicketsTest.kt        # Ticket lifecycle (create→assign→resolve)
│       │   ├── ScansTest.kt          # Scan submission + AI queue
│       │   ├── AttendanceTest.kt     # Check-in/out
│       │   ├── SyncTest.kt           # Offline batch sync
│       │   └── RealtimeTest.kt       # Supabase Realtime events
│       └── e2e/
│           └── FullFlowTest.kt       # Citizen raises ticket → AI triage → sweeper assigned → resolved
```

### 22.2 AI Gateway Tests (JUnit 5)

```kotlin
class SegregationTest {
    @Test
    fun `classify wet bin`() = runTest {
        val result = aiGateway.segregate("test_images/wet_bin.jpg", "wet")
        assertEquals("wet", result.classification)
        assertTrue(result.confidence > 0.70)
    }

    @Test
    fun `detect mixed waste`() = runTest {
        val result = aiGateway.segregate("test_images/mixed_bin.jpg", "dry")
        assertEquals("mixed", result.classification)
    }

    @Test
    fun `low confidence on blurry photo`() = runTest {
        val result = aiGateway.segregate("test_images/blurry.jpg", "wet")
        assertTrue(result.confidence < 0.60)
        assertFalse(result.verified)
    }
}

class TriageTest {
    @Test
    fun `classify overflow ticket`() = runTest {
        val result = aiGateway.triage("Bin near hostel is overflowing with food waste")
        assertEquals("overflow", result.category)
        assertTrue(result.priority in listOf("high", "critical"))
    }
}

class CircuitBreakerTest {
    @Test
    fun `fallback to next provider on failure`() = runTest {
        // Mock Cerebras failure
        // Verify Groq is called
    }
}
```

### 22.3 Frontend Tests

**Next.js (Vitest + Testing Library):**
- Component rendering tests
- TanStack Query hook tests (mock API)
- Supabase Realtime event handler tests
- Role-based access tests (sidebar visibility)

**KMP Compose (kotlin.test + Compose UI Test):**
- Composable rendering tests for each screen
- ViewModel StateFlow tests
- Offline sync flow tests
- QR parsing tests

### 22.4 E2E Tests

| Scenario | Steps |
|----------|-------|
| Citizen raises ticket | Login → Map → Raise Ticket → Submit → Verify in DB → Verify on dashboard |
| Sweeper scans bin | Login → Scan → Photo → Submit → AI result → Verify scan in DB |
| Offline scan | Disable network → Scan → Photo → Submit → Verify in Room → Enable network → Verify sync |
| Ticket lifecycle | Citizen raises → AI triages → Auto-assign → Sweeper resolves → Citizen notified |
| SLA escalation | Create ticket → Wait past SLA → Verify escalation → Verify notification sent |

### 22.5 Performance Tests

| Test | Target |
|------|--------|
| API response time (p95) | <200ms for standard endpoints |
| QR scan → result | <3s end-to-end (including upload) |
| LLM AI inference | <500ms per image (via provider API) |
| Dashboard load | <2s initial render (Next.js SSR) |
| Leaflet map render with 200 bins | <1s |
| Supabase Realtime event latency | <500ms DB→client |
| Offline sync (100 items) | <10s |

---

## 23. Monitoring & Observability

### 23.1 Logging (Ktor)

```kotlin
// Ktor structured logging via ktor-server-call-logging
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/api") }
    format { call ->
        val status = call.response.status()?.value ?: "?"
        val method = call.request.httpMethod.value
        val path = call.request.path()
        val duration = call.processingTimeMillis()
        "$method $path → $status (${duration}ms)"
    }
}

// Custom MDC context for structured JSON logging
install(MDCProvider) {
    putMDC("request_id") { UUID.randomUUID().toString() }
    putMDC("user_id") { /* extract from JWT */ }
}
```

### 23.2 Health Checks

```
GET /health           → { status: 'ok', uptime, version }
GET /health/db        → { status: 'ok', latency_ms, connections }
GET /health/ai        → { status: 'ok', providers_active, queue_depth, circuit_breakers }
```

### 23.3 Alerting

| Alert | Condition | Channel |
|-------|-----------|---------|
| API down | Render health check fails 3x | Email + SMS |
| DB connection exhausted | Supabase pool >80% | Email |
| AI queue backlog | Queue depth >100 | Email |
| High error rate | >5% of requests in 5 min | Email |
| LLM provider down | Circuit breaker open | Email (auto-failover) |
| SSL expiring | <14 days to expiry | Email (Render/Vercel auto-renew) |

### 23.4 Metrics (for future Prometheus/Grafana)

```
http_requests_total{method, path, status}
http_request_duration_seconds{method, path}
bin_scans_total{zone_id, waste_type}
tickets_created_total{type, priority}
tickets_resolved_total
ai_inference_duration_seconds{provider}
ai_provider_circuit_breaker_state{provider}
ai_queue_depth
supabase_realtime_connections_active
db_pool_connections{state}
```

---

## 24. Build Roadmap — Sprint-by-Sprint

### Sprint 1 (Week 1–2): Foundation

**Backend:**
- [ ] Initialize Ktor + Kotlin project (Gradle KMP)
- [ ] Set up Supabase project (PostgreSQL + PostGIS)
- [ ] Create all database tables + Supabase migrations
- [ ] Implement auth: Supabase Auth OTP, JWT verification
- [ ] Implement role-based middleware (Ktor)
- [ ] Set up Ktor routing structure

**Mobile:**
- [ ] Initialize KMP Compose project (composeApp module)
- [ ] Set up Koin DI + ViewModel infrastructure
- [ ] Create Ktor HTTP client with auth interceptor
- [ ] Build login screen (phone → OTP) in Compose
- [ ] Build registration screen

**Web:**
- [ ] Initialize Next.js 14 + TypeScript project
- [ ] Set up Tailwind + shadcn/ui
- [ ] Build login page (Supabase Auth)
- [ ] Set up TanStack Query + Supabase client

### Sprint 2 (Week 3–4): QR + Bins + Maps

**Backend:**
- [ ] Bin CRUD endpoints (Ktor routes)
- [ ] QR code generation endpoint
- [ ] QR lookup endpoint
- [ ] Zone CRUD endpoints
- [ ] PostGIS nearby query endpoint (Supabase)
- [ ] File upload to Supabase Storage (signed URLs)

**Mobile:**
- [ ] QR scanner integration (BarcodeScanner.kt — CameraX)
- [ ] GPS capture (GeoUtils.kt)
- [ ] Bin scan submission flow
- [ ] Photo capture (MediaPicker.kt)
- [ ] Campus map with Leaflet (KMP)
- [ ] Bin markers on map
- [ ] Zone polygons on map

**Web:**
- [ ] Leaflet.js integration (dynamic import)
- [ ] Campus map with zones + bins
- [ ] Bin management table
- [ ] Zone management page

### Sprint 3 (Week 5–6): Tickets + Attendance + Dashboard

**Backend:**
- [ ] Ticket CRUD + assignment + resolution
- [ ] Attendance check-in/out
- [ ] Dashboard overview endpoint
- [ ] Supabase Realtime setup (postgres_changes)
- [ ] Notification service (FCM + SMS)
- [ ] SLA scheduled coroutine

**Mobile:**
- [ ] Citizen: raise ticket screen
- [ ] Citizen: ticket list
- [ ] Sweeper: task list (assigned tickets)
- [ ] Sweeper: attendance check-in/out
- [ ] Sweeper: scan result screen
- [ ] FCM push notification handling
- [ ] Deep linking setup (Compose Navigation)

**Web:**
- [ ] Dashboard overview page (KPI cards)
- [ ] Ticket management page
- [ ] Attendance reports page
- [ ] Real-time Supabase Realtime updates
- [ ] Notification bell

### Sprint 4 (Week 7–8): Offline + Gamification + Committee

**Backend:**
- [ ] Batch sync endpoint
- [ ] Points ledger + leaderboard endpoints
- [ ] Committee-specific endpoints (zone management, staff assignment)
- [ ] Report generation (CSV/PDF)

**Mobile:**
- [ ] Room (SQLite) local database setup via KMP
- [ ] Offline scan storage + sync
- [ ] Offline ticket storage + sync
- [ ] Offline attendance + sync
- [ ] Connectivity detection + Compose banner
- [ ] Leaderboard screen
- [ ] Citizen profile + points

**Web:**
- [ ] Committee dashboard views
- [ ] Leaderboard page
- [ ] Reports page with charts
- [ ] Zone management with map editor

### Sprint 5 (Week 9–10): AI Segregation + Photo Pipeline

**AI Gateway (Ktor):**
- [ ] Multi-provider LLM gateway setup
- [ ] Circuit breaker implementation
- [ ] Provider fallback chain
- [ ] Segregation vision prompt + image URL flow
- [ ] Internal job queue (PewsJobQueue pattern)

**Backend:**
- [ ] AI job queue integration
- [ ] AI result callback handler
- [ ] Update bin.ai_result + ai_verified in Supabase
- [ ] Supabase Realtime emit on AI result

**Mobile:**
- [ ] Display AI result on scan completion
- [ ] "Mixed waste" warning UI (Compose)
- [ ] Photo retake prompt for low quality

**Web:**
- [ ] AI verification status on bin table
- [ ] Missegregation dashboard widget

### Sprint 6 (Week 11–12): Polish + Testing + Deploy

**All:**
- [ ] End-to-end testing (all scenarios)
- [ ] Performance testing
- [ ] Security audit (OWASP top 10)
- [ ] Bug fixes
- [ ] UI polish (Compose + Next.js)

**DevOps:**
- [ ] Render deployment configuration
- [ ] Vercel deployment configuration
- [ ] Supabase production project setup
- [ ] GitHub Actions CI pipeline
- [ ] Keep-alive cron (UptimeRobot or GitHub Actions)
- [ ] Health check endpoints
- [ ] Alerting setup

**Mobile:**
- [ ] Play Store listing preparation
- [ ] APK + AAB builds (Android)
- [ ] Beta testing with team

**Launch:**
- [ ] Deploy Ktor API to Render
- [ ] Deploy Next.js to Vercel
- [ ] DNS configuration (api.zerow.in, dashboard.zerow.in)
- [ ] Seed Supabase with campus data (zones, departments, hostels)
- [ ] Install QR-coded bins in 2–3 pilot zones
- [ ] Onboard sweeping staff
- [ ] Go live!

---

## 25. Third-Party Service Integration Details

### 25.1 Leaflet (Maps)

```
Setup:
1. No API key required (OpenStreetMap tiles are free)
2. Web: leaflet npm package + react-leaflet wrapper
3. Mobile: Leaflet via KMP expect/actual (WebView or native wrapper)
4. Offline: pre-download OSM tiles for campus area

Usage:
- Tiles: https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png
- Custom markers: DivIcon for bin/ticket markers
- Heatmap: leaflet.heat plugin
- GeoJSON: zone polygons + sweeper routes

Cost: Free (OSM tiles). Optional: Mapbox tiles for custom styles ($0.50/1K loads after free tier)
```

### 25.2 Supabase Storage

```
Setup:
1. Create Supabase project → Storage buckets
2. Auto-generated API keys (anon + service_role)

Buckets:
- bin-photos/        → bin scan photos (6-month retention policy)
- ticket-photos/     → ticket evidence photos (1-year retention)
- qr-codes/          → generated QR code images
- reports/           → generated PDF/CSV reports

Upload flow:
1. Client requests signed URL from Ktor API
2. API generates signed URL (24h expiry) via Supabase client
3. Client uploads directly to Supabase Storage
4. Client sends storage key to API
5. API stores URL in database

Cost:
- Free tier: 1GB storage, 2GB bandwidth
- Pro: 8GB storage, 250GB bandwidth ($25/mo)
```

### 25.3 Firebase Cloud Messaging (Push)

```
Setup:
1. Create Firebase project
2. Add Android app → google-services.json
3. Add iOS app → GoogleService-Info.plist
4. Get server key for API-side sending

Flow:
1. KMP app registers FCM token on login → POST /api/v1/auth/fcm-token
2. Ktor stores token in users.fcm_token (Supabase)
3. When notification needed:
   - Ktor calls FCM API with token + payload
   - Payload: { title: "New Ticket", body: "Overflow at Hostel 3", data: { type: "ticket", id: "uuid" } }
4. KMP app receives push → navigates to deep link

Cost: Free, unlimited messages
```

### 25.4 SMS Gateways (Multi-Provider Chain)

```
Primary: Fast2SMS
  Setup: Create account → get API key → prepaid wallet
  OTP: POST https://www.fast2sms.com/dev/bulkV2
  Cost: ₹0.11-0.20/SMS

Fallback 1: MSG91
  Cost: ₹0.15/SMS
  Used when Fast2SMS fails or rate-limited

Fallback 2: Twilio (international)
  Cost: $0.05/SMS (international numbers)
  Used for non-Indian phone numbers

Fallback 3: Firebase OTP Sender (SMS gateway)
  Cost: Free (via Firebase Auth)
  Used as zero-cost OTP fallback
```

### 25.5 LLM Providers (6-Provider Gateway)

```
1. Cerebras
   - Free tier: 1,000 req/day
   - Fastest inference (~200ms)
   - Vision + text support

2. Groq
   - Free tier: 30 req/min, 14,400 req/day
   - Fast (~300ms)
   - Text + some vision models

3. SambaNova
   - Free tier: limited
   - Fast (~400ms)
   - Vision + text

4. Mistral
   - Free tier: 500K req/month
   - ~500ms
   - Vision + text

5. OpenRouter
   - Pay-per-use (routes to best available)
   - Fallback router — always available
   - All model types

6. NVIDIA NIM
   - Free tier: 1,000 req/day
   - ~400ms
   - Vision + text

Total free capacity: ~30,000+ req/day across all providers
Pilot needs: ~200-500 AI calls/day (well within free tiers)
```

### 25.6 Render (Backend Hosting)

```
Setup:
1. Connect GitHub repo to Render
2. Auto-deploy on push to main
3. Dockerfile-based deployment
4. Environment variables set in Render dashboard

Plans:
- Free: 512MB RAM, spin-down after 15min idle → $0/mo
- Starter: 512MB RAM, no spin-down → $7/mo
- Pro: 4GB RAM, 1 CPU → $85/mo (production)
- Pro+: 8GB RAM, 2 CPU → $170/mo (scale)

Scaling:
- Pilot: Starter ($7/mo) — sufficient for <500 WAU
- Phase 2: Pro ($85/mo) — handles ~5,000 WAU
- Phase 3: Pro+ or 2× Pro — handles ~20,000 WAU
```

### 25.7 Vercel (Web Hosting)

```
Setup:
1. Connect GitHub repo to Vercel
2. Auto-deploy on push to main
3. Next.js framework auto-detected
4. Environment variables in Vercel dashboard

Plans:
- Hobby: Free — sufficient for pilot
- Pro: $20/mo — team features, higher limits
- Enterprise: Custom — for large scale

Cost: $0/mo (Hobby tier is sufficient)
```

### 25.8 Supabase (Database + Auth + Storage + Realtime)

```
Setup:
1. Create Supabase project
2. Enable PostGIS extension
3. Configure Auth (phone OTP provider)
4. Set up RLS policies
5. Create Storage buckets
6. Enable Realtime on tables

Plans:
- Free: 500MB DB, 1GB Storage, 50 concurrent connections
- Pro: 8GB DB, 100GB Storage, 200 pooler connections → $25/mo
- Team: 8GB DB, 100GB Storage → $80/mo
- Enterprise: Custom

Cost: $0/mo (free tier for pilot) → $25/mo (Pro for production)
```

---

## Summary — Complete System at a Glance

```
┌─────────────────────────────────────────────────────────────┐
│                     ZERO WASTE APP                          │
│                                                             │
│  CITIZENS          SWEEPERS         ADMIN/COMMITTEE         │
│  (KMP Compose)     (KMP Compose)    (Next.js Web)           │
│  - Raise tickets   - Scan QR        - Live dashboard        │
│  - View map        - Take photo     - Manage zones          │
│  - Leaderboard     - AI feedback    - Assign staff          │
│  - Track tickets   - Offline sync   - View reports          │
│                    - Attendance     - Compliance            │
│                                                             │
│         ┌─────────────────────────────────┐                │
│         │     Ktor API (Render)           │                │
│         │     - REST API                  │                │
│         │     - AI Gateway                │                │
│         │     - Job Scheduler             │                │
│         └──────────┬──────────────────────┘                │
│                    │                                        │
│    ┌───────────────┼───────────────────┐                   │
│    │               │                   │                    │
│  Core API      AI Gateway         Supabase Realtime        │
│  (Ktor)        (Multi-LLM)        (postgres_changes)       │
│  - Auth        - Segregation        - Bin updates           │
│  - Bins          Vision (LLM)       - Ticket events         │
│  - Tickets     - Ticket triage      - SLA alerts            │
│  - Attendance    (LLM NLP)                                  │
│  - Dashboard   - Overflow pred.                           │
│  - Marketplace  - Anomaly detect.                        │
│  - Gamification  (Circuit Breakers)                       │
│    │               │                   │                    │
│    └───────────────┼───────────────────┘                   │
│                    │                                        │
│         Supabase (PostgreSQL + PostGIS)                    │
│         - Auth (OTP, JWT)                                  │
│         - Storage (bin photos, QR codes)                   │
│         - Realtime (live updates)                          │
│         - RLS (Row-Level Security)                         │
│                    │                                        │
│         LLM Providers: Cerebras → Groq → SambaNova         │
│         → Mistral → OpenRouter → NVIDIA NIM                │
│                                                             │
│  EXTERNAL: Leaflet/OSM (maps), FCM (push),                 │
│  Fast2SMS/MSG91/Twilio (SMS chain)                         │
│                                                             │
│  DEPLOY: Render (API, $7-85/mo) + Vercel (Web, $0/mo)     │
│  + Supabase (DB, $0-25/mo) = $7-110/mo total              │
│  CI/CD: GitHub Actions + Render/Vercel auto-deploy         │
│  BACKUP: Supabase automated daily (Pro plan)               │
└─────────────────────────────────────────────────────────────┘
```

---

*End of Part 2. Both parts together form the complete ZeroW App Technical Implementation Plan.*

**Files:**
- `ZEROW_APP_TECHNICAL_PLAN_PART1.md` — Architecture, Tech Stack, Phases, DB Schema, API Design, Maps, QR, Auth, Real-Time, Budget, EPR/Legal
- `ZEROW_APP_TECHNICAL_PLAN_PART2.md` — AI Layer (LLM-based, 4 features), KMP Compose App, Next.js Dashboard, Offline-First (Room), Security, DevOps (Render+Vercel+Supabase), Testing (JUnit5), Monitoring, Sprint Roadmap, Third-Party Integrations
