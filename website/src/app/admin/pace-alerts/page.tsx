"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconPace } from "@/components/admin/icons";

interface PaceAlertDto {
  id: string;
  class_name: string;
  subject: string;
  teacher_name: string;
  pace_status: string;
  units_behind: number;
  last_updated: string;
}

export default function PaceAlertsPage() {
  const [alerts, setAlerts] = useState<PaceAlertDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ alerts: PaceAlertDto[] } | PaceAlertDto[]>("/api/v1/school/pace/alerts");
      const raw = res as Record<string, unknown>;
      setAlerts((Array.isArray(raw) ? raw : (raw.alerts as PaceAlertDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load pace alerts: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const paceTone = (s: string): "success" | "warning" | "danger" | "neutral" => {
    if (s === "on_track") return "success";
    if (s === "behind" || s === "at_risk") return "warning";
    if (s === "critical" || s === "severely_behind") return "danger";
    return "neutral";
  };

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconPace />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Pace Alerts</h1>
            <p className="text-[13px] text-ink-3">Syllabus pace monitoring — classes falling behind curriculum targets.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Active Alerts" subtitle={`${alerts.length} alert${alerts.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconPace />} /> : alerts.length === 0 ? <EmptyState title="No alerts" hint="All classes are on track." icon={<IconPace />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Class</th>
                    <th className="px-5 py-3 font-semibold">Subject</th>
                    <th className="px-5 py-3 font-semibold">Teacher</th>
                    <th className="px-5 py-3 font-semibold">Units Behind</th>
                    <th className="px-5 py-3 font-semibold">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {alerts.map((a) => (
                    <tr key={a.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{a.class_name}</td>
                      <td className="px-5 py-3 text-ink-2">{a.subject}</td>
                      <td className="px-5 py-3 text-ink-3">{a.teacher_name}</td>
                      <td className="px-5 py-3 text-ink-3">{a.units_behind}</td>
                      <td className="px-5 py-3"><Badge tone={paceTone(a.pace_status)}>{a.pace_status}</Badge></td>
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
