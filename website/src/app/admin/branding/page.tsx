"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconBranding } from "@/components/admin/icons";

interface BrandingDto {
  school_id: string;
  primary_color: string;
  logo_url: string;
  subdomain: string;
  tagline: string;
}

export default function BrandingPage() {
  const [data, setData] = useState<BrandingDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<BrandingDto>("/api/v1/school/branding");
      setData(res);
    } catch (e) {
      setError(`Failed to load branding: ${(e as Error).message}`);
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
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconBranding />} /> : !data ? <EmptyState title="No branding configured" hint="Set up your school's brand identity." icon={<IconBranding />} /> : (
            <div className="space-y-4 px-5 py-4">
              <div className="flex items-center gap-4">
                {data.logo_url && <img src={data.logo_url} alt="Logo" className="h-16 w-16 rounded-2xl object-cover" />}
                <div>
                  <p className="text-[14px] font-semibold text-navy-deep">Logo</p>
                  <p className="text-[12px] text-ink-3">{data.logo_url ? "Custom logo uploaded" : "No logo set"}</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="h-10 w-10 rounded-xl" style={{ backgroundColor: data.primary_color || "#6C5CE0" }} />
                <div>
                  <p className="text-[14px] font-semibold text-navy-deep">Primary Color</p>
                  <p className="text-[12px] text-ink-3">{data.primary_color || "Default"}</p>
                </div>
              </div>
              <div>
                <p className="text-[14px] font-semibold text-navy-deep">Subdomain</p>
                <Badge tone={data.subdomain ? "success" : "neutral"}>{data.subdomain ? `${data.subdomain}.enrollplus.in` : "Not configured"}</Badge>
              </div>
              {data.tagline && (
                <div>
                  <p className="text-[14px] font-semibold text-navy-deep">Tagline</p>
                  <p className="text-[13px] text-ink-2">{data.tagline}</p>
                </div>
              )}
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
