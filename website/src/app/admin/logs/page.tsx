"use client";

import { useState, useCallback } from "react";
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
          <LoggingToggle />
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <LogViewer />
      </FadeIn>
    </div>
  );
}

function LoggingToggle() {
  const { data, mutate } = useSWR<{ enabled: boolean }>(
    "logging-toggle",
    () => adminApi.serverLogToggleGet(),
    { revalidateOnFocus: false, dedupingInterval: 5000 }
  );
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const enabled = data?.enabled ?? true;

  const toggle = useCallback(async () => {
    if (busy) return;
    setError(null);
    setBusy(true);
    try {
      const newValue = !enabled;
      const res = await adminApi.serverLogToggleSet(newValue);
      // Optimistically update SWR cache with the server response
      await mutate({ enabled: res.enabled }, { revalidate: false });
      // Then revalidate to confirm
      await mutate();
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "Failed to toggle logging");
      // Revalidate to get the true server state
      await mutate();
    } finally {
      setBusy(false);
    }
  }, [busy, enabled, mutate]);

  return (
    <div className="flex flex-col items-end gap-1">
      <button
        type="button"
        onClick={toggle}
        disabled={busy || !data}
        className={`flex items-center gap-2.5 rounded-full px-4 py-2.5 text-[13px] font-semibold transition-colors ${
          enabled
            ? "bg-green-100 text-green-700 hover:bg-green-200"
            : "bg-red-100 text-red-700 hover:bg-red-200"
        } disabled:opacity-60`}
        title={
          !data
            ? "Loading logging status…"
            : enabled
            ? "Logging is ON — click to disable all server logging"
            : "Logging is OFF — click to enable all server logging"
        }
      >
        <span
          className={`h-2 w-2 rounded-full ${
            busy ? "bg-gray-400 animate-pulse" : enabled ? "bg-green-500" : "bg-red-500"
          }`}
        />
        {busy ? "Updating…" : !data ? "Loading…" : enabled ? "Logging: ON" : "Logging: OFF"}
      </button>
      {error && (
        <span className="text-[11px] text-red-600">{error}</span>
      )}
    </div>
  );
}
