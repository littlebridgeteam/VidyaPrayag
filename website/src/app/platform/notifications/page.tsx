"use client";

import { useEffect, useState } from "react";
import { platformApi, type PlatformNotificationDto } from "@/lib/admin/platform-client";
import { Card, LoadingState, ErrorState, EmptyState, Button } from "@/components/admin/platform/PlatformUI";

export default function NotificationsPage() {
  const [items, setItems] = useState<PlatformNotificationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = () => {
    setLoading(true);
    platformApi.listNotifications()
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const markAllRead = async () => {
    await platformApi.markAllNotificationsRead();
    load();
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">Notifications</h2>
        <Button variant="secondary" onClick={markAllRead}>Mark all read</Button>
      </div>

      {loading && <LoadingState message="Loading notifications…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && (
        <>
          {items.length === 0 ? <EmptyState title="No notifications" /> : (
            <div className="space-y-2">
              {items.map((n) => (
                <Card key={n.id} className={!n.is_read ? "border-indigo-800/50" : ""}>
                  <div className="flex items-start justify-between">
                    <div>
                      <div className="flex items-center gap-2">
                        {!n.is_read && <span className="h-2 w-2 rounded-full bg-indigo-500" />}
                        <span className="text-sm font-medium text-slate-300">{n.title}</span>
                      </div>
                      {n.body && <p className="mt-1 text-xs text-slate-500">{n.body}</p>}
                    </div>
                    <span className="text-xs text-slate-600">{new Date(n.created_at).toLocaleString()}</span>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}
