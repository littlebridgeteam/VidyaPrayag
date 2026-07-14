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
| HTTPS only | Nginx SSL + HSTS header, redirect HTTP→HTTPS |
| JWT validation | Verify signature + expiry on every request |
| Rate limiting | 100 req/min per user (Redis token bucket) |
| Input validation | Zod schemas on all endpoints |
| SQL injection | Prisma ORM parameterized queries |
| XSS | React auto-escapes; Content-Security-Policy header |
| CSRF | SameSite cookies for web; JWT in header for API |
| File upload | Max 5MB, image-only (magic byte check), sharp processing |
| CORS | Whitelist specific origins (app domain, dashboard domain) |
| Helmet | HTTP security headers via `helmet` middleware |

### 20.2 Data Security

| Measure | Implementation |
|---------|---------------|
| Password hashing | bcrypt (cost factor 12) |
| OTP security | 6-digit, 10-min expiry, max 3 attempts, rate-limited per phone |
| PII encryption | Phone numbers encrypted at rest (AES-256-GCM) |
| Geo data anonymization | GPS coordinates rounded to 5 decimal places (~1m) for analytics |
| Photo access | R2 presigned URLs with 24h expiry |
| DB backups | Daily pg_dump to R2, 7-day retention |
| Redis | Require AUTH password, bind to localhost only |
| Secrets | Environment variables, never in code, `.env` in `.gitignore` |

### 20.3 Privacy-by-Design

1. **Consent**: Cleaners must explicitly consent to location tracking during work hours. Can opt out (manual SMS fallback).
2. **Transparency**: App shows "Your location is being recorded for scan verification" during active sessions.
3. **Retention**: Attendance data deleted after 1 year. Photos after 6 months. OTP codes after 10 minutes.
4. **Access control**: Only admin can view full attendance records. Committee sees zone-level only.
5. **Data export**: Users can request their data (DPDP Act 2023 compliance).
6. **Data deletion**: Users can request account deletion → soft delete → hard delete after 90 days.

---

## 21. Deployment & DevOps

### 21.1 Docker Compose (Pilot)

```yaml
# docker-compose.yml
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    ports: ['80:80', '443:443']
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./certbot/conf:/etc/letsencrypt
      - ./web-build:/usr/share/nginx/html    # React dashboard
    depends_on: [api, ai-service]
    restart: always

  api:
    build: ./server
    environment:
      - DATABASE_URL=postgresql://zerow:password@db:5432/zerow
      - REDIS_URL=redis://redis:6379
      - JWT_SECRET=${JWT_SECRET}
      - R2_ACCOUNT_ID=${R2_ACCOUNT_ID}
      - R2_ACCESS_KEY=${R2_ACCESS_KEY}
      - R2_SECRET_KEY=${R2_SECRET_KEY}
      - MAPBOX_TOKEN=${MAPBOX_TOKEN}
      - FCM_SERVER_KEY=${FCM_SERVER_KEY}
      - SMS_API_KEY=${SMS_API_KEY}
    depends_on: [db, redis]
    restart: always

  ai-service:
    build: ./ai-service
    environment:
      - REDIS_URL=redis://redis:6379
      - MODEL_PATH=/models
    volumes:
      - ./models:/models:ro
    depends_on: [redis]
    restart: always

  db:
    image: postgis/postgis:16-3.4
    environment:
      - POSTGRES_DB=zerow
      - POSTGRES_USER=zerow
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql
    restart: always

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    restart: always

volumes:
  pgdata:
```

### 21.2 Nginx Configuration

```nginx
upstream api_server { server api:3000; }
upstream ai_server { server ai-service:8000; }

server {
    listen 80;
    server_name api.zerow.in;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.zerow.in;
    
    ssl_certificate /etc/letsencrypt/live/api.zerow.in/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.zerow.in/privkey.pem;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    
    # API proxy
    location /api/ {
        proxy_pass http://api_server;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # WebSocket proxy
    location /ws {
        proxy_pass http://api_server;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # AI service proxy (internal only — not exposed publicly)
    location /ai/ {
        proxy_pass http://ai_server/;
        allow 172.16.0.0/12;  # Docker network only
        deny all;
    }
}

server {
    listen 443 ssl http2;
    server_name dashboard.zerow.in;
    
    ssl_certificate /etc/letsencrypt/live/dashboard.zerow.in/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/dashboard.zerow.in/privkey.pem;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;  # SPA routing
    }
}
```

### 21.3 CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: cd server && npm ci && npm test
      - run: cd web && npm ci && npm run build
      - run: cd ai-service && pip install -r requirements.txt && pytest

  deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build and deploy
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.DROPLET_IP }}
          username: root
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/zerow
            git pull origin main
            docker compose build
            docker compose up -d
            docker compose exec api npx prisma migrate deploy
```

### 21.4 Database Migrations

```bash
# Create migration
npx prisma migrate dev --name add_marketplace_tables

# Apply in production
npx prisma migrate deploy

# Seed data (zones, departments, initial admin)
npx prisma db seed
```

### 21.5 Backup Strategy

| Data | Method | Frequency | Retention |
|------|--------|-----------|-----------|
| PostgreSQL | `pg_dump` → R2 | Daily 2AM | 7 days |
| Redis | RDB snapshot | Daily | 3 days |
| R2 files | Versioning enabled | Continuous | 30 days |
| Code | Git (GitHub) | Every push | Permanent |

---

## 22. Testing Strategy

### 22.1 Backend Tests (Jest)

```
server/src/test/
├── unit/
│   ├── auth.test.ts           # OTP generation, JWT, password hashing
│   ├── qr.test.ts             # QR generation, parsing
│   ├── sla.test.ts            # SLA deadline calculation
│   └── geo.test.ts            # Distance calculations
├── integration/
│   ├── bins.test.ts           # Bin CRUD + QR lookup
│   ├── tickets.test.ts        # Ticket lifecycle (create→assign→resolve)
│   ├── scans.test.ts          # Scan submission + AI queue
│   ├── attendance.test.ts     # Check-in/out
│   ├── sync.test.ts           # Offline batch sync
│   └── websocket.test.ts      # WebSocket events
└── e2e/
    └── full_flow.test.ts      # Student raises ticket → AI triage → cleaner assigned → resolved
```

### 22.2 AI Service Tests (Pytest)

```python
# test_segregation.py
def test_waste_classification():
    result = predict_segregation("test_images/wet_bin.jpg")
    assert result['class'] == 'wet'
    assert result['confidence'] > 0.70

def test_mixed_detection():
    result = predict_segregation("test_images/mixed_bin.jpg")
    assert result['class'] == 'mixed'

def test_low_confidence():
    result = predict_segregation("test_images/blurry.jpg")
    assert result['confidence'] < 0.60
    assert result['verified'] == False

# test_triage.py
def test_overflow_classification():
    result = predict_triage("Bin near hostel is overflowing with food waste")
    assert result['category'] == 'overflow'
    assert result['priority'] in ['high', 'critical']
```

### 22.3 Frontend Tests

**React (Vitest + Testing Library):**
- Component rendering tests
- React Query hook tests (mock API)
- WebSocket event handler tests
- Role-based access tests (sidebar visibility)

**Flutter (flutter_test):**
- Widget tests for each screen
- Riverpod provider tests
- Offline sync flow tests
- QR parsing tests

### 22.4 E2E Tests

| Scenario | Steps |
|----------|-------|
| Student raises ticket | Login → Map → Raise Ticket → Submit → Verify in DB → Verify on dashboard |
| Cleaner scans bin | Login → Scan → Photo → Submit → AI result → Verify scan in DB |
| Offline scan | Disable network → Scan → Photo → Submit → Verify in local SQLite → Enable network → Verify sync |
| Ticket lifecycle | Student raises → AI triages → Auto-assign → Cleaner resolves → Student notified |
| SLA escalation | Create ticket → Wait past SLA → Verify escalation → Verify notification sent |

### 22.5 Performance Tests

| Test | Target |
|------|--------|
| API response time (p95) | <200ms for standard endpoints |
| QR scan → result | <3s end-to-end (including upload) |
| AI inference | <200ms per image |
| Dashboard load | <2s initial render |
| Map render with 200 bins | <1s |
| WebSocket event latency | <500ms server→client |
| Offline sync (100 items) | <10s |

---

## 23. Monitoring & Observability

### 23.1 Logging

```typescript
// Winston logger configuration
const logger = winston.createLogger({
  format: winston.format.json(),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' })
  ]
});

// Structured logging for every API request
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    logger.info('request', {
      method: req.method,
      path: req.path,
      status: res.statusCode,
      duration_ms: Date.now() - start,
      user_id: req.user?.id,
      ip: req.ip
    });
  });
  next();
});
```

### 23.2 Health Checks

```
GET /health           → { status: 'ok', uptime, version }
GET /health/db        → { status: 'ok', latency_ms, connections }
GET /health/redis     → { status: 'ok', latency_ms }
GET /health/ai        → { status: 'ok', models_loaded, queue_depth }
```

### 23.3 Alerting

| Alert | Condition | Channel |
|-------|-----------|---------|
| API down | Health check fails 3x | Email + SMS |
| DB connection exhausted | Pool usage >80% | Email |
| AI queue backlog | Queue depth >100 | Email |
| High error rate | >5% of requests in 5 min | Email |
| Disk space | >80% used | Email |
| SSL expiring | <14 days to expiry | Email |

### 23.4 Metrics (for future Prometheus/Grafana)

```
http_requests_total{method, path, status}
http_request_duration_seconds{method, path}
bin_scans_total{zone_id, waste_type}
tickets_created_total{type, priority}
tickets_resolved_total
ai_inference_duration_seconds{model}
ai_queue_depth
websocket_connections_active
db_pool_connections{state}
```

---

## 24. Build Roadmap — Sprint-by-Sprint

### Sprint 1 (Week 1–2): Foundation

**Backend:**
- [ ] Initialize Node.js + Express + TypeScript project
- [ ] Set up Prisma + PostgreSQL + PostGIS
- [ ] Create all database tables + migrations
- [ ] Implement auth: send-otp, verify-otp, JWT generation
- [ ] Implement role-based middleware
- [ ] Set up Redis connection

**Mobile:**
- [ ] Initialize Flutter project
- [ ] Set up Riverpod providers
- [ ] Create API client (Dio) with auth interceptor
- [ ] Build login screen (phone → OTP)
- [ ] Build registration screen

**Web:**
- [ ] Initialize React + Vite + TypeScript project
- [ ] Set up Tailwind + shadcn/ui
- [ ] Build login page
- [ ] Set up React Query + API client

### Sprint 2 (Week 3–4): QR + Bins + Maps

**Backend:**
- [ ] Bin CRUD endpoints
- [ ] QR code generation endpoint
- [ ] QR lookup endpoint
- [ ] Zone CRUD endpoints
- [ ] PostGIS nearby query endpoint
- [ ] File upload to R2 (presigned URLs)

**Mobile:**
- [ ] QR scanner integration (mobile_scanner)
- [ ] GPS capture (geolocator)
- [ ] Bin scan submission flow
- [ ] Photo capture (image_picker)
- [ ] Campus map with flutter_map
- [ ] Bin markers on map
- [ ] Zone polygons on map

**Web:**
- [ ] Mapbox GL JS integration
- [ ] Campus map with zones + bins
- [ ] Bin management table
- [ ] Zone management page

### Sprint 3 (Week 5–6): Tickets + Attendance + Dashboard

**Backend:**
- [ ] Ticket CRUD + assignment + resolution
- [ ] Attendance check-in/out
- [ ] Dashboard overview endpoint
- [ ] WebSocket server (Socket.io)
- [ ] Notification service (FCM + SMS)
- [ ] SLA cron job

**Mobile:**
- [ ] Student: raise ticket screen
- [ ] Student: ticket list
- [ ] Cleaner: task list (assigned tickets)
- [ ] Cleaner: attendance check-in/out
- [ ] Cleaner: scan result screen
- [ ] Push notification handling
- [ ] Deep linking setup

**Web:**
- [ ] Dashboard overview page (KPI cards)
- [ ] Ticket management page
- [ ] Attendance reports page
- [ ] Real-time WebSocket updates
- [ ] Notification bell

### Sprint 4 (Week 7–8): Offline + Gamification + Committee

**Backend:**
- [ ] Batch sync endpoint
- [ ] Points ledger + leaderboard endpoints
- [ ] Committee-specific endpoints (zone management, staff assignment)
- [ ] Report generation (CSV/PDF)

**Mobile:**
- [ ] Drift (SQLite) local database setup
- [ ] Offline scan storage + sync
- [ ] Offline ticket storage + sync
- [ ] Offline attendance + sync
- [ ] Connectivity detection + banner
- [ ] Leaderboard screen
- [ ] Student profile + points

**Web:**
- [ ] Committee dashboard views
- [ ] Leaderboard page
- [ ] Reports page with charts
- [ ] Zone management with map editor

### Sprint 5 (Week 9–10): AI Segregation + Photo Pipeline

**AI Service:**
- [ ] FastAPI service setup
- [ ] Redis Stream consumer
- [ ] Image download from R2
- [ ] MobileNetV3 model loading (ONNX runtime)
- [ ] Segregation inference endpoint
- [ ] Model registry + hot-swap

**Backend:**
- [ ] AI job queue (BullMQ → Redis Streams)
- [ ] AI result callback handler
- [ ] Update bin.ai_result + ai_verified
- [ ] WebSocket emit on AI result

**Mobile:**
- [ ] Display AI result on scan completion
- [ ] "Mixed waste" warning UI
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
- [ ] UI polish

**DevOps:**
- [ ] Docker Compose finalization
- [ ] Nginx configuration
- [ ] SSL certificates (Let's Encrypt)
- [ ] GitHub Actions CI/CD
- [ ] Database backup automation
- [ ] Health check endpoints
- [ ] Alerting setup

**Mobile:**
- [ ] App store listing preparation
- [ ] APK + IPA builds
- [ ] Beta testing with team

**Launch:**
- [ ] Deploy to DigitalOcean
- [ ] DNS configuration (api.zerow.in, dashboard.zerow.in)
- [ ] Seed database with AMU campus data (zones, departments, hostels)
- [ ] Install QR-coded bins in 2–3 pilot zones
- [ ] Onboard cleaning staff
- [ ] Go live!

---

## 25. Third-Party Service Integration Details

### 25.1 Mapbox

```
Setup:
1. Create Mapbox account → get access token
2. Create custom map style (streets-v12 base + custom colors for zones)
3. Web: mapbox-gl npm package
4. Mobile: flutter_map with Mapbox tile URL template
5. Offline: pre-download tiles for campus area

API Usage:
- Static tiles: https://api.mapbox.com/styles/v1/{style}/tiles/256/{z}/{x}/{y}@2x?access_token={token}
- Geocoding: https://api.mapbox.com/geocoding/v5/mapbox.places/{query}.json?access_token={token}
- Directions (for route optimization): https://api.mapbox.com/directions/v5/mapbox/walking/{coordinates}?access_token={token}

Cost:
- Free tier: 50,000 map loads/month, 100,000 tile requests/month
- Pilot usage: ~5,000 loads/month (well within free tier)
- Phase 2: may need $5/mo plan for higher usage
```

### 25.2 Cloudflare R2

```
Setup:
1. Create Cloudflare account → R2 bucket
2. Generate API tokens (access key + secret key)
3. S3-compatible endpoint: https://{account_id}.r2.cloudflarestorage.com

Buckets:
- zerow-bin-photos/     → bin scan photos (6-month retention)
- zerow-ticket-photos/  → ticket evidence photos (1-year retention)
- zerow-qr-codes/       → generated QR code images
- zerow-reports/        → generated PDF/CSV reports
- zerow-backups/        → database backups

Upload flow:
1. Client requests presigned URL from API
2. API generates presigned URL (24h expiry) using AWS SDK (R2-compatible)
3. Client uploads directly to R2 (bypasses API server — saves bandwidth)
4. Client sends R2 object key to API
5. API stores URL in database

Cost:
- 10GB free, $0.015/GB after
- Zero egress (free downloads)
- Pilot: well within 10GB free tier
```

### 25.3 Firebase Cloud Messaging (Push)

```
Setup:
1. Create Firebase project
2. Add Android + iOS apps → get google-services.json + GoogleService-Info.plist
3. Get server key for API-side sending

Flow:
1. Mobile app registers FCM token on login → POST /auth/fcm-token
2. Server stores token in users.fcm_token
3. When notification needed:
   - Server calls FCM API with token + payload
   - Payload includes: title, body, data (for deep linking)
   - Example: { title: "New Ticket", body: "Overflow at Hostel 3", data: { type: "ticket", id: "uuid" } }
4. Mobile app receives push → navigates to deep link

Cost: Free, unlimited messages
```

### 25.4 Fast2SMS (SMS Gateway)

```
Setup:
1. Create Fast2SMS account → get API key
2. Wallet-based: prepaid credits

Usage:
- OTP delivery: POST https://www.fast2sms.com/dev/bulkV2
  Body: { sender_id: "ZEROW", message: "Your ZeroW OTP is 123456", numbers: "91XXXXXXXXXX" }
- Cleaner alerts: same API, different message templates
- Cost: ₹0.11-0.20/SMS

Fallback: MSG91 (₹0.15/SMS) if Fast2SMS fails
```

### 25.5 DigitalOcean

```
Droplet setup:
1. Create droplet: 4GB RAM, 2 vCPU, Ubuntu 22.04, $24/month
2. SSH access: key-based only, password disabled
3. Firewall: ufw — allow 22 (SSH), 80 (HTTP), 443 (HTTPS) only
4. Docker + Docker Compose installed
5. Fail2ban for SSH brute-force protection
6. Automatic security updates: unattended-upgrades

Scaling plan:
- Pilot (1 institution): 4GB/2vCPU = $24/mo
- Phase 2 (full campus): 8GB/4vCPU = $48/mo
- Phase 3 (multi-tenant): 16GB/8vCPU = $96/mo or split into 2 droplets
```

---

## Summary — Complete System at a Glance

```
┌─────────────────────────────────────────────────────────────┐
│                     ZERO WASTE APP                          │
│                                                             │
│  STUDENTS          CLEANERS         ADMIN/COMMITTEE         │
│  (Flutter)         (Flutter)        (React Web)             │
│  - Raise tickets   - Scan QR        - Live dashboard        │
│  - View map        - Take photo     - Manage zones          │
│  - Leaderboard     - AI feedback    - Assign staff          │
│  - Track tickets   - Offline sync   - View reports          │
│                   - Attendance      - Compliance            │
│                                                             │
│         ┌─────────────────────────────────┐                │
│         │      API Gateway (Nginx)        │                │
│         └──────────┬──────────────────────┘                │
│                    │                                        │
│    ┌───────────────┼───────────────────┐                   │
│    │               │                   │                    │
│  Core API      AI Service          Redis Cache             │
│  (Node.js)     (FastAPI)          (Sessions,              │
│  - Auth        - Segregation        Pub/Sub,               │
│  - Bins          CV classifier      Queue)                 │
│  - Tickets     - Ticket triage                           │
│  - Attendance    NLP                                 │
│  - Dashboard   - Overflow pred.                           │
│  - Marketplace  - Anomaly detect.                        │
│  - Gamification                                          │
│    │               │                   │                    │
│    └───────────────┼───────────────────┘                   │
│                    │                                        │
│         PostgreSQL 16 + PostGIS + TimescaleDB              │
│         (Spatial queries, time-series, ACID)               │
│                    │                                        │
│         Cloudflare R2 (Photos, QR codes, Reports)          │
│                                                             │
│  EXTERNAL: Mapbox (maps), FCM (push), Fast2SMS (SMS)       │
│                                                             │
│  DEPLOY: DigitalOcean Docker Compose, $24-48/mo           │
│  CI/CD: GitHub Actions → SSH → docker compose up          │
│  BACKUP: Daily pg_dump → R2, 7-day retention              │
└─────────────────────────────────────────────────────────────┘
```

---

*End of Part 2. Both parts together form the complete ZeroW App Technical Implementation Plan.*

**Files created:**
- `ZEROW_APP_TECHNICAL_PLAN_PART1.md` — Architecture, Tech Stack, Phases, DB Schema, API Design, Maps, QR, Auth, Real-Time, Budget, EPR/Legal
- `ZEROW_APP_TECHNICAL_PLAN_PART2.md` — AI Layer (4 models), Flutter App Architecture, React Dashboard, Offline-First, Security, DevOps, Testing, Monitoring, Sprint Roadmap, Third-Party Integrations
