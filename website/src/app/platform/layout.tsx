import type { Metadata } from "next";
import { AdminAuthProvider } from "@/lib/admin/session";
import { PlatformShell } from "@/components/admin/platform/PlatformShell";

export const metadata: Metadata = {
  title: "Platform — Feature & QA",
  robots: { index: false, follow: false },
};

export default function PlatformLayout({ children }: { children: React.ReactNode }) {
  return (
    <AdminAuthProvider>
      <PlatformShell>{children}</PlatformShell>
    </AdminAuthProvider>
  );
}
