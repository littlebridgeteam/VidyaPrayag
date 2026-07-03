"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconLibrary } from "@/components/admin/icons";

interface BookDto {
  id: string;
  isbn: string | null;
  title: string;
  author: string | null;
  category: string | null;
  totalCopies: number;
  availableCopies: number;
  isArchived: boolean;
}

export default function LibraryPage() {
  const [books, setBooks] = useState<BookDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<BookDto[] | { data: BookDto[] }>("/api/v1/school/library/books?limit=100");
      setBooks(Array.isArray(res) ? res : (res as { data: BookDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load library: ${(e as Error).message}`);
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
            <IconLibrary />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Library</h1>
            <p className="text-[13px] text-ink-3">Book inventory, issues, and categories.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="Book Inventory" subtitle={`${books.length} book${books.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconLibrary />} /> : books.length === 0 ? <EmptyState title="No books" hint="Library inventory will appear here." icon={<IconLibrary />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Title</th>
                    <th className="px-5 py-3 font-semibold">Author</th>
                    <th className="px-5 py-3 font-semibold">Category</th>
                    <th className="px-5 py-3 font-semibold">Available</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {books.map((b) => (
                    <tr key={b.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{b.title}</td>
                      <td className="px-5 py-3 text-ink-2">{b.author ?? "—"}</td>
                      <td className="px-5 py-3 text-ink-3">{b.category ?? "—"}</td>
                      <td className="px-5 py-3">
                        <Badge tone={b.availableCopies > 0 ? "success" : "danger"}>
                          {b.availableCopies}/{b.totalCopies}
                        </Badge>
                      </td>
                      <td className="px-5 py-3">
                        <AdminButton variant="danger" onClick={async () => { await authRequest(`/api/v1/school/library/books/${b.id}`, { method: "DELETE" }); setBooks(prev => prev.filter(x => x.id !== b.id)); }}>Delete</AdminButton>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
