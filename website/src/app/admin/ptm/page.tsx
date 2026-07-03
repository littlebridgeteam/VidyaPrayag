"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconPtm } from "@/components/admin/icons";

interface PtmEventDto {
  id: string;
  title: string;
  date: string;
  total_slots: number;
  booked_slots: number;
  status: string;
}

export default function PtmPage() {
  const [events, setEvents] = useState<PtmEventDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ events: PtmEventDto[] } | PtmEventDto[]>("/api/v1/school/events/admin?type=PTM");
      const raw = res as Record<string, unknown>;
      setEvents((Array.isArray(raw) ? raw : (raw.events as PtmEventDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load PTM events: ${(e as Error).message}`);
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
            <IconPtm />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">PTM Scheduling</h1>
            <p className="text-[13px] text-ink-3">Parent-Teacher Meeting events with slot booking.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="PTM Events" subtitle={`${events.length} event${events.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconPtm />} /> : events.length === 0 ? <EmptyState title="No PTM events" hint="PTM events will appear here." icon={<IconPtm />} /> : (
            <div className="divide-y divide-navy/[0.04]">
              {events.map((e) => (
                <div key={e.id} className="flex items-center justify-between px-5 py-3">
                  <div>
                    <p className="text-[14px] font-semibold text-navy-deep">{e.title}</p>
                    <p className="text-[12px] text-ink-3">{new Date(e.date).toLocaleDateString()} · {e.booked_slots}/{e.total_slots} slots booked</p>
                  </div>
                  <Badge tone={e.status === "active" ? "success" : "neutral"}>{e.status}</Badge>
                </div>
              ))}
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
