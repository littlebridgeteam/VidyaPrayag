"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconMessage } from "@/components/admin/icons";

interface ThreadDto {
  id: string;
  title: string;
  last_message: string;
  last_message_at: string;
  unread_count: number;
  peer_name: string;
  peer_role: string;
}

interface ThreadsResponse {
  threads: ThreadDto[];
}

export default function MessagesPage() {
  const [data, setData] = useState<ThreadDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<ThreadsResponse>("/api/v1/school/messages/threads");
      setData(res.threads ?? []);
    } catch (e) {
      setError(`Failed to load messages: ${(e as Error).message}`);
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
            <IconMessage />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Messages</h1>
            <p className="text-[13px] text-ink-3">School communication threads with parents and staff.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="All Threads" subtitle={`${data.length} conversation${data.length !== 1 ? "s" : ""}`} />
          {loading ? (
            <Skeleton className="h-40" />
          ) : error ? (
            <EmptyState title="Error" hint={error} icon={<IconMessage />} />
          ) : data.length === 0 ? (
            <EmptyState title="No messages" hint="Conversation threads will appear here." icon={<IconMessage />} />
          ) : (
            <div className="divide-y divide-navy/[0.04]">
              {data.map((t) => (
                <div key={t.id} className="flex items-center gap-4 px-5 py-4 hover:bg-navy/[0.02] transition-colors">
                  <div className="min-w-0 flex-1">
                    <p className="text-[14px] font-semibold text-navy-deep truncate">{t.title}</p>
                    <p className="text-[12px] text-ink-3 truncate">{t.last_message}</p>
                  </div>
                  <div className="text-right shrink-0">
                    <p className="text-[11px] text-ink-3">{new Date(t.last_message_at).toLocaleDateString()}</p>
                    {t.unread_count > 0 && (
                      <Badge tone="danger" className="mt-1">{t.unread_count} unread</Badge>
                    )}
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
