"use client";

import { useEffect } from "react";
import { useRouter, useParams } from "next/navigation";

export default function PewsStudentRedirectPage() {
  const router = useRouter();
  const params = useParams<{ code: string }>();
  const code = params?.code;

  useEffect(() => {
    if (code) {
      router.replace(`/admin/early-warning?student=${encodeURIComponent(code)}`);
    }
  }, [code, router]);

  return (
    <div className="flex h-40 items-center justify-center text-ink-2">
      Redirecting to Early Warning…
    </div>
  );
}
