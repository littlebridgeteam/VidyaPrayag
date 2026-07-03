"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge, Avatar } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconMessage, IconClose, IconCheck } from "@/components/admin/icons";

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

interface MessageDto {
  id: string;
  body: string;
  isMine: boolean;
  senderId: string | null;
  createdAt: string;
  time: string;
  seq: number;
  status: string | null;
}

interface ThreadMessagesResponse {
  threadId: string;
  senderName: string;
  messages: MessageDto[];
  hasMore: boolean;
  totalCount: number;
}

interface RecipientDto {
  id: string;
  name: string;
  role: string;
  subtitle: string;
  imageUrl: string | null;
  childName: string | null;
}

interface RecipientsResponse {
  recipients: RecipientDto[];
}

export default function MessagesPage() {
  const [threads, setThreads] = useState<ThreadDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedThread, setSelectedThread] = useState<ThreadDto | null>(null);
  const [messages, setMessages] = useState<MessageDto[]>([]);
  const [msgLoading, setMsgLoading] = useState(false);
  const [msgError, setMsgError] = useState<string | null>(null);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const [showCompose, setShowCompose] = useState(false);
  const [recipients, setRecipients] = useState<RecipientDto[]>([]);
  const [recipientSearch, setRecipientSearch] = useState("");
  const [recipLoading, setRecipLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<ThreadsResponse>("/api/v1/school/messages/threads");
      setThreads(res.threads ?? []);
    } catch (e) {
      setError(`Failed to load messages: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const openThread = useCallback(async (thread: ThreadDto) => {
    setSelectedThread(thread);
    setMsgLoading(true);
    setMsgError(null);
    setMessages([]);
    try {
      const res = await authRequest<ThreadMessagesResponse>(`/api/v1/school/messages/threads/${thread.id}/messages?limit=50`);
      setMessages(res.messages ?? []);
      await authRequest(`/api/v1/school/messages/threads/${thread.id}/read`, { method: "POST" });
      setThreads(prev => prev.map(t => t.id === thread.id ? { ...t, unread_count: 0 } : t));
    } catch (e) {
      setMsgError(`Failed to load conversation: ${(e as Error).message}`);
    } finally {
      setMsgLoading(false);
    }
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = useCallback(async () => {
    if (!draft.trim() || sending) return;
    setSending(true);
    try {
      const body = draft.trim();
      setDraft("");
      if (selectedThread) {
        const res = await authRequest<MessageDto>("/api/v1/school/messages", {
          method: "POST",
          body: { thread_id: selectedThread.id, body },
        });
        setMessages(prev => [...prev, res]);
        setThreads(prev => prev.map(t => t.id === selectedThread.id ? { ...t, last_message: body, last_message_at: new Date().toISOString() } : t));
      }
    } catch (e) {
      setMsgError(`Failed to send: ${(e as Error).message}`);
      setDraft(draft);
    } finally {
      setSending(false);
    }
  }, [draft, sending, selectedThread]);

  const startConversation = useCallback(async (recipient: RecipientDto) => {
    setShowCompose(false);
    setMsgLoading(true);
    setMsgError(null);
    setMessages([]);
    try {
      const res = await authRequest<MessageDto>("/api/v1/school/messages", {
        method: "POST",
        body: { recipient_user_id: recipient.id, body: `Hello ${recipient.name}, this is the school office.` },
      });
      await load();
      const newThread: ThreadDto = {
        id: res.id,
        title: recipient.name,
        last_message: res.body,
        last_message_at: res.createdAt,
        unread_count: 0,
        peer_name: recipient.name,
        peer_role: recipient.role,
      };
      setSelectedThread(newThread);
      setMessages([res]);
    } catch (e) {
      setMsgError(`Failed to start conversation: ${(e as Error).message}`);
    } finally {
      setMsgLoading(false);
    }
  }, [load]);

  const openCompose = useCallback(async () => {
    setShowCompose(true);
    setRecipientSearch("");
    if (recipients.length === 0) {
      setRecipLoading(true);
      try {
        const res = await authRequest<RecipientsResponse>("/api/v1/school/messages/recipients");
        setRecipients(res.recipients ?? []);
      } catch (e) {
        setMsgError(`Failed to load recipients: ${(e as Error).message}`);
      } finally {
        setRecipLoading(false);
      }
    }
  }, [recipients]);

  const filteredRecipients = recipients.filter(r =>
    !recipientSearch || r.name.toLowerCase().includes(recipientSearch.toLowerCase()) || r.subtitle.toLowerCase().includes(recipientSearch.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
              <IconMessage />
            </div>
            <div>
              <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Messages</h1>
              <p className="text-[13px] text-ink-3">School communication threads with parents and staff.</p>
            </div>
          </div>
          <AdminButton onClick={openCompose}>New Conversation</AdminButton>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-[320px_1fr]">
          <Card className="flex h-[600px] flex-col">
            <CardHeader title="Threads" subtitle={`${threads.length} conversation${threads.length !== 1 ? "s" : ""}`} />
            <div className="flex-1 overflow-y-auto">
              {loading ? <div className="space-y-2 p-3">{Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-14" />)}</div>
              : error ? <EmptyState title="Error" hint={error} icon={<IconMessage />} />
              : threads.length === 0 ? <EmptyState title="No messages" hint="Start a new conversation." icon={<IconMessage />} />
              : <div className="divide-y divide-navy/[0.04]">
                  {threads.map((t) => (
                    <button key={t.id} onClick={() => openThread(t)} className={`flex w-full items-center gap-3 px-4 py-3 text-left transition-colors hover:bg-navy/[0.02] ${selectedThread?.id === t.id ? "bg-accent/5" : ""}`}>
                      <Avatar name={t.peer_name || t.title} size={36} />
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center justify-between gap-2">
                          <p className="text-[13px] font-semibold text-navy-deep truncate">{t.peer_name || t.title}</p>
                          <span className="text-[10px] text-ink-3 shrink-0">{new Date(t.last_message_at).toLocaleDateString()}</span>
                        </div>
                        <p className="text-[12px] text-ink-3 truncate">{t.last_message}</p>
                      </div>
                      {t.unread_count > 0 && <Badge tone="danger">{t.unread_count}</Badge>}
                    </button>
                  ))}
                </div>}
            </div>
          </Card>

          <Card className="flex h-[600px] flex-col">
            {selectedThread ? (
              <>
                <div className="flex items-center gap-3 border-b border-navy/[0.06] px-5 py-3">
                  <Avatar name={selectedThread.peer_name || selectedThread.title} size={36} />
                  <div className="min-0 flex-1">
                    <p className="text-[14px] font-semibold text-navy-deep">{selectedThread.peer_name || selectedThread.title}</p>
                    <p className="text-[12px] text-ink-3 capitalize">{selectedThread.peer_role}</p>
                  </div>
                </div>
                <div className="flex-1 overflow-y-auto px-5 py-4 space-y-3">
                  {msgLoading ? <div className="space-y-2">{Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-12" />)}</div>
                  : msgError ? <EmptyState title="Error" hint={msgError} icon={<IconMessage />} />
                  : messages.map((m) => (
                    <div key={m.id} className={`flex ${m.isMine ? "justify-end" : "justify-start"}`}>
                      <div className={`max-w-[70%] rounded-2xl px-4 py-2.5 text-[13px] ${m.isMine ? "bg-accent text-white" : "bg-navy/[0.06] text-navy-deep"}`}>
                        <p>{m.body}</p>
                        <p className={`mt-1 text-[10px] ${m.isMine ? "text-white/60" : "text-ink-3"}`}>{m.time}</p>
                      </div>
                    </div>
                  ))}
                  <div ref={messagesEndRef} />
                </div>
                <div className="border-t border-navy/[0.06] p-3">
                  <div className="flex items-center gap-2">
                    <input
                      type="text"
                      value={draft}
                      onChange={(e) => setDraft(e.target.value)}
                      onKeyDown={(e) => { if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); sendMessage(); } }}
                      placeholder="Type a message…"
                      className="flex-1 rounded-xl border border-navy/12 bg-white/80 px-4 py-2.5 text-[14px] text-ink outline-none focus:border-accent"
                    />
                    <AdminButton onClick={sendMessage} disabled={!draft.trim() || sending}>Send</AdminButton>
                  </div>
                </div>
              </>
            ) : (
              <div className="flex h-full items-center justify-center">
                <EmptyState title="Select a conversation" hint="Choose a thread or start a new one." icon={<IconMessage />} />
              </div>
            )}
          </Card>
        </div>
      </FadeIn>

      {showCompose && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-navy-deep/40 backdrop-blur-[2px]" onClick={() => setShowCompose(false)}>
          <div className="relative z-10 w-full max-w-md overflow-hidden rounded-2xl border border-navy/10 bg-white shadow-xl" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between border-b border-navy/8 px-5 py-4">
              <h2 className="text-[16px] font-bold text-navy-deep">New Conversation</h2>
              <button onClick={() => setShowCompose(false)} className="rounded-lg p-1.5 text-ink-3 hover:bg-navy/6"><IconClose width={18} height={18} /></button>
            </div>
            <div className="max-h-[60vh] overflow-y-auto">
              <div className="border-b border-navy/8 px-5 py-3">
                <input type="search" value={recipientSearch} onChange={(e) => setRecipientSearch(e.target.value)} placeholder="Search recipients…" className="w-full rounded-xl border border-navy/12 bg-white/80 px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
              </div>
              {recipLoading ? <div className="space-y-2 p-4">{Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-12" />)}</div>
              : filteredRecipients.length === 0 ? <p className="px-5 py-8 text-center text-[13px] text-ink-3">No recipients found.</p>
              : <div className="divide-y divide-navy/[0.04]">
                  {filteredRecipients.map((r) => (
                    <button key={r.id} onClick={() => startConversation(r)} className="flex w-full items-center gap-3 px-5 py-3 text-left hover:bg-navy/[0.02] transition-colors">
                      <Avatar name={r.name} size={36} />
                      <div className="min-w-0 flex-1">
                        <p className="text-[14px] font-semibold text-navy-deep">{r.name}</p>
                        <p className="text-[12px] text-ink-3">{r.subtitle}</p>
                      </div>
                      <span className="text-[10px] font-semibold uppercase text-ink-3">{r.role}</span>
                    </button>
                  ))}
                </div>}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
