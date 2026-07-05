"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { adminApi } from "@/lib/admin/client";
import type { SchoolClassDto } from "@/lib/admin/types";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton } from "@/components/admin/Primitives";
import { IconClasses } from "@/components/admin/icons";

export default function ClassesPage() {
  const [data, setData] = useState<SchoolClassDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await adminApi.schoolClasses();
      setData(res.classes ?? []);
    } catch (e) {
      setError(`Failed to load classes: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const classes = data;

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconClasses />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Classes & Subjects</h1>
            <p className="text-[13px] text-ink-3">Manage school classes, sections, and subject assignments.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Classes" subtitle={`${classes.length} class${classes.length !== 1 ? "es" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconClasses />} /> : classes.length === 0 ? <EmptyState title="No classes" hint="Classes will appear here once created." icon={<IconClasses />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Name</th>
                    <th className="px-5 py-3 font-semibold">Section</th>
                    <th className="px-5 py-3 font-semibold">Subjects</th>
                    <th className="px-5 py-3 font-semibold">Students</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {classes.map((c, i) => {
                    return (
                      <tr key={c.id ?? i} className="hover:bg-navy/[0.02] transition-colors">
                        <td className="px-5 py-3 font-semibold text-navy-deep">{c.name ?? "—"}</td>
                        <td className="px-5 py-3 text-ink-2">{Array.isArray(c.sections) ? c.sections.join(", ") : "—"}</td>
                        <td className="px-5 py-3 text-ink-3">{c.subject_count ?? "—"}</td>
                        <td className="px-5 py-3 text-ink-3">—</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
