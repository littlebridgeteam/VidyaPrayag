# API Versioning Strategy

## Current State

All API routes are under `/api/v1/` (with some admin routes under `/api/admin/`). Health check (`/api/v1/health`), metrics (`/metrics`), and root (`/`) are unversioned infrastructure endpoints.

## Strategy: URL-Based Versioning

### Versioning Scheme

- **URL path versioning**: `/api/v1/...`, `/api/v2/...`
- This is the current approach, formalized here.
- Infrastructure endpoints (`/health`, `/metrics`, `/`) remain unversioned.

### Deprecation Policy

When introducing `/api/v2/` for a feature:

1. **6-month overlap**: Both v1 and v2 endpoints must be maintained simultaneously for at least 6 months.
2. **`Deprecation` header**: v1 responses include `Deprecation: true` header once v2 is available.
3. **`Sunset` header**: v1 responses include `Sunset: <date>` header 3 months before removal, indicating the removal date.
4. **Warning logs**: The server logs a warning when a deprecated v1 endpoint is called.
5. **Client notification**: The `app-status` endpoint (`/api/v1/config/app-status`) includes a `deprecations` field listing deprecated endpoints and their sunset dates.

### Version Negotiation

Clients can request a specific version via:
1. **URL path** (primary): `/api/v1/...` or `/api/v2/...`
2. **Accept header** (optional): `Accept: application/vnd.vidyaprayag.v1+json`

When both are specified, the URL path takes precedence.

### Migration Process

When a breaking change is needed:

1. Create the new endpoint under `/api/v2/` with the updated contract.
2. Add `Deprecation: true` and `Sunset: <date>` headers to the v1 response.
3. Update API documentation (OpenAPI spec) to mark v1 as deprecated.
4. Notify clients via the `app-status` deprecations field.
5. After 6 months (or sunset date), remove the v1 endpoint.

### Non-Breaking Changes (No Version Bump)

These changes do NOT require a new version:
- Adding new optional fields to request/response bodies
- Adding new endpoints
- Adding new enum values (clients must handle unknown values)
- Changing error messages
- Performance improvements

### Breaking Changes (Require Version Bump)

These changes require a new version:
- Removing or renaming fields in request/response bodies
- Changing field types
- Changing URL path structure
- Changing authentication requirements
- Removing endpoints
- Changing HTTP methods
- Changing status codes

## Implementation

The `ApiVersionInterceptor` in `core/ApiVersionInterceptor.kt`:
- Extracts the API version from the URL path
- Sets `Deprecation` and `Sunset` headers on v1 responses when v2 is available
- Logs warnings when deprecated endpoints are called
- Skips unversioned endpoints (`/health`, `/metrics`, `/`)

## Current Version

- **Active**: v1 (`/api/v1/`)
- **Deprecated**: none
- **Sunset**: none
