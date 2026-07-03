"use client";

import { use } from "react";
import { useRouter } from "next/navigation";

export default function PewsStudentRedirectPage({ params }: { params: Promise<{ code: string }> }) {
  const { code } = use(params);
  const router = useRouter();
  router.replace(`/admin/early-warning?student=${encodeURIComponent(code)}`);
  return (
    <div className="flex h-40 items-center justify-center text-ink-2">
      Redirecting to Early Warning…
    </div>
  );
}
