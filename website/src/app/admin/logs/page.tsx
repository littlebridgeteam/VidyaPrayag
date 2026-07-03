"use client";

import { useState, useEffect } from "react";
import useSWR from "swr";
import { useAdminAuth } from "@/lib/admin/session";
import { adminApi } from "@/lib/admin/client";
import { Card, EmptyState, FadeIn } from "@/components/admin/Primitives";
import { IconWarning, IconLogs } from "@/components/admin/icons";
import { LogViewer } from "@/components/admin/LogViewer";

export default function ServerLogsPage() {
  const { session } = useAdminAuth();
  const isSuperAdmin = session?.role === "super_admin";

  if (!isSuperAdmin) {
    return (
      <Card>
        <EmptyState
          title="Access Denied"
          hint="Server Logs are only available to super admin accounts."
          icon={<IconWarning />}
        />
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
              <IconLogs />
            </div>
            <div>
              <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Server Logs</h1>
              <p className="text-[13px] text-ink-3">
                Structured server-side log viewer with live SSE streaming — super admin only.
              </p>
            </div>
          </div>
          <HttpLoggingToggle />
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <LogViewer />
      </FadeIn>
    </div>
  );
}

function HttpLoggingToggle() {
  const { data, mutate } = useSWR("http-logging-toggle", () => adminApi.serverLogHttpToggleGet());
  const [busy, setBusy] = useState(false);

  async function toggle() {
    const current = data?.enabled ?? true;
    setBusy(true);
    try {
      await adminApi.serverLogHttpToggleSet(!current);
      await mutate();
    } catch {
      // non-fatal
    } finally {
      setBusy(false);
    }
  }

  const enabled = data?.enabled ?? true;

  return (
    <button
      type="button"
      onClick={toggle}
      disabled={busy}
      className={`flex items-center gap-2.5 rounded-full px-4 py-2.5 text-[13px] font-semibold transition-colors ${
        enabled
          ? "bg-green-100 text-green-700 hover:bg-green-200"
          : "bg-red-100 text-red-700 hover:bg-red-200"
      } disabled:opacity-60`}
      title={enabled ? "HTTP request logging is ON — click to disable" : "HTTP request logging is OFF — click to enable"}
    >
      <span className={`h-2 w-2 rounded-full ${enabled ? "bg-green-500" : "bg-red-500"}`} />
      {busy ? "Updating…" : enabled ? "HTTP Logging: ON" : "HTTP Logging: OFF"}
    </button>
  );
}

