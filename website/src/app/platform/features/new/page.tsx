"use client";

import { useState } from "react";
import { platformApi } from "@/lib/admin/platform-client";
import { Card, Button, Input, Select, LoadingState, ErrorState } from "@/components/admin/platform/PlatformUI";
import { FEATURE_STATUSES, FEATURE_PRIORITIES } from "@/lib/admin/platform-nav";

export default function NewFeaturePage() {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // CSV import state
  const [importing, setImporting] = useState(false);
  const [importResult, setImportResult] = useState<{ imported: number; skipped: number; errors: string[] } | null>(null);

  // Form fields
  const [featureId, setFeatureId] = useState("");
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState("planned");
  const [priority, setPriority] = useState("P2");
  const [productArea, setProductArea] = useState("");
  const [category, setCategory] = useState("");
  const [module, setModule] = useState("");
  const [team, setTeam] = useState("");
  const [ownerName, setOwnerName] = useState("");
  const [completionPct, setCompletionPct] = useState("0");
  const [businessGoal, setBusinessGoal] = useState("");

  const handleSave = () => {
    setSaving(true);
    setError(null);
    setSuccess(null);
    platformApi.createFeature({
      feature_id: featureId || undefined,
      name,
      description: description || undefined,
      status,
      priority,
      product_area: productArea || undefined,
      category: category || undefined,
      module: module || undefined,
      team: team || undefined,
      owner_name: ownerName || undefined,
      completion_pct: parseInt(completionPct) || 0,
      business_goal: businessGoal || undefined,
    })
      .then((data) => {
        setSuccess(`Feature "${data.feature.name}" created successfully.`);
        window.location.href = `/platform/features/${data.feature.id}`;
      })
      .catch((e) => setError(e.message))
      .finally(() => setSaving(false));
  };

  const handleCsvImport = () => {
    setImporting(true);
    setError(null);
    setImportResult(null);
    platformApi.importCsv()
      .then((result) => setImportResult(result))
      .catch((e) => setError(e.message))
      .finally(() => setImporting(false));
  };

  if (saving) return <LoadingState message="Creating feature…" />;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-200">New Feature</h2>
        <a href="/platform/features" className="text-xs text-slate-500 hover:text-slate-300">← Back to list</a>
      </div>

      {error && <ErrorState message={error} />}
      {success && (
        <Card className="border-green-800 bg-green-900/20">
          <p className="text-sm text-green-400">{success}</p>
        </Card>
      )}

      {/* CSV Import */}
      <Card>
        <h3 className="mb-2 text-sm font-semibold text-slate-300">Bulk Import from CSV</h3>
        <p className="mb-3 text-xs text-slate-500">
          Import features from the seeded <code className="text-slate-400">feature_audit.csv</code> file.
        </p>
        <Button onClick={handleCsvImport} disabled={importing}>
          {importing ? "Importing…" : "Run CSV Import"}
        </Button>
        {importResult && (
          <div className="mt-3 text-xs">
            <p className="text-green-400">Imported: {importResult.imported} · Skipped: {importResult.skipped}</p>
            {importResult.errors.length > 0 && (
              <ul className="mt-2 space-y-1 text-red-400">
                {importResult.errors.map((e, i) => <li key={i}>{e}</li>)}
              </ul>
            )}
          </div>
        )}
      </Card>

      {/* Manual form */}
      <Card>
        <h3 className="mb-4 text-sm font-semibold text-slate-300">Create Manually</h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input label="Feature ID" value={featureId} onChange={setFeatureId} placeholder="auto-generated if blank" />
          <Input label="Name" value={name} onChange={setName} placeholder="Feature name" />
          <div className="sm:col-span-2">
            <label className="mb-1 block text-xs font-medium text-slate-400">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Short description"
              rows={3}
              className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
            />
          </div>
          <Select label="Status" value={status} onChange={setStatus} options={FEATURE_STATUSES.map(s => ({ value: s, label: s.replace(/_/g, " ") }))} />
          <Select label="Priority" value={priority} onChange={setPriority} options={FEATURE_PRIORITIES.map(p => ({ value: p, label: p }))} />
          <Input label="Product Area" value={productArea} onChange={setProductArea} placeholder="e.g. Academics" />
          <Input label="Category" value={category} onChange={setCategory} placeholder="e.g. Core" />
          <Input label="Module" value={module} onChange={setModule} placeholder="e.g. admin" />
          <Input label="Team" value={team} onChange={setTeam} placeholder="e.g. Backend" />
          <Input label="Owner" value={ownerName} onChange={setOwnerName} placeholder="Owner name" />
          <Input label="Completion %" value={completionPct} onChange={setCompletionPct} placeholder="0" />
          <div className="sm:col-span-2">
            <label className="mb-1 block text-xs font-medium text-slate-400">Business Goal</label>
            <textarea
              value={businessGoal}
              onChange={(e) => setBusinessGoal(e.target.value)}
              placeholder="Business goal"
              rows={3}
              className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
            />
          </div>
        </div>
        <div className="mt-4 flex gap-3">
          <Button onClick={handleSave} disabled={!name.trim()}>Create Feature</Button>
          <Button variant="secondary" onClick={() => window.location.href = "/platform/features"}>Cancel</Button>
        </div>
      </Card>
    </div>
  );
}
