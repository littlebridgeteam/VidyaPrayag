"use client";

import { useMemo, useState } from "react";
import { Skeleton } from "./Primitives";

export interface Column<T> {
  key: string;
  header: string;
  /** value used for sorting + default render */
  accessor: (row: T) => string | number;
  /** custom cell render (defaults to accessor) */
  cell?: (row: T) => React.ReactNode;
  sortable?: boolean;
  align?: "left" | "right" | "center";
  className?: string;
  /** hide this column on mobile card layout (e.g. low-priority columns) */
  hideOnMobile?: boolean;
}

/**
 * Generic sortable data table. Sorting is client-side on the accessor.
 * Filtering is the caller's job (passed-in rows are already filtered) so the
 * same table serves server-filtered and client-filtered lists.
 *
 * On mobile (<640px), renders a card-based layout instead of a horizontal-scroll
 * table. Each row becomes a card with label/value pairs. Pass `mobilePrimary` to
 * highlight one column as the card title; pass `mobileSecondary` for a subtitle.
 */
export function DataTable<T>({
  columns,
  rows,
  rowKey,
  loading,
  emptyState,
  onRowClick,
  initialSort,
  mobilePrimary,
  mobileSecondary,
}: {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string;
  loading?: boolean;
  emptyState?: React.ReactNode;
  onRowClick?: (row: T) => void;
  initialSort?: { key: string; dir: "asc" | "desc" };
  /** Column key to use as the card title on mobile (defaults to first column) */
  mobilePrimary?: string;
  /** Column key to use as the card subtitle on mobile */
  mobileSecondary?: string;
}) {
  const [sort, setSort] = useState<{ key: string; dir: "asc" | "desc" } | null>(
    initialSort ?? null
  );

  const sorted = useMemo(() => {
    if (!sort) return rows;
    const col = columns.find((c) => c.key === sort.key);
    if (!col) return rows;
    const copy = [...rows];
    copy.sort((a, b) => {
      const av = col.accessor(a);
      const bv = col.accessor(b);
      if (typeof av === "number" && typeof bv === "number") {
        return sort.dir === "asc" ? av - bv : bv - av;
      }
      const r = String(av).localeCompare(String(bv), undefined, { numeric: true });
      return sort.dir === "asc" ? r : -r;
    });
    return copy;
  }, [rows, sort, columns]);

  function toggleSort(key: string, sortable?: boolean) {
    if (!sortable) return;
    setSort((cur) => {
      if (cur?.key !== key) return { key, dir: "asc" };
      if (cur.dir === "asc") return { key, dir: "desc" };
      return null;
    });
  }

  if (loading) {
    return (
      <div className="space-y-2 p-4">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton key={i} className="h-11" />
        ))}
      </div>
    );
  }

  if (sorted.length === 0) return <>{emptyState}</>;

  const primaryCol = columns.find((c) => c.key === (mobilePrimary ?? columns[0]?.key));
  const secondaryCol = mobileSecondary ? columns.find((c) => c.key === mobileSecondary) : undefined;
  const detailCols = columns.filter((c) => c !== primaryCol && c !== secondaryCol && !c.hideOnMobile);

  return (
    <>
      {/* Desktop / tablet: traditional table */}
      <div className="hidden overflow-x-auto sm:block">
        <table className="w-full min-w-[640px] border-collapse text-left">
          <thead>
            <tr className="border-b border-navy/8">
              {columns.map((c) => {
                const active = sort?.key === c.key;
                return (
                  <th
                    key={c.key}
                    scope="col"
                    className={`px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-ink-3 ${
                      c.align === "right" ? "text-right" : c.align === "center" ? "text-center" : "text-left"
                    } ${c.sortable ? "cursor-pointer select-none hover:text-navy-deep" : ""}`}
                    onClick={() => toggleSort(c.key, c.sortable)}
                    aria-sort={active ? (sort!.dir === "asc" ? "ascending" : "descending") : undefined}
                  >
                    <span className="inline-flex items-center gap-1">
                      {c.header}
                      {c.sortable && (
                        <span className={`text-[9px] ${active ? "text-accent" : "text-ink-placeholder"}`}>
                          {active ? (sort!.dir === "asc" ? "▲" : "▼") : "↕"}
                        </span>
                      )}
                    </span>
                  </th>
                );
              })}
            </tr>
          </thead>
          <tbody>
            {sorted.map((row) => (
              <tr
                key={rowKey(row)}
                onClick={onRowClick ? () => onRowClick(row) : undefined}
                className={`border-b border-navy/5 transition-colors last:border-0 ${
                  onRowClick ? "cursor-pointer hover:bg-accent/[0.04]" : "hover:bg-navy/[0.02]"
                }`}
              >
                {columns.map((c) => (
                  <td
                    key={c.key}
                    className={`px-4 py-3 text-[13.5px] text-ink-2 ${
                      c.align === "right" ? "text-right" : c.align === "center" ? "text-center" : "text-left"
                    } ${c.className ?? ""}`}
                  >
                    {c.cell ? c.cell(row) : c.accessor(row)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Mobile: card-based layout */}
      <div className="space-y-3 p-4 sm:hidden">
        {sorted.map((row) => (
          <div
            key={rowKey(row)}
            onClick={onRowClick ? () => onRowClick(row) : undefined}
            className={`rounded-2xl border border-navy/[0.06] bg-white p-4 ${
              onRowClick ? "cursor-pointer active:bg-accent/[0.04]" : ""
            }`}
          >
            {primaryCol && (
              <div className="text-[15px] font-semibold text-navy-deep">
                {primaryCol.cell ? primaryCol.cell(row) : primaryCol.accessor(row)}
              </div>
            )}
            {secondaryCol && (
              <div className="mt-0.5 text-[13px] text-ink-3">
                {secondaryCol.cell ? secondaryCol.cell(row) : secondaryCol.accessor(row)}
              </div>
            )}
            {detailCols.length > 0 && (
              <dl className="mt-3 grid grid-cols-2 gap-x-4 gap-y-2">
                {detailCols.map((c) => (
                  <div key={c.key} className="col-span-1">
                    <dt className="text-[10px] font-semibold uppercase tracking-wide text-ink-placeholder">
                      {c.header}
                    </dt>
                    <dd className="text-[13px] text-ink-2">
                      {c.cell ? c.cell(row) : c.accessor(row)}
                    </dd>
                  </div>
                ))}
              </dl>
            )}
          </div>
        ))}
      </div>
    </>
  );
}
