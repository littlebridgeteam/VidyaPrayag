"use client";

import { useEffect, useState, useCallback } from "react";
import { platformApi, type FeatureDto, type FeatureListResponse } from "@/lib/admin/platform-client";
import { FEATURE_STATUSES, FEATURE_PRIORITIES } from "@/lib/admin/platform-nav";
import { Card, StatusBadge, PriorityBadge, ProgressBar, Button, Input, Select, LoadingState, ErrorState, EmptyState } from "@/components/admin/platform/PlatformUI";

export default function FeatureListPage() {
  const [data, setData] = useState<FeatureListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState("");

  const load = useCallback(() => {
    setLoading(true);
    platformApi.listFeatures({ page, search: search || undefined, status: statusFilter || undefined, priority: priorityFilter || undefined, page_size: 25 })
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [page, search, statusFilter, priorityFilter]);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">Feature Registry</h2>
        <Button onClick={() => window.location.href = "/platform/features/new"}>
          + New Feature
        </Button>
      </div>

      {/* Filters */}
      <Card className="flex flex-wrap gap-3">
        <Input value={search} onChange={setSearch} placeholder="Search features…" className="flex-1 min-w-[200px]" />
        <Select value={statusFilter} onChange={setStatusFilter} options={[{ value: "", label: "All Statuses" }, ...FEATURE_STATUSES.map(s => ({ value: s, label: s.replace(/_/g, " ") }))]} />
        <Select value={priorityFilter} onChange={setPriorityFilter} options={[{ value: "", label: "All Priorities" }, ...FEATURE_PRIORITIES.map(p => ({ value: p, label: p }))]} />
        <Button variant="secondary" onClick={load}>Refresh</Button>
      </Card>

      {loading && <LoadingState message="Loading features…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && data && (
        <>
          {data.items.length === 0 ? (
            <EmptyState title="No features found" message="Try adjusting filters or create a new feature." />
          ) : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left font-medium">Feature ID</th>
                    <th className="px-4 py-3 text-left font-medium">Name</th>
                    <th className="px-4 py-3 text-left font-medium">Status</th>
                    <th className="px-4 py-3 text-left font-medium">Priority</th>
                    <th className="px-4 py-3 text-left font-medium">Completion</th>
                    <th className="px-4 py-3 text-left font-medium">Owner</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.items.map((f: FeatureDto) => (
                    <tr
                      key={f.id}
                      className="cursor-pointer hover:bg-slate-800/30"
                      onClick={() => window.location.href = `/platform/features/${f.id}`}
                    >
                      <td className="px-4 py-3 font-mono text-xs text-slate-400">{f.feature_id}</td>
                      <td className="px-4 py-3 text-slate-200">{f.name}</td>
                      <td className="px-4 py-3"><StatusBadge status={f.status} /></td>
                      <td className="px-4 py-3"><PriorityBadge priority={f.priority} /></td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <div className="w-20"><ProgressBar value={f.completion_pct} /></div>
                          <span className="text-xs text-slate-400">{f.completion_pct}%</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-xs text-slate-400">{f.owner_name ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Pagination */}
          {data.total_pages > 1 && (
            <div className="flex items-center justify-between">
              <span className="text-xs text-slate-500">{data.total} features · Page {data.page} of {data.total_pages}</span>
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
