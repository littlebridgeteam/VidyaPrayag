"use client";

import { useEffect, useState, useCallback } from "react";
import { platformApi, type BugListResponse, type BugSummaryDto } from "@/lib/admin/platform-client";
import { BUG_STATUSES, BUG_SEVERITIES } from "@/lib/admin/platform-nav";
import { Card, StatusBadge, PriorityBadge, SeverityBadge, Button, Input, Select, LoadingState, ErrorState, EmptyState, Tabs } from "@/components/admin/platform/PlatformUI";

const KANBAN_COLUMNS = ["reported", "triaged", "in_progress", "fixed", "ready_for_qa", "verified", "closed"];

export default function BugsPage() {
  const [view, setView] = useState<"list" | "kanban">("list");
  const [data, setData] = useState<BugListResponse | null>(null);
  const [kanban, setKanban] = useState<Record<string, BugSummaryDto[]> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [severityFilter, setSeverityFilter] = useState("");

  const loadList = useCallback(() => {
    setLoading(true);
    platformApi.listBugs({ page, search: search || undefined, status: statusFilter || undefined, severity: severityFilter || undefined, page_size: 25 })
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [page, search, statusFilter, severityFilter]);

  const loadKanban = useCallback(() => {
    setLoading(true);
    platformApi.bugKanban()
      .then(setKanban)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (view === "list") loadList(); else loadKanban();
  }, [view, loadList, loadKanban]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">Bugs</h2>
        <Button onClick={() => window.location.href = "/platform/bugs/new"}>+ New Bug</Button>
      </div>

      <Tabs tabs={[{ id: "list", label: "List View" }, { id: "kanban", label: "Kanban" }]} active={view} onChange={(t) => setView(t as "list" | "kanban")} />

      {view === "list" && (
        <>
          <Card className="flex flex-wrap gap-3">
            <Input value={search} onChange={setSearch} placeholder="Search bugs…" className="flex-1 min-w-[200px]" />
            <Select value={statusFilter} onChange={setStatusFilter} options={[{ value: "", label: "All Statuses" }, ...BUG_STATUSES.map(s => ({ value: s, label: s.replace(/_/g, " ") }))]} />
            <Select value={severityFilter} onChange={setSeverityFilter} options={[{ value: "", label: "All Severities" }, ...BUG_SEVERITIES.map(s => ({ value: s, label: s }))]} />
            <Button variant="secondary" onClick={loadList}>Refresh</Button>
          </Card>

          {loading && <LoadingState message="Loading bugs…" />}
          {error && <ErrorState message={error} />}
          {!loading && !error && data && (
            <>
              {data.items.length === 0 ? <EmptyState title="No bugs found" /> : (
                <div className="overflow-hidden rounded-xl border border-slate-800">
                  <table className="w-full text-sm">
                    <thead className="bg-[#1e293b] text-xs text-slate-500">
                      <tr>
                        <th className="px-4 py-3 text-left">Bug ID</th>
                        <th className="px-4 py-3 text-left">Title</th>
                        <th className="px-4 py-3 text-left">Status</th>
                        <th className="px-4 py-3 text-left">Priority</th>
                        <th className="px-4 py-3 text-left">Severity</th>
                        <th className="px-4 py-3 text-left">Assigned</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800">
                      {data.items.map((b) => (
                        <tr key={b.id} className="cursor-pointer hover:bg-slate-800/30" onClick={() => window.location.href = `/platform/bugs/${b.id}`}>
                          <td className="px-4 py-3 font-mono text-xs text-slate-400">{b.bug_id}</td>
                          <td className="px-4 py-3 text-slate-200">{b.title}</td>
                          <td className="px-4 py-3"><StatusBadge status={b.status} /></td>
                          <td className="px-4 py-3"><PriorityBadge priority={b.priority} /></td>
                          <td className="px-4 py-3"><SeverityBadge severity={b.severity} /></td>
                          <td className="px-4 py-3 text-xs text-slate-400">{b.assigned_to_name ?? "—"}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
              {data.total_pages > 1 && (
                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500">{data.total} bugs · Page {data.page} of {data.total_pages}</span>
                  <div className="flex gap-2">
                    <Button variant="secondary" size="sm" disabled={page <= 1} onClick={() => setPage(page - 1)}>Prev</Button>
                    <Button variant="secondary" size="sm" disabled={page >= data.total_pages} onClick={() => setPage(page + 1)}>Next</Button>
                  </div>
                </div>
              )}
            </>
          )}
        </>
      )}

      {view === "kanban" && (
        <>
          {loading && <LoadingState message="Loading kanban…" />}
          {error && <ErrorState message={error} />}
          {!loading && !error && kanban && (
            <div className="flex gap-3 overflow-x-auto pb-4">
              {KANBAN_COLUMNS.map((col) => {
                const bugs = kanban[col] ?? [];
                return (
                  <div key={col} className="w-64 shrink-0">
                    <div className="mb-2 flex items-center justify-between">
                      <span className="text-xs font-semibold capitalize text-slate-400">{col.replace(/_/g, " ")}</span>
                      <span className="rounded-full bg-slate-800 px-2 py-0.5 text-[10px] text-slate-500">{bugs.length}</span>
                    </div>
                    <div className="space-y-2">
                      {bugs.map((b) => (
                        <a key={b.id} href={`/platform/bugs/${b.id}`} className="block rounded-lg border border-slate-800 bg-[#1e293b] p-3 hover:border-slate-700">
                          <div className="flex items-center gap-2">
                            <span className="font-mono text-[10px] text-slate-500">{b.bug_id}</span>
                            <PriorityBadge priority={b.priority} />
                          </div>
                          <p className="mt-1.5 text-xs text-slate-300">{b.title}</p>
                          {b.assigned_to_name && <p className="mt-1 text-[10px] text-slate-500">@{b.assigned_to_name}</p>}
                        </a>
                      ))}
                      {bugs.length === 0 && <div className="rounded-lg border border-dashed border-slate-800 p-4 text-center text-[10px] text-slate-600">Empty</div>}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </>
      )}
    </div>
  );
}
