"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconCalendarPlatform } from "@/components/admin/icons";

interface CalendarEventDto {
  id: string;
  title: string;
  type: string;
  startDate: string;
  endDate: string;
  status: string;
  venue: string | null;
}

export default function CalendarPage() {
  const [events, setEvents] = useState<CalendarEventDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ events: CalendarEventDto[] } | CalendarEventDto[]>("/api/v1/school/calendar?view_type=month");
      const raw = res as Record<string, unknown>;
      setEvents((Array.isArray(raw) ? raw : (raw.events as CalendarEventDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load calendar: ${(e as Error).message}`);
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
            <IconCalendarPlatform />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Academic Calendar</h1>
            <p className="text-[13px] text-ink-3">School-wide events, holidays, exams, and PTM scheduling.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Events" subtitle={`${events.length} event${events.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconCalendarPlatform />} /> : events.length === 0 ? <EmptyState title="No events" hint="Calendar events will appear here." icon={<IconCalendarPlatform />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Title</th>
                    <th className="px-5 py-3 font-semibold">Type</th>
                    <th className="px-5 py-3 font-semibold">Start</th>
                    <th className="px-5 py-3 font-semibold">End</th>
                    <th className="px-5 py-3 font-semibold">Venue</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {events.map((e) => (
                    <tr key={e.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{e.title}</td>
                      <td className="px-5 py-3"><Badge tone="neutral">{e.type}</Badge></td>
                      <td className="px-5 py-3 text-ink-3">{new Date(e.startDate).toLocaleDateString()}</td>
                      <td className="px-5 py-3 text-ink-3">{new Date(e.endDate).toLocaleDateString()}</td>
                      <td className="px-5 py-3 text-ink-3">{e.venue ?? "—"}</td>
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
