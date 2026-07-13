"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconIdCard } from "@/components/admin/icons";

interface TemplateDto {
  id: string;
  name: string;
  roleType: string;
  isActive: boolean;
  createdAt: string;
}

export default function IdCardsPage() {
  const [templates, setTemplates] = useState<TemplateDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<TemplateDto[]>("/api/v1/school/id-cards/templates");
      setTemplates(Array.isArray(res) ? res : []);
    } catch (e) {
      setError(`Failed to load ID card templates: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconIdCard />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">ID Cards</h1>
            <p className="text-[13px] text-ink-3">ID card templates and generation for students and staff.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Templates" subtitle={`${templates.length} template${templates.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconIdCard />} /> : templates.length === 0 ? <EmptyState title="No templates" hint="ID card templates will appear here." icon={<IconIdCard />} /> : (
            <div className="divide-y divide-navy/[0.04]">
              {templates.map((t) => (
                <div key={t.id} className="flex items-center justify-between px-5 py-3">
                  <div>
                    <p className="text-[14px] font-semibold text-navy-deep">{t.name}</p>
                    <p className="text-[12px] text-ink-3 capitalize">{t.roleType} · Created {new Date(t.createdAt).toLocaleDateString()}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge tone={t.isActive ? "success" : "neutral"}>{t.isActive ? "Active" : "Inactive"}</Badge>
                    <AdminButton variant="danger" onClick={async () => { await authRequest(`/api/v1/school/id-cards/templates/${t.id}`, { method: "DELETE" }); setTemplates(prev => prev.filter(x => x.id !== t.id)); }}>Delete</AdminButton>
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
