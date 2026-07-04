"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconScholarship, IconCheck, IconClose } from "@/components/admin/icons";

interface SchemeDto {
  id: string;
  title: string;
  scholarshipType: string;
  isActive: boolean;
  eligibilityCriteria: string;
  waiverPercentage: number | null;
  numericAmount: number | null;
  amount: string;
  category: string;
}
interface ApplicationDto {
  id: string;
  studentName: string | null;
  scholarshipTitle: string | null;
  status: string;
  parentApplicationText: string | null;
  remarks: string | null;
}

export default function ScholarshipsPage() {
  const [schemes, setSchemes] = useState<SchemeDto[]>([]);
  const [applications, setApplications] = useState<ApplicationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const [s, a] = await Promise.all([
        authRequest<SchemeDto[]>("/api/v1/school/scholarships?all=true"),
        authRequest<ApplicationDto[]>("/api/v1/school/scholarship-applications"),
      ]);
      setSchemes(Array.isArray(s) ? s : []);
      setApplications(Array.isArray(a) ? a : []);
    } catch (e) {
      setError(`Failed to load scholarships: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const approveApp = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/scholarship-applications/${id}/approve`, { method: "POST", body: {} });
      setApplications(prev => prev.map(a => a.id === id ? { ...a, status: "APPROVED" } : a));
    } catch (e) {
      setError(`Failed to approve: ${errorMessage(e)}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  const rejectApp = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/scholarship-applications/${id}/reject`, { method: "POST", body: {} });
      setApplications(prev => prev.map(a => a.id === id ? { ...a, status: "REJECTED" } : a));
    } catch (e) {
      setError(`Failed to reject: ${errorMessage(e)}`);
    } finally {
      setBusyId(null);
    }
  }, []);

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
                      <p className="text-[12px] text-ink-3">{s.scholarshipType} · {s.category}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge tone={s.isActive ? "success" : "neutral"}>{s.isActive ? "Active" : "Inactive"}</Badge>
                      <AdminButton variant="danger" onClick={async () => { await authRequest(`/api/v1/school/scholarships/${s.id}`, { method: "DELETE" }); setSchemes(prev => prev.filter(x => x.id !== s.id)); }}>Delete</AdminButton>
                    </div>
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
                  <div key={a.id} className="flex flex-col gap-3 px-5 py-3 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                      <p className="text-[14px] font-semibold text-navy-deep">{a.studentName ?? "Unknown"}</p>
                      <p className="text-[12px] text-ink-3">{a.scholarshipTitle ?? "—"}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge tone={a.status === "APPROVED" || a.status === "DISBURSED" ? "success" : a.status === "REJECTED" ? "danger" : "warning"}>{a.status}</Badge>
                      {a.status === "PENDING" && (
                        <>
                          <AdminButton variant="ghost" onClick={() => rejectApp(a.id)} disabled={busyId === a.id}><IconClose width={14} height={14} /> Reject</AdminButton>
                          <AdminButton onClick={() => approveApp(a.id)} disabled={busyId === a.id}><IconCheck width={14} height={14} /> Approve</AdminButton>
                        </>
                      )}
                    </div>
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
