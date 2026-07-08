"use client";

import { useEffect, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { readSession } from "@/lib/admin/session";
import { loadAuth } from "@/lib/auth";

export function OnboardingGate({ children }: { children: ReactNode }) {
  const router = useRouter();

  useEffect(() => {
    const adminSession = readSession();
    const wizardAuth = loadAuth();
    if (adminSession?.token || wizardAuth?.token) {
      router.replace("/admin/dashboard");
    }
  }, [router]);

  return <>{children}</>;
}
