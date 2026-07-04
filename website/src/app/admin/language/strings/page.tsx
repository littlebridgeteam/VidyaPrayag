"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function StringsRedirectPage() {
  const router = useRouter();
  useEffect(() => {
    router.replace("/admin/language");
  }, [router]);
  return null;
}
