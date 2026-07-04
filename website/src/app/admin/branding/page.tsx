"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton, Modal } from "@/components/admin/Toolbar";
import { IconBranding } from "@/components/admin/icons";

interface BrandingDto {
  schoolId: string;
  schoolName: string;
  logoUrl: string | null;
  primaryColor: string;
  secondaryColor: string;
  accentColor: string;
  customSubdomain: string | null;
  isCustomized: boolean;
}

export default function BrandingPage() {
  const [data, setData] = useState<BrandingDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [showEdit, setShowEdit] = useState(false);
  const [editForm, setEditForm] = useState({ primaryColor: "", logoUrl: "" });
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<BrandingDto>("/api/v1/school/branding");
      setData(res);
      setEditForm({ primaryColor: res.primaryColor ?? "", logoUrl: res.logoUrl ?? "" });
    } catch (e) {
      setError(`Failed to load branding: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const saveBranding = useCallback(async () => {
    setSaving(true);
    setError(null);
    try {
      const body: Record<string, string> = {};
      if (editForm.primaryColor) body.primaryColor = editForm.primaryColor;
      if (editForm.logoUrl) body.logoUrl = editForm.logoUrl;
      const res = await authRequest<BrandingDto>("/api/v1/school/branding", { method: "PATCH", body });
      setData(res);
      setShowEdit(false);
    } catch (e) {
      setError(`Failed to update branding: ${errorMessage(e)}`);
    } finally {
      setSaving(false);
    }
  }, [editForm]);

  const resetBranding = useCallback(async () => {
    setSaving(true);
    setError(null);
    try {
      const res = await authRequest<BrandingDto>("/api/v1/school/branding/reset", { method: "POST" });
      setData(res);
      setEditForm({ primaryColor: res.primaryColor ?? "", logoUrl: res.logoUrl ?? "" });
    } catch (e) {
      setError(`Failed to reset branding: ${errorMessage(e)}`);
    } finally {
      setSaving(false);
    }
  }, []);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconBranding />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Branding Kit</h1>
            <p className="text-[13px] text-ink-3">School branding, logo, colors, and subdomain configuration.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Brand Identity" />
          {data && (
            <div className="flex justify-end gap-2 px-5 pt-3">
              <AdminButton variant="ghost" onClick={resetBranding} disabled={saving}>Reset to Default</AdminButton>
              <AdminButton onClick={() => setShowEdit(true)}>Edit Branding</AdminButton>
            </div>
          )}
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconBranding />} /> : !data ? <EmptyState title="No branding configured" hint="Set up your school's brand identity." icon={<IconBranding />} /> : (
            <div className="space-y-4 px-5 py-4">
              <div className="flex items-center gap-4">
                {data.logoUrl && <img src={data.logoUrl} alt="Logo" className="h-16 w-16 rounded-2xl object-cover" />}
                <div>
                  <p className="text-[14px] font-semibold text-navy-deep">Logo</p>
                  <p className="text-[12px] text-ink-3">{data.logoUrl ? "Custom logo uploaded" : "No logo set"}</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="h-10 w-10 rounded-xl" style={{ backgroundColor: data.primaryColor || "#6C5CE0" }} />
                <div>
                  <p className="text-[14px] font-semibold text-navy-deep">Primary Color</p>
                  <p className="text-[12px] text-ink-3">{data.primaryColor || "Default"}</p>
                </div>
              </div>
              <div>
                <p className="text-[14px] font-semibold text-navy-deep">Subdomain</p>
                <Badge tone={data.customSubdomain ? "success" : "neutral"}>{data.customSubdomain ? `${data.customSubdomain}.enrollplus.in` : "Not configured"}</Badge>
              </div>
              <div>
                <p className="text-[14px] font-semibold text-navy-deep">School Name</p>
                <p className="text-[13px] text-ink-2">{data.schoolName}</p>
              </div>
            </div>
          )}
        </Card>
      </FadeIn>

      <Modal open={showEdit} onClose={() => setShowEdit(false)} title="Edit Branding" description="Update school colors and logo URL."
        footer={
          <>
            <AdminButton variant="ghost" onClick={() => setShowEdit(false)}>Cancel</AdminButton>
            <AdminButton onClick={saveBranding} disabled={saving}>{saving ? "Saving…" : "Save"}</AdminButton>
          </>
        }
      >
        <div className="space-y-4">
          {error && <p className="text-[13px] font-medium text-danger">{error}</p>}
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Primary Color (hex)</label>
            <input type="text" value={editForm.primaryColor} onChange={(e) => setEditForm(p => ({ ...p, primaryColor: e.target.value }))} placeholder="#2563EB" className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Logo URL</label>
            <input type="text" value={editForm.logoUrl} onChange={(e) => setEditForm(p => ({ ...p, logoUrl: e.target.value }))} placeholder="https://…" className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
        </div>
      </Modal>
    </div>
  );
}
