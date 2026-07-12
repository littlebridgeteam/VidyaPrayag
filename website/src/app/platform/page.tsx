"use client";

import { useEffect, useState } from "react";
import { platformApi, type DashboardHealthDto, type TestingProgressDto, type BugSummaryBySeverityDto, type RiskIndicatorDto, type RecentActivityDto } from "@/lib/admin/platform-client";
import { Card, StatCard, ProgressBar, LoadingState, ErrorState, StatusBadge } from "@/components/admin/platform/PlatformUI";

export default function PlatformDashboardPage() {
  const [health, setHealth] = useState<DashboardHealthDto | null>(null);
  const [testing, setTesting] = useState<TestingProgressDto | null>(null);
  const [bugs, setBugs] = useState<BugSummaryBySeverityDto | null>(null);
  const [risks, setRisks] = useState<RiskIndicatorDto | null>(null);
  const [activity, setActivity] = useState<RecentActivityDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      platformApi.dashboardHealth(),
      platformApi.dashboardTestingProgress(),
      platformApi.dashboardBugSummary(),
      platformApi.dashboardRiskIndicators(),
      platformApi.dashboardRecentActivity(),
    ])
      .then(([h, t, b, r, a]) => {
        setHealth(h);
        setTesting(t);
        setBugs(b);
        setRisks(r);
        setActivity(a);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingState message="Loading dashboard…" />;
  if (error) return <ErrorState message={error} />;
  if (!health) return null;

  const scoreColor = health.overall_score >= 75 ? "text-green-400" : health.overall_score >= 50 ? "text-amber-400" : "text-red-400";

  return (
    <div className="space-y-6">
      {/* Health score */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Overall Health Score" value={`${health.overall_score}%`} color={scoreColor} sublabel="Weighted composite" />
        <StatCard label="Feature Completion" value={`${health.feature_completion}%`} sublabel={`${health.total_features} features`} />
        <StatCard label="Testing Progress" value={`${health.testing_progress}%`} sublabel={`${health.total_test_cases} test cases`} />
        <StatCard label="Release Readiness" value={`${health.release_readiness}%`} sublabel={`${health.open_bugs} open bugs`} />
      </div>

      {/* Progress bars */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <Card>
          <h3 className="mb-4 text-sm font-semibold text-slate-300">Health Breakdown</h3>
          <div className="space-y-3">
            <div>
              <div className="mb-1 flex justify-between text-xs">
                <span className="text-slate-400">Feature Completion</span>
                <span className="text-slate-300">{health.feature_completion}%</span>
              </div>
              <ProgressBar value={health.feature_completion} color="bg-blue-500" />
            </div>
            <div>
              <div className="mb-1 flex justify-between text-xs">
                <span className="text-slate-400">Testing Progress</span>
                <span className="text-slate-300">{health.testing_progress}%</span>
              </div>
              <ProgressBar value={health.testing_progress} color="bg-green-500" />
            </div>
            <div>
              <div className="mb-1 flex justify-between text-xs">
                <span className="text-slate-400">Release Readiness</span>
                <span className="text-slate-300">{health.release_readiness}%</span>
              </div>
              <ProgressBar value={health.release_readiness} color="bg-indigo-500" />
            </div>
            <div>
              <div className="mb-1 flex justify-between text-xs">
                <span className="text-slate-400">Bug Health</span>
                <span className="text-slate-300">{health.bug_health}%</span>
              </div>
              <ProgressBar value={health.bug_health} color="bg-amber-500" />
            </div>
          </div>
        </Card>

        {/* Risk indicators */}
        {risks && (
          <Card>
            <h3 className="mb-4 text-sm font-semibold text-slate-300">Risk Indicators</h3>
            <div className="grid grid-cols-2 gap-3">
              <RiskItem label="Blocked Features" value={risks.blocked_features} severity="warn" />
              <RiskItem label="Critical Bugs" value={risks.critical_bugs} severity="danger" />
              <RiskItem label="SLA Breaches" value={risks.sla_breaches} severity="danger" />
              <RiskItem label="High-Risk Features" value={risks.high_risk_features} severity="warn" />
              <RiskItem label="APIs Down" value={risks.apis_down} severity="danger" />
              <RiskItem label="Bug Density" value={health.bug_density} severity={health.bug_density === "low" ? "ok" : "warn"} />
            </div>
          </Card>
        )}
      </div>

      {/* Testing + Bugs */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        {testing && (
          <Card>
            <h3 className="mb-4 text-sm font-semibold text-slate-300">Testing Progress</h3>
            <div className="space-y-2">
              <TestingBar label="Passed" value={testing.passed} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-green-500" />
              <TestingBar label="Failed" value={testing.failed} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-red-500" />
              <TestingBar label="Pending" value={testing.pending} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-slate-600" />
              <TestingBar label="In Progress" value={testing.in_progress} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-blue-500" />
              <TestingBar label="Need Retest" value={testing.need_retest} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-amber-500" />
              <TestingBar label="Blocked" value={testing.blocked} total={testing.passed + testing.failed + testing.pending + testing.blocked + testing.need_retest + testing.in_progress} color="bg-orange-500" />
            </div>
          </Card>
        )}

        {bugs && (
          <Card>
            <h3 className="mb-4 text-sm font-semibold text-slate-300">Open Bugs by Severity</h3>
            <div className="space-y-2">
              <TestingBar label="Critical" value={bugs.critical} total={bugs.critical + bugs.major + bugs.normal + bugs.minor + bugs.cosmetic} color="bg-red-500" />
              <TestingBar label="Major" value={bugs.major} total={bugs.critical + bugs.major + bugs.normal + bugs.minor + bugs.cosmetic} color="bg-orange-500" />
              <TestingBar label="Normal" value={bugs.normal} total={bugs.critical + bugs.major + bugs.normal + bugs.minor + bugs.cosmetic} color="bg-yellow-500" />
              <TestingBar label="Minor" value={bugs.minor} total={bugs.critical + bugs.major + bugs.normal + bugs.minor + bugs.cosmetic} color="bg-blue-500" />
              <TestingBar label="Cosmetic" value={bugs.cosmetic} total={bugs.critical + bugs.major + bugs.normal + bugs.minor + bugs.cosmetic} color="bg-slate-600" />
            </div>
          </Card>
        )}
      </div>

      {/* Recent activity */}
      <Card>
        <h3 className="mb-4 text-sm font-semibold text-slate-300">Recent Activity</h3>
        {activity.length === 0 ? (
          <p className="py-4 text-center text-xs text-slate-500">No recent activity</p>
        ) : (
          <div className="space-y-2">
            {activity.slice(0, 15).map((a) => (
              <div key={a.id} className="flex items-center justify-between border-b border-slate-800 py-2 text-xs last:border-0">
                <div className="flex items-center gap-2">
                  <span className="text-slate-400">{a.actor_name ?? "System"}</span>
                  <span className="text-slate-600">·</span>
                  <span className="text-slate-300">{a.action.replace(/_/g, " ")}</span>
                  <span className="text-slate-600">·</span>
                  <span className="text-slate-500">{a.entity_type}</span>
                </div>
                <span className="text-slate-600">{new Date(a.created_at).toLocaleString()}</span>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
}

function RiskItem({ label, value, severity }: { label: string; value: number | string; severity: "ok" | "warn" | "danger" }) {
  const colors = { ok: "text-green-400", warn: "text-amber-400", danger: "text-red-400" };
  return (
    <div className="flex flex-col gap-0.5 rounded-lg bg-slate-900/50 p-3">
      <span className="text-[10px] text-slate-500">{label}</span>
      <span className={`text-lg font-bold ${colors[severity]}`}>{value}</span>
    </div>
  );
}

function TestingBar({ label, value, total, color }: { label: string; value: number; total: number; color: string }) {
  const pct = total > 0 ? (value / total) * 100 : 0;
  return (
    <div>
      <div className="mb-1 flex justify-between text-xs">
        <span className="text-slate-400">{label}</span>
        <span className="text-slate-300">{value}</span>
      </div>
      <ProgressBar value={pct} color={color} />
    </div>
  );
}
