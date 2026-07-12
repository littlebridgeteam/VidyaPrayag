"use client";

import { useEffect, useState, useCallback } from "react";
import { platformApi, type TestCaseListResponse } from "@/lib/admin/platform-client";
import { TEST_CASE_STATUSES } from "@/lib/admin/platform-nav";
import { Card, StatusBadge, PriorityBadge, Button, Input, Select, LoadingState, ErrorState, EmptyState } from "@/components/admin/platform/PlatformUI";

export default function TestCasesPage() {
  const [data, setData] = useState<TestCaseListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const load = useCallback(() => {
    setLoading(true);
    platformApi.listTestCases({ page, search: search || undefined, status: statusFilter || undefined, page_size: 25 })
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [page, search, statusFilter]);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">Test Cases</h2>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => window.location.href = "/platform/test-cases/my"}>My Assignments</Button>
        </div>
      </div>

      <Card className="flex flex-wrap gap-3">
        <Input value={search} onChange={setSearch} placeholder="Search test cases…" className="flex-1 min-w-[200px]" />
        <Select value={statusFilter} onChange={setStatusFilter} options={[{ value: "", label: "All Statuses" }, ...TEST_CASE_STATUSES.map(s => ({ value: s, label: s.replace(/_/g, " ") }))]} />
        <Button variant="secondary" onClick={load}>Refresh</Button>
      </Card>

      {loading && <LoadingState message="Loading test cases…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && data && (
        <>
          {data.items.length === 0 ? (
            <EmptyState title="No test cases found" message="Adjust filters or create a new test case." />
          ) : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left">Case ID</th>
                    <th className="px-4 py-3 text-left">Title</th>
                    <th className="px-4 py-3 text-left">Feature</th>
                    <th className="px-4 py-3 text-left">Status</th>
                    <th className="px-4 py-3 text-left">Priority</th>
                    <th className="px-4 py-3 text-left">Assigned To</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.items.map((t) => (
                    <tr key={t.id} className="cursor-pointer hover:bg-slate-800/30" onClick={() => window.location.href = `/platform/test-cases/${t.id}`}>
                      <td className="px-4 py-3 font-mono text-xs text-slate-400">{t.case_id}</td>
                      <td className="px-4 py-3 text-slate-200">{t.title}</td>
                      <td className="px-4 py-3 text-xs text-slate-400">{t.feature_name ?? "—"}</td>
                      <td className="px-4 py-3"><StatusBadge status={t.status} /></td>
                      <td className="px-4 py-3"><PriorityBadge priority={t.priority} /></td>
                      <td className="px-4 py-3 text-xs text-slate-400">{t.assigned_to_name ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          {data.total_pages > 1 && (
            <div className="flex items-center justify-between">
              <span className="text-xs text-slate-500">{data.total} cases · Page {data.page} of {data.total_pages}</span>
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
