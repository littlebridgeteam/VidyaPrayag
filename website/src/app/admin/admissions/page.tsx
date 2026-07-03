"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconAdmissions } from "@/components/admin/icons";

interface InquiryDto {
  id: string;
  student_name: string;
  parent_name: string;
  phone: string;
  grade_applying: string;
  status: string;
  created_at: string;
}

export default function AdmissionsPage() {
  const [inquiries, setInquiries] = useState<InquiryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ data: InquiryDto[] } | InquiryDto[]>("/api/v1/school/admissions/inquiries");
      setInquiries(Array.isArray(res) ? res : (res as { data: InquiryDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load admissions: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const statusTone = (s: string): "success" | "warning" | "danger" | "neutral" => {
    if (s === "enrolled" || s === "admitted") return "success";
    if (s === "pending" || s === "review") return "warning";
    if (s === "rejected" || s === "withdrawn") return "danger";
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
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {inquiries.map((i) => (
                    <tr key={i.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{i.student_name}</td>
                      <td className="px-5 py-3 text-ink-2">{i.parent_name}</td>
                      <td className="px-5 py-3 text-ink-3">{i.phone}</td>
                      <td className="px-5 py-3 text-ink-3">{i.grade_applying}</td>
                      <td className="px-5 py-3"><Badge tone={statusTone(i.status)}>{i.status}</Badge></td>
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
