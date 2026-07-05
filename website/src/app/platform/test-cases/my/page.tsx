"use client";

import { useEffect, useState } from "react";
import { platformApi, type TestCaseDto } from "@/lib/admin/platform-client";
import { Card, StatusBadge, PriorityBadge, LoadingState, ErrorState, EmptyState } from "@/components/admin/platform/PlatformUI";

export default function MyTestCasesPage() {
  const [data, setData] = useState<TestCaseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    platformApi.myTestCases()
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">My Test Assignments</h2>
        <a href="/platform/test-cases" className="text-xs text-slate-500 hover:text-slate-300">← All test cases</a>
      </div>

      {loading && <LoadingState message="Loading assignments…" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && (
        <>
          {data.length === 0 ? <EmptyState title="No test cases assigned to you" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left">Case ID</th>
                    <th className="px-4 py-3 text-left">Title</th>
                    <th className="px-4 py-3 text-left">Feature</th>
                    <th className="px-4 py-3 text-left">Status</th>
                    <th className="px-4 py-3 text-left">Priority</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.map((t) => (
                    <tr key={t.id} className="cursor-pointer hover:bg-slate-800/30" onClick={() => window.location.href = `/platform/test-cases/${t.id}`}>
                      <td className="px-4 py-3 font-mono text-xs text-slate-400">{t.case_id}</td>
                      <td className="px-4 py-3 text-slate-200">{t.title}</td>
                      <td className="px-4 py-3 text-xs text-slate-400">{t.feature_name ?? "—"}</td>
                      <td className="px-4 py-3"><StatusBadge status={t.status} /></td>
                      <td className="px-4 py-3"><PriorityBadge priority={t.priority} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
}
