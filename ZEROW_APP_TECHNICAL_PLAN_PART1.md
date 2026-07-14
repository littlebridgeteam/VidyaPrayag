# ZeroW App — Super-Detailed Technical Implementation Plan
## Part 1 of 2: Architecture, Tech Stack, Data Models, Backend API, Maps System

> **Purpose:** Engineering blueprint for building the ZeroW app — a working waste management platform with live maps, QR bin tracking, ticketing, dashboards, AI segregation verification, and offline support.

> **Key document changes incorporated:**
> 1. Hospital segment removed — JNMCH already has biomedical waste segregation. ZeroW does not touch hospital waste.
> 2. Phases separated into explicit time periods (months/years).
> 3. University already has paid cleaning staff — ZeroW coordinates, does not hire new workers.
> 4. Departments do not work independently — University operates as one entity independent from Municipal Corporation. Departments are sub-units.
> 5. Budget revised upward with realistic figures.
> 6. Short EPR registration & legal/privacy description included.

---

## 1. System Architecture Overview

### 1.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER                           │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌──────────┐         │
│  │Student │  │Cleaner │  │Admin   │  │Committee │         │
│  │Mobile  │  │Mobile  │  │Web     │  │Web/Mobile│         │
│  │(Flutter│  │(Flutter│  │(React) │  │(React)   │         │
│  └───┬────┘  └───┬────┘  └───┬────┘  └────┬─────┘         │
│      └───────────┴───────────┴─────────────┘               │
│                    │                                        │
│           ┌────────┴────────┐                              │
│           │  API Gateway     │                              │
│           │  (Nginx + SSL)   │                              │
│           └────────┬────────┘                              │
└────────────────────┼────────────────────────────────────────┘
                     │
┌────────────────────┼────────────────────────────────────────┐
│                SERVICE LAYER                                │
│  ┌──────────┐ ┌────┴───────┐ ┌──────────┐ ┌────────────┐  │
│  │Auth Svc  │ │Core API    │ │AI Service│ │File Storage│  │
│  │(JWT+OTP) │ │(Node.js)   │ │(FastAPI) │ │(R2/S3)     │  │
│  └──────────┘ └────┬───────┘ └────┬─────┘ └────────────┘  │
│  ┌──────────┐ ┌────┴───────┐ ┌───┴──────┐                 │
│  │Notif Svc │ │Maps/Geo    │ │Redis     │                 │
│  │(FCM+SMS) │ │(PostGIS)   │ │Cache+Pub │                 │
│  └──────────┘ └────────────┘ └──────────┘                 │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┼────────────────────────────────────────┐
│                  DATA LAYER                                │
│  ┌──────────────┐  ┌─────────┐  ┌──────────────┐          │
│  │PostgreSQL 16 │  │Redis 7  │  │TimescaleDB   │          │
│  │+ PostGIS     │  │Cache    │  │(extension)   │          │
│  └──────────────┘  └─────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Mobile | Flutter (Dart) | Single codebase Android+iOS, strong offline support, QR/camera libs |
| Web | React + TypeScript + Vite | Complex dashboards, large ecosystem, shared types with API |
| Backend | Node.js + Express + TypeScript | Fast I/O for scans/tickets, shared types with frontend |
| AI | Python + FastAPI | Native ML/CV (PyTorch, HuggingFace) |
| DB | PostgreSQL 16 + PostGIS | Spatial queries, ACID, proven at scale |
| Cache | Redis 7 | Sessions, rate limiting, pub/sub, bin fill cache |
| Time-series | TimescaleDB (PG extension) | Waste trends, fill predictions — no separate DB |
| Storage | Cloudflare R2 | Zero egress, S3-compatible, cheap for bin photos |
| Maps | Mapbox GL JS (web) + Mapbox SDK (mobile) | Best rendering, offline tiles, India data |
| QR | Dynamic server-side generation | Trackable, rotatable, invalidatable |

### 1.3 Deployment Topology

Single DigitalOcean droplet (4GB/2vCPU, $24/mo) running Docker Compose:
- Nginx reverse proxy + SSL (Let's Encrypt)
- Core API container (Node.js, port 3000)
- AI Service container (FastAPI, port 8000)
- PostgreSQL container (port 5432) with PostGIS + TimescaleDB
- Redis container (port 6379)
- Web dashboard served as static files via Nginx

Scale to 8GB/4vCPU ($48/mo) when expanding to multiple institutions.

---

## 2. Technology Stack

### 2.1 Mobile App (Flutter)

| Package | Purpose |
|---------|---------|
| `flutter_map` + `latlong2` | Map rendering with offline tile support |
| `mobile_scanner` | QR code scanning |
| `geolocator` | GPS for bin scans |
| `connectivity_plus` | Online/offline detection |
| `drift` (SQLite) | Local offline-first storage |
| `flutter_secure_storage` | Encrypted JWT storage |
| `image_picker` | Bin photo capture |
| `dio` | API client with interceptors (auth, retry, offline queue) |
| `flutter_riverpod` | State management |
| `firebase_messaging` | Push notifications |
| `cached_network_image` | Map tile + image caching |

### 2.2 Web Dashboard (React)

| Package | Purpose |
|---------|---------|
| `react` 18 + `typescript` + `vite` | UI framework + build |
| `react-router-dom` 6 | Routing |
| `@tanstack/react-query` | Server state (caching, refetch) |
| `zustand` | Client state |
| `mapbox-gl` | Interactive maps |
| `recharts` | Charts |
| `@tanstack/react-table` | Data tables |
| `tailwindcss` + `shadcn/ui` | Styling + components |
| `socket.io-client` | WebSocket real-time |

### 2.3 Backend (Node.js)

| Package | Purpose |
|---------|---------|
| `express` 4 + `typescript` 5 | HTTP framework |
| `prisma` | ORM + type-safe DB |
| `ioredis` | Cache + pub/sub |
| `bullmq` | Background jobs (notifications, AI dispatch) |
| `jsonwebtoken` + `bcrypt` | Auth |
| `zod` | Request validation |
| `multer` + `sharp` | Image upload + processing |
| `socket.io` | WebSocket server |
| `node-cron` | Scheduled jobs (reports, SLA escalation) |
| `qrcode` | Server-side QR generation |

### 2.4 AI Service (Python)

| Package | Purpose |
|---------|---------|
| `fastapi` + `uvicorn` | HTTP + ASGI |
| `torch` + `torchvision` | Segregation classifier (CV) |
| `transformers` | NLP for ticket triage |
| `Pillow` | Image processing |
| `redis` | Queue consumer |

### 2.5 Infrastructure Cost (Pilot)

| Component | Cost |
|-----------|------|
| DigitalOcean 4GB/2vCPU | $24/mo |
| Cloudflare R2 (10GB free) | $0 |
| Mapbox (50K loads/mo free) | $0 |
| Firebase FCM | $0 |
| SMS (Fast2SMS) | ~₹500-1000/mo |
| Domain + SSL | ~₹1,000/yr |
| **Total pilot** | **~$25-30/mo** |

---

## 3. Phase Timeline & Milestones

### Phase 0 — Foundation & Pilot Setup (Month 1–2)

| Month | Milestone | Deliverables |
|-------|-----------|-------------|
| M1 | Project setup & core infra | DB schema, auth, role-based login, API skeleton, Flutter+React skeletons |
| M1 | QR system | QR generation, QR scanning in Flutter, bin registration |
| M2 | Maps integration | Mapbox setup, campus zone mapping, bin markers, zone boundaries |
| M2 | Basic ticketing | Student raise ticket, admin view, cleaner assignment, SLA timer |
| M2 | MVP dashboard | Real-time bin status, ticket queue, attendance log, zone heat map |

**Exit criteria:** Student can raise ticket, cleaner can scan bin QR, admin sees both on live map dashboard. No AI yet.

### Phase 1 — Campus Rollout (Month 3–6)

| Month | Milestone | Deliverables |
|-------|-----------|-------------|
| M3 | Bin installation | QR-coded segregated bins in 2–3 high-waste zones (mess, canteen, academic block) |
| M3 | Staff coordination | Onboard existing university cleaning staff into app. No new hiring. Assign zones, QR attendance. |
| M4 | AI segregation v1 | Photo on bin scan → classifier (wet/dry/mixed) → flag missegregation |
| M4 | Offline mode | Local SQLite cache, sync when online, SMS fallback for critical alerts |
| M5 | Gamification | Student leaderboard, department rankings, clean zone badges |
| M5 | Committee dashboard | Manage zones, assign staff, view reports, approve resolutions |
| M6 | Analytics & reporting | Daily/weekly/monthly waste reports, zone breakdown, compliance score |
| M6 | Notifications | FCM push for ticket updates, SMS for cleaners without smartphones |

**Exit criteria:** Full pilot in 2–3 zones with AI, offline, gamification, reporting.

### Phase 2 — Full Campus & AI Enhancement (Month 7–12)

| Month | Milestone | Deliverables |
|-------|-----------|-------------|
| M7 | Campus-wide | All 117 departments, 20 halls of residence, all canteens/messes |
| M7-8 | Predictive overflow | Time-series model → predict bin overflow → route optimization |
| M8-9 | Ticket triage NLP | Auto-classify tickets, auto-prioritize, auto-route to correct zone |
| M9-10 | Anomaly detection | Detect impossible scan patterns, GPS spoofing |
| M10 | Marketplace v1 | Dry waste aggregation → connect with recyclers, commission ledger |
| M11 | Composting tracker | Organic waste → compost output estimation → buyer matching |
| M12 | Compliance reporting | Auto-generate SWM Rules 2026 compliance reports |

**Exit criteria:** Full AMU campus, AI operational, marketplace live, compliance reports generated.

### Phase 3 — Municipal Expansion (Year 2)

| Quarter | Milestone |
|---------|-----------|
| Q1 (M13-15) | Aligarh Municipal Corporation pilot — 2–3 wards |
| Q2 (M16-18) | Multi-tenant SaaS architecture |
| Q3 (M19-21) | Delhi pilot — 2–3 wards, scale testing |
| Q4 (M22-24) | Certification-as-a-service (Green Campus / Swachh Survekshan) |

### Phase 4 — Pan-India Scale (Year 3–5)

| Year | Target |
|------|--------|
| Y3 | 6–8 institutions + 1–2 municipal pilots, regional language support |
| Y4 | 12–15 institutions, EPR compliance module, pan-India recycler network |
| Y5 | 18–22 institutions + 3–5 municipal partnerships, white-label option |

---

## 4. Database Schema & Data Models

### 4.1 Core Tables

```sql
-- USERS & AUTH
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone         VARCHAR(15) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE,
    name          VARCHAR(255) NOT NULL,
    role          VARCHAR(20) CHECK (role IN ('student','cleaner','admin','committee','superadmin')),
    department_id UUID REFERENCES departments(id),
    hostel_id     UUID REFERENCES hostels(id),
    is_active     BOOLEAN DEFAULT true,
    password_hash VARCHAR(255),
    fcm_token     TEXT,
    created_at    TIMESTAMPTZ DEFAULT NOW(),
    updated_at    TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE otp_codes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone       VARCHAR(15) NOT NULL,
    code        VARCHAR(6) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    attempts    INT DEFAULT 0,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

-- ORGANIZATIONAL STRUCTURE
CREATE TABLE institutions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    type         VARCHAR(20) CHECK (type IN ('university','municipal_corp','corporate')),
    address      TEXT,
    geo_boundary GEOGRAPHY(POLYGON, 4326),
    settings     JSONB DEFAULT '{}',
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE faculties (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id  UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL
);

CREATE TABLE departments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id  UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    faculty_id      UUID REFERENCES faculties(id),
    name            VARCHAR(255) NOT NULL,
    code            VARCHAR(20),
    geo_location    GEOGRAPHY(POINT, 4326)
);

CREATE TABLE hostels (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id  UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    geo_location    GEOGRAPHY(POINT, 4326),
    resident_count  INT
);

-- ZONES & BINS
CREATE TABLE zones (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id  UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    type            VARCHAR(30) CHECK (type IN ('hostel','mess','canteen','academic','garden','common','ward')),
    geo_boundary    GEOGRAPHY(POLYGON, 4326),
    center_point    GEOGRAPHY(POINT, 4326),
    waste_intensity VARCHAR(10) DEFAULT 'medium' CHECK (waste_intensity IN ('low','medium','high','critical'))
);

CREATE TABLE bins (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_id         UUID NOT NULL REFERENCES zones(id) ON DELETE CASCADE,
    qr_code         VARCHAR(100) UNIQUE NOT NULL,
    waste_type      VARCHAR(20) CHECK (waste_type IN ('wet','dry','sanitary','special')),
    capacity_litres INT DEFAULT 120,
    current_fill    INT DEFAULT 0,
    geo_location    GEOGRAPHY(POINT, 4326) NOT NULL,
    is_active       BOOLEAN DEFAULT true,
    last_scanned_at TIMESTAMPTZ,
    photo_url       TEXT,
    ai_verified     BOOLEAN DEFAULT false,
    ai_result       JSONB,
    installed_at    TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX idx_bins_qr ON bins(qr_code);
CREATE INDEX idx_bins_geo ON bins USING GIST(geo_location);

-- SCANS & ATTENDANCE
CREATE TABLE bin_scans (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bin_id          UUID NOT NULL REFERENCES bins(id) ON DELETE CASCADE,
    cleaner_id      UUID NOT NULL REFERENCES users(id),
    scan_type       VARCHAR(20) CHECK (scan_type IN ('cleaning','inspection','fill_check')),
    photo_url       TEXT,
    geo_location    GEOGRAPHY(POINT, 4326) NOT NULL,
    scan_distance   FLOAT,
    ai_classification JSONB,
    is_offline      BOOLEAN DEFAULT false,
    device_id       VARCHAR(255),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    synced_at       TIMESTAMPTZ
);
CREATE INDEX idx_scans_bin ON bin_scans(bin_id);
CREATE INDEX idx_scans_created ON bin_scans(created_at);
SELECT create_hypertable('bin_scans', 'created_at');

CREATE TABLE attendance (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cleaner_id    UUID NOT NULL REFERENCES users(id),
    zone_id       UUID REFERENCES zones(id),
    check_in_time TIMESTAMPTZ NOT NULL,
    check_out_time TIMESTAMPTZ,
    check_in_geo  GEOGRAPHY(POINT, 4326) NOT NULL,
    total_scans   INT DEFAULT 0,
    is_offline    BOOLEAN DEFAULT false
);

-- TICKETS
CREATE TABLE tickets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_number   VARCHAR(20) UNIQUE NOT NULL,
    raised_by       UUID NOT NULL REFERENCES users(id),
    zone_id         UUID NOT NULL REFERENCES zones(id),
    bin_id          UUID REFERENCES bins(id),
    type            VARCHAR(30) CHECK (type IN ('overflow','misplacement','missegregration','damaged_bin','hygiene','pothole','other')),
    priority        VARCHAR(10) DEFAULT 'medium' CHECK (priority IN ('low','medium','high','critical')),
    ai_priority     VARCHAR(10),
    ai_category     VARCHAR(50),
    description     TEXT,
    photo_url       TEXT,
    geo_location    GEOGRAPHY(POINT, 4326) NOT NULL,
    status          VARCHAR(20) DEFAULT 'open' CHECK (status IN ('open','assigned','in_progress','resolved','closed','escalated')),
    assigned_to     UUID REFERENCES users(id),
    sla_deadline    TIMESTAMPTZ,
    escalation_level INT DEFAULT 0,
    is_offline      BOOLEAN DEFAULT false,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ
);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_geo ON tickets USING GIST(geo_location);

-- GAMIFICATION
CREATE TABLE points_ledger (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id),
    points       INT NOT NULL,
    reason       VARCHAR(100) NOT NULL,
    reference_id UUID,
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID REFERENCES users(id),
    title      VARCHAR(255) NOT NULL,
    body       TEXT,
    type       VARCHAR(50) NOT NULL,
    data       JSONB,
    channel    VARCHAR(20) DEFAULT 'push' CHECK (channel IN ('push','sms','email','in_app')),
    is_read    BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- MARKETPLACE
CREATE TABLE marketplace_listings (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id UUID NOT NULL REFERENCES institutions(id),
    waste_type     VARCHAR(20) NOT NULL,
    quantity_kg    DECIMAL(10,2) NOT NULL,
    quality_grade  VARCHAR(10),
    asking_price   DECIMAL(10,2),
    status         VARCHAR(20) DEFAULT 'listed' CHECK (status IN ('listed','negotiating','sold','expired','withdrawn')),
    created_at     TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE marketplace_transactions (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id     UUID NOT NULL REFERENCES marketplace_listings(id),
    quantity_kg    DECIMAL(10,2) NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    commission_pct DECIMAL(5,2) DEFAULT 7.00,
    status         VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending','completed','cancelled')),
    created_at     TIMESTAMPTZ DEFAULT NOW()
);

-- AUDIT LOG
CREATE TABLE audit_log (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   UUID,
    old_value   JSONB,
    new_value   JSONB,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);
```

### 4.2 Data Retention

| Data | Retention | Rationale |
|------|-----------|-----------|
| bin_scans | 2 years | Trend analysis, then aggregate |
| tickets | 3 years | Compliance audit |
| attendance | 1 year | Worker privacy |
| notifications | 90 days | Operational only |
| audit_log | 5 years | Legal compliance |
| photos (R2) | 6 months | AI training, then delete |
| OTP codes | 10 minutes | Security |

---

## 5. Backend API Design

### 5.1 Conventions

- Base URL: `https://api.zerow.in/api/v1`
- Auth: Bearer JWT in `Authorization` header
- Pagination: `?page=1&limit=20` → `{ data, total, page, limit, hasMore }`
- Errors: `{ error: { code, message, details } }`
- Rate limit: 100 req/min per user (Redis token bucket)
- WebSocket: `wss://api.zerow.in/ws`

### 5.2 Auth Endpoints

```
POST   /auth/send-otp          Send OTP to phone
POST   /auth/verify-otp        Verify OTP → JWT + refresh token
POST   /auth/refresh           Refresh access token
POST   /auth/register          Register (student/cleaner)
GET    /auth/me                Current user profile
PATCH  /auth/me                Update profile
POST   /auth/logout            Invalidate session
POST   /auth/fcm-token         Register FCM push token
```

### 5.3 Bin Endpoints

```
GET    /bins                    List (filter: zone_id, waste_type, is_active, fill_level)
GET    /bins/:id                Details
GET    /bins/qr/:qrCode         Lookup by QR (cleaner app)
POST   /bins                    Register (admin)
PATCH  /bins/:id                Update (relocate, deactivate)
GET    /bins/:id/scans          Scan history
GET    /bins/:id/fill-history   Fill level time-series
POST   /bins/:id/scan           Submit scan (cleaner)
       Body: { photo, geo_location, scan_type, is_offline, device_id }
       → Queues AI verification if photo provided
GET    /bins/nearby             Bins near location (PostGIS)
       Query: ?lat=27.915&lng=78.082&radius=500
```

### 5.4 Ticket Endpoints

```
GET    /tickets                 List (filter: status, zone_id, priority, assigned_to)
GET    /tickets/:id             Details
POST   /tickets                 Raise ticket (student)
       Body: { zone_id, bin_id?, type, description?, photo?, geo_location }
       → Queues AI triage (NLP classification + priority)
PATCH  /tickets/:id             Update (assign, change status)
POST   /tickets/:id/assign      Assign to cleaner
POST   /tickets/:id/resolve     Resolve (cleaner)
POST   /tickets/:id/escalate    Escalate (auto or manual)
GET    /tickets/stats           Statistics for dashboard
```

### 5.5 Zone & Map Endpoints

```
GET    /zones                   List zones
GET    /zones/:id               Zone details + live bin status
GET    /zones/:id/heatmap       Waste heat map data → { points: [{ lat, lng, intensity }] }
GET    /zones/:id/stats         Zone stats (waste, tickets, compliance)
POST   /zones                   Create (admin)
GET    /map/campus              Full campus map → { zones, bins, hotspots, cleaner_routes }
GET    /map/heatmap             Campus-wide heat map → GeoJSON FeatureCollection
```

### 5.6 Attendance Endpoints

```
POST   /attendance/check-in     Body: { zone_id, geo_location }
POST   /attendance/check-out    Body: { geo_location }
GET    /attendance/today        Today's attendance
GET    /attendance/report       Report (admin) ?cleaner_id&start_date&end_date
```

### 5.7 Dashboard Endpoints

```
GET    /dashboard/overview      Real-time: total_waste, bins, overflow, tickets, compliance, zones
GET    /dashboard/waste-trends  ?period=daily|weekly|monthly&start_date&end_date
GET    /dashboard/ticket-stats  Ticket statistics
GET    /dashboard/leaderboard   ?scope=student|department|hostel&period=weekly|monthly
GET    /dashboard/compliance    Compliance score breakdown
GET    /dashboard/reports/:type Generate PDF/CSV report
```

### 5.8 Offline Sync

```
POST   /sync/batch              Batch sync offline data
       Body: {
         scans: [{ bin_id, photo, geo_location, scan_type, created_at, device_id }],
         tickets: [{ zone_id, type, description, photo, geo_location, created_at }],
         attendance: [{ check_in_time, check_out_time, geo_location }]
       }
       Returns: { synced: { scans: 5, tickets: 2, attendance: 1 }, errors: [] }
```

### 5.9 WebSocket Events

```
Client → Server:
  'join_zone'        { zone_id }
  'join_dashboard'   { institution_id }

Server → Client:
  'bin_updated'      { bin_id, fill_level, last_scanned_at }
  'bin_overflow'     { bin_id, zone_id, predicted_overflow_time }
  'ticket_created'   { ticket_id, zone_id, type, priority }
  'ticket_assigned'  { ticket_id, assigned_to, sla_deadline }
  'ticket_resolved'  { ticket_id, resolved_at }
  'ticket_escalated' { ticket_id, escalation_level }
  'sla_breach'       { ticket_id, time_overdue }
  'leaderboard_update' { scope, period, changes: [] }
  'compliance_alert' { zone_id, score, issues: [] }
```

---

## 6. Maps & Geospatial System

### 6.1 Map Requirements

1. Show campus with zone boundaries (polygons)
2. Display all bins as markers with real-time fill status (color-coded)
3. Waste heat maps (intensity by zone)
4. Cleaner routes and live positions
5. Ticket locations with priority indicators
6. Offline on mobile (cached tiles)

### 6.2 Web Dashboard (Mapbox GL JS)

```typescript
const map = new mapboxgl.Map({
  container: 'campus-map',
  style: 'mapbox://styles/mapbox/streets-v12',
  center: [78.082, 27.915],  // AMU campus
  zoom: 15
});

// Sources
map.addSource('zones', { type: 'geojson', data: zonesGeoJSON });
map.addSource('bins', { type: 'geojson', data: binsGeoJSON, cluster: true, clusterRadius: 30 });
map.addSource('heatmap', { type: 'geojson', data: heatMapGeoJSON });
map.addSource('tickets', { type: 'geojson', data: ticketsGeoJSON });

// Zone fills — color by waste_intensity
map.addLayer({
  id: 'zone-fills', type: 'fill', source: 'zones',
  paint: { 'fill-color': ['get','color'], 'fill-opacity': 0.15 }
});

// Heatmap layer
map.addLayer({
  id: 'heatmap-layer', type: 'heatmap', source: 'heatmap', maxzoom: 18,
  paint: {
    'heatmap-weight': ['get','intensity'],
    'heatmap-color': ['interpolate',['linear'],['heatmap-density'],
      0,'rgba(33,102,172,0)', 0.4,'rgb(209,229,240)',
      0.6,'rgb(253,219,199)', 0.8,'rgb(239,138,98)', 1,'rgb(178,24,43)'],
    'heatmap-radius': ['interpolate',['linear'],['zoom'], 0,5, 15,30]
  }
});

// Bin markers — color by fill status
map.addLayer({
  id: 'bins-unclustered', type: 'circle', source: 'bins',
  filter: ['!',['has','point_count']],
  paint: {
    'circle-color': ['match',['get','fill_status'],
      'empty','#22c55e', 'low','#22c55e',
      'medium','#eab308', 'high','#f97316',
      'overflow','#ef4444', '#6b7280'],
    'circle-radius': 8, 'circle-stroke-width': 1.5, 'circle-stroke-color': '#fff'
  }
});
```

### 6.3 Mobile App (Flutter + flutter_map)

```dart
FlutterMap(
  options: MapOptions(center: LatLng(27.915, 78.082), zoom: 15),
  children: [
    TileLayer(
      urlTemplate: 'https://api.mapbox.com/styles/v1/mapbox/streets-v12/tiles/256/{z}/{x}/{y}@2x?access_token={token}',
      tileProvider: CachedNetworkTileProvider(),  // offline cache
    ),
    PolygonLayer(polygons: zones.map((z) => Polygon(
      points: z.boundary, color: _zoneColor(z.intensity).withOpacity(0.15),
      borderColor: _zoneColor(z.intensity), borderStrokeWidth: 2,
    )).toList()),
    MarkerLayer(markers: bins.map((b) => Marker(
      point: b.location, width: 30, height: 30,
      builder: (ctx) => _BinMarker(bin: b, onTap: () => _showBinDetails(b)),
    )).toList()),
    MarkerLayer(markers: tickets.map((t) => Marker(
      point: t.location, width: 35, height: 35,
      builder: (ctx) => _TicketMarker(ticket: t),
    )).toList()),
  ],
)
```

### 6.4 Offline Map Tiles (Mobile)

1. On first login, download tiles for zoom 13–18 covering campus + 500m buffer (~5–15MB)
2. `CachedNetworkTileProvider` stores tiles in app internal storage
3. Tiles expire after 30 days, re-download on refresh
4. Fallback: blank grid with zone overlays from local SQLite

### 6.5 PostGIS Spatial Queries

```sql
-- Bins within X meters of a point (cleaner app)
SELECT id, qr_code, waste_type, current_fill,
       ST_Distance(geo_location, ST_MakePoint($lng,$lat)::geography) as distance
FROM bins
WHERE ST_DWithin(geo_location, ST_MakePoint($lng,$lat)::geography, $radius)
  AND is_active = true
ORDER BY distance;

-- Waste intensity by zone (heat map)
SELECT z.id, z.name, ST_AsGeoJSON(z.center_point) as center,
       COUNT(b.id) as bin_count, AVG(b.current_fill) as avg_fill,
       COUNT(t.id) FILTER (WHERE t.status='open') as open_tickets,
       CASE WHEN AVG(b.current_fill)>80 THEN 'critical'
            WHEN AVG(b.current_fill)>60 THEN 'high'
            WHEN AVG(b.current_fill)>30 THEN 'medium' ELSE 'low' END as intensity
FROM zones z
LEFT JOIN bins b ON b.zone_id=z.id AND b.is_active=true
LEFT JOIN tickets t ON t.zone_id=z.id AND t.status='open'
WHERE z.institution_id=$inst_id
GROUP BY z.id, z.name, z.center_point;

-- Which zone is a point in?
SELECT id, name FROM zones
WHERE ST_Contains(geo_boundary::geometry, ST_MakePoint($lng,$lat)::geometry);
```

### 6.6 Real-Time Map Updates

| Event | Trigger | Map Action |
|-------|---------|------------|
| Bin scan | POST /bins/:id/scan | Marker color changes |
| Ticket created | POST /tickets | New marker with animation |
| Ticket resolved | PATCH /tickets/:id | Marker removed/changed |
| Overflow predicted | AI cron job | Marker pulses red |
| Cleaner check-in | POST /attendance/check-in | Cleaner icon appears |

Heat map recalculates every 5 min (server cron) → pushes updated GeoJSON via WebSocket.

---

## 7. QR Code System

### 7.1 Generation (Server-Side)

```typescript
async function generateBinQR(binId: string): Promise<Buffer> {
  const bin = await prisma.bin.findUnique({ where: { id: binId } });
  const payload = JSON.stringify({ t: 'ZW_BIN', id: bin.id, q: bin.qr_code, z: bin.zone_id });
  return QRCode.toBuffer(payload, { errorCorrectionLevel: 'H', width: 512, margin: 2 });
}
```

### 7.2 Scan Flow (Cleaner App)

1. Cleaner taps "Scan Bin" → camera opens
2. QR detected → parse → extract bin ID
3. GPS captured → compare with bin's registered location
4. If distance > 50m → warning
5. Photo prompt → capture photo of bin contents
6. Photo → upload to R2 → get URL
7. Create scan: online = POST /bins/:id/scan + AI queued; offline = local SQLite
8. Confirmation + points awarded
9. If AI result available: show "WET (87%)" or "⚠️ Mixed waste"

### 7.3 Anti-Spoofing

| Measure | Phase | Description |
|---------|-------|-------------|
| GPS distance check | P0 | Reject scans >100m from bin location |
| Photo required | P0 | No photo = no scan |
| AI photo check | P1 | Verify photo shows a bin |
| Scan frequency | P1 | Max 1 scan/bin/30min/cleaner |
| QR rotation | P2 | QR codes rotate every 24h |
| Device binding | P1 | Cleaner registered to device |
| Anomaly ML | P2 | Flag impossible scan patterns |

### 7.4 QR Sticker

- Material: Laminated waterproof vinyl, 10cm × 10cm
- Content: QR code + zone name + bin ID + "Scan to report cleaning"
- Placement: Front of bin at eye level
- Fallback: Manual bin ID entry if QR damaged

---

## 8. Authentication & RBAC

### 8.1 Auth Flow

```
User enters phone → Send OTP (SMS) → Verify OTP → JWT + Refresh token
                                               → First time? Register (name, role, dept)
```

### 8.2 JWT Structure

```json
{
  "sub": "user_uuid",
  "role": "cleaner",
  "institution_id": "inst_uuid",
  "permissions": ["scan_bin", "view_assigned_tickets", "check_in"],
  "exp": 1735689600
}
```

- Access token: 24h expiry
- Refresh token: 30 days
- Storage: `flutter_secure_storage` (mobile), httpOnly cookie (web)

### 8.3 Role Permissions

| Action | Student | Cleaner | Committee | Admin |
|--------|---------|---------|-----------|-------|
| Raise ticket | ✅ | ✅ | ✅ | ✅ |
| Scan bin | ❌ | ✅ | ✅ | ✅ |
| View own tickets | ✅ | ✅ | ✅ | ✅ |
| View all tickets | ❌ | assigned | zone-level | all |
| Assign ticket | ❌ | ❌ | ✅ | ✅ |
| Resolve ticket | ❌ | ✅ | ✅ | ✅ |
| View dashboard | ❌ | limited | zone | full |
| Manage bins | ❌ | ❌ | ❌ | ✅ |
| Manage zones | ❌ | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ❌ | ✅ |
| View reports | ❌ | ❌ | ✅ | ✅ |
| Manage staff | ❌ | ❌ | ✅ | ✅ |

---

## 9. Real-Time Communication

### 9.1 WebSocket Architecture

```
Client (app/web) ←→ Nginx ←→ Socket.io Server (Node.js) ←→ Redis pub/sub
```

- Each client joins rooms: `zone:{id}`, `institution:{id}`, `user:{id}`
- Server emits events to relevant rooms
- Redis pub/sub allows horizontal scaling (multiple Node.js instances)
- Heartbeat: client pings every 30s, server disconnects after 90s silence

### 9.2 Notification Pipeline

```
Event occurs (e.g., ticket assigned)
  → Server publishes to Redis channel 'notifications'
  → Notification worker picks up
  → Checks user's FCM token → sends FCM push
  → If no FCM token or push fails → sends SMS (Fast2SMS)
  → Stores in notifications table for in-app view
  → Emits WebSocket event for real-time UI update
```

### 9.3 SLA Escalation (node-cron)

```
Every 15 minutes:
  → Query tickets WHERE status NOT IN ('resolved','closed') AND sla_deadline < NOW()
  → For each: increment escalation_level
    - Level 1: Notify committee member for zone
    - Level 2: Notify admin
    - Level 3: Notify superadmin + mark as 'escalated'
  → Send push/SMS + WebSocket event
```

---

## 10. Revised Budget & Resource Allocation

### 10.1 Phase 0–1 Budget (Month 1–6)

| Item | Details | Cost |
|------|---------|------|
| Segregated bins (4-bin set: wet/dry/sanitary/special) | 15 sets × ₹4,500/set = ₹67,500 | ₹67,500 |
| QR stickers (laminated vinyl) | 60 stickers × ₹35 = ₹2,100 | ₹2,100 |
| Bin installation (labor + hardware) | Brackets, stands, signage | ₹8,000 |
| Awareness materials | Posters, banners, 2 events/month × 6 months | ₹48,000 |
| Server (DigitalOcean 4GB) | $24/mo × 6 months ≈ ₹12,500 | ₹12,500 |
| SMS (OTP + alerts) | ~₹1,000/month × 6 | ₹6,000 |
| Domain + SSL | 1 year | ₹1,000 |
| Mapbox (free tier) | 50K loads/month | ₹0 |
| Cloudflare R2 (free tier) | 10GB | ₹0 |
| Firebase FCM | Free | ₹0 |
| App development | Team Decode (in-house) | ₹0 (sweat equity) |
| Miscellaneous | Contingency 10% | ₹14,500 |
| **Total Phase 0–1** | | **₹1,59,600** |

> **Note on bins:** ₹4,500 per 4-bin set is based on market rates for 120L color-coded segregated bins with lids (green=wet, blue=dry, red=sanitary, black=special). This is significantly higher than the original ₹1,20,000 figure which was unrealistic for quality segregated bins across multiple zones.

### 10.2 Phase 2 Budget (Month 7–12)

| Item | Details | Cost |
|------|---------|------|
| Additional bins (campus-wide) | 100 sets × ₹4,500 | ₹4,50,000 |
| QR stickers | 400 × ₹35 | ₹14,000 |
| Composting setup | 2 composting units for organic waste | ₹40,000 |
| Server upgrade (8GB/4vCPU) | $48/mo × 6 ≈ ₹25,000 | ₹25,000 |
| SMS + notifications | ₹2,000/month × 6 | ₹12,000 |
| AI training data collection | Labeling, annotation | ₹15,000 |
| Awareness + events | 4 events/month × 6 | ₹48,000 |
| Miscellaneous | 10% contingency | ₹60,000 |
| **Total Phase 2** | | **₹6,64,000** |

### 10.3 Funding Sources

1. **UGC Green Fund** — primary grant for capex (bins, composting)
2. **CSR sponsorship** — corporate funding for campus-wide expansion
3. **AMU administration** — co-fund as part of Swachh Survekshan compliance
4. **Grants** — MoEFCC, startup schemes (Startup IndiaSeed Fund)

### 10.4 Key Budget Changes from Original

| Original | Revised | Reason |
|----------|---------|--------|
| ₹1,20,000 for bins | ₹67,500 (Phase 1) + ₹4,50,000 (Phase 2) | Original was too low for quality 4-stream segregated bins |
| ₹8,000/month labour | ₹0 (coordinate with existing staff) | University already has paid cleaning staff |
| ₹30,000 miscellaneous | 10% contingency per phase | More realistic |
| No server cost | ₹12,500 + ₹25,000 | Real infrastructure cost |
| Hospital segment included | Removed | JNMCH already handles biomedical waste |

---

## 11. EPR Registration & Legal/Privacy (Short Description)

> **Note:** This is a compliance-awareness summary, not legal advice. Full legal research is a further-stage task.

### 11.1 EPR (Extended Producer Responsibility)

If ZeroW facilitates aggregation and sale of plastic waste to recyclers, it may need to be aware of EPR registration under the Plastic Waste Management Rules. EPR requires producers/importers/brand-owners to register with CPCB and ensure plastic waste is collected and recycled. ZeroW as an **aggregator platform** may not need direct EPR registration, but its recycler partners must be EPR-registered. This should be verified legally before marketplace launch in Phase 2.

### 11.2 Key Legal Frameworks (Awareness Level)

- **SWM Rules 2026**: Mandates 4-stream segregation. ZeroW's core value proposition is compliance tool.
- **Biomedical Waste Rules 2016**: Applies to JNMCH — ZeroW does NOT touch hospital waste. No compliance burden here.
- **E-Waste Rules**: If e-waste collection feature expands, route to CPCB-authorised recyclers.
- **Digital Personal Data Protection Act 2023**: Applies to attendance/location data of cleaning staff and student ticket data. Implement privacy-by-design: limited retention, purpose limitation, consent.
- **MoU Requirements**: Any pilot with AMU or municipal corporation needs written MoU covering data ownership, liability for missed collections, and IP ownership.

### 11.3 Privacy-by-Design Principles

1. **Limited retention**: Attendance data deleted after 1 year. Photos after 6 months.
2. **Purpose limitation**: Location data used only for scan verification, not surveillance.
3. **Consent**: Cleaners must consent to location tracking during work hours. Opt-out available (with manual fallback).
4. **Data minimization**: Collect only what's needed — no personal data beyond name + phone.
5. **Anonymization**: Analytics and AI training data anonymized — no student PII in models.

---

*End of Part 1. Part 2 covers: AI Layer (segregation verification, NLP triage, predictive overflow, anomaly detection), Frontend Apps (Flutter screens, React dashboard), Offline Mode architecture, Security hardening, Deployment & DevOps, Testing strategy, and detailed Build Roadmap.*
