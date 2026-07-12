"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconHealth } from "@/components/admin/icons";

interface HealthIncidentDto {
  id: string;
  student_id: string;
  date: string;
  time: string | null;
  description: string;
  treatment: string | null;
  medication_given: string | null;
  parent_notified: boolean;
  severity: string;
  attended_by_name: string | null;
}

export default function HealthRecordsPage() {
  const [records, setRecords] = useState<HealthIncidentDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ incidents: HealthIncidentDto[] }>("/api/v1/school/health/incidents");
      setRecords(res.incidents ?? []);
    } catch (e) {
      setError(`Failed to load health records: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const notifyParent = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/health/incidents/${id}/notify`, { method: "PATCH" });
      setRecords(prev => prev.map(r => r.id === id ? { ...r, parent_notified: true } : r));
    } catch (e) {
      setError(`Failed to mark notified: ${errorMessage(e)}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconHealth />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Health Records</h1>
            <p className="text-[13px] text-ink-3">Student health profiles, allergies, and medical conditions.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Health Incidents" subtitle={`${records.length} incident${records.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconHealth />} /> : records.length === 0 ? <EmptyState title="No incidents" hint="Health incidents will appear here." icon={<IconHealth />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Date</th>
                    <th className="px-5 py-3 font-semibold">Description</th>
                    <th className="px-5 py-3 font-semibold">Severity</th>
                    <th className="px-5 py-3 font-semibold">Treatment</th>
                    <th className="px-5 py-3 font-semibold">Notified</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {records.map((r) => (
                    <tr key={r.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 text-ink-3">{r.date}{r.time ? ` ${r.time}` : ""}</td>
                      <td className="px-5 py-3 font-semibold text-navy-deep">{r.description}</td>
                      <td className="px-5 py-3"><Badge tone={r.severity === "major" ? "danger" : r.severity === "moderate" ? "warning" : "neutral"}>{r.severity}</Badge></td>
                      <td className="px-5 py-3 text-ink-2">{r.treatment || "—"}</td>
                      <td className="px-5 py-3"><Badge tone={r.parent_notified ? "success" : "neutral"}>{r.parent_notified ? "Yes" : "No"}</Badge></td>
                      <td className="px-5 py-3">
                        {!r.parent_notified && <AdminButton onClick={() => notifyParent(r.id)} disabled={busyId === r.id}>Mark Notified</AdminButton>}
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
