"use client";

import { useAdminAuth } from "@/lib/admin/session";
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
      </FadeIn>

      <FadeIn delay={0.05}>
        <LogViewer />
      </FadeIn>
    </div>
  );
}
