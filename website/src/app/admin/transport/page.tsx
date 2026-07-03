"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconTransport } from "@/components/admin/icons";

interface RouteDto {
  id: string;
  name: string;
  vehicle_number: string;
  driver_name: string;
  stops_count: number;
  is_active: boolean;
}
interface VehicleDto {
  id: string;
  bus_number: string;
  capacity: number;
  driver_name: string;
  is_active: boolean;
}

export default function TransportPage() {
  const [routes, setRoutes] = useState<RouteDto[]>([]);
  const [vehicles, setVehicles] = useState<VehicleDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const [r, v] = await Promise.all([
        authRequest<{ data: RouteDto[] } | RouteDto[]>("/api/v1/school/transport/routes"),
        authRequest<{ data: VehicleDto[] } | VehicleDto[]>("/api/v1/school/transport/vehicles"),
      ]);
      setRoutes(Array.isArray(r) ? r : (r as { data: RouteDto[] }).data ?? []);
      setVehicles(Array.isArray(v) ? v : (v as { data: VehicleDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load transport data: ${(e as Error).message}`);
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
            <IconTransport />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Transport Management</h1>
            <p className="text-[13px] text-ink-3">Routes, vehicles, stops, and student assignments.</p>
          </div>
        </div>
      </FadeIn>

      <div className="grid gap-6 lg:grid-cols-2">
        <FadeIn delay={0.05}>
          <Card>
            <CardHeader title="Routes" subtitle={`${routes.length} route${routes.length !== 1 ? "s" : ""}`} />
            {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconTransport />} /> : routes.length === 0 ? <EmptyState title="No routes" hint="Transport routes will appear here." icon={<IconTransport />} /> : (
              <div className="divide-y divide-navy/[0.04]">
                {routes.map((r) => (
                  <div key={r.id} className="flex items-center justify-between px-5 py-3">
                    <div>
                      <p className="text-[14px] font-semibold text-navy-deep">{r.name}</p>
                      <p className="text-[12px] text-ink-3">{r.stops_count} stops · {r.driver_name}</p>
                    </div>
                    <Badge tone={r.is_active ? "success" : "neutral"}>{r.is_active ? "Active" : "Inactive"}</Badge>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>

        <FadeIn delay={0.1}>
          <Card>
            <CardHeader title="Vehicles" subtitle={`${vehicles.length} vehicle${vehicles.length !== 1 ? "s" : ""}`} />
            {loading ? <Skeleton className="h-32" /> : error ? <EmptyState title="Error" hint={error} icon={<IconTransport />} /> : vehicles.length === 0 ? <EmptyState title="No vehicles" hint="Transport vehicles will appear here." icon={<IconTransport />} /> : (
              <div className="divide-y divide-navy/[0.04]">
                {vehicles.map((v) => (
                  <div key={v.id} className="flex items-center justify-between px-5 py-3">
                    <div>
                      <p className="text-[14px] font-semibold text-navy-deep">{v.bus_number}</p>
                      <p className="text-[12px] text-ink-3">Capacity: {v.capacity} · {v.driver_name}</p>
                    </div>
                    <Badge tone={v.is_active ? "success" : "neutral"}>{v.is_active ? "Active" : "Inactive"}</Badge>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>
      </div>
    </div>
  );
}
