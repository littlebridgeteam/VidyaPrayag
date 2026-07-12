"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconAdmissions } from "@/components/admin/icons";

interface InquiryDto {
  id: string;
  student_name: string;
  parent_name: string;
  class: string;
  date: string;
  status: string;
  profile_pic: string | null;
}

export default function AdmissionsPage() {
  const [inquiries, setInquiries] = useState<InquiryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ enquiries: InquiryDto[] }>("/api/v1/admissions/enquiries?limit=100");
      setInquiries(res.enquiries ?? []);
    } catch (e) {
      setError(`Failed to load admissions: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const updateStatus = useCallback(async (id: string, status: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/admissions/enquiries/${id}/status`, { method: "PATCH", body: { status } });
      setInquiries(prev => prev.map(i => i.id === id ? { ...i, status } : i));
    } catch (e) {
      setError(`Failed to update status: ${errorMessage(e)}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  const statusTone = (s: string): "success" | "warning" | "danger" | "neutral" => {
    if (s === "converted") return "success";
    if (s === "new" || s === "followup") return "warning";
    if (s === "rejected") return "danger";
    return "neutral";
  };

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconAdmissions />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Admissions CRM</h1>
            <p className="text-[13px] text-ink-3">Track and manage admission inquiries and applications.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Inquiries" subtitle={`${inquiries.length} inquir${inquiries.length !== 1 ? "ies" : "y"}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconAdmissions />} /> : inquiries.length === 0 ? <EmptyState title="No inquiries" hint="Admission inquiries will appear here." icon={<IconAdmissions />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Student</th>
                    <th className="px-5 py-3 font-semibold">Parent</th>
                    <th className="px-5 py-3 font-semibold">Phone</th>
                    <th className="px-5 py-3 font-semibold">Grade</th>
                    <th className="px-5 py-3 font-semibold">Status</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {inquiries.map((i) => (
                    <tr key={i.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{i.student_name}</td>
                      <td className="px-5 py-3 text-ink-2">{i.parent_name}</td>
                      <td className="px-5 py-3 text-ink-3">{i.date}</td>
                      <td className="px-5 py-3 text-ink-3">{i.class}</td>
                      <td className="px-5 py-3"><Badge tone={statusTone(i.status)}>{i.status}</Badge></td>
                      <td className="px-5 py-3">
                        <div className="flex items-center gap-1.5">
                          {i.status !== "followup" && <AdminButton variant="ghost" onClick={() => updateStatus(i.id, "followup")} disabled={busyId === i.id}>Follow-up</AdminButton>}
                          {i.status !== "converted" && <AdminButton onClick={() => updateStatus(i.id, "converted")} disabled={busyId === i.id}>Convert</AdminButton>}
                          {i.status !== "rejected" && <AdminButton variant="danger" onClick={() => updateStatus(i.id, "rejected")} disabled={busyId === i.id}>Reject</AdminButton>}
                        </div>
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
