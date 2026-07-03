"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconHealth } from "@/components/admin/icons";

interface HealthRecordDto {
  id: string;
  student_name: string;
  blood_group: string;
  allergies: string;
  conditions: string;
  last_checkup: string;
}

export default function HealthRecordsPage() {
  const [records, setRecords] = useState<HealthRecordDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ data: HealthRecordDto[] } | HealthRecordDto[]>("/api/v1/school/health-records");
      setRecords(Array.isArray(res) ? res : (res as { data: HealthRecordDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load health records: ${(e as Error).message}`);
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
          <CardHeader title="Health Records" subtitle={`${records.length} record${records.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconHealth />} /> : records.length === 0 ? <EmptyState title="No records" hint="Health records will appear here." icon={<IconHealth />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Student</th>
                    <th className="px-5 py-3 font-semibold">Blood Group</th>
                    <th className="px-5 py-3 font-semibold">Allergies</th>
                    <th className="px-5 py-3 font-semibold">Conditions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {records.map((r) => (
                    <tr key={r.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{r.student_name}</td>
                      <td className="px-5 py-3"><Badge tone="neutral">{r.blood_group || "—"}</Badge></td>
                      <td className="px-5 py-3 text-ink-2">{r.allergies || "None"}</td>
                      <td className="px-5 py-3 text-ink-3">{r.conditions || "None"}</td>
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
