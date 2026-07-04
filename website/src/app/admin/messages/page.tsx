"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge, Avatar } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconMessage, IconClose } from "@/components/admin/icons";

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
  is_mine: boolean;
  sender_id: string | null;
  created_at: string;
  time: string;
  seq: number;
  status: string | null;
}

interface ThreadMessagesResponse {
  thread_id: string;
  sender_name: string;
  messages: MessageDto[];
  has_more: boolean;
  total_count: number;
}

interface RecipientDto {
  id: string;
  name: string;
  role: string;
  subtitle: string;
  image_url: string | null;
  child_name: string | null;
}

interface RecipientsResponse {
  recipients: RecipientDto[];
}

function ReadReceipt({ status }: { status: string | null }) {
  if (!status) return null;
  if (status === "READ") return <span className="text-[10px] text-sky-300">✓✓</span>;
  if (status === "DELIVERED") return <span className="text-[10px] text-white/50">✓✓</span>;
  return <span className="text-[10px] text-white/50">✓</span>;
}

function ChatBubble({ m }: { m: MessageDto }) {
  const mine = m.is_mine;
  return (
    <div className={`flex ${mine ? "justify-end" : "justify-start"}`}>
      <div
        className={`max-w-[72%] rounded-2xl px-4 py-2.5 text-[13.5px] leading-relaxed shadow-sm transition-all duration-200 ${
          mine
            ? "rounded-br-md bg-gradient-to-br from-accent to-accent-deep text-white"
            : "rounded-bl-md bg-white text-navy-deep ring-1 ring-inset ring-navy/[0.06]"
        }`}
      >
        <p className="whitespace-pre-wrap break-words">{m.body}</p>
        <div className={`mt-1 flex items-center gap-1.5 ${mine ? "justify-end" : "justify-start"}`}>
          <span className={`text-[10px] ${mine ? "text-white/50" : "text-ink-3"}`}>{m.time}</span>
          {mine && <ReadReceipt status={m.status} />}
        </div>
      </div>
    </div>
  );
}

function ThreadRow({
  thread,
  active,
  onClick,
}: {
  thread: ThreadDto;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className={`flex w-full items-center gap-3 px-4 py-3.5 text-left transition-all duration-200 ${
        active
          ? "bg-gradient-to-r from-accent/8 to-transparent"
          : "hover:bg-navy/[0.025]"
      }`}
    >
      <div className="relative shrink-0">
        <Avatar name={thread.peer_name || thread.title} size={40} />
        {thread.unread_count > 0 && (
          <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-danger px-1 text-[9px] font-bold text-white">
            {thread.unread_count}
          </span>
        )}
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex items-center justify-between gap-2">
          <p className={`truncate text-[13.5px] ${thread.unread_count > 0 ? "font-bold text-navy-deep" : "font-semibold text-navy-deep"}`}>
            {thread.peer_name || thread.title}
          </p>
          <span className="shrink-0 text-[10px] text-ink-3">
            {new Date(thread.last_message_at).toLocaleDateString([], { month: "short", day: "numeric" })}
          </span>
        </div>
        <p className={`mt-0.5 truncate text-[12px] ${thread.unread_count > 0 ? "font-medium text-ink-2" : "text-ink-3"}`}>
          {thread.last_message}
        </p>
      </div>
    </button>
  );
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
  const scrollRef = useRef<HTMLDivElement>(null);

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
      setThreads(prev => prev.map(t => t.id === thread.id ? { ...t, unread_count: 0 } : t));
      authRequest(`/api/v1/school/messages/threads/${thread.id}/read`, { method: "POST" }).catch(() => {});
    } catch (e) {
      setMsgError(`Failed to load conversation: ${(e as Error).message}`);
    } finally {
      setMsgLoading(false);
    }
  }, []);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, msgLoading]);

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
        setMessages(prev => [...prev, { ...res, is_mine: true }]);
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
        last_message_at: res.created_at,
        unread_count: 0,
        peer_name: recipient.name,
        peer_role: recipient.role,
      };
      setSelectedThread(newThread);
      setMessages([{ ...res, is_mine: true }]);
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

  const totalUnread = threads.reduce((sum, t) => sum + (t.unread_count || 0), 0);

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
              <p className="text-[13px] text-ink-3">
                School communication threads with parents and staff.
                {totalUnread > 0 && <span className="ml-1.5 font-semibold text-danger">{totalUnread} unread</span>}
              </p>
            </div>
          </div>
          <AdminButton onClick={openCompose}>New Conversation</AdminButton>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <div className="grid grid-cols-1 gap-0 overflow-hidden rounded-2xl border border-navy/[0.06] bg-white/80 shadow-sm lg:grid-cols-[340px_1fr]">
          {/* Thread list */}
          <div className="flex h-[640px] flex-col border-r border-navy/[0.06]">
            <div className="flex items-center justify-between border-b border-navy/[0.06] px-5 py-4">
              <div>
                <p className="text-[15px] font-bold text-navy-deep">Inbox</p>
                <p className="text-[11px] text-ink-3">{threads.length} conversation{threads.length !== 1 ? "s" : ""}</p>
              </div>
            </div>
            <div className="flex-1 overflow-y-auto [scrollbar-width:thin]">
              {loading ? (
                <div className="space-y-2 p-3">{Array.from({ length: 5 }).map((_, i) => <Skeleton key={i} className="h-16" />)}</div>
              ) : error ? (
                <EmptyState title="Error" hint={error} icon={<IconMessage />} />
              ) : threads.length === 0 ? (
                <EmptyState title="No messages" hint="Start a new conversation to see threads here." icon={<IconMessage />} />
              ) : (
                <div className="divide-y divide-navy/[0.03]">
                  {threads.map((t) => (
                    <ThreadRow
                      key={t.id}
                      thread={t}
                      active={selectedThread?.id === t.id}
                      onClick={() => openThread(t)}
                    />
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Chat panel */}
          <div className="flex h-[640px] flex-col bg-gradient-to-b from-navy/[0.015] to-transparent">
            {selectedThread ? (
              <>
                {/* Header */}
                <div className="flex items-center gap-3 border-b border-navy/[0.06] bg-white/60 px-5 py-3.5 backdrop-blur-sm">
                  <Avatar name={selectedThread.peer_name || selectedThread.title} size={40} />
                  <div className="min-w-0 flex-1">
                    <p className="text-[14.5px] font-bold text-navy-deep">{selectedThread.peer_name || selectedThread.title}</p>
                    <p className="text-[11.5px] capitalize text-ink-3">
                      <span className="inline-flex h-1.5 w-1.5 rounded-full bg-green-400 mr-1.5 align-middle" />
                      {selectedThread.peer_role}
                    </p>
                  </div>
                </div>

                {/* Messages */}
                <div ref={scrollRef} className="flex-1 overflow-y-auto px-5 py-5 space-y-3 [scrollbar-width:thin]">
                  {msgLoading ? (
                    <div className="space-y-3">
                      <div className="flex justify-start"><Skeleton className="h-12 w-48 rounded-2xl rounded-bl-md" /></div>
                      <div className="flex justify-end"><Skeleton className="h-12 w-36 rounded-2xl rounded-br-md" /></div>
                      <div className="flex justify-start"><Skeleton className="h-12 w-52 rounded-2xl rounded-bl-md" /></div>
                    </div>
                  ) : msgError ? (
                    <EmptyState title="Error" hint={msgError} icon={<IconMessage />} />
                  ) : messages.length === 0 ? (
                    <div className="flex h-full items-center justify-center">
                      <p className="text-[13px] text-ink-3">No messages yet. Say hello 👋</p>
                    </div>
                  ) : (
                    <>
                      {messages.map((m, i) => {
                        const prev = messages[i - 1];
                        const showDateSep = !prev || new Date(prev.created_at).toDateString() !== new Date(m.created_at).toDateString();
                        return (
                          <div key={m.id}>
                            {showDateSep && (
                              <div className="my-4 flex items-center justify-center">
                                <span className="rounded-full bg-navy/[0.04] px-3 py-1 text-[10.5px] font-medium text-ink-3">
                                  {new Date(m.created_at).toLocaleDateString([], { weekday: "long", month: "short", day: "numeric" })}
                                </span>
                              </div>
                            )}
                            <ChatBubble m={m} />
                          </div>
                        );
                      })}
                    </>
                  )}
                  <div ref={messagesEndRef} />
                </div>

                {/* Input */}
                <div className="border-t border-navy/[0.06] bg-white/60 px-4 py-3 backdrop-blur-sm">
                  <div className="flex items-end gap-2.5">
                    <textarea
                      value={draft}
                      onChange={(e) => setDraft(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                          e.preventDefault();
                          sendMessage();
                        }
                      }}
                      rows={1}
                      placeholder="Type a message…"
                      className="flex-1 resize-none rounded-2xl border border-navy/10 bg-white px-4 py-3 text-[14px] text-ink outline-none transition-colors placeholder:text-ink-3/60 focus:border-accent focus:ring-2 focus:ring-accent/15"
                      style={{ maxHeight: "120px" }}
                    />
                    <button
                      type="button"
                      onClick={sendMessage}
                      disabled={!draft.trim() || sending}
                      className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-accent to-accent-deep text-white shadow-md transition-all duration-200 hover:-translate-y-0.5 hover:shadow-lg disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:translate-y-0"
                      aria-label="Send message"
                    >
                      {sending ? (
                        <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="3" />
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v2a6 6 0 00-6 6H4z" />
                        </svg>
                      ) : (
                        <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" />
                        </svg>
                      )}
                    </button>
                  </div>
                </div>
              </>
            ) : (
              <div className="flex h-full items-center justify-center">
                <div className="text-center">
                  <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-3xl bg-accent/8">
                    <IconMessage />
                  </div>
                  <p className="text-[15px] font-semibold text-navy-deep">Select a conversation</p>
                  <p className="mt-1 text-[13px] text-ink-3">Choose a thread from the left or start a new one.</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </FadeIn>

      {/* Compose modal */}
      {showCompose && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-navy-deep/40 backdrop-blur-[3px]" onClick={() => setShowCompose(false)}>
          <div className="relative z-10 w-full max-w-md overflow-hidden rounded-2xl border border-navy/10 bg-white shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between border-b border-navy/8 px-5 py-4">
              <h2 className="text-[16px] font-bold text-navy-deep">New Conversation</h2>
              <button onClick={() => setShowCompose(false)} className="rounded-lg p-1.5 text-ink-3 transition-colors hover:bg-navy/6"><IconClose width={18} height={18} /></button>
            </div>
            <div className="max-h-[60vh] overflow-y-auto [scrollbar-width:thin]">
              <div className="border-b border-navy/8 px-5 py-3">
                <input
                  type="search"
                  value={recipientSearch}
                  onChange={(e) => setRecipientSearch(e.target.value)}
                  placeholder="Search recipients…"
                  className="w-full rounded-xl border border-navy/10 bg-white px-4 py-2.5 text-[14px] outline-none transition-colors focus:border-accent focus:ring-2 focus:ring-accent/10"
                />
              </div>
              {recipLoading ? (
                <div className="space-y-2 p-4">{Array.from({ length: 5 }).map((_, i) => <Skeleton key={i} className="h-14" />)}</div>
              ) : filteredRecipients.length === 0 ? (
                <p className="px-5 py-10 text-center text-[13px] text-ink-3">No recipients found.</p>
              ) : (
                <div className="divide-y divide-navy/[0.03]">
                  {filteredRecipients.map((r) => (
                    <button
                      key={r.id}
                      onClick={() => startConversation(r)}
                      className="flex w-full items-center gap-3 px-5 py-3.5 text-left transition-colors hover:bg-accent/[0.04]"
                    >
                      <Avatar name={r.name} size={40} />
                      <div className="min-w-0 flex-1">
                        <p className="text-[14px] font-semibold text-navy-deep">{r.name}</p>
                        <p className="text-[12px] text-ink-3">{r.subtitle}</p>
                      </div>
                      <span className="rounded-full bg-navy/[0.04] px-2.5 py-1 text-[10px] font-semibold uppercase text-ink-3">{r.role}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
