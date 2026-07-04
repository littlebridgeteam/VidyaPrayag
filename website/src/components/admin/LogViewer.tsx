"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useCallback, useEffect } from "react";
import { adminApi } from "@/lib/admin/client";
import { Card, CardHeader, Badge, EmptyState } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import type { ServerLogsPageDto, ServerLogStatsDto, ServerLogDto } from "@/lib/admin/types";

const LEVEL_COLORS: Record<string, string> = {
  ERROR: "bg-red-100 text-red-700 border-red-200",
  WARN: "bg-amber-100 text-amber-700 border-amber-200",
  INFO: "bg-blue-100 text-blue-700 border-blue-200",
  DEBUG: "bg-gray-100 text-gray-600 border-gray-200",
  TRACE: "bg-purple-100 text-purple-600 border-purple-200",
};

const CATEGORIES = ["http", "ai", "job", "auth", "notification", "pews", "sync", "general"];
const LEVELS = ["ERROR", "WARN", "INFO", "DEBUG", "TRACE"];

export function LogViewer() {
  const [logs, setLogs] = useState<ServerLogDto[]>([]);
  const [stats, setStats] = useState<ServerLogStatsDto | null>(null);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [level, setLevel] = useState("");
  const [category, setCategory] = useState("");
  const [search, setSearch] = useState("");
  const [offset, setOffset] = useState(0);
  const [limit] = useState(50);
  const [selectedLog, setSelectedLog] = useState<ServerLogDto | null>(null);
  const [refreshNonce, setRefreshNonce] = useState(0);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, unknown> = { limit, offset };
      if (level) params.level = level;
      if (category) params.category = category;
      if (search) params.search = search;
      const res = await adminApi.serverLogs(params);
      setLogs(res.logs);
      setTotal(res.total);
    } catch (e: unknown) {
      setError(`Failed to load logs: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, [level, category, search, offset, limit, refreshNonce]);

  const loadStats = useCallback(async () => {
    try {
      const s = await adminApi.serverLogStats();
      setStats(s);
    } catch {
      // silent — stats are supplementary
    }
  }, []);

  useEffect(() => {
    load();
    loadStats();
  }, [load, loadStats]);

  // Auto-refresh logs every 500ms (polling — EventSource can't send JWT headers).
  useEffect(() => {
    const interval = setInterval(() => {
      load();
    }, 500);
    return () => clearInterval(interval);
  }, [load]);

  const handleFilter = () => {
    setOffset(0);
    setRefreshNonce((n) => n + 1);
  };

  const handleClearFilters = () => {
    setLevel("");
    setCategory("");
    setSearch("");
    setOffset(0);
  };

  return (
    <Card>
      <CardHeader
        title="Server Logs"
        subtitle="Structured server-side log viewer — super admin only"
        action={
          <AdminButton onClick={load} disabled={loading}>
            {loading ? "Loading…" : "Refresh"}
          </AdminButton>
        }
      />

      {/* Stats summary */}
      {stats && (
        <div className="grid grid-cols-2 gap-3 px-5 pb-3 sm:grid-cols-4">
          <StatCard label="Total (24h)" value={stats.total_last_24h} />
          <StatCard label="Errors" value={stats.by_level.ERROR ?? 0} color="text-red-600" />
          <StatCard label="Warnings" value={stats.by_level.WARN ?? 0} color="text-amber-600" />
          <StatCard
            label="AI Requests"
            value={stats.ai_token_usage.total_requests}
            color="text-blue-600"
          />
        </div>
      )}

      {/* Filters */}
      <div className="flex flex-wrap items-end gap-3 px-5 pb-4">
        <div className="flex flex-col gap-1">
          <label className="text-[11px] font-semibold uppercase tracking-wide text-ink-3">
            Level
          </label>
          <select
            value={level}
            onChange={(e) => setLevel(e.target.value)}
            className="h-9 rounded-lg border border-hairline bg-white px-3 text-[13px] text-ink"
          >
            <option value="">All</option>
            {LEVELS.map((l) => (
              <option key={l} value={l}>
                {l}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-[11px] font-semibold uppercase tracking-wide text-ink-3">
            Category
          </label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="h-9 rounded-lg border border-hairline bg-white px-3 text-[13px] text-ink"
          >
            <option value="">All</option>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-1 flex-col gap-1">
          <label className="text-[11px] font-semibold uppercase tracking-wide text-ink-3">
            Search
          </label>
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleFilter()}
            placeholder="Search message…"
            className="h-9 rounded-lg border border-hairline bg-white px-3 text-[13px] text-ink"
          />
        </div>
        <AdminButton onClick={handleFilter} disabled={loading}>
          Filter
        </AdminButton>
        <AdminButton onClick={handleClearFilters} disabled={loading}>
          Clear
        </AdminButton>
      </div>

      {/* Log table */}
      {error ? (
        <div className="px-5 pb-5 text-[13px] text-red-600">{error}</div>
      ) : logs.length === 0 && !loading ? (
        <div className="px-5 pb-5">
          <EmptyState title="No logs found" hint="Try adjusting filters or wait for new requests." icon={<span>📋</span>} />
        </div>
      ) : (
        <div className="max-h-[600px] overflow-y-auto">
          <table className="w-full text-[12px]">
            <thead className="sticky top-0 z-10 bg-cream/95 backdrop-blur">
              <tr className="border-b border-hairline text-left text-[11px] uppercase tracking-wide text-ink-3">
                <th className="px-3 py-2 font-semibold">Time</th>
                <th className="px-3 py-2 font-semibold">Level</th>
                <th className="px-3 py-2 font-semibold">Category</th>
                <th className="px-3 py-2 font-semibold">Message</th>
                <th className="px-3 py-2 font-semibold">Status</th>
                <th className="px-3 py-2 font-semibold">Duration</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr
                  key={log.id}
                  className="cursor-pointer border-b border-hairline/50 hover:bg-cream/50"
                  onClick={() => setSelectedLog(log)}
                >
                  <td className="whitespace-nowrap px-3 py-2 text-ink-3">
                    {new Date(log.timestamp).toLocaleTimeString()}
                  </td>
                  <td className="px-3 py-2">
                    <span
                      className={`inline-block rounded border px-1.5 py-0.5 text-[10px] font-bold ${
                        LEVEL_COLORS[log.level] ?? "bg-gray-100 text-gray-600 border-gray-200"
                      }`}
                    >
                      {log.level}
                    </span>
                  </td>
                  <td className="whitespace-nowrap px-3 py-2 text-ink-2">{log.category}</td>
                  <td className="max-w-[300px] truncate px-3 py-2 text-ink">
                    {log.message}
                  </td>
                  <td className="px-3 py-2 text-ink-3">
                    {log.status_code ?? "—"}
                  </td>
                  <td className="whitespace-nowrap px-3 py-2 text-ink-3">
                    {log.duration_ms != null ? `${log.duration_ms}ms` : "—"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Pagination */}
      {total > limit && (
        <div className="flex items-center justify-between px-5 py-3 text-[12px] text-ink-3">
          <span>
            Showing {offset + 1}–{Math.min(offset + limit, total)} of {total}
          </span>
          <div className="flex gap-2">
            <AdminButton
              onClick={() => {
                setOffset(Math.max(0, offset - limit));
              }}
              disabled={offset === 0 || loading}
            >
              ← Prev
            </AdminButton>
            <AdminButton
              onClick={() => {
                setOffset(offset + limit);
              }}
              disabled={offset + limit >= total || loading}
            >
              Next →
            </AdminButton>
          </div>
        </div>
      )}

      {/* Detail modal */}
      {selectedLog && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
          onClick={() => setSelectedLog(null)}
        >
          <div
            className="max-h-[80vh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-white p-6 shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-[16px] font-bold text-navy-deep">Log Detail</h3>
              <button
                onClick={() => setSelectedLog(null)}
                className="text-[20px] text-ink-3 hover:text-ink"
              >
                ×
              </button>
            </div>
            <div className="space-y-3 text-[13px]">
              <DetailRow label="Timestamp" value={new Date(selectedLog.timestamp).toLocaleString()} />
              <DetailRow label="Level" value={selectedLog.level} />
              <DetailRow label="Category" value={selectedLog.category} />
              <DetailRow label="Message" value={selectedLog.message} />
              {selectedLog.endpoint && (
                <DetailRow label="Endpoint" value={selectedLog.endpoint} />
              )}
              {selectedLog.status_code != null && (
                <DetailRow label="Status Code" value={String(selectedLog.status_code)} />
              )}
              {selectedLog.duration_ms != null && (
                <DetailRow label="Duration" value={`${selectedLog.duration_ms}ms`} />
              )}
              {selectedLog.actor_id && (
                <DetailRow label="Actor ID" value={selectedLog.actor_id} />
              )}
              {selectedLog.details && Object.keys(selectedLog.details).length > 0 && (
                <div>
                  <div className="mb-1 text-[11px] font-semibold uppercase tracking-wide text-ink-3">
                    Details
                  </div>
                  <pre className="overflow-x-auto rounded-lg bg-cream p-3 text-[12px] text-ink-2">
                    {JSON.stringify(selectedLog.details, null, 2)}
                  </pre>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </Card>
  );
}

function StatCard({
  label,
  value,
  color,
}: {
  label: string;
  value: number;
  color?: string;
}) {
  return (
    <div className="rounded-xl border border-hairline bg-cream/50 p-3">
      <div className="text-[11px] font-semibold uppercase tracking-wide text-ink-3">
        {label}
      </div>
      <div className={`text-[20px] font-bold ${color ?? "text-navy-deep"}`}>
        {value.toLocaleString()}
      </div>
    </div>
  );
}

function DetailRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex gap-2">
      <span className="w-24 shrink-0 text-[11px] font-semibold uppercase tracking-wide text-ink-3">
        {label}
      </span>
      <span className="flex-1 break-words text-ink">{value}</span>
    </div>
  );
}
