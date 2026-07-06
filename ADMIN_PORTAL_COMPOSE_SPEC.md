# Admin Portal — Compose Multiplatform Conversion Spec

> Source: `preview/enrollplus-admin-prototype.html` + JSON style dumps
> Rule: 1px = 1dp, font sizes in sp, NO approximations.

## 1. Shell Layout (1087×730dp)

```
Row(.shell) → Column(.sidebar 260dp) + Row(.phone-wrap) → .phone(331×716) → Column(.screen-area 317×703)
  .screen-area: StatusBar(30dp) + PortalHeader(53dp) + TabContent(545dp) + BottomNav(68dp) + Overlay
```

## 2. Design Tokens (EXACT from JSON)

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| surfaceBase | #FBF8F4 | Screen bg |
| cardWhite | #FFFFFF | Cards |
| pillBg | #F8F4EF | Subtab containers |
| trackBg | #F5F0E8 | Progress tracks |
| inkPrimary | #1A1614 | Primary text |
| inkSecondary | #8A8078 | Secondary text |
| inkTertiary | #5C544E | Tertiary text |
| accentSienna | #B45309 | Active/brand |
| accentSiennaBg | #FEF3C7 | Sienna tint |
| alertRed | #E76F51 | Alerts/badges |
| alertRedBg | #FCE8E2 | Alert icon bg |
| goodGreen | #2D7A4A | Positive |
| goodGreenBg | #D4EDDB | Green tint |
| amber | #D4A017 | Warning |
| skyBlue | #3B82A0 | Info accent |
| skyBlueBg | #DBEEF5 | Sky tint |
| purpleBg | #F0E4ED | Purple tint |
| goldBg | #FBF0D6 | Gold tint |
| sidebarBg | #131218 | Dark sidebar |
| sidebarText | #8B8895 | Inactive nav |
| sidebarGroup | #56545F | Nav group headers |
| sidebarAdmin | #5A5764 | "Admin" label |
| headerLine | #F0EAE0 | Header bottom border |

### Shadows
- cardShadow: `0,1,2,0 rgba(26,22,20,.04) + 0,1,3,0 rgba(26,22,20,.06)`
- bottomNavShadow: `0,2,8,-2 rgba(26,22,20,.08) + 0,1,3,0 rgba(26,22,20,.04)`
- activeNavShadow: `0,4,12,-2 rgba(180,83,9,.35)`

### Radii: card=14, hero=18, iconBg=10, pill=14/10, badge=full, bottomNav=24, phone=50, screen=42, island=20, homeBar=4

### Typography (Inter)
| sp | weight | usage |
|----|--------|-------|
| 8 | w700 | ring sub-label |
| 9 | w800 | badges, bar labels |
| 10 | w700 | QA labels, btn labels |
| 11 | w500-w700 | meta, section labels, subtabs |
| 12 | w500-w800 | alerts, filters, summary sub |
| 13 | w600-w700 | bar names, row titles |
| 14 | w600-w800 | statusbar, person names, nav items |
| 16 | w800 | overlay title, profile name |
| 17 | w800 | header name |
| 18 | w800-w900 | avatar, ring num |
| 22 | w900 | pulse num |
| 28 | w900 | summary val |
| 32 | w900 | hero big |

## 3. Screen Content Summary

### Records: SubtabPill(6) + SummaryCard + BarChart(5 rows) + AlertCards(2)
### People: LinkCard + SubtabPill(4) + AddButton + PersonCards(N)
### Settings: ProfileCard + SettingRows(N)
### Comms: Greeting + Hero(ring+bars) + QuickActions(5) + PulseScroll(N)
### Overlay: Header(back+title) + FilterRow(2) + NotifList

## 4. Composable List (70)

### Shell (9): AdminShell, AdminSidebar, SidebarBrand, SidebarNavGroup, SidebarNavItem, PhoneFrame, DynamicIsland, HomeBar, ScreenArea
### Header/Status (6): StatusBar, PortalHeader, PortalHeaderLeft, PortalHeaderRight, HeaderIconButton, HeaderBadge
### BottomNav (3): BottomNav, BottomNavItem, BottomNavBadge
### SubtabPill (2): SubtabPill, SubtabPillItem
### Records (10): RecordsScreen, RecordsSubtabContent, RecordsSummary, RecordsBarChartCard, RecordsBarRow, RecordsBarTrack, RecordsAlertCard, RecordsAlertIcon, RecordsAlertButton, InfoBlockGroup
### People (8): PeopleScreen, PeopleLinkCard, PeopleLinkIcon, PeopleLinkBadge, PeopleAddButton, PersonCard, PersonAvatar, PersonActionButton
### Settings (6): SettingsScreen, SettingsProfileCard, SettingsProfileAvatar, SettingsProfileBar, SettingsRow, SettingsRowIcon
### Comms (13): CommsScreen, CommsGreeting, CommsHero, CommsHeroLeft, CommsHeroRing, CommsHeroBars, CommsHeroBarLabels, QuickActionRow, QuickActionButton, QuickActionIcon, PulseScrollRow, PulseCard, PulseTrendBadge
### Overlay (6): NotificationsOverlay, OverlayHeader, OverlayBackButton, OverlayTitle, NotifFilterRow, NotifFilterItem
### Shared (7): CardSurface, PillButton, MetaText, TitleText, ChevronRight, SectionLabel, Avatar

## 5. File Structure

```
composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/admin/
├── AdminPortalScreen.kt          # AdminShell + state management
├── AdminShell.kt                 # Sidebar + PhoneFrame + ScreenArea
├── AdminSidebar.kt               # SidebarBrand + NavGroups + NavItems
├── AdminPhoneFrame.kt            # PhoneFrame + Island + HomeBar
├── AdminStatusBar.kt             # StatusBar
├── AdminPortalHeader.kt          # PortalHeader + HeaderIcon + Badge
├── AdminBottomNav.kt             # BottomNav + NavItem + Badge
├── AdminOverlay.kt               # Overlay shell + header + back
├── components/
│   ├── AdminTokens.kt            # AdminColors, AdminTypography, AdminShapes
│   ├── SubtabPill.kt             # Shared subtab pill
│   ├── CardSurface.kt            # White card with shadow
│   ├── PillButton.kt             # Generic pill button
│   ├── Avatar.kt                 # Circular avatar
│   └── Common.kt                 # MetaText, TitleText, ChevronRight, SectionLabel
├── records/
│   ├── RecordsScreen.kt
│   ├── RecordsSummary.kt
│   ├── RecordsBarChart.kt
│   └── RecordsAlert.kt
├── people/
│   ├── PeopleScreen.kt
│   ├── PeopleLinkCard.kt
│   └── PersonCard.kt
├── settings/
│   ├── SettingsScreen.kt
│   └── SettingsRow.kt
├── comms/
│   ├── CommsScreen.kt
│   ├── CommsHero.kt
│   ├── QuickActions.kt
│   └── PulseCards.kt
└── notifications/
    └── NotificationsOverlay.kt
```

## 6. Implementation Order

1. **Tokens** — AdminColors, AdminTypography, AdminShapes (from JSON exact values)
2. **Shell** — AdminShell, Sidebar, PhoneFrame, ScreenArea, StatusBar, PortalHeader, BottomNav
3. **Shared** — SubtabPill, CardSurface, PillButton, Avatar, common text helpers
4. **Records** — Summary, BarChart, Alerts
5. **People** — LinkCard, PersonCard, AddButton
6. **Settings** — ProfileCard, SettingRows
7. **Comms** — Hero, QuickActions, PulseCards
8. **Notifications** — Overlay header, filters, list
9. **Wire up** — AdminPortalScreen with tab switching + overlay state

## 7. Key Mapping Rules

- CSS `display:flex` + `flexDirection:row` → Compose `Row`
- CSS `display:flex` + `flexDirection:column` → Compose `Column`
- CSS `display:block` → Compose `Box` or `Column` (depending on children)
- CSS `justifyContent:space-between` → `Arrangement.SpaceBetween`
- CSS `alignItems:center` → `Alignment.CenterVertically`
- CSS `gap:Xpx` → `Arrangement.spacedBy(X.dp)`
- CSS `borderRadius:9999px` → `RoundedCornerShape(50)` or `CircleShape`
- CSS `boxShadow` → `Modifier.shadow()` or custom `drawBehind`
- CSS `padding: A B C D` → `PaddingValues(start=B, top=A, end=B, bottom=C)` (or use dp directly)
- CSS `margin` → `Modifier.padding()` (outer spacing) or `Spacer`
- Font: Inter (already bundled in composeResources)
