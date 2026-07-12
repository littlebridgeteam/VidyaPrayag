"use client";

import { useEffect, useState } from "react";
import { platformApi, type FeatureDetailDto } from "@/lib/admin/platform-client";
import { Card, StatusBadge, PriorityBadge, ProgressBar, LoadingState, ErrorState, Tabs, EmptyState, SeverityBadge } from "@/components/admin/platform/PlatformUI";

export default function FeatureDetailPage({ params }: { params: { id: string } }) {
  const { id } = params;
  const [data, setData] = useState<FeatureDetailDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState("overview");

  useEffect(() => {
    platformApi.getFeature(id)
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <LoadingState message="Loading feature…" />;
  if (error) return <ErrorState message={error} />;
  if (!data) return <EmptyState title="Feature not found" />;

  const f = data.feature;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <div className="flex items-center gap-3">
            <span className="font-mono text-xs text-slate-500">{f.feature_id}</span>
            <StatusBadge status={f.status} />
            <PriorityBadge priority={f.priority} />
            {f.severity && <SeverityBadge severity={f.severity} />}
          </div>
          <h2 className="mt-2 text-xl font-bold text-slate-200">{f.name}</h2>
          {f.description && <p className="mt-1 text-sm text-slate-400">{f.description}</p>}
        </div>
        <a href="/platform/features" className="text-xs text-slate-500 hover:text-slate-300">← Back to list</a>
      </div>

      {/* Completion bar */}
      <Card>
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium text-slate-300">Completion</span>
          <span className="text-lg font-bold text-slate-200">{f.completion_pct}%</span>
        </div>
        <div className="mt-2"><ProgressBar value={f.completion_pct} /></div>
      </Card>

      {/* Tabs */}
      <Tabs
        tabs={[
          { id: "overview", label: "Overview" },
          { id: "flows", label: "Flows", count: data.flows.length },
          { id: "screens", label: "Screens", count: data.screens.length },
          { id: "apis", label: "APIs", count: data.apis.length },
          { id: "tests", label: "Test Cases", count: data.test_cases.length },
          { id: "bugs", label: "Bugs", count: data.bugs.length },
          { id: "children", label: "Sub-features", count: data.children.length },
        ]}
        active={activeTab}
        onChange={setActiveTab}
      />

      {/* Tab content */}
      {activeTab === "overview" && (
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          <Card>
            <h3 className="mb-3 text-sm font-semibold text-slate-300">Details</h3>
            <dl className="space-y-2 text-xs">
              <DetailRow label="Product Area" value={f.product_area} />
              <DetailRow label="Category" value={f.category} />
              <DetailRow label="Module" value={f.module} />
              <DetailRow label="Team" value={f.team} />
              <DetailRow label="Sprint" value={f.sprint} />
              <DetailRow label="Owner" value={f.owner_name} />
              <DetailRow label="Estimated Effort" value={f.estimated_effort} />
              <DetailRow label="Target Release" value={f.target_release} />
              <DetailRow label="Release Status" value={f.release_status} />
              <DetailRow label="Risk Level" value={f.risk_level} />
              <DetailRow label="Business Impact" value={f.business_impact} />
              <DetailRow label="Tech Complexity" value={f.tech_complexity} />
            </dl>
          </Card>
          <Card>
            <h3 className="mb-3 text-sm font-semibold text-slate-300">Business Goal</h3>
            <p className="text-sm text-slate-400">{f.business_goal ?? "Not specified"}</p>
            {f.blockers && (
              <>
                <h4 className="mt-4 mb-2 text-xs font-semibold text-red-400">Blockers</h4>
                <p className="text-sm text-slate-400">{f.blockers}</p>
              </>
            )}
          </Card>
        </div>
      )}

      {activeTab === "flows" && (
        <div className="space-y-3">
          {data.flows.length === 0 ? <EmptyState title="No flows defined" /> : (
            data.flows.map((flow) => (
              <Card key={flow.id}>
                <h4 className="text-sm font-semibold text-slate-300">{flow.flow_name}</h4>
                {flow.flow_description && <p className="mt-1 text-xs text-slate-500">{flow.flow_description}</p>}
              </Card>
            ))
          )}
        </div>
      )}

      {activeTab === "screens" && (
        <div className="space-y-3">
          {data.screens.length === 0 ? <EmptyState title="No screens linked" /> : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              {data.screens.map((s) => (
                <Card key={s.id}>
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-slate-300">{s.name}</span>
                    <span className="font-mono text-xs text-slate-500">{s.screen_id}</span>
                  </div>
                  {s.module && <span className="mt-1 inline-block text-xs text-slate-500">{s.module}</span>}
                </Card>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === "apis" && (
        <div className="space-y-3">
          {data.apis.length === 0 ? <EmptyState title="No API mappings" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr><th className="px-4 py-2 text-left">Method</th><th className="px-4 py-2 text-left">Endpoint</th><th className="px-4 py-2 text-left">Description</th></tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.apis.map((a) => (
                    <tr key={a.id}>
                      <td className="px-4 py-2"><span className="rounded bg-slate-800 px-1.5 py-0.5 font-mono text-xs text-indigo-400">{a.method}</span></td>
                      <td className="px-4 py-2 font-mono text-xs text-slate-300">{a.endpoint}</td>
                      <td className="px-4 py-2 text-xs text-slate-500">{a.description ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === "tests" && (
        <div className="space-y-3">
          {data.test_cases.length === 0 ? <EmptyState title="No test cases" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr><th className="px-4 py-2 text-left">Case ID</th><th className="px-4 py-2 text-left">Title</th><th className="px-4 py-2 text-left">Status</th><th className="px-4 py-2 text-left">Priority</th></tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.test_cases.map((t) => (
                    <tr key={t.id} className="cursor-pointer hover:bg-slate-800/30" onClick={() => window.location.href = `/platform/test-cases/${t.id}`}>
                      <td className="px-4 py-2 font-mono text-xs text-slate-400">{t.case_id}</td>
                      <td className="px-4 py-2 text-slate-200">{t.title}</td>
                      <td className="px-4 py-2"><StatusBadge status={t.status} /></td>
                      <td className="px-4 py-2"><PriorityBadge priority={t.priority} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === "bugs" && (
        <div className="space-y-3">
          {data.bugs.length === 0 ? <EmptyState title="No bugs reported" /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr><th className="px-4 py-2 text-left">Bug ID</th><th className="px-4 py-2 text-left">Title</th><th className="px-4 py-2 text-left">Status</th><th className="px-4 py-2 text-left">Severity</th></tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {data.bugs.map((b) => (
                    <tr key={b.id} className="cursor-pointer hover:bg-slate-800/30" onClick={() => window.location.href = `/platform/bugs/${b.id}`}>
                      <td className="px-4 py-2 font-mono text-xs text-slate-400">{b.bug_id}</td>
                      <td className="px-4 py-2 text-slate-200">{b.title}</td>
                      <td className="px-4 py-2"><StatusBadge status={b.status} /></td>
                      <td className="px-4 py-2"><SeverityBadge severity={b.severity} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {activeTab === "children" && (
        <div className="space-y-3">
          {data.children.length === 0 ? <EmptyState title="No sub-features" /> : (
            data.children.map((c) => (
              <Card key={c.id} className="cursor-pointer hover:border-slate-700" >
                <a href={`/platform/features/${c.id}`}>
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-slate-300">{c.name}</span>
                    <StatusBadge status={c.status} />
                  </div>
                </a>
              </Card>
            ))
          )}
        </div>
      )}
    </div>
  );
}

function DetailRow({ label, value }: { label: string; value?: string | null }) {
  return (
    <div className="flex justify-between">
      <dt className="text-slate-500">{label}</dt>
      <dd className="text-slate-300">{value ?? "—"}</dd>
    </div>
  );
}
