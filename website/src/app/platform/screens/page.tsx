"use client";

import { useEffect, useState, useCallback } from "react";
import { platformApi, type ScreenListResponse } from "@/lib/admin/platform-client";
import { Card, Button, Input, Select, LoadingState, ErrorState, EmptyState } from "@/components/admin/platform/PlatformUI";

export default function ScreensPage() {
  const [data, setData] = useState<ScreenListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState("");
  const [moduleFilter, setModuleFilter] = useState("");

  const load = useCallback(() => {
    setLoading(true);
    platformApi.listScreens({ page, search: search || undefined, module: moduleFilter || undefined, page_size: 25 })
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [page, search, moduleFilter]);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">Screen Registry</h2>
      </div>

      <Card className="flex flex-wrap gap-3">
        <Input value={search} onChange={setSearch} placeholder="Search screens…" className="flex-1 min-w-[200px]" />
        <Select value={moduleFilter} onChange={setModuleFilter} options={[
          { value: "", label: "All Modules" },
          { value: "composeApp", label: "Compose App" },
          { value: "website", label: "Website" },
          { value: "shared", label: "Shared" },
          { value: "server", label: "Server" },
        ]} />
        <Button variant="secondary" onClick={load}>Refresh</Button>
      </Card>

      {loading && <LoadingState message="Loading screens…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && data && (
        <>
          {data.items.length === 0 ? <EmptyState title="No screens found" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left">Screen ID</th>
                    <th className="px-4 py-3 text-left">Name</th>
                    <th className="px-4 py-3 text-left">Module</th>
                    <th className="px-4 py-3 text-left">Route</th>
                    <th className="px-4 py-3 text-left">Feature</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.items.map((s) => (
                    <tr key={s.id} className="hover:bg-slate-800/30">
                      <td className="px-4 py-3 font-mono text-xs text-slate-400">{s.screen_id}</td>
                      <td className="px-4 py-3 text-slate-200">{s.name}</td>
                      <td className="px-4 py-3 text-xs text-slate-400">{s.module ?? "—"}</td>
                      <td className="px-4 py-3 font-mono text-xs text-slate-500">{s.route ?? "—"}</td>
                      <td className="px-4 py-3 text-xs text-slate-400">{s.feature_name ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          {data.total_pages > 1 && (
            <div className="flex items-center justify-between">
              <span className="text-xs text-slate-500">{data.total} screens · Page {data.page} of {data.total_pages}</span>
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
