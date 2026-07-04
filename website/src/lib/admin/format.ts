/** Small formatting helpers for the admin surface. Pure, no side effects. */

export function initials(name: string): string {
  return name
    .trim()
    .split(/\s+/)
    .map((p) => p[0]?.toUpperCase() ?? "")
    .slice(0, 2)
    .join("");
}

const CURRENCY_SYMBOL: Record<string, string> = {
  INR: "₹",
  USD: "$",
  EUR: "€",
  GBP: "£",
};

const LOCALE = typeof navigator !== "undefined" ? navigator.language || "en-IN" : "en-IN";

export function money(amount: number | undefined | null, currency = "INR"): string {
  const n = amount ?? 0;
  const sym = CURRENCY_SYMBOL[currency] ?? `${currency} `;
  return `${sym}${n.toLocaleString(LOCALE, { maximumFractionDigits: 0 })}`;
}

export function compactMoney(amount: number | undefined | null, currency = "INR"): string {
  const n = amount ?? 0;
  const sym = CURRENCY_SYMBOL[currency] ?? `${currency} `;
  if (n >= 1_00_00_000) return `${sym}${(n / 1_00_00_000).toFixed(2)} Cr`;
  if (n >= 1_00_000) return `${sym}${(n / 1_00_000).toFixed(2)} L`;
  if (n >= 1_000) return `${sym}${(n / 1_000).toFixed(1)}k`;
  return `${sym}${n.toLocaleString(LOCALE)}`;
}

export function pct(n: number): string {
  return `${Math.round(n)}%`;
}

/** A short, stable avatar background from a name (deterministic). */
export function avatarHue(name: string): string {
  let h = 0;
  for (let i = 0; i < name.length; i++) h = (h * 31 + name.charCodeAt(i)) % 360;
  return `hsl(${h} 42% 92%)`;
}
export function avatarInkHue(name: string): string {
  let h = 0;
  for (let i = 0; i < name.length; i++) h = (h * 31 + name.charCodeAt(i)) % 360;
  return `hsl(${h} 38% 34%)`;
}

/** Parse a server "delta" string like "+4.2%" → numeric sign for colouring. */
export function deltaSign(s: string | undefined | null): "up" | "down" | "flat" {
  if (!s) return "flat";
  if (s.trim().startsWith("-")) return "down";
  if (/[1-9]/.test(s)) return "up";
  return "flat";
}

const MONTH_SHORT = [
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
];

function parseIsoDate(iso: string): [number, number, number] | null {
  if (!iso || iso.length < 10) return null;
  const y = parseInt(iso.substring(0, 4), 10);
  const m = parseInt(iso.substring(5, 7), 10);
  const d = parseInt(iso.substring(8, 10), 10);
  if (isNaN(y) || isNaN(m) || isNaN(d) || m < 1 || m > 12 || d < 1 || d > 31) return null;
  return [y, m, d];
}

/** "2026-06-25" → "25 Jun 2026"; blank-safe. */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return "";
  const parsed = parseIsoDate(iso);
  if (!parsed) return iso;
  const [y, m, d] = parsed;
  const mon = MONTH_SHORT[m - 1] ?? iso;
  return `${d} ${mon} ${y}`;
}

/** "2026-06-25" → "25 Jun" (short, no year). */
export function formatDateShort(iso: string | null | undefined): string {
  if (!iso) return "";
  const parsed = parseIsoDate(iso);
  if (!parsed) return iso;
  const [, m, d] = parsed;
  const mon = MONTH_SHORT[m - 1] ?? iso;
  return `${d} ${mon}`;
}

/** "2026-06-25T14:30:00" → "25 Jun 2026, 2:30 PM"; blank-safe. */
export function formatDateTime(iso: string | null | undefined): string {
  if (!iso) return "";
  const datePart = iso.split("T")[0]?.slice(0, 10) ?? "";
  const timePart = iso.split("T")[1] ?? "";
  const dateStr = formatDate(datePart);
  if (!timePart || timePart.length < 5) return dateStr;
  const h = parseInt(timePart.substring(0, 2), 10);
  const mi = parseInt(timePart.substring(3, 5), 10);
  if (isNaN(h) || isNaN(mi)) return dateStr;
  const period = h < 12 ? "AM" : "PM";
  const h12 = h % 12 === 0 ? 12 : h % 12;
  return `${dateStr}, ${h12}:${mi.toString().padStart(2, "0")} ${period}`;
}

/** "2026-06-25T14:30:00" → "2:30 PM"; blank-safe. */
export function formatTime(iso: string | null | undefined): string {
  if (!iso) return "";
  const timePart = iso.split("T")[1] ?? "";
  if (timePart.length < 5) return "";
  const h = parseInt(timePart.substring(0, 2), 10);
  const mi = parseInt(timePart.substring(3, 5), 10);
  if (isNaN(h) || isNaN(mi)) return "";
  const period = h < 12 ? "AM" : "PM";
  const h12 = h % 12 === 0 ? 12 : h % 12;
  return `${h12}:${mi.toString().padStart(2, "0")} ${period}`;
}

/** "2026-06-25" → "Jun 25, 2026" (display format with month first). */
export function formatDateDisplay(iso: string | null | undefined): string {
  if (!iso) return "";
  const parsed = parseIsoDate(iso);
  if (!parsed) return iso;
  const [y, m, d] = parsed;
  const mon = MONTH_SHORT[m - 1] ?? iso;
  return `${mon} ${d}, ${y}`;
}
