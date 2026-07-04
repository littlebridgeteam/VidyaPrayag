"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";

export default function LeaveRequestsRedirectPage() {
  const router = useRouter();
  const params = useSearchParams();
  const qs = params?.toString();

  useEffect(() => {
    router.replace(`/admin/leave${qs ? `?${qs}` : ""}`);
  }, [router, qs]);

  return (
    <div className="flex h-40 items-center justify-center text-ink-2">
      Redirecting to Leave…
    </div>
  );
}
