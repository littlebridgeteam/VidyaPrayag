"use client";

import { useEffect, useState, useCallback, useMemo } from "react";
import { adminApi } from "@/lib/admin/client";
import { ApiError } from "@/lib/api";
import type {
  LanguageAdoptionDto,
  LanguageDistributionDto,
  ServerStringsResponse,
  BulkUpsertServerStringItem,
  StringOverrideHistoryEntry,
} from "@/lib/admin/types";
import {
  Card,
  CardHeader,
  Badge,
  ProgressBar,
  FadeIn,
  Skeleton,
  EmptyState,
} from "@/components/admin/Primitives";
import { AdminButton, Modal } from "@/components/admin/Toolbar";

const LANG_LABELS: Record<string, string> = {
  en: "English",
  hi: "हिन्दी",
  bn: "বাংলা",
  ta: "தமிழ்",
  te: "తెలుగు",
  mr: "मराठी",
  gu: "ગુજરాતી",
  kn: "ಕನ್ನಡ",
  ml: "മലയാളം",
  pa: "ਪੰਜਾਬੀ",
};

const LANGS = ["en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa"];

type Tab = "overview" | "translations" | "history";

export default function LanguageDashboardPage() {
  const [tab, setTab] = useState<Tab>("overview");

  return (
    <div className="max-w-6xl space-y-6">
      <FadeIn>
        <div>
          <h1 className="text-[22px] font-bold text-navy-deep">Translation Management</h1>
          <p className="mt-1 text-[13px] text-ink-3">
            Browse, search, compare, and update server-side translations across all supported languages.
          </p>
        </div>
      </FadeIn>

      <div className="flex items-center gap-1.5 rounded-xl bg-navy/[0.04] p-1">
        {(["overview", "translations", "history"] as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`rounded-lg px-4 py-2 text-[13px] font-semibold capitalize transition-colors ${
              tab === t
                ? "bg-white text-navy-deep shadow-sm"
                : "text-ink-3 hover:text-ink"
            }`}
          >
            {t === "overview" ? "Adoption Overview" : t === "translations" ? "Translations" : "Audit Log"}
          </button>
        ))}
      </div>

      {tab === "overview" && <OverviewTab />}
      {tab === "translations" && <TranslationsTab />}
      {tab === "history" && <HistoryTab />}
    </div>
  );
}

// ═══════════════════════════════════════════════════════════════════════
// Tab 1: Adoption Overview
// ═══════════════════════════════════════════════════════════════════════

function OverviewTab() {
  const [data, setData] = useState<LanguageAdoptionDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const result = await adminApi.languageAdoption();
        if (!cancelled) setData(result);
      } catch (e) {
        if (!cancelled) setErr(e instanceof ApiError ? e.message : "Failed to load language data.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, []);

  if (loading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-64" />
      </div>
    );
  }

  if (err) {
    return (
      <Card>
        <div className="p-6 text-center">
          <p className="text-[14px] font-medium text-danger">{err}</p>
        </div>
      </Card>
    );
  }

  if (!data) return null;

  return (
    <div className="space-y-6">
      <FadeIn delay={0.04}>
        <Card>
          <CardHeader title="Overall distribution" subtitle={`${data.total_users} users across all roles.`} />
          <div className="p-5">
            <DistributionBar items={data.by_language} />
          </div>
        </Card>
      </FadeIn>

      {Object.entries(data.by_role).map(([role, dist], i) => (
        <FadeIn key={role} delay={0.06 + i * 0.03}>
          <Card>
            <CardHeader
              title={`${role.charAt(0).toUpperCase() + role.slice(1)}s`}
              subtitle={`${dist.reduce((s, d) => s + d.count, 0)} users`}
            />
            <div className="p-5">
              <DistributionBar items={dist} />
            </div>
          </Card>
        </FadeIn>
      ))}
    </div>
  );
}

function DistributionBar({ items }: { items: LanguageDistributionDto[] }) {
  const total = items.reduce((s, d) => s + d.count, 0);
  if (total === 0) return <p className="text-[13px] text-ink-3">No data yet.</p>;

  return (
    <div className="space-y-3">
      {items.map((item) => {
        const pct = Math.round(item.percentage);
        return (
          <div key={item.language} className="flex items-center gap-3">
            <span className="w-24 shrink-0 text-[13px] font-medium text-ink">
              {LANG_LABELS[item.language] ?? item.language}
            </span>
            <div className="h-7 flex-1 overflow-hidden rounded-lg bg-navy/6">
              <div
                className="flex h-full items-center justify-end rounded-lg bg-accent/80 px-2 text-[11px] font-semibold text-white transition-all duration-500"
                style={{ width: `${Math.max(pct, 3)}%` }}
              >
                {item.count}
              </div>
            </div>
            <span className="w-12 shrink-0 text-right text-[12px] text-ink-3">{pct}%</span>
          </div>
        );
      })}
    </div>
  );
}

// ═══════════════════════════════════════════════════════════════════════
// Tab 2: Translations Manager
// ═══════════════════════════════════════════════════════════════════════

type EditDraft = Record<string, string>; // key -> value for a specific lang

function TranslationsTab() {
  const [data, setData] = useState<ServerStringsResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const [langFilter, setLangFilter] = useState<string>("all");
  const [moduleFilter, setModuleFilter] = useState<string>("all");
  const [showMissingOnly, setShowMissingOnly] = useState(false);
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const [drafts, setDrafts] = useState<Record<string, EditDraft>>({});
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState<string | null>(null);
  const [bulkResults, setBulkResults] = useState<{ updated: number; errors: Array<{ key: string; lang: string; error: string }> } | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setErr(null);
    try {
      const result = await adminApi.serverStrings();
      setData(result);
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to load server strings.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const modules = useMemo(() => {
    if (!data) return [];
    const set = new Set<string>();
    data.strings.forEach((s) => {
      const prefix = s.key.split(".")[0];
      if (prefix) set.add(prefix);
    });
    return Array.from(set).sort();
  }, [data]);

  const filtered = useMemo(() => {
    if (!data) return [];
    return data.strings.filter((s) => {
      if (moduleFilter !== "all" && !s.key.startsWith(moduleFilter + ".")) return false;
      if (search) {
        const q = search.toLowerCase();
        const keyMatch = s.key.toLowerCase().includes(q);
        const valueMatch = Object.entries(s.translations).some(
          ([, t]) => t.value.toLowerCase().includes(q)
        );
        if (!keyMatch && !valueMatch) return false;
      }
      if (showMissingOnly) {
        const hasMissing = LANGS.some((lang) => {
          const t = s.translations[lang];
          return !t || t.value === s.key;
        });
        if (!hasMissing) return false;
      }
      return true;
    });
  }, [data, search, moduleFilter, showMissingOnly]);

  const completionStats = useMemo(() => {
    if (!data) return null;
    const stats: Record<string, { total: number; translated: number; overridden: number }> = {};
    LANGS.forEach((lang) => {
      let translated = 0;
      let overridden = 0;
      data.strings.forEach((s) => {
        const t = s.translations[lang];
        if (t) {
          if (t.value !== s.key) translated++;
          if (t.is_override) overridden++;
        }
      });
      stats[lang] = { total: data.total_keys, translated, overridden };
    });
    return stats;
  }, [data]);

  const hasDrafts = Object.keys(drafts).length > 0;

  useEffect(() => {
    if (!hasDrafts) return;
    const handler = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = "";
    };
    window.addEventListener("beforeunload", handler);
    return () => window.removeEventListener("beforeunload", handler);
  }, [hasDrafts]);

  function updateDraft(key: string, lang: string, value: string) {
    setDrafts((prev) => {
      const next = { ...prev };
      if (!next[key]) next[key] = {};
      next[key] = { ...next[key], [lang]: value };
      return next;
    });
  }

  function clearDrafts() {
    setDrafts({});
    setEditingKey(null);
  }

  async function saveAllDrafts() {
    const items: BulkUpsertServerStringItem[] = [];
    Object.entries(drafts).forEach(([key, langDrafts]) => {
      Object.entries(langDrafts).forEach(([lang, value]) => {
        items.push({ key, lang, value });
      });
    });
    if (items.length === 0) return;

    setBusy(true);
    setMsg(null);
    try {
      const result = await adminApi.bulkUpsertServerStrings({ items });
      setBulkResults(result);
      if (result.errors.length === 0) {
        setMsg(`Saved ${result.updated} translation${result.updated !== 1 ? "s" : ""}.`);
        clearDrafts();
      } else {
        setMsg(`Saved ${result.updated}, ${result.errors.length} error${result.errors.length !== 1 ? "s" : ""}.`);
        // Remove successful drafts, keep failed ones
        const failedKeys = new Set(result.errors.map((e) => `${e.key}:${e.lang}`));
        setDrafts((prev) => {
          const next: Record<string, EditDraft> = {};
          Object.entries(prev).forEach(([key, langDrafts]) => {
            const filtered: EditDraft = {};
            Object.entries(langDrafts).forEach(([lang, value]) => {
              if (failedKeys.has(`${key}:${lang}`)) filtered[lang] = value;
            });
            if (Object.keys(filtered).length > 0) next[key] = filtered;
          });
          return next;
        });
      }
      await load();
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Bulk save failed.");
    } finally {
      setBusy(false);
    }
  }

  async function deleteOverride(key: string, lang: string) {
    if (!confirm(`Delete override for "${key}" (${LANG_LABELS[lang] ?? lang})? The default translation will be restored.`)) return;
    try {
      await adminApi.deleteServerString(key, lang);
      await load();
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Could not delete override.");
    }
  }

  if (loading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-56" />
        <Skeleton className="h-96" />
      </div>
    );
  }

  if (err && !data) {
    return (
      <Card>
        <div className="p-6 text-center">
          <p className="text-[14px] font-medium text-danger">{err}</p>
          <AdminButton onClick={load} className="mt-3">Retry</AdminButton>
        </div>
      </Card>
    );
  }

  if (!data) return null;

  return (
    <div className="space-y-6">
      {err && (
        <div className="rounded-xl bg-danger/8 px-4 py-3 text-[13px] font-medium text-danger">
          {err}
          <button onClick={() => setErr(null)} className="ml-3 underline">Dismiss</button>
        </div>
      )}

      {msg && (
        <div className="rounded-xl bg-success/8 px-4 py-3 text-[13px] font-medium text-success">
          {msg}
          <button onClick={() => setMsg(null)} className="ml-3 underline">Dismiss</button>
        </div>
      )}

      {/* Completion Stats */}
      {completionStats && (
        <FadeIn delay={0.02}>
          <Card>
            <CardHeader title="Translation Completion" subtitle="Coverage across all supported languages." />
            <div className="grid grid-cols-2 gap-4 p-5 md:grid-cols-5">
              {LANGS.map((lang) => {
                const s = completionStats[lang];
                const pct = s.total > 0 ? Math.round((s.translated / s.total) * 100) : 0;
                return (
                  <div key={lang} className="space-y-1.5">
                    <div className="flex items-center justify-between">
                      <span className="text-[12px] font-semibold text-ink">{LANG_LABELS[lang] ?? lang}</span>
                      <span className="text-[11px] text-ink-3">{pct}%</span>
                    </div>
                    <ProgressBar value={pct} tone={pct === 100 ? "success" : "accent"} />
                    {s.overridden > 0 && (
                      <p className="text-[10px] text-ink-3">{s.overridden} override{s.overridden !== 1 ? "s" : ""}</p>
                    )}
                  </div>
                );
              })}
            </div>
          </Card>
        </FadeIn>
      )}

      {/* Filters */}
      <FadeIn delay={0.04}>
        <Card>
          <CardHeader
            title="Filters"
            action={
              <div className="flex items-center gap-2">
                <AdminButton onClick={load} disabled={busy} variant="ghost">Refresh</AdminButton>
                {hasDrafts && (
                  <>
                    <AdminButton onClick={clearDrafts} disabled={busy} variant="ghost">Discard</AdminButton>
                    <AdminButton onClick={saveAllDrafts} disabled={busy}>
                      {busy ? "Saving…" : `Save ${Object.values(drafts).reduce((s, d) => s + Object.keys(d).length, 0)} change${Object.values(drafts).reduce((s, d) => s + Object.keys(d).length, 0) !== 1 ? "s" : ""}`}
                    </AdminButton>
                  </>
                )}
              </div>
            }
          />
          <div className="flex flex-wrap items-center gap-3 p-4">
            <input
              type="text"
              placeholder="Search by key or translation text…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="flex-1 rounded-xl border border-navy/12 bg-white/80 px-3.5 py-2 text-[13px] text-ink outline-none focus:border-accent"
            />
            <select
              value={langFilter}
              onChange={(e) => setLangFilter(e.target.value)}
              className="rounded-xl border border-navy/12 bg-white/80 px-3 py-2 text-[13px] text-ink outline-none focus:border-accent"
            >
              <option value="all">All languages</option>
              {LANGS.map((l) => <option key={l} value={l}>{LANG_LABELS[l] ?? l}</option>)}
            </select>
            <select
              value={moduleFilter}
              onChange={(e) => setModuleFilter(e.target.value)}
              className="rounded-xl border border-navy/12 bg-white/80 px-3 py-2 text-[13px] text-ink outline-none focus:border-accent"
            >
              <option value="all">All modules</option>
              {modules.map((m) => <option key={m} value={m}>{m}</option>)}
            </select>
            <label className="flex items-center gap-2 text-[12.5px] font-medium text-ink-2">
              <input
                type="checkbox"
                checked={showMissingOnly}
                onChange={(e) => setShowMissingOnly(e.target.checked)}
                className="h-4 w-4 rounded accent-accent"
              />
              Missing only
            </label>
          </div>
        </Card>
      </FadeIn>

      {/* Translation Table */}
      <FadeIn delay={0.06}>
        <Card>
          <CardHeader
            title={`${filtered.length} of ${data.total_keys} string keys`}
            subtitle={hasDrafts ? "You have unsaved changes. Click Save to commit." : undefined}
          />
          <div className="overflow-x-auto">
            <table className="w-full text-[13px]">
              <thead>
                <tr className="border-b border-navy/8 text-left text-[11px] font-semibold uppercase tracking-wide text-ink-3">
                  <th className="px-5 py-3">Key</th>
                  {(langFilter === "all" ? LANGS : [langFilter]).map((lang) => (
                    <th key={lang} className="px-3 py-3 min-w-[200px]">
                      <div className="flex items-center gap-1.5">
                        <span>{LANG_LABELS[lang] ?? lang}</span>
                        {completionStats && completionStats[lang].overridden > 0 && (
                          <Badge tone="accent" className="text-[9px] px-1.5 py-0.5">
                            {completionStats[lang].overridden}
                          </Badge>
                        )}
                      </div>
                    </th>
                  ))}
                  <th className="px-3 py-3 w-24">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-navy/4">
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={12} className="px-5 py-10">
                      <EmptyState title="No translations found" hint="Try adjusting your search or filters." />
                    </td>
                  </tr>
                )}
                {filtered.map((entry) => {
                  const visibleLangs = langFilter === "all" ? LANGS : [langFilter];
                  const isEditing = editingKey === entry.key;
                  const hasDraft = !!drafts[entry.key];
                  return (
                    <tr key={entry.key} className={`transition-colors ${hasDraft ? "bg-accent/[0.03]" : ""}`}>
                      <td className="px-5 py-3 align-top">
                        <div className="flex items-start gap-2">
                          <div>
                            <span className="font-mono text-[11.5px] font-semibold text-navy-deep">{entry.key}</span>
                            {hasDraft && <Badge tone="warning" className="ml-1.5 text-[9px]">Edited</Badge>}
                          </div>
                        </div>
                      </td>
                      {visibleLangs.map((lang) => {
                        const t = entry.translations[lang];
                        const draftValue = drafts[entry.key]?.[lang];
                        const isMissing = !t || t.value === entry.key;
                        return (
                          <td key={lang} className="px-3 py-3 align-top">
                            {isEditing || draftValue !== undefined ? (
                              <textarea
                                value={draftValue !== undefined ? draftValue : t?.value ?? ""}
                                onChange={(e) => updateDraft(entry.key, lang, e.target.value)}
                                rows={2}
                                className="w-full rounded-lg border border-accent/40 bg-white px-2.5 py-1.5 text-[12.5px] text-ink outline-none focus:border-accent"
                                placeholder="Enter translation…"
                              />
                            ) : (
                              <div
                                className="cursor-text rounded-lg px-2.5 py-1.5 text-[12.5px] text-ink-2 hover:bg-navy/[0.03]"
                                onClick={() => setEditingKey(entry.key)}
                              >
                                <div className="flex items-center gap-1.5">
                                  {isMissing && <Badge tone="danger" className="text-[9px] px-1.5 py-0.5">Missing</Badge>}
                                  {t?.is_override && <Badge tone="accent" className="text-[9px] px-1.5 py-0.5">Override</Badge>}
                                </div>
                                <p className={`mt-0.5 ${isMissing ? "italic text-ink-3" : ""}`}>
                                  {t?.value ?? "(not translated)"}
                                </p>
                                {t?.updated_at && (
                                  <p className="mt-0.5 text-[10px] text-ink-3">
                                    {new Date(t.updated_at).toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" })}
                                  </p>
                                )}
                              </div>
                            )}
                          </td>
                        );
                      })}
                      <td className="px-3 py-3 align-top">
                        <div className="flex flex-col gap-1">
                          <button
                            onClick={() => setEditingKey(isEditing ? null : entry.key)}
                            className="rounded-lg px-2.5 py-1 text-[11px] font-medium text-accent hover:bg-accent/8"
                          >
                            {isEditing ? "Done" : "Edit"}
                          </button>
                          {entry.translations[langFilter !== "all" ? langFilter : "en"]?.is_override && (
                            <button
                              onClick={() => deleteOverride(entry.key, langFilter !== "all" ? langFilter : "en")}
                              className="rounded-lg px-2.5 py-1 text-[11px] font-medium text-danger hover:bg-danger/8"
                            >
                              Reset
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>
      </FadeIn>

      {/* Bulk Results Modal */}
      <Modal
        open={!!bulkResults}
        onClose={() => setBulkResults(null)}
        title="Bulk Save Results"
        size="md"
        footer={<AdminButton onClick={() => setBulkResults(null)}>Close</AdminButton>}
      >
        {bulkResults && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="rounded-xl bg-success/10 px-4 py-2">
                <span className="text-[20px] font-bold text-success">{bulkResults.updated}</span>
                <span className="ml-1.5 text-[13px] text-success">saved</span>
              </div>
              {bulkResults.errors.length > 0 && (
                <div className="rounded-xl bg-danger/10 px-4 py-2">
                  <span className="text-[20px] font-bold text-danger">{bulkResults.errors.length}</span>
                  <span className="ml-1.5 text-[13px] text-danger">errors</span>
                </div>
              )}
            </div>
            {bulkResults.errors.length > 0 && (
              <div className="space-y-2">
                <p className="text-[13px] font-semibold text-ink-2">Errors:</p>
                {bulkResults.errors.map((e, i) => (
                  <div key={i} className="rounded-lg bg-danger/5 px-3 py-2 text-[12px]">
                    <span className="font-mono font-semibold text-navy-deep">{e.key}</span>
                    <span className="text-ink-3"> ({e.lang})</span>
                    <span className="text-danger"> — {e.error}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}

// ═══════════════════════════════════════════════════════════════════════
// Tab 3: Audit Log / History
// ═══════════════════════════════════════════════════════════════════════

function HistoryTab() {
  const [history, setHistory] = useState<StringOverrideHistoryEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [keyFilter, setKeyFilter] = useState("");
  const [langFilter, setLangFilter] = useState("all");

  const load = useCallback(async () => {
    setLoading(true);
    setErr(null);
    try {
      const result = await adminApi.stringOverrideHistory({
        key: keyFilter || undefined,
        lang: langFilter !== "all" ? langFilter : undefined,
        limit: 200,
      });
      setHistory(result.history);
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to load audit log.");
    } finally {
      setLoading(false);
    }
  }, [keyFilter, langFilter]);

  useEffect(() => { load(); }, [load]);

  if (loading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-96" />
      </div>
    );
  }

  if (err) {
    return (
      <Card>
        <div className="p-6 text-center">
          <p className="text-[14px] font-medium text-danger">{err}</p>
          <AdminButton onClick={load} className="mt-3">Retry</AdminButton>
        </div>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <FadeIn delay={0.02}>
        <Card>
          <CardHeader
            title="Audit Log"
            subtitle="Version history of all translation changes."
            action={<AdminButton onClick={load} variant="ghost">Refresh</AdminButton>}
          />
          <div className="flex flex-wrap items-center gap-3 p-4">
            <input
              type="text"
              placeholder="Filter by string key…"
              value={keyFilter}
              onChange={(e) => setKeyFilter(e.target.value)}
              className="flex-1 rounded-xl border border-navy/12 bg-white/80 px-3.5 py-2 text-[13px] text-ink outline-none focus:border-accent"
            />
            <select
              value={langFilter}
              onChange={(e) => setLangFilter(e.target.value)}
              className="rounded-xl border border-navy/12 bg-white/80 px-3 py-2 text-[13px] text-ink outline-none focus:border-accent"
            >
              <option value="all">All languages</option>
              {LANGS.map((l) => <option key={l} value={l}>{LANG_LABELS[l] ?? l}</option>)}
            </select>
          </div>
        </Card>
      </FadeIn>

      <FadeIn delay={0.04}>
        <Card>
          <CardHeader title={`${history.length} entries`} />
          {history.length === 0 ? (
            <EmptyState title="No changes recorded" hint="Translation edits will appear here with full audit trail." />
          ) : (
            <div className="divide-y divide-navy/4">
              {history.map((entry) => (
                <div key={entry.id} className="px-5 py-4">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2">
                        <span className="font-mono text-[12px] font-semibold text-navy-deep">{entry.string_key}</span>
                        <Badge tone="neutral" className="text-[9px]">{LANG_LABELS[entry.lang] ?? entry.lang}</Badge>
                        <Badge tone={entry.action === "delete" ? "danger" : "success"} className="text-[9px]">
                          {entry.action}
                        </Badge>
                      </div>
                      <div className="mt-1.5 space-y-1">
                        {entry.old_value && (
                          <p className="text-[12px] text-ink-3 line-through">{entry.old_value}</p>
                        )}
                        <p className="text-[12.5px] text-ink">{entry.new_value || "(deleted)"}</p>
                      </div>
                    </div>
                    <div className="shrink-0 text-right">
                      <p className="text-[11px] font-medium text-ink-2">{entry.changed_by_name ?? "Unknown"}</p>
                      <p className="text-[10px] text-ink-3">
                        {new Date(entry.changed_at).toLocaleString(undefined, {
                          month: "short", day: "numeric", year: "numeric",
                          hour: "2-digit", minute: "2-digit",
                        })}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
