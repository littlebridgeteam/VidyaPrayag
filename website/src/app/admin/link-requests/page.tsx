"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconLink } from "@/components/admin/icons";

interface LinkRequestDto {
  id: string;
  parent_name: string;
  student_name: string;
  relationship: string;
  status: string;
  created_at: string;
}

export default function LinkRequestsPage() {
  const [requests, setRequests] = useState<LinkRequestDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ data: LinkRequestDto[] } | LinkRequestDto[]>("/api/v1/school/link-requests");
      setRequests(Array.isArray(res) ? res : (res as { data: LinkRequestDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load link requests: ${(e as Error).message}`);
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
          <CardHeader title="Pending Link Requests" subtitle={`${requests.length} request${requests.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconLink />} /> : requests.length === 0 ? <EmptyState title="No pending requests" hint="Parent-child link requests will appear here." icon={<IconLink />} /> : (
            <div className="divide-y divide-navy/[0.04]">
              {requests.map((r) => (
                <div key={r.id} className="flex items-center justify-between px-5 py-3">
                  <div>
                    <p className="text-[14px] font-semibold text-navy-deep">{r.parent_name} → {r.student_name}</p>
                    <p className="text-[12px] text-ink-3">{r.relationship} · {new Date(r.created_at).toLocaleDateString()}</p>
                  </div>
                  <Badge tone={r.status === "pending" ? "warning" : r.status === "approved" ? "success" : "danger"}>{r.status}</Badge>
                </div>
              ))}
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
