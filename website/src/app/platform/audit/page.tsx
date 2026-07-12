"use client";

import { useEffect, useState } from "react";
import { API_BASE_URL } from "@/lib/api";
import { readSession } from "@/lib/admin/session";
import type { AuditListResponse } from "@/lib/admin/platform-client";
import { Card, LoadingState, ErrorState, EmptyState, Button, Input, Select } from "@/components/admin/platform/PlatformUI";

export default function AuditPage() {
  const [data, setData] = useState<AuditListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [entityType, setEntityType] = useState("");
  const [action, setAction] = useState("");

  useEffect(() => {
    setLoading(true);
    const qs = new URLSearchParams({ page: String(page), page_size: "25" });
    if (entityType) qs.set("entity_type", entityType);
    if (action) qs.set("action", action);
    fetch(`${API_BASE_URL}/api/admin/platform/audit?${qs}`, {
      headers: { Authorization: `Bearer ${readSession()?.token ?? ""}` },
    })
      .then((r) => r.json())
      .then((j) => { if (j.success) setData(j.data); else setError(j.message); })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [page, entityType, action]);

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-200">Audit Log</h2>

      <Card className="flex flex-wrap gap-3">
        <Select value={entityType} onChange={setEntityType} options={[
          { value: "", label: "All Entity Types" },
          { value: "feature", label: "Feature" },
          { value: "screen", label: "Screen" },
          { value: "test_case", label: "Test Case" },
          { value: "bug", label: "Bug" },
          { value: "flow", label: "Flow" },
          { value: "api_mapping", label: "API Mapping" },
        ]} />
        <Select value={action} onChange={setAction} options={[
          { value: "", label: "All Actions" },
          { value: "feature.created", label: "Feature Created" },
          { value: "feature.updated", label: "Feature Updated" },
          { value: "feature.archived", label: "Feature Archived" },
          { value: "bug.created", label: "Bug Created" },
          { value: "bug.status_changed", label: "Bug Status Changed" },
          { value: "test_case.created", label: "Test Case Created" },
          { value: "test_case.status_changed", label: "Test Case Status Changed" },
        ]} />
      </Card>

      {loading && <LoadingState message="Loading audit log…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && data && (
        <>
          {data.items.length === 0 ? <EmptyState title="No audit entries" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left">Actor</th>
                    <th className="px-4 py-3 text-left">Action</th>
                    <th className="px-4 py-3 text-left">Entity</th>
                    <th className="px-4 py-3 text-left">IP</th>
                    <th className="px-4 py-3 text-left">Timestamp</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.items.map((a) => (
                    <tr key={a.id}>
                      <td className="px-4 py-3 text-xs text-slate-300">{a.actor_name ?? "System"}</td>
                      <td className="px-4 py-3 text-xs text-slate-400">{a.action.replace(/_/g, " ")}</td>
                      <td className="px-4 py-3 text-xs text-slate-500">{a.entity_type}</td>
                      <td className="px-4 py-3 font-mono text-xs text-slate-600">{a.ip_address ?? "—"}</td>
                      <td className="px-4 py-3 text-xs text-slate-500">{new Date(a.created_at).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          {data.total_pages > 1 && (
            <div className="flex items-center justify-between">
              <span className="text-xs text-slate-500">{data.total} entries · Page {data.page} of {data.total_pages}</span>
              <div className="flex gap-2">
                <Button variant="secondary" size="sm" disabled={page <= 1} onClick={() => setPage(page - 1)}>Prev</Button>
                <Button variant="secondary" size="sm" disabled={page >= data.total_pages} onClick={() => setPage(page + 1)}>Next</Button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
