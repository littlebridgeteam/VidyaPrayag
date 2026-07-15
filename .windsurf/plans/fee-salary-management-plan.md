# Fee & Salary Management System — End-to-End Build Plan

## 1. Overview

A ledger-based fee and salary management system for EnrollPlus Phase 1 (no payment gateway — "Coming Soon" messaging). The system covers:

- **Admin**: Fee structure setup (common monthly fees + per-class overrides + additional charges), fee payment tracking by student/month, fee reminder date configuration
- **Parent**: Fee breakdown with descriptions, payment status visibility, "Pay Now" → "Coming Soon" overlay
- **Teacher**: Salary setup (admin-side from teacher profile), salary payment history overlay (teacher-visible)

## 2. Database Schema (New Tables + Migration)

### 2.1 New Tables in `Tables.kt`

**`FeeStructuresTable`** — Template for recurring monthly fees per school/class
```
school_id      uuid
class_id       uuid (nullable — null = school-wide default)
title          text (e.g. "Tuition Fee", "Bus Fee")
amount         double
currency       varchar(8) default "INR"
frequency      varchar(16) default "MONTHLY"
is_active      bool default true
created_at     timestamp
updated_at     timestamp
```

**`FeeAdditionalChargesTable`** — One-off charges per student/month with description
```
school_id      uuid
child_id       uuid
class_id       uuid (nullable)
month          varchar(7) — "YYYY-MM"
title          text
description    text (nullable)
amount         double
currency       varchar(8) default "INR"
created_at     timestamp
updated_at     timestamp
```

**`FeeReminderConfigTable`** — Per-school reminder day config
```
school_id      uuid (unique)
reminder_day   int (1-28, day of month)
is_active      bool default true
updated_at     timestamp
```

**`SalaryRecordsTable`** — Teacher/staff salary setup + payment history
```
school_id      uuid
teacher_id     uuid (app_users.id, role=teacher)
month          varchar(7) — "YYYY-MM"
base_salary    double
allowances     double default 0.0
deductions     double default 0.0
net_amount     double
currency       varchar(8) default "INR"
status         varchar(16) default "UNPAID" — UNPAID | PAID
paid_at        timestamp (nullable)
notes          text (nullable)
created_at     timestamp
updated_at     timestamp
```

### 2.2 Migration SQL
File: `database/migrations/setup_fee_salary_management.sql`

### 2.3 Existing Table (No changes needed)
`FeeRecordsTable` — already stores per-line-item fees with status (PAID/DUE/OVERDUE), amounts, due dates, descriptions. We'll use this as the ledger.

## 3. Backend Routes

### 3.1 New File: `FeeSalaryRouting.kt`
Location: `server/src/main/kotlin/com/littlebridge/enrollplus/feature/school/FeeSalaryRouting.kt`

**Admin Fee Structure endpoints** (JWT + school-scoped):
- `GET  /api/v1/school/fees/structures` — list all fee structures (optional classId filter)
- `POST /api/v1/school/fees/structures` — create fee structure
- `PUT  /api/v1/school/fees/structures/{id}` — update fee structure
- `DELETE /api/v1/school/fees/structures/{id}` — delete fee structure

**Admin Fee Additional Charges endpoints**:
- `GET  /api/v1/school/fees/charges?childId=&month=` — list additional charges
- `POST /api/v1/school/fees/charges` — add additional charge to a student/month
- `DELETE /api/v1/school/fees/charges/{id}` — remove additional charge

**Admin Fee Payment Tracking endpoints**:
- `GET  /api/v1/school/fees/students?classId=&section=&month=&search=` — list students with fee status for filters
- `POST /api/v1/school/fees/mark-paid` — mark fee record(s) as PAID for student + month(s)
- `POST /api/v1/school/fees/generate` — generate monthly fee records from structures + additional charges for a given month

**Admin Fee Reminder Config endpoints**:
- `GET  /api/v1/school/fees/reminder-config` — get current reminder day
- `PUT  /api/v1/school/fees/reminder-config` — set reminder day (1-28)

**Admin Salary endpoints**:
- `GET  /api/v1/school/salary?teacherId=&month=` — get salary record(s)
- `POST /api/v1/school/salary` — set/upsert salary for a teacher/month
- `PUT  /api/v1/school/salary/{id}/mark-paid` — mark salary as paid

**Teacher Salary endpoint**:
- `GET  /api/v1/teacher/salary` — get own salary history (current + previous months)

### 3.2 Modify: `ParentFeesRouting.kt`
- Enhance `GET /api/v1/parent/fees` to include fee breakdown items (title, description, amount, status, month, category) — not just aggregates
- Keep `POST /api/v1/parent/fees/pay` but change behavior: instead of marking paid, return a "coming soon" response (payment gateway not integrated)

### 3.3 Modify: `NotificationScheduler.kt`
- Update `checkFeeReminders()` to use `FeeReminderConfigTable.reminderDay` for each school instead of just checking `dueDate`

### 3.4 Route Registration
Add `feeSalaryRouting()` to `Application.kt` alongside existing `schoolRecordsRouting()`.
Add `teacherSalaryRouting()` to the teacher routes section.

## 4. Shared Models (DTOs)

### 4.1 New File: `shared/.../feature/admin/domain/model/FeeSalaryModels.kt`

**Fee Structure models**:
- `FeeStructureDto` (id, schoolId, classId?, title, amount, currency, frequency, isActive)
- `FeeStructureListResponse` (structures: List<FeeStructureDto>)
- `CreateFeeStructureRequest` (classId?, title, amount, currency, frequency)
- `UpdateFeeStructureRequest` (title, amount, currency, frequency, isActive)

**Fee Additional Charge models**:
- `FeeAdditionalChargeDto` (id, childId, childName, month, title, description?, amount, currency)
- `CreateFeeAdditionalChargeRequest` (childId, month, title, description?, amount)

**Fee Student List models**:
- `FeeStudentDto` (childId, childName, parentId, className, section, month, totalAmount, paidAmount, dueAmount, status, feeItems: List<FeeItemDto>)
- `FeeItemDto` (id, title, description?, amount, status, category, month)
- `FeeStudentListResponse` (students: List<FeeStudentDto>, totalDue, totalPaid, currency)
- `MarkPaidRequest` (childId, months: List<String>)
- `GenerateFeesRequest` (month, classId?)

**Fee Reminder Config models**:
- `FeeReminderConfigDto` (reminderDay, isActive)
- `UpdateFeeReminderConfigRequest` (reminderDay, isActive)

**Salary models**:
- `SalaryRecordDto` (id, teacherId, teacherName, month, baseSalary, allowances, deductions, netAmount, currency, status, paidAt?, notes?)
- `SalaryListResponse` (records: List<SalaryRecordDto>)
- `SetSalaryRequest` (teacherId, month, baseSalary, allowances, deductions, notes?)
- `TeacherSalaryResponse` (records: List<SalaryRecordDto>, currentMonth: SalaryRecordDto?)

### 4.2 Modify: `ParentFeatureModels.kt`
- Add `FeeBreakdownItem` (id, title, description?, amount, status, category, month) to `FeeData`
- Add `feeItems: List<FeeBreakdownItem>` to `FeeData`

## 5. API Client

### 5.1 New File: `shared/.../feature/admin/data/remote/FeeSalaryApi.kt`
Ktor client with all admin fee/salary endpoints. Follows `AdminDashboardApi` pattern:
- `getFeeStructures(token, classId?)`
- `createFeeStructure(token, request)`
- `updateFeeStructure(token, id, request)`
- `deleteFeeStructure(token, id)`
- `getAdditionalCharges(token, childId?, month?)`
- `createAdditionalCharge(token, request)`
- `deleteAdditionalCharge(token, id)`
- `getFeeStudents(token, classId?, section?, month?, search?)`
- `markFeesPaid(token, request)`
- `generateFees(token, request)`
- `getReminderConfig(token)`
- `updateReminderConfig(token, request)`
- `getSalaryRecords(token, teacherId?, month?)`
- `setSalary(token, request)`
- `markSalaryPaid(token, id)`

### 5.2 New File: `shared/.../feature/teacher/data/remote/TeacherSalaryApi.kt`
- `getMySalary(token)` — GET /api/v1/teacher/salary

### 5.3 Modify: `ParentApi.kt`
- Update `getFees()` to handle the enhanced response with fee items

## 6. Repository

### 6.1 New File: `shared/.../feature/admin/data/repository/FeeSalaryRepositoryImpl.kt`
- Interface: `FeeSalaryRepository` in domain layer
- Methods mirror all API client methods
- Uses `safeApiCall` pattern

### 6.2 New File: `shared/.../feature/teacher/data/repository/TeacherSalaryRepositoryImpl.kt`
- Interface: `TeacherSalaryRepository`
- `getMySalary(token: String)`

### 6.3 Modify: `ParentRepositoryImpl.kt`
- Update `getFees` to handle enhanced response (no interface change, just deserialization)

## 7. ViewModels

### 7.1 New: `FeeSalaryViewModel` (admin)
Location: `shared/.../feature/admin/presentation/FeeSalaryViewModel.kt`

State: `FeeSalaryState` with sub-states for two tabs:
- **Fees tab**: structures list, students list (with filters), reminder config, additional charges
- **Salary tab**: salary records list, selected teacher salary form

UI state: loading, error, content (with all data), empty states per sub-section

### 7.2 New: `TeacherSalaryViewModel` (teacher)
Location: `shared/.../feature/teacher/presentation/TeacherSalaryViewModel.kt`

State: `TeacherSalaryState` — loading, error, records list, current month highlight

### 7.3 Modify: `FeeViewModel` (parent)
- Add `feeItems: List<FeeBreakdownItem>` to state
- Handle enhanced API response

## 8. Koin Registration

### 8.1 In `Koin.kt` commonModule:
- Register `FeeSalaryApi` singleton
- Register `FeeSalaryRepository` singleton
- Register `TeacherSalaryApi` singleton
- Register `TeacherSalaryRepository` singleton

### 8.2 In `Koin.kt` viewModelModule:
- `factory { FeeSalaryViewModel(get(), get()) }`
- `factory { TeacherSalaryViewModel(get(), get()) }`

## 9. UI Screens

### 9.1 Admin: `FeeSalaryManagementScreen.kt`
Location: `composeApp/.../ui/v2/screens/school/FeeSalaryManagementScreen.kt`

Two-tab layout (Fees | Salary):

**Fees Tab**:
- Sub-tab 1: **Fee Structure** — list of fee structures with add/edit/delete, class filter dropdown
- Sub-tab 2: **Payment Tracking** — student list with class/section/month filters + search, tap student → fee detail with mark-paid action, "Generate Monthly Fees" button
- Sub-tab 3: **Reminder Settings** — reminder day picker (1-28), active toggle

**Salary Tab**:
- Teacher list (from existing teacher roster endpoint) → tap teacher → salary setup form (base, allowances, deductions, notes) + payment history list with mark-paid action

Uses: VBackHeader, VStateHost, VPullRefresh, VCard, VButton, VTypography, VColors tokens

### 9.2 Parent: Modify `ParentFeesScreenV2.kt`
- Add fee breakdown section showing individual fee items with descriptions
- "Pay Now" → show "Payment Gateway Coming Soon" message instead of payment flow

### 9.3 Parent: Modify `ParentFeePaymentScreenV2.kt`
- Replace payment UI with "Coming Soon" message + ledger info

### 9.4 Teacher: `TeacherSalaryOverlayScreen.kt`
Location: `composeApp/.../ui/v2/screens/teacher/TeacherSalaryOverlayScreen.kt`

- Shows salary history (current + previous months)
- Each month: amount breakdown (base, allowances, deductions, net), status badge (PAID/UNPAID)
- Uses VBackHeader, VStateHost, VCard, VTypography

## 10. Navigation & Wiring

### 10.1 Admin Portal (`SchoolPortalV2.kt`)
- Add `FeeSalaryManagement` to `SchoolOverlay` enum
- Add overlay rendering branch → `FeeSalaryManagementScreen(onBack = { overlay = SchoolOverlay.None })`
- In `SchoolSettingsScreenV2.kt`:
  - Change "Fee structure" row from `comingSoon = true` to `comingSoon = false, onClick = onOpenFeeSalary`
  - Add `onOpenFeeSalary: () -> Unit` parameter
  - Rename label to "Fee & Salary"
- In `SchoolPortalV2.kt` settings wiring: `onOpenFeeSalary = { overlay = SchoolOverlay.FeeSalaryManagement }`

### 10.2 Teacher Portal (`TeacherPortalV2.kt`)
- Add `SalaryHistory` to `TeacherOverlay` enum
- Add overlay rendering branch → `TeacherSalaryOverlayScreen(onBack = { overlay = TeacherOverlay.None })`
- In `TeacherProfileScreenV2.kt`: add a "Salary & Payments" section row that triggers `onOpenSalary` callback
- Wire `onOpenSalary = { overlay = TeacherOverlay.SalaryHistory }` in TeacherPortalV2

### 10.3 Deep Links
- Admin: `school/fees` → `SchoolOverlay.FeeSalaryManagement`
- Teacher: `teacher/salary` → `TeacherOverlay.SalaryHistory`

## 11. Implementation Order

1. **Database**: Add tables to `Tables.kt` + migration SQL
2. **Backend routes**: `FeeSalaryRouting.kt` + teacher salary route + modify parent fees route + modify notification scheduler
3. **Route registration**: Wire in `Application.kt`
4. **Shared models**: `FeeSalaryModels.kt` + modify parent models
5. **API clients**: `FeeSalaryApi.kt` + `TeacherSalaryApi.kt` + modify `ParentApi.kt`
6. **Repositories**: `FeeSalaryRepositoryImpl.kt` + `TeacherSalaryRepositoryImpl.kt`
7. **ViewModels**: `FeeSalaryViewModel.kt` + `TeacherSalaryViewModel.kt` + modify `FeeViewModel.kt`
8. **Koin**: Register all new APIs, repos, VMs
9. **Admin UI**: `FeeSalaryManagementScreen.kt` + overlay wiring in `SchoolPortalV2.kt` + settings row update
10. **Teacher UI**: `TeacherSalaryOverlayScreen.kt` + overlay wiring in `TeacherPortalV2.kt` + profile row
11. **Parent UI**: Modify `ParentFeesScreenV2.kt` + `ParentFeePaymentScreenV2.kt`
12. **Verification**: Round 1 (full-chain wiring) + Round 2 (user flow + edge cases)

## 12. Full-Chain Wiring Checklist

- [ ] Server routes exist and are registered in Application.kt
- [ ] DTOs match between server and shared models (field names, types)
- [ ] Business logic: fee generation, mark-paid, salary calculation
- [ ] SQL migration file created
- [ ] Shared models: all DTOs serializable with @SerialName matching server
- [ ] API client: all endpoints covered, uses safeApiCall
- [ ] Repository: interface + impl, delegates to API
- [ ] ViewModel: state sealed/data class, loading/error/empty/content
- [ ] Koin: API, repo, VM all registered
- [ ] UI composables: use VCard, VButton, VTypography, VColors, VStateHost, VBackHeader
- [ ] 4 states handled: loading, error, empty, content
- [ ] Overlay enum + rendering branch added
- [ ] Callback wiring: settings row → overlay → screen → VM → API → server
- [ ] Source button/entry point visible and accessible
- [ ] Deep links: paths added to deep link routing
- [ ] No screen overflow: scrollable, no fixed heights on growing content
- [ ] No hardcoded data: all data from ViewModel state

## 13. Verification Protocol

### Round 1: Full-Chain Wiring (per checklist above)
Verify each link from DB → Server → DTO → API → Repo → VM → UI → Navigation.

### Round 2: User Flow & Edge Cases
- Admin creates fee structure → generates monthly fees → parent sees them
- Admin marks fee paid → parent sees updated status instantly
- Admin sets reminder day → notification fires on that day
- Admin sets teacher salary → marks paid → teacher sees in salary overlay
- Empty states: no fee structures, no students, no salary records
- Error states: network failure, server error
- Edge cases: multiple children, multiple months, partial payments
