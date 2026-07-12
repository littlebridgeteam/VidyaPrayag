"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAdminAuth } from "@/lib/admin/session";
import { PLATFORM_NAV, PLATFORM_ICON_PATHS } from "@/lib/admin/platform-nav";
import { platformApi, type NotificationSummaryDto } from "@/lib/admin/platform-client";

const PLATFORM_ROLES = new Set(["super_admin", "qa"]);

function PlatformIcon({ name, className }: { name: string; className?: string }) {
  const path = PLATFORM_ICON_PATHS[name] ?? "";
  return (
    <svg viewBox="0 0 24 24" className={className} fill="currentColor" aria-hidden="true">
      <path d={path} />
    </svg>
  );
}

export function PlatformShell({ children }: { children: React.ReactNode }) {
  const { session, ready } = useAdminAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [unreadCount, setUnreadCount] = useState(0);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  useEffect(() => {
    if (!ready) return;
    if (!session) {
      router.replace(`/login?returnTo=${encodeURIComponent(pathname)}`);
      return;
    }
    if (!PLATFORM_ROLES.has(session.role)) {
      router.replace("/admin/dashboard");
      return;
    }
  }, [ready, session, router, pathname]);

  useEffect(() => {
    if (session && PLATFORM_ROLES.has(session.role)) {
      platformApi.notificationSummary()
        .then((s: NotificationSummaryDto) => setUnreadCount(s.unread_count))
        .catch(() => {});
    }
  }, [session, pathname]);

  if (!ready || !session || !PLATFORM_ROLES.has(session.role)) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[#0f172a]">
        <div className="flex flex-col items-center gap-3">
          <div className="h-10 w-10 animate-pulse rounded-lg bg-indigo-500/30" />
          <p className="text-sm font-medium text-slate-400">Loading platform…</p>
        </div>
      </div>
    );
  }

  const isSuperAdmin = session.role === "super_admin";
  const navItems = PLATFORM_NAV.filter((item) => !item.superAdminOnly || isSuperAdmin);

  const activeItem = navItems.find(
    (i) => pathname === i.href || (i.href !== "/admin/platform" && pathname.startsWith(i.href))
  );
  const title = activeItem?.label ?? "Dashboard";

  return (
    <div className="flex min-h-screen bg-[#0f172a] text-slate-200">
      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-40 flex flex-col border-r border-slate-800 bg-[#1e293b] transition-all duration-300 ${
          sidebarCollapsed ? "w-[68px]" : "w-[240px]"
        }`}
      >
        <div className="flex h-14 items-center gap-2 border-b border-slate-800 px-4">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-indigo-600 text-sm font-bold text-white">
            VP
          </div>
          {!sidebarCollapsed && (
            <span className="text-sm font-semibold text-slate-200">Platform</span>
          )}
        </div>

        <nav className="flex-1 overflow-y-auto py-2">
          {navItems.map((item) => {
            const isActive =
              pathname === item.href ||
              (item.href !== "/admin/platform" && pathname.startsWith(item.href));
            return (
              <a
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 px-4 py-2.5 text-sm transition-colors ${
                  isActive
                    ? "bg-indigo-600/20 text-indigo-400 border-r-2 border-indigo-500"
                    : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
                }`}
                title={item.label}
              >
                <PlatformIcon name={item.icon} className="h-5 w-5 shrink-0" />
                {!sidebarCollapsed && <span>{item.label}</span>}
              </a>
            );
          })}
        </nav>

        <button
          onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
          className="flex items-center gap-2 border-t border-slate-800 px-4 py-3 text-xs text-slate-500 hover:text-slate-300"
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="currentColor">
            <path d={sidebarCollapsed ? "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z" : "M14 7l-5 5 5 5V7z"} />
          </svg>
          {!sidebarCollapsed && <span>Collapse</span>}
        </button>
      </aside>

      {/* Main content */}
      <div
        className={`flex flex-1 flex-col transition-all duration-300 ${
          sidebarCollapsed ? "ml-[68px]" : "ml-[240px]"
        }`}
      >
        {/* Topbar */}
        <header className="sticky top-0 z-30 flex h-14 items-center justify-between border-b border-slate-800 bg-[#1e293b]/95 px-6 backdrop-blur">
          <h1 className="text-base font-semibold text-slate-200">{title}</h1>
          <div className="flex items-center gap-4">
            <a
              href="/admin/platform/notifications"
              className="relative flex h-9 w-9 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-200"
              title="Notifications"
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5" fill="currentColor">
                <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
              </svg>
              {unreadCount > 0 && (
                <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-red-500 px-1 text-[10px] font-bold text-white">
                  {unreadCount > 99 ? "99+" : unreadCount}
                </span>
              )}
            </a>
            <a
              href="/admin/dashboard"
              className="flex items-center gap-2 text-xs text-slate-500 hover:text-slate-300"
              title="Back to school admin"
            >
              <svg viewBox="0 0 24 24" className="h-4 w-4" fill="currentColor">
                <path d="M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z" />
              </svg>
              <span>Back to Admin</span>
            </a>
            <div className="flex items-center gap-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-indigo-600 text-xs font-semibold text-white">
                {session.name.charAt(0).toUpperCase()}
              </div>
              <div className="hidden flex-col sm:flex">
                <span className="text-xs font-medium text-slate-300">{session.name}</span>
                <span className="text-[10px] text-slate-500">{session.role}</span>
              </div>
            </div>
          </div>
        </header>

        {/* Content area */}
        <main className="flex-1 overflow-y-auto p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
