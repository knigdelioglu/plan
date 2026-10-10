import type { PlanWeek, PublishedPlan } from "@/types/catalog";

export function todayKey(now: Date = new Date()): string {
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return [year, month, day].join("-");
}

function parseDate(iso: string): Date {
  const [year, month, day] = iso.split("-").map(Number);
  return new Date(year || 2000, (month || 1) - 1, day || 1);
}

export function shortDate(iso: string): string {
  return new Intl.DateTimeFormat("tr-TR", { day: "numeric", month: "long" }).format(parseDate(iso));
}

export function weekRange(week: PlanWeek): string {
  return shortDate(week.start_date) + " – " + shortDate(week.end_date);
}

export function closestWeekIndex(weeks: PlanWeek[], date: string = todayKey()): number {
  if (!weeks.length) return 0;
  const active = weeks.findIndex((week) => week.start_date <= date && date <= week.end_date);
  if (active >= 0) return active;
  const upcoming = weeks.findIndex((week) => week.start_date > date);
  return upcoming >= 0 ? upcoming : weeks.length - 1;
}

export function currentBreak(plan: PublishedPlan, date: string = todayKey()) {
  return plan.breaks.find((item) => item.start_date <= date && date <= item.end_date);
}
