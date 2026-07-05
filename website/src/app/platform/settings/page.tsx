"use client";

import { Card, EmptyState } from "@/components/admin/platform/PlatformUI";

export default function SettingsPage() {
  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-200">Platform Settings</h2>
      <Card>
        <EmptyState
          title="Settings coming soon"
          message="Platform-level configuration, role management, and feature flags will appear here."
        />
      </Card>
    </div>
  );
}
