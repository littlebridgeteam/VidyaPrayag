"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge, Avatar } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconLink, IconCheck, IconClose } from "@/components/admin/icons";

interface LinkRequestDto {
  id: string;
  parent_id: string;
  parent_name: string | null;
  student_name: string | null;
  student_code: string | null;
  roll_number: string | null;
  class_name: string | null;
  relationship: string | null;
  status: string;
  created_at: string;
}

const STATUS_FILTERS = [
  { value: "pending", label: "Pending" },
  { value: "approved", label: "Approved" },
  { value: "rejected", label: "Rejected" },
  { value: "needs_review", label: "Needs Review" },
];

function statusTone(s: string): "success" | "warning" | "danger" | "neutral" {
  if (s === "approved") return "success";
  if (s === "rejected") return "danger";
  if (s === "pending") return "warning";
  if (s === "needs_review") return "warning";
  return "neutral";
}

export default function LinkRequestsPage() {
  const [requests, setRequests] = useState<LinkRequestDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState("pending");
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await authRequest<{ requests: LinkRequestDto[] } | LinkRequestDto[]>(
        `/api/v1/school/link-requests?status=${statusFilter}`
      );
      const raw = res as Record<string, unknown>;
      setRequests((Array.isArray(raw) ? raw : (raw.requests as LinkRequestDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load link requests: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const approve = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/link-requests/${id}/approve`, { method: "POST" });
      setRequests(prev => prev.filter(r => r.id !== id));
    } catch (e) {
      setError(`Failed to approve: ${(e as Error).message}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  const reject = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/link-requests/${id}/reject`, { method: "POST" });
      setRequests(prev => prev.filter(r => r.id !== id));
    } catch (e) {
      setError(`Failed to reject: ${(e as Error).message}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconLink />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Link Requests</h1>
            <p className="text-[13px] text-ink-3">Parent-child link approval queue.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <div className="border-b border-navy/8 p-4">
            <div className="flex items-center gap-1.5">
              {STATUS_FILTERS.map(f => (
                <button key={f.value} onClick={() => setStatusFilter(f.value)} className={`shrink-0 rounded-full px-3 py-1.5 text-[12.5px] font-semibold transition-colors ${statusFilter === f.value ? "bg-navy-deep text-white" : "bg-navy/6 text-ink-2 hover:bg-navy/10"}`}>
                  {f.label}
                </button>
              ))}
            </div>
          </div>
          <CardHeader title={`${statusFilter.replace("_", " ").replace(/\b\w/g, c => c.toUpperCase())} Requests`} subtitle={`${requests.length} request${requests.length !== 1 ? "s" : ""}`} />
          {error && <p className="px-5 pt-3 text-[13px] font-medium text-danger">{error}</p>}
          {loading ? <div className="space-y-2 p-4">{Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-16" />)}</div>
          : requests.length === 0 ? <EmptyState title={`No ${statusFilter} requests`} hint="Parent-child link requests will appear here." icon={<IconLink />} />
          : <div className="divide-y divide-navy/[0.04]">
              {requests.map((r) => (
                <div key={r.id} className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex items-start gap-3">
                    <Avatar name={r.parent_name ?? "?"} size={36} />
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <p className="font-semibold text-navy-deep">{r.parent_name ?? "Unknown parent"}</p>
                        <Badge tone="neutral">{r.relationship ?? "guardian"}</Badge>
                        <Badge tone={statusTone(r.status)}>{r.status}</Badge>
                      </div>
                      <p className="mt-0.5 text-[13px] text-ink-2">
                        {r.student_name ? `→ ${r.student_name}` : `→ Roll ${r.roll_number ?? r.student_code ?? "?"}`}
                        {r.class_name ? ` · ${r.class_name}` : ""}
                      </p>
                      <p className="mt-0.5 text-[12px] text-ink-3">{new Date(r.created_at).toLocaleDateString()}</p>
                    </div>
                  </div>
                  {(r.status === "pending" || r.status === "needs_review") && (
                    <div className="flex shrink-0 gap-2">
                      <AdminButton variant="ghost" onClick={() => reject(r.id)} disabled={busyId === r.id}>
                        <IconClose width={15} height={15} /> Reject
                      </AdminButton>
                      <AdminButton onClick={() => approve(r.id)} disabled={busyId === r.id}>
                        <IconCheck width={15} height={15} /> Approve
                      </AdminButton>
                    </div>
                  )}
                </div>
              ))}
            </div>}
        </Card>
      </FadeIn>
    </div>
  );
}
