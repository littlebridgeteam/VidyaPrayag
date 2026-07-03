"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconScholarship } from "@/components/admin/icons";

interface SchemeDto {
  id: string;
  title: string;
  scholarship_type: string;
  status: string;
  eligibility_criteria: string;
  waiver_percentage: number;
  total_amount: number;
  application_count: number;
}
interface ApplicationDto {
  id: string;
  student_name: string;
  scheme_title: string;
  status: string;
  applied_at: string;
}

export default function ScholarshipsPage() {
  const [schemes, setSchemes] = useState<SchemeDto[]>([]);
  const [applications, setApplications] = useState<ApplicationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const [s, a] = await Promise.all([
        authRequest<{ data: SchemeDto[] } | SchemeDto[]>("/api/v1/school/scholarships?all=true"),
        authRequest<{ data: ApplicationDto[] } | ApplicationDto[]>("/api/v1/school/scholarship-applications"),
      ]);
      setSchemes(Array.isArray(s) ? s : (s as { data: SchemeDto[] }).data ?? []);
      setApplications(Array.isArray(a) ? a : (a as { data: ApplicationDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load scholarships: ${(e as Error).message}`);
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
            <IconScholarship />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Scholarships</h1>
            <p className="text-[13px] text-ink-3">Scholarship schemes, applications, and renewals.</p>
          </div>
        </div>
      </FadeIn>

      <div className="grid gap-6 lg:grid-cols-2">
        <FadeIn delay={0.05}>
          <Card>
            <CardHeader title="Schemes" subtitle={`${schemes.length} scheme${schemes.length !== 1 ? "s" : ""}`} />
            {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconScholarship />} /> : schemes.length === 0 ? <EmptyState title="No schemes" hint="Scholarship schemes will appear here." icon={<IconScholarship />} /> : (
              <div className="divide-y divide-navy/[0.04]">
                {schemes.map((s) => (
                  <div key={s.id} className="flex items-center justify-between px-5 py-3">
                    <div>
                      <p className="text-[14px] font-semibold text-navy-deep">{s.title}</p>
                      <p className="text-[12px] text-ink-3">{s.scholarship_type} · {s.application_count} applications</p>
                    </div>
                    <Badge tone={s.status === "active" ? "success" : "neutral"}>{s.status}</Badge>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>

        <FadeIn delay={0.1}>
          <Card>
            <CardHeader title="Applications" subtitle={`${applications.length} application${applications.length !== 1 ? "s" : ""}`} />
            {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconScholarship />} /> : applications.length === 0 ? <EmptyState title="No applications" hint="Scholarship applications will appear here." icon={<IconScholarship />} /> : (
              <div className="divide-y divide-navy/[0.04]">
                {applications.map((a) => (
                  <div key={a.id} className="flex items-center justify-between px-5 py-3">
                    <div>
                      <p className="text-[14px] font-semibold text-navy-deep">{a.student_name}</p>
                      <p className="text-[12px] text-ink-3">{a.scheme_title}</p>
                    </div>
                    <Badge tone={a.status === "approved" ? "success" : a.status === "rejected" ? "danger" : "warning"}>{a.status}</Badge>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>
      </div>
    </div>
  );
}
