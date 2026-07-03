"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconPace, IconCheck } from "@/components/admin/icons";

interface PaceAlertDto {
  id: string;
  className?: string;
  subjectName?: string;
  teacherName?: string;
  alertLevel?: string;
  expectedCoveragePct?: number;
  actualCoveragePct?: number;
  unitsBehind?: number;
  createdAt?: string;
  resolvedAt?: string | null;
}

export default function PaceAlertsPage() {
  const [alerts, setAlerts] = useState<PaceAlertDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [resolvingId, setResolvingId] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ alerts: PaceAlertDto[] } | PaceAlertDto[]>("/api/v1/school/syllabus-pace/alerts");
      const raw = res as Record<string, unknown>;
      setAlerts((Array.isArray(raw) ? raw : (raw.alerts as PaceAlertDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load pace alerts: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const resolveAlert = useCallback(async (id: string) => {
    setResolvingId(id);
    try {
      await authRequest(`/api/v1/school/syllabus-pace/alerts/${id}/resolve`, { method: "POST" });
      setAlerts(prev => prev.filter(a => a.id !== id));
    } catch (e) {
      setError(`Failed to resolve alert: ${(e as Error).message}`);
    } finally {
      setResolvingId(null);
    }
  }, []);

  const alertTone = (s?: string): "success" | "warning" | "danger" | "neutral" => {
    if (!s) return "neutral";
    const v = s.toLowerCase();
    if (v === "ahead" || v === "on_track") return "success";
    if (v === "behind") return "warning";
    if (v === "critical") return "danger";
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
                    <th className="px-5 py-3 font-semibold">Coverage</th>
                    <th className="px-5 py-3 font-semibold">Level</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {alerts.map((a) => (
                    <tr key={a.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{a.className ?? "-"}</td>
                      <td className="px-5 py-3 text-ink-2">{a.subjectName ?? "-"}</td>
                      <td className="px-5 py-3 text-ink-3">{a.teacherName ?? "-"}</td>
                      <td className="px-5 py-3 text-ink-3">{a.actualCoveragePct ?? 0}% / {a.expectedCoveragePct ?? 0}%</td>
                      <td className="px-5 py-3"><Badge tone={alertTone(a.alertLevel)}>{a.alertLevel ?? "unknown"}</Badge></td>
                      <td className="px-5 py-3">
                        <AdminButton variant="ghost" onClick={() => resolveAlert(a.id)} disabled={resolvingId === a.id}>
                          <IconCheck width={14} height={14} /> Resolve
                        </AdminButton>
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
