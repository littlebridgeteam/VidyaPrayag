"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconEvent } from "@/components/admin/icons";

interface EventDto {
  id: string;
  title: string;
  type: string;
  status: string;
  start_date: string;
  end_date: string;
  audience: string;
  description: string;
}

export default function EventsPage() {
  const [events, setEvents] = useState<EventDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ events: EventDto[]; total: number }>("/api/admin/calendar/events");
      setEvents(res.events ?? []);
    } catch (e) {
      setError(`Failed to load events: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconEvent />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Event Registration</h1>
            <p className="text-[13px] text-ink-3">School events with parent registration tracking.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Upcoming Events" subtitle={`${events.length} event${events.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconEvent />} /> : events.length === 0 ? <EmptyState title="No events" hint="Events with registration will appear here." icon={<IconEvent />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Title</th>
                    <th className="px-5 py-3 font-semibold">Type</th>
                    <th className="px-5 py-3 font-semibold">Date</th>
                    <th className="px-5 py-3 font-semibold">Audience</th>
                    <th className="px-5 py-3 font-semibold">Status</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {events.map((e) => (
                    <tr key={e.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{e.title}</td>
                      <td className="px-5 py-3 text-ink-2">{e.type}</td>
                      <td className="px-5 py-3 text-ink-3">{new Date(e.start_date).toLocaleDateString()}</td>
                      <td className="px-5 py-3 text-ink-3">{e.audience}</td>
                      <td className="px-5 py-3"><Badge tone={e.status === "PUBLISHED" ? "success" : "neutral"}>{e.status}</Badge></td>
                      <td className="px-5 py-3">
                        {e.status !== "CANCELLED" && (
                          <AdminButton variant="danger" onClick={async () => { await authRequest(`/api/admin/calendar/events/${e.id}`, { method: "PUT", body: { status: "CANCELLED" } }); setEvents(prev => prev.map(x => x.id === e.id ? { ...x, status: "CANCELLED" } : x)); }}>Cancel</AdminButton>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
