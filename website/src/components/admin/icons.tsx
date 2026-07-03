/** Minimal stroked icon set, 24px grid, currentColor, no dependency. */
import type { SVGProps } from "react";

type P = SVGProps<SVGSVGElement>;
const base = (props: P) => ({
  width: 20,
  height: 20,
  viewBox: "0 0 24 24",
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.8,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
  ...props,
});

export const IconDashboard = (p: P) => (
  <svg {...base(p)}><rect x="3" y="3" width="7" height="9" rx="1.5" /><rect x="14" y="3" width="7" height="5" rx="1.5" /><rect x="14" y="12" width="7" height="9" rx="1.5" /><rect x="3" y="16" width="7" height="5" rx="1.5" /></svg>
);
export const IconPeople = (p: P) => (
  <svg {...base(p)}><circle cx="9" cy="8" r="3.2" /><path d="M3.5 19a5.5 5.5 0 0 1 11 0" /><path d="M16 5.2a3 3 0 0 1 0 5.6" /><path d="M17.5 19a5 5 0 0 0-3-4.6" /></svg>
);
export const IconAttendance = (p: P) => (
  <svg {...base(p)}><rect x="3" y="4.5" width="18" height="16" rx="2" /><path d="M3 9h18M8 3v3M16 3v3" /><path d="m8.5 14 2 2 4-4" /></svg>
);
export const IconMarks = (p: P) => (
  <svg {...base(p)}><path d="M4 19V5a1 1 0 0 1 1-1h11l4 4v11a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1Z" /><path d="M15 4v4h4" /><path d="M8 13h6M8 16.5h4" /></svg>
);
export const IconFees = (p: P) => (
  <svg {...base(p)}><circle cx="12" cy="12" r="8.5" /><path d="M9 8.5h6M9 12h6M14 8.5c0 3.5-2.5 4-4.5 4 2 0 4.5 1 4.5 3.5" /></svg>
);
export const IconAnnounce = (p: P) => (
  <svg {...base(p)}><path d="M4 9v6a1 1 0 0 0 1 1h2l4 4V5L7 9H5a1 1 0 0 0-1 1Z" /><path d="M14.5 8.5a4 4 0 0 1 0 7" /><path d="M17.5 6a7 7 0 0 1 0 12" /></svg>
);
export const IconLeave = (p: P) => (
  <svg {...base(p)}><rect x="4" y="4" width="16" height="16" rx="2" /><path d="M8 3v3M16 3v3M4 9h16" /><path d="m9.5 14.5 1.5 1.5 3.5-3.5" /></svg>
);
export const IconSettings = (p: P) => (
  <svg {...base(p)}><circle cx="12" cy="12" r="3" /><path d="M19.4 12a7.4 7.4 0 0 0-.1-1.2l2-1.5-2-3.4-2.3 1a7.3 7.3 0 0 0-2-1.2l-.3-2.5H8.3L8 4.7a7.3 7.3 0 0 0-2 1.2l-2.3-1-2 3.4 2 1.5a7.4 7.4 0 0 0 0 2.4l-2 1.5 2 3.4 2.3-1a7.3 7.3 0 0 0 2 1.2l.3 2.5h3.4l.3-2.5a7.3 7.3 0 0 0 2-1.2l2.3 1 2-3.4-2-1.5c.1-.4.1-.8.1-1.2Z" /></svg>
);
export const IconBell = (p: P) => (
  <svg {...base(p)}><path d="M18 8a6 6 0 1 0-12 0c0 7-3 9-3 9h18s-3-2-3-9" /><path d="M13.7 21a2 2 0 0 1-3.4 0" /></svg>
);
export const IconSearch = (p: P) => (
  <svg {...base(p)}><circle cx="11" cy="11" r="7" /><path d="m20 20-3.5-3.5" /></svg>
);
export const IconPlus = (p: P) => (
  <svg {...base(p)}><path d="M12 5v14M5 12h14" /></svg>
);
export const IconChevronRight = (p: P) => (
  <svg {...base(p)}><path d="m9 6 6 6-6 6" /></svg>
);
export const IconLogout = (p: P) => (
  <svg {...base(p)}><path d="M15 4h3a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-3" /><path d="M10 12H3m0 0 3-3m-3 3 3 3" /></svg>
);
export const IconArrowUp = (p: P) => (
  <svg {...base(p)}><path d="M12 19V5m0 0-6 6m6-6 6 6" /></svg>
);
export const IconArrowDown = (p: P) => (
  <svg {...base(p)}><path d="M12 5v14m0 0 6-6m-6 6-6-6" /></svg>
);
export const IconClose = (p: P) => (
  <svg {...base(p)}><path d="M6 6l12 12M18 6 6 18" /></svg>
);
export const IconMenu = (p: P) => (
  <svg {...base(p)}><path d="M4 6h16M4 12h16M4 18h16" /></svg>
);
export const IconCheck = (p: P) => (
  <svg {...base(p)}><path d="m5 12 5 5 9-11" /></svg>
);
export const IconTrash = (p: P) => (
  <svg {...base(p)}><path d="M4 7h16M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2m2 0v12a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V7" /></svg>
);
export const IconExternal = (p: P) => (
  <svg {...base(p)}><path d="M14 4h6v6M20 4l-9 9M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5" /></svg>
);
export const IconWarning = (p: P) => (
  <svg {...base(p)}><path d="M12 3 2 20h20L12 3Z" /><path d="M12 10v4m0 3h.01" /></svg>
);
export const IconPulse = (p: P) => (
  <svg {...base(p)}><path d="M3 12h4l2-6 4 14 2-8h6" /></svg>
);
export const IconCalendar = (p: P) => (
  <svg {...base(p)}><rect x="3" y="4" width="18" height="17" rx="2" /><path d="M3 9h18M8 2v4M16 2v4" /></svg>
);
export const IconMessage = (p: P) => (
  <svg {...base(p)}><path d="M4 5h16a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H8l-4 4V6a1 1 0 0 1 1-1Z" /></svg>
);
export const IconBolt = (p: P) => (
  <svg {...base(p)}><path d="M13 2 4 14h6l-1 8 9-12h-6l1-8Z" /></svg>
);
export const IconGrid = (p: P) => (
  <svg {...base(p)}><rect x="3" y="3" width="7" height="7" rx="1.5" /><rect x="14" y="3" width="7" height="7" rx="1.5" /><rect x="3" y="14" width="7" height="7" rx="1.5" /><rect x="14" y="14" width="7" height="7" rx="1.5" /></svg>
);
export const IconChevronLeft = (p: P) => (
  <svg {...base(p)}><path d="m15 6-6 6 6 6" /></svg>
);
export const IconPanelLeft = (p: P) => (
  <svg {...base(p)}><rect x="3" y="4" width="18" height="16" rx="2" /><path d="M9 4v16" /></svg>
);
export const IconSparkle = (p: P) => (
  <svg {...base(p)}><path d="M12 3v4M12 17v4M3 12h4M17 12h4M5.6 5.6l2.8 2.8M15.6 15.6l2.8 2.8M18.4 5.6l-2.8 2.8M8.4 15.6l-2.8 2.8" /></svg>
);
export const IconAlumni = (p: P) => (
  <svg {...base(p)}><path d="M12 4 2 9l10 5 10-5-10-5Z" /><path d="M6 11v4c0 1 3 3 6 3s6-2 6-3v-4" /><path d="M22 9v5" /></svg>
);
export const IconShield = (p: P) => (
  <svg {...base(p)}><path d="M12 3l7 3v5c0 4.5-3 8-7 10-4-2-7-5.5-7-10V6l7-3Z" /><path d="M9.5 12l1.8 1.8L15 10" /></svg>
);
export const IconBook = (p: P) => (
  <svg {...base(p)}><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" /><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z" /><path d="M9 7h7M9 11h5" /></svg>
);
export const IconReport = (p: P) => (
  <svg {...base(p)}><path d="M4 4h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1Z" /><path d="M7 8h6M7 12h10M7 16h7" /><path d="M15 4v4h2" /></svg>
);
export const IconLogs = (p: P) => (
  <svg {...base(p)}><rect x="3" y="4" width="18" height="16" rx="2" /><path d="M7 9l3 3-3 3" /><path d="M13 15h4" /></svg>
);
export const IconTransport = (p: P) => (
  <svg {...base(p)}><rect x="4" y="5" width="16" height="12" rx="2" /><path d="M4 11h16" /><circle cx="8" cy="19" r="1.5" /><circle cx="16" cy="19" r="1.5" /><path d="M7 19h10" /></svg>
);
export const IconLibrary = (p: P) => (
  <svg {...base(p)}><path d="M5 4v16M9 4v16M13 4v16M17 4v16" /><path d="M3 4h18" /></svg>
);
export const IconScholarship = (p: P) => (
  <svg {...base(p)}><path d="M12 3l8 4-8 4-8-4 8-4Z" /><path d="M8 9v5c0 1 2 2 4 2s4-1 4-2V9" /><path d="M20 7v5" /></svg>
);
export const IconSchedule = (p: P) => (
  <svg {...base(p)}><circle cx="12" cy="12" r="8.5" /><path d="M12 7v5l3 2" /></svg>
);
export const IconClasses = (p: P) => (
  <svg {...base(p)}><rect x="3" y="5" width="18" height="14" rx="2" /><path d="M3 9h18M9 5v14" /></svg>
);
export const IconHealth = (p: P) => (
  <svg {...base(p)}><path d="M12 21s-7-4.5-7-10a4 4 0 0 1 7-2.5A4 4 0 0 1 19 11c0 5.5-7 10-7 10Z" /><path d="M12 11v3" /></svg>
);
export const IconIdCard = (p: P) => (
  <svg {...base(p)}><rect x="3" y="6" width="18" height="12" rx="2" /><circle cx="8" cy="11" r="1.5" /><path d="M6 15h4M14 10h4M14 14h4" /></svg>
);
export const IconEvent = (p: P) => (
  <svg {...base(p)}><rect x="3" y="5" width="18" height="16" rx="2" /><path d="M3 9h18M8 3v4M16 3v4" /><path d="M8 14l2 2 4-4" /></svg>
);
export const IconAdmissions = (p: P) => (
  <svg {...base(p)}><path d="M14 3v5h5" /><path d="M14 3H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-5Z" /><path d="M9 13l2 2 4-4" /></svg>
);
export const IconLink = (p: P) => (
  <svg {...base(p)}><path d="M10 14a4 4 0 0 0 5.66 0l3-3a4 4 0 0 0-5.66-5.66l-1 1" /><path d="M14 10a4 4 0 0 0-5.66 0l-3 3a4 4 0 0 0 5.66 5.66l1-1" /></svg>
);
export const IconBranding = (p: P) => (
  <svg {...base(p)}><circle cx="12" cy="12" r="8.5" /><path d="M12 7v10M9.5 9.5h4a2 2 0 0 1 0 4h-4" /></svg>
);
export const IconCalendarPlatform = (p: P) => (
  <svg {...base(p)}><rect x="3" y="5" width="18" height="16" rx="2" /><path d="M3 9h18M8 3v4M16 3v4M7 13h4M13 13h4M7 17h4" /></svg>
);
export const IconPtm = (p: P) => (
  <svg {...base(p)}><path d="M4 5h16a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H8l-4 4V6a1 1 0 0 1 1-1Z" /><path d="M9 10h6M9 13h4" /></svg>
);
export const IconPace = (p: P) => (
  <svg {...base(p)}><path d="M3 12h4l2-6 4 14 2-8h6" /><path d="M19 9l2 3-2 3" /></svg>
);
