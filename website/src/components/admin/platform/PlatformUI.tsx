"use client";

function cn(...classes: (string | false | undefined | null)[]): string {
  return classes.filter(Boolean).join(" ");
}

const STATUS_COLORS: Record<string, string> = {
  planned: "bg-slate-700 text-slate-300",
  in_progress: "bg-blue-900/50 text-blue-300",
  complete: "bg-green-900/50 text-green-300",
  blocked: "bg-red-900/50 text-red-300",
  on_hold: "bg-amber-900/50 text-amber-300",
  deprecated: "bg-slate-800 text-slate-500",
  not_run: "bg-slate-700 text-slate-400",
  passed: "bg-green-900/50 text-green-300",
  failed: "bg-red-900/50 text-red-300",
  need_retest: "bg-amber-900/50 text-amber-300",
  reported: "bg-orange-900/50 text-orange-300",
  triaged: "bg-purple-900/50 text-purple-300",
  assigned: "bg-indigo-900/50 text-indigo-300",
  fixed: "bg-cyan-900/50 text-cyan-300",
  ready_for_qa: "bg-yellow-900/50 text-yellow-300",
  retest: "bg-yellow-900/50 text-yellow-300",
  verified: "bg-green-900/50 text-green-300",
  reopened: "bg-red-900/50 text-red-300",
  closed: "bg-slate-800 text-slate-500",
  duplicate: "bg-slate-800 text-slate-500",
  rejected: "bg-red-900/50 text-red-300",
};

const PRIORITY_COLORS: Record<string, string> = {
  low: "bg-slate-700 text-slate-300",
  medium: "bg-blue-900/50 text-blue-300",
  high: "bg-orange-900/50 text-orange-300",
  critical: "bg-red-900/50 text-red-300",
};

const SEVERITY_COLORS: Record<string, string> = {
  critical: "bg-red-900/60 text-red-300",
  major: "bg-orange-900/60 text-orange-300",
  normal: "bg-yellow-900/50 text-yellow-300",
  minor: "bg-blue-900/50 text-blue-300",
  cosmetic: "bg-slate-700 text-slate-400",
};

export function StatusBadge({ status, className }: { status: string; className?: string }) {
  return (
    <span className={cn("inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium capitalize", STATUS_COLORS[status] ?? "bg-slate-700 text-slate-300", className)}>
      {status.replace(/_/g, " ")}
    </span>
  );
}

export function PriorityBadge({ priority, className }: { priority: string; className?: string }) {
  return (
    <span className={cn("inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium capitalize", PRIORITY_COLORS[priority] ?? "bg-slate-700 text-slate-300", className)}>
      {priority}
    </span>
  );
}

export function SeverityBadge({ severity, className }: { severity?: string; className?: string }) {
  if (!severity) return null;
  return (
    <span className={cn("inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium capitalize", SEVERITY_COLORS[severity] ?? "bg-slate-700 text-slate-300", className)}>
      {severity}
    </span>
  );
}

export function Card({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <div className={cn("rounded-xl border border-slate-800 bg-[#1e293b] p-5", className)}>
      {children}
    </div>
  );
}

export function StatCard({ label, value, sublabel, color }: { label: string; value: string | number; sublabel?: string; color?: string }) {
  return (
    <Card className="flex flex-col gap-1">
      <span className="text-xs font-medium text-slate-500">{label}</span>
      <span className={cn("text-2xl font-bold", color ?? "text-slate-200")}>{value}</span>
      {sublabel && <span className="text-xs text-slate-500">{sublabel}</span>}
    </Card>
  );
}

export function ProgressBar({ value, max = 100, color }: { value: number; max?: number; color?: string }) {
  const pct = Math.min(100, Math.max(0, (value / max) * 100));
  return (
    <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
      <div
        className={cn("h-full rounded-full transition-all", color ?? "bg-indigo-500")}
        style={{ width: `${pct}%` }}
      />
    </div>
  );
}

export function Button({
  children,
  onClick,
  variant = "primary",
  size = "md",
  className,
  disabled,
  type = "button",
}: {
  children: React.ReactNode;
  onClick?: () => void;
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md";
  className?: string;
  disabled?: boolean;
  type?: "button" | "submit";
}) {
  const variants = {
    primary: "bg-indigo-600 text-white hover:bg-indigo-500",
    secondary: "bg-slate-800 text-slate-200 hover:bg-slate-700 border border-slate-700",
    ghost: "text-slate-400 hover:text-slate-200 hover:bg-slate-800",
    danger: "bg-red-600 text-white hover:bg-red-500",
  };
  const sizes = { sm: "px-2.5 py-1 text-xs", md: "px-4 py-2 text-sm" };
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={cn("rounded-lg font-medium transition-colors disabled:opacity-50", variants[variant], sizes[size], className)}
    >
      {children}
    </button>
  );
}

export function Input({ label, value, onChange, placeholder, type = "text", className }: {
  label?: string;
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  type?: string;
  className?: string;
}) {
  return (
    <div className={cn("flex flex-col gap-1", className)}>
      {label && <label className="text-xs font-medium text-slate-400">{label}</label>}
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 placeholder-slate-600 focus:border-indigo-500 focus:outline-none"
      />
    </div>
  );
}

export function Select({ label, value, onChange, options, className }: {
  label?: string;
  value: string;
  onChange: (v: string) => void;
  options: { value: string; label: string }[];
  className?: string;
}) {
  return (
    <div className={cn("flex flex-col gap-1", className)}>
      {label && <label className="text-xs font-medium text-slate-400">{label}</label>}
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 focus:border-indigo-500 focus:outline-none"
      >
        {options.map((o) => (
          <option key={o.value} value={o.value}>{o.label}</option>
        ))}
      </select>
    </div>
  );
}

export function EmptyState({ title, message, action }: { title: string; message?: string; action?: React.ReactNode }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-800">
        <svg viewBox="0 0 24 24" className="h-6 w-6 text-slate-600" fill="currentColor">
          <path d="M19 3H5c-1.11 0-2 .89-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.11-.9-2-2-2zm0 16H5V5h14v14zm-7-2h2V7h-4v2h2z" />
        </svg>
      </div>
      <h3 className="text-sm font-semibold text-slate-300">{title}</h3>
      {message && <p className="max-w-sm text-xs text-slate-500">{message}</p>}
      {action}
    </div>
  );
}

export function LoadingState({ message }: { message?: string }) {
  return (
    <div className="flex items-center justify-center gap-3 py-16">
      <div className="h-5 w-5 animate-spin rounded-full border-2 border-slate-700 border-t-indigo-500" />
      <span className="text-sm text-slate-500">{message ?? "Loading…"}</span>
    </div>
  );
}

export function ErrorState({ message }: { message: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-red-900/30">
        <svg viewBox="0 0 24 24" className="h-5 w-5 text-red-400" fill="currentColor">
          <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z" />
        </svg>
      </div>
      <p className="text-sm text-red-400">{message}</p>
    </div>
  );
}

export function Tabs({ tabs, active, onChange }: {
  tabs: { id: string; label: string; count?: number }[];
  active: string;
  onChange: (id: string) => void;
}) {
  return (
    <div className="flex gap-1 border-b border-slate-800">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => onChange(tab.id)}
          className={cn(
            "flex items-center gap-2 border-b-2 px-4 py-2.5 text-sm font-medium transition-colors",
            active === tab.id
              ? "border-indigo-500 text-indigo-400"
              : "border-transparent text-slate-500 hover:text-slate-300"
          )}
        >
          {tab.label}
          {tab.count != null && (
            <span className="rounded-full bg-slate-800 px-1.5 py-0.5 text-[10px] text-slate-400">
              {tab.count}
            </span>
          )}
        </button>
      ))}
    </div>
  );
}
