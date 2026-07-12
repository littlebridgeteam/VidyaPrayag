"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { errorMessage } from "@/lib/errorUtils";
import {
  Card,
  CardHeader,
  EmptyState,
  FadeIn,
  Skeleton,
  Badge,
} from "@/components/admin/Primitives";
import { AdminButton } from "@/components/admin/Toolbar";
import { IconTrophy, IconCheck, IconClose } from "@/components/admin/icons";

interface GamificationFlags {
  isGamificationEnabled: boolean;
  gamificationLeaderboards: boolean;
  gamificationRewards: boolean;
  gamificationHouses: boolean;
  gamificationQuests: boolean;
  gamificationMentor: boolean;
  gamificationShoutouts: boolean;
  gamificationEvents: boolean;
  gamificationClassGoals: boolean;
  gamificationCombos: boolean;
  gamificationBoosts: boolean;
}

interface BadgeDef {
  id: string;
  code: string;
  name: string;
  description: string;
  iconName: string;
  category: string;
  rarity: string;
  xpRequirement: number;
  isSeasonal: boolean;
}

interface LevelDef {
  level: number;
  xpRequired: number;
  title: string;
  iconName: string;
}

interface HouseDto {
  id: string;
  name: string;
  iconName: string;
  color: string;
  totalPoints: number;
  memberCount: number;
}

interface LeaderboardEntry {
  rank: number;
  studentId: string;
  totalXp: number;
  currentLevel: number;
  levelTitle: string;
  streakDays: number;
}

const FLAG_LABELS: Record<keyof Omit<GamificationFlags, "isGamificationEnabled">, string> = {
  gamificationLeaderboards: "Leaderboards",
  gamificationRewards: "Rewards Shop",
  gamificationHouses: "Houses",
  gamificationQuests: "Quests",
  gamificationMentor: "Mentor Program",
  gamificationShoutouts: "Shoutouts",
  gamificationEvents: "Seasonal Events",
  gamificationClassGoals: "Class Goals",
  gamificationCombos: "Combo Streaks",
  gamificationBoosts: "XP Boosts",
};

export default function GamificationPage() {
  const [flags, setFlags] = useState<GamificationFlags | null>(null);
  const [badges, setBadges] = useState<BadgeDef[]>([]);
  const [levels, setLevels] = useState<LevelDef[]>([]);
  const [houses, setHouses] = useState<HouseDto[]>([]);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [toggling, setToggling] = useState(false);

  const load = useCallback(async () => {
    try {
      const [f, b, l, h, lb] = await Promise.all([
        authRequest<GamificationFlags>("/api/v1/admin/gamification/flags"),
        authRequest<BadgeDef[]>("/api/v1/admin/gamification/badges"),
        authRequest<LevelDef[]>("/api/v1/admin/gamification/levels"),
        authRequest<HouseDto[]>("/api/v1/admin/gamification/houses"),
        authRequest<LeaderboardEntry[]>("/api/v1/admin/gamification/leaderboard"),
      ]);
      setFlags(f);
      setBadges(Array.isArray(b) ? b : []);
      setLevels(Array.isArray(l) ? l : []);
      setHouses(Array.isArray(h) ? h : []);
      setLeaderboard(Array.isArray(lb) ? lb : []);
    } catch (e) {
      setError(`Failed to load gamification data: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const toggleGamification = useCallback(async () => {
    if (!flags) return;
    setToggling(true);
    setError(null);
    try {
      await authRequest("/api/v1/admin/gamification/flags", {
        method: "PUT",
        body: { isGamificationEnabled: !flags.isGamificationEnabled },
      });
      setFlags((prev) =>
        prev ? { ...prev, isGamificationEnabled: !prev.isGamificationEnabled } : prev
      );
    } catch (e) {
      setError(`Failed to toggle: ${errorMessage(e)}`);
    } finally {
      setToggling(false);
    }
  }, [flags]);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconTrophy />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">
              Gamification
            </h1>
            <p className="text-[13px] text-ink-3">
              XP system, badges, houses, quests, rewards, and leaderboards.
            </p>
          </div>
        </div>
      </FadeIn>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-[13px] text-red-700">
          {error}
        </div>
      )}

      {/* Kill Switch */}
      <FadeIn delay={0.05}>
        <Card>
          <CardHeader
            title="Kill Switch"
            subtitle="Master control for the entire gamification system"
          />
          {loading ? (
            <Skeleton className="h-16" />
          ) : flags ? (
            <div className="flex items-center justify-between px-5 py-4">
              <div className="flex items-center gap-3">
                <Badge tone={flags.isGamificationEnabled ? "success" : "danger"}>
                  {flags.isGamificationEnabled ? "ENABLED" : "DISABLED"}
                </Badge>
                <span className="text-[13px] text-ink-3">
                  {flags.isGamificationEnabled
                    ? "Students are earning XP and badges."
                    : "All XP awarding is paused. No new XP or badges will be awarded."}
                </span>
              </div>
              <AdminButton
                variant={flags.isGamificationEnabled ? "danger" : "primary"}
                onClick={toggleGamification}
                disabled={toggling}
              >
                {flags.isGamificationEnabled ? (
                  <>
                    <IconClose width={14} height={14} /> Disable
                  </>
                ) : (
                  <>
                    <IconCheck width={14} height={14} /> Enable
                  </>
                )}
              </AdminButton>
            </div>
          ) : (
            <EmptyState title="No data" hint="Could not load flags." icon={<IconTrophy />} />
          )}
        </Card>
      </FadeIn>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Houses */}
        <FadeIn delay={0.1}>
          <Card>
            <CardHeader
              title="Houses"
              subtitle={`${houses.length} house${houses.length !== 1 ? "s" : ""}`}
            />
            {loading ? (
              <Skeleton className="h-24" />
            ) : houses.length === 0 ? (
              <EmptyState
                title="No houses"
                hint="Houses will appear here once configured."
                icon={<IconTrophy />}
              />
            ) : (
              <div className="divide-y divide-navy/[0.04]">
                {houses.map((h) => (
                  <div
                    key={h.id}
                    className="flex items-center justify-between px-5 py-3"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className="h-8 w-8 rounded-full"
                        style={{ backgroundColor: `#${h.color}` }}
                      />
                      <div>
                        <p className="text-[14px] font-semibold text-navy-deep">
                          {h.name}
                        </p>
                        <p className="text-[12px] text-ink-3">
                          {h.memberCount} members
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-[16px] font-bold text-navy-deep">
                        {h.totalPoints.toLocaleString()}
                      </p>
                      <p className="text-[11px] text-ink-3">total XP</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>

        {/* Leaderboard */}
        <FadeIn delay={0.15}>
          <Card>
            <CardHeader
              title="School Leaderboard"
              subtitle={`Top ${leaderboard.length} students by XP`}
            />
            {loading ? (
              <Skeleton className="h-32" />
            ) : leaderboard.length === 0 ? (
              <EmptyState
                title="No data"
                hint="Leaderboard will appear once students earn XP."
                icon={<IconTrophy />}
              />
            ) : (
              <div className="divide-y divide-navy/[0.04]">
                {leaderboard.slice(0, 10).map((e) => (
                  <div
                    key={e.studentId}
                    className="flex items-center justify-between px-5 py-2.5"
                  >
                    <div className="flex items-center gap-3">
                      <span
                        className={`flex h-7 w-7 items-center justify-center rounded-full text-[12px] font-bold ${
                          e.rank === 1
                            ? "bg-amber-100 text-amber-700"
                            : e.rank === 2
                              ? "bg-slate-200 text-slate-700"
                              : e.rank === 3
                                ? "bg-orange-100 text-orange-700"
                                : "bg-navy/[0.06] text-ink-3"
                        }`}
                      >
                        {e.rank}
                      </span>
                      <div>
                        <p className="text-[13px] font-semibold text-navy-deep">
                          {e.levelTitle}
                        </p>
                        <p className="text-[11px] text-ink-3">
                          Level {e.currentLevel} · {e.streakDays}d streak
                        </p>
                      </div>
                    </div>
                    <p className="text-[14px] font-bold text-navy-deep">
                      {e.totalXp.toLocaleString()} XP
                    </p>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Badges */}
        <FadeIn delay={0.2}>
          <Card>
            <CardHeader
              title="Badge Definitions"
              subtitle={`${badges.length} badge${badges.length !== 1 ? "s" : ""}`}
            />
            {loading ? (
              <Skeleton className="h-32" />
            ) : badges.length === 0 ? (
              <EmptyState
                title="No badges"
                hint="Badge definitions will appear here."
                icon={<IconTrophy />}
              />
            ) : (
              <div className="divide-y divide-navy/[0.04]">
                {badges.map((b) => (
                  <div key={b.id} className="px-5 py-3">
                    <div className="flex items-center justify-between">
                      <p className="text-[14px] font-semibold text-navy-deep">
                        {b.name}
                      </p>
                      <div className="flex items-center gap-2">
                        <Badge tone="neutral">{b.category}</Badge>
                        <Badge
                          tone={
                            b.rarity === "LEGENDARY"
                              ? "warning"
                              : b.rarity === "EPIC"
                                ? "success"
                                : "neutral"
                          }
                        >
                          {b.rarity}
                        </Badge>
                        {b.isSeasonal && <Badge tone="warning">Seasonal</Badge>}
                      </div>
                    </div>
                    <p className="mt-1 text-[12px] text-ink-3">{b.description}</p>
                    <p className="mt-0.5 text-[11px] text-ink-3">
                      Requires {b.xpRequirement} XP · Code: {b.code}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>

        {/* Levels */}
        <FadeIn delay={0.25}>
          <Card>
            <CardHeader
              title="Level Definitions"
              subtitle={`${levels.length} level${levels.length !== 1 ? "s" : ""}`}
            />
            {loading ? (
              <Skeleton className="h-32" />
            ) : levels.length === 0 ? (
              <EmptyState
                title="No levels"
                hint="Level definitions will appear here."
                icon={<IconTrophy />}
              />
            ) : (
              <div className="divide-y divide-navy/[0.04]">
                {levels.map((l) => (
                  <div
                    key={l.level}
                    className="flex items-center justify-between px-5 py-2.5"
                  >
                    <div className="flex items-center gap-3">
                      <span className="flex h-8 w-8 items-center justify-center rounded-xl bg-accent/10 text-[13px] font-bold text-accent-deep">
                        {l.level}
                      </span>
                      <div>
                        <p className="text-[14px] font-semibold text-navy-deep">
                          {l.title}
                        </p>
                        <p className="text-[11px] text-ink-3">{l.iconName}</p>
                      </div>
                    </div>
                    <p className="text-[13px] font-semibold text-ink-2">
                      {l.xpRequired.toLocaleString()} XP
                    </p>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </FadeIn>
      </div>
    </div>
  );
}
