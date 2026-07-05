"use client";

import { useEffect, useState } from "react";
import { platformApi, type DiscoveredScreenDto, type DiscoveredApiDto, type HealthSummaryDto, type FeatureDto } from "@/lib/admin/platform-client";
import { Card, Button, LoadingState, ErrorState, EmptyState, Tabs, StatCard } from "@/components/admin/platform/PlatformUI";

type Tab = "screens" | "apis" | "health" | "import";

export default function DiscoveryPage() {
  const [tab, setTab] = useState<Tab>("screens");
  const [screens, setScreens] = useState<DiscoveredScreenDto[]>([]);
  const [apis, setApis] = useState<DiscoveredApiDto[]>([]);
  const [health, setHealth] = useState<HealthSummaryDto | null>(null);
  const [features, setFeatures] = useState<FeatureDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [scanning, setScanning] = useState(false);
  const [scanResult, setScanResult] = useState<string | null>(null);

  const loadScreens = () => {
    setLoading(true);
    platformApi.listDiscoveredScreens({ page: 1, page_size: 100 })
      .then((r) => setScreens(r.items))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  const loadApis = () => {
    setLoading(true);
    platformApi.listDiscoveredApis({ page: 1, page_size: 100 })
      .then((r) => setApis(r.items))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  const loadHealth = () => {
    setLoading(true);
    platformApi.healthSummary()
      .then(setHealth)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  const loadFeatures = () => {
    platformApi.listFeatures({ page: 1, page_size: 200 })
      .then((r) => setFeatures(r.items))
      .catch(() => {});
  };

  useEffect(() => {
    loadFeatures();
    if (tab === "screens") loadScreens();
    else if (tab === "apis") loadApis();
    else if (tab === "health") loadHealth();
  }, [tab]);

  const handleScanScreens = async () => {
    setScanning(true);
    setScanResult(null);
    try {
      const r = await platformApi.scanScreens();
      setScanResult(`Discovered: ${r.discovered}, Updated: ${r.updated}, Errors: ${r.errors.length}`);
      loadScreens();
    } catch (e: any) {
      setScanResult(`Error: ${e.message}`);
    } finally {
      setScanning(false);
    }
  };

  const handleScanApis = async () => {
    setScanning(true);
    setScanResult(null);
    try {
      const r = await platformApi.scanApis();
      setScanResult(`Discovered: ${r.discovered}, Updated: ${r.updated}, Errors: ${r.errors.length}`);
      loadApis();
    } catch (e: any) {
      setScanResult(`Error: ${e.message}`);
    } finally {
      setScanning(false);
    }
  };

  const handleHealthCheckAll = async () => {
    setScanning(true);
    setScanResult(null);
    try {
      const r = await platformApi.healthCheckAll();
      setScanResult(`Checked: ${r.discovered}, Errors: ${r.errors.length}`);
      loadHealth();
    } catch (e: any) {
      setScanResult(`Error: ${e.message}`);
    } finally {
      setScanning(false);
    }
  };

  const handleImportCsv = async () => {
    setScanning(true);
    setScanResult(null);
    try {
      const r = await platformApi.importCsv();
      setScanResult(`Imported: ${r.imported}, Skipped: ${r.skipped}, Errors: ${r.errors.length}`);
    } catch (e: any) {
      setScanResult(`Error: ${e.message}`);
    } finally {
      setScanning(false);
    }
  };

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-200">Auto-Discovery</h2>

      <Tabs
        tabs={[
          { id: "screens", label: "Screens" },
          { id: "apis", label: "APIs" },
          { id: "health", label: "Health" },
          { id: "import", label: "CSV Import" },
        ]}
        active={tab}
        onChange={(t) => { setTab(t as Tab); setError(null); setScanResult(null); }}
      />

      {scanResult && (
        <Card className="border-indigo-800/50 bg-indigo-900/20">
          <p className="text-sm text-indigo-300">{scanResult}</p>
        </Card>
      )}

      {tab === "screens" && (
        <div className="space-y-4">
          <div className="flex gap-2">
            <Button onClick={handleScanScreens} disabled={scanning}>{scanning ? "Scanning…" : "Scan Screens"}</Button>
          </div>
          {loading ? <LoadingState /> : error ? <ErrorState message={error} /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-2 text-left">Screen ID</th>
                    <th className="px-4 py-2 text-left">Name</th>
                    <th className="px-4 py-2 text-left">Module</th>
                    <th className="px-4 py-2 text-left">File</th>
                    <th className="px-4 py-2 text-left">Mapped</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {screens.map((s) => (
                    <tr key={s.id} className="hover:bg-slate-800/30">
                      <td className="px-4 py-2 font-mono text-xs text-slate-400">{s.screen_id}</td>
                      <td className="px-4 py-2 text-slate-200">{s.name}</td>
                      <td className="px-4 py-2 text-xs text-slate-400">{s.module}</td>
                      <td className="px-4 py-2 font-mono text-xs text-slate-500">{s.file_path}</td>
                      <td className="px-4 py-2">
                        {s.is_mapped ? (
                          <span className="text-xs text-green-400">✓ Mapped</span>
                        ) : (
                          <select
                            className="rounded bg-slate-800 px-2 py-1 text-xs text-slate-300"
                            onChange={(e) => {
                              if (e.target.value) {
                                platformApi.linkDiscoveredScreen(s.id, e.target.value).then(() => loadScreens());
                                e.target.value = "";
                              }
                            }}
                            defaultValue=""
                          >
                            <option value="">Link to feature…</option>
                            {features.map((f) => (
                              <option key={f.id} value={f.id}>{f.name}</option>
                            ))}
                          </select>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {screens.length === 0 && <EmptyState title="No screens discovered yet" message="Run a scan to discover screens from the codebase." />}
            </div>
          )}
        </div>
      )}

      {tab === "apis" && (
        <div className="space-y-4">
          <div className="flex gap-2">
            <Button onClick={handleScanApis} disabled={scanning}>{scanning ? "Scanning…" : "Scan APIs"}</Button>
          </div>
          {loading ? <LoadingState /> : error ? <ErrorState message={error} /> : (
            <div className="overflow-hidden rounded-xl border border-slate-800">
              <table className="w-full text-sm">
                <thead className="bg-[#1e293b] text-xs text-slate-500">
                  <tr>
                    <th className="px-4 py-2 text-left">Method</th>
                    <th className="px-4 py-2 text-left">Path</th>
                    <th className="px-4 py-2 text-left">File</th>
                    <th className="px-4 py-2 text-left">Mapped</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {apis.map((a) => (
                    <tr key={a.id} className="hover:bg-slate-800/30">
                      <td className="px-4 py-2"><span className="rounded bg-slate-800 px-1.5 py-0.5 font-mono text-xs text-indigo-400">{a.method}</span></td>
                      <td className="px-4 py-2 font-mono text-xs text-slate-300">{a.path}</td>
                      <td className="px-4 py-2 font-mono text-xs text-slate-500">{a.file_path}</td>
                      <td className="px-4 py-2">
                        {a.is_mapped ? (
                          <span className="text-xs text-green-400">✓ Mapped</span>
                        ) : (
                          <select
                            className="rounded bg-slate-800 px-2 py-1 text-xs text-slate-300"
                            onChange={(e) => {
                              if (e.target.value) {
                                platformApi.linkDiscoveredApi(a.id, e.target.value).then(() => loadApis());
                                e.target.value = "";
                              }
                            }}
                            defaultValue=""
                          >
                            <option value="">Link to feature…</option>
                            {features.map((f) => (
                              <option key={f.id} value={f.id}>{f.name}</option>
                            ))}
                          </select>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {apis.length === 0 && <EmptyState title="No APIs discovered yet" message="Run a scan to discover API routes from the server codebase." />}
            </div>
          )}
        </div>
      )}

      {tab === "health" && (
        <div className="space-y-4">
          <div className="flex gap-2">
            <Button onClick={handleHealthCheckAll} disabled={scanning}>{scanning ? "Checking…" : "Check All APIs"}</Button>
          </div>
          {loading ? <LoadingState /> : error ? <ErrorState message={error} /> : health && (
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-5">
              <StatCard label="Total" value={health.total} />
              <StatCard label="Alive" value={health.alive} color="text-green-400" />
              <StatCard label="Down" value={health.down} color="text-red-400" />
              <StatCard label="Slow" value={health.slow} color="text-amber-400" />
              <StatCard label="Untested" value={health.untested} color="text-slate-400" />
            </div>
          )}
        </div>
      )}

      {tab === "import" && (
        <div className="space-y-4">
          <Card>
            <h3 className="mb-2 text-sm font-semibold text-slate-300">Import from feature_audit.csv</h3>
            <p className="mb-4 text-xs text-slate-500">
              Imports 163 rows from the feature audit CSV. Each row becomes a feature with
              <code className="mx-1 rounded bg-slate-800 px-1 text-slate-400">legacy_imported = true</code>.
              Existing features are skipped.
            </p>
            <Button onClick={handleImportCsv} disabled={scanning}>{scanning ? "Importing…" : "Import CSV"}</Button>
          </Card>
        </div>
      )}
    </div>
  );
}
