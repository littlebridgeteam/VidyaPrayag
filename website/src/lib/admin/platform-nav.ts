export interface PlatformNavItem {
  href: string;
  label: string;
  icon: string;
  superAdminOnly?: boolean;
}

export const PLATFORM_NAV: PlatformNavItem[] = [
  { href: "/platform", label: "Dashboard", icon: "dashboard" },
  { href: "/platform/features", label: "Features", icon: "features" },
  { href: "/platform/screens", label: "Screens", icon: "screens" },
  { href: "/platform/test-cases", label: "Test Cases", icon: "tests" },
  { href: "/platform/bugs", label: "Bugs", icon: "bugs" },
  { href: "/platform/discovery", label: "Discovery", icon: "discovery", superAdminOnly: true },
  { href: "/platform/audit", label: "Audit Log", icon: "audit", superAdminOnly: true },
  { href: "/platform/settings", label: "Settings", icon: "settings", superAdminOnly: true },
];

export const PLATFORM_ICON_PATHS: Record<string, string> = {
  dashboard: "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z",
  features: "M20 6h-8l-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2z",
  screens: "M4 5h16v14H4V5zm0-2h16c1.1 0 2 .9 2 2v14c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V5c0-1.1.9-2 2-2z",
  tests: "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z",
  bugs: "M20 8h-2.81c-.45-.78-1.07-1.45-1.82-1.96L19 3.41 17.59 2l-2.2 2.2C14.97 3.74 14.02 3.5 13 3.5s-1.97.24-2.39.7L8.41 2 7 3.41l1.62 1.63C7.88 6.55 7.26 7.22 6.81 8H4v2h2.09c-.05.33-.09.66-.09 1v1H4v2h2v1c0 .34.04.67.09 1H4v2h2.81c1.04 1.79 2.97 3 5.19 3s4.15-1.21 5.19-3H20v-2h-2.09c.05-.33.09-.66.09-1v-1h2v-2h-2v-1c0-.34-.04-.67-.09-1H20V8zm-6 8h-2v-2h2v2zm0-4h-2V8h2v4z",
  discovery: "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z",
  audit: "M19 3H5c-1.11 0-2 .89-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.11-.9-2-2-2zm-2 14H7v-2h10v2zm0-4H7v-2h10v2zm0-4H7V7h10v2z",
  settings: "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z",
};

export const FEATURE_STATUSES = [
  "planned", "in_progress", "complete", "blocked", "on_hold", "deprecated",
] as const;

export const FEATURE_PRIORITIES = ["low", "medium", "high", "critical"] as const;

export const TEST_CASE_STATUSES = [
  "not_run", "in_progress", "passed", "failed", "blocked", "need_retest",
] as const;

export const BUG_STATUSES = [
  "reported", "triaged", "assigned", "in_progress", "fixed", "ready_for_qa",
  "retest", "verified", "failed", "reopened", "closed", "blocked", "duplicate", "rejected",
] as const;

export const BUG_SEVERITIES = ["critical", "major", "normal", "minor", "cosmetic"] as const;
