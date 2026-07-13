"use client";

import { useState, useEffect, useCallback } from "react";
import { adminApi } from "@/lib/admin/client";
import type { ExportTypeDto, ExportResponse } from "@/lib/admin/types";
import { errorMessage } from "@/lib/errorUtils";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconExport } from "@/components/admin/icons";

const CATEGORY_COLORS: Record<string, "accent" | "success" | "warning" | "neutral"> = {
  Students: "accent",
  Academic: "success",
  Finance: "warning",
  Staff: "neutral",
  Operations: "accent",
};

export default function RecordsExportPage() {
  const [exportTypes, setExportTypes] = useState<ExportTypeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [generating, setGenerating] = useState<string | null>(null);
  const [result, setResult] = useState<ExportResponse | null>(null);
  const [filter, setFilter] = useState<string>("");

  const load = useCallback(async () => {
    try {
      const res = await adminApi.exportTypes();
      setExportTypes(res.exports ?? []);
    } catch (e) {
      setError(`Failed to load export types: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleGenerate = useCallback(async (type: ExportTypeDto, format: string) => {
    setGenerating(`${type.type}_${format}`);
    setError(null);
    setResult(null);
    try {
      const res = await adminApi.generateExport({ type: type.type, format });
      setResult(res);
    } catch (e) {
      setError(`Export failed: ${errorMessage(e)}`);
    } finally {
      setGenerating(null);
    }
  }, []);

  const categories = Array.from(new Set(exportTypes.map(t => t.category)));
  const filtered = filter ? exportTypes.filter(t => t.category === filter) : exportTypes;

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconExport />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Records &amp; Exports</h1>
            <p className="text-[13px] text-ink-3">Generate branded PDF and CSV reports for any data category.</p>
          </div>
        </div>
      </FadeIn>

      {result && (
        <FadeIn delay={0.05}>
          <Card className="border-accent/20 bg-accent/[0.03]">
            <div className="flex items-center justify-between gap-4 px-5 py-4">
              <div>
                <p className="text-[14px] font-semibold text-navy-deep">
                  {result.message ?? "Export generated successfully"}
                </p>
                {result.file_name && (
                  <p className="text-[12px] text-ink-3">{result.file_name} · {(result.file_size / 1024).toFixed(1)} KB</p>
                )}
              </div>
              {result.download_url && (
                <a
                  href={result.download_url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="rounded-xl bg-accent px-4 py-2 text-[13px] font-semibold text-white transition-colors hover:bg-accent-deep"
                >
                  Download
                </a>
              )}
            </div>
          </Card>
        </FadeIn>
      )}

      {error && (
        <FadeIn delay={0.05}>
          <Card className="border-red-200 bg-red-50">
            <div className="px-5 py-4 text-[13px] text-red-700">{error}</div>
          </Card>
        </FadeIn>
      )}

      <FadeIn delay={0.05}>
        <div className="flex flex-wrap items-center gap-1.5">
          <button
            type="button"
            onClick={() => setFilter("")}
            className={`shrink-0 rounded-full px-3 py-1.5 text-[12.5px] font-semibold transition-colors ${
              !filter ? "bg-navy-deep text-white" : "bg-navy/6 text-ink-2 hover:bg-navy/10"
            }`}
          >
            All
          </button>
          {categories.map(cat => (
            <button
              key={cat}
              type="button"
              onClick={() => setFilter(cat)}
              className={`shrink-0 rounded-full px-3 py-1.5 text-[12.5px] font-semibold transition-colors ${
                filter === cat ? "bg-navy-deep text-white" : "bg-navy/6 text-ink-2 hover:bg-navy/10"
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </FadeIn>

      <FadeIn delay={0.1}>
        <Card>
          <CardHeader title="Available Exports" subtitle={`${filtered.length} report${filtered.length !== 1 ? "s" : ""}`} />
          {loading ? (
            <Skeleton className="h-40" />
          ) : filtered.length === 0 ? (
            <EmptyState title="No exports available" hint="Export types will appear here once configured." icon={<IconExport />} />
          ) : (
            <div className="divide-y divide-navy/[0.04]">
              {filtered.map((exp) => (
                <div key={exp.type} className="flex items-center justify-between gap-4 px-5 py-4 hover:bg-navy/[0.02] transition-colors">
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <span className="text-[14px] font-semibold text-navy-deep">{exp.label}</span>
                      <Badge tone={CATEGORY_COLORS[exp.category] ?? "neutral"}>{exp.category}</Badge>
                      {exp.admin_only && <Badge tone="warning">Admin</Badge>}
                    </div>
                    {exp.filters.length > 0 && (
                      <p className="mt-0.5 text-[12px] text-ink-3">
                        Filters: {exp.filters.join(", ")}
                      </p>
                    )}
                  </div>
                  <div className="flex shrink-0 items-center gap-2">
                    {exp.formats.map(fmt => (
                      <AdminButton
                        key={fmt}
                        onClick={() => handleGenerate(exp, fmt)}
                        disabled={generating === `${exp.type}_${fmt}`}
                      >
                        {generating === `${exp.type}_${fmt}` ? "Generating…" : fmt.toUpperCase()}
                      </AdminButton>
                    ))}
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
