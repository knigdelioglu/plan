import catalogJSON from './catalog.json';
import type { Catalog, Plan, PlanWeek } from './types';

export const catalog = catalogJSON as unknown as Catalog;
export const plans: Plan[] = catalog.plans;

export const findPlan = (id: string): Plan | undefined => plans.find(p => p.id === id);
export const findWeek = (plan: Plan, number: number): PlanWeek | undefined =>
  plan.weeks.find(w => w.number === number);

export function localDateISO(date = new Date()): string {
  return [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0')].join('-');
}
export function defaultWeek(plan: Plan, today = localDateISO()): PlanWeek | undefined {
  return plan.weeks.find(w => w.start <= today && today <= w.end)
    ?? plan.weeks.find(w => w.start > today)
    ?? plan.weeks.at(-1);
}
export function currentBreak(plan: Plan, today = localDateISO()): SchoolBreak | undefined {
  return plan.breaks.find(b => b.start <= today && today <= b.end);
}
export function dateLabel(start: string, end: string): string {
  const toLabel = (iso: string) => {
    const [y, m, d] = iso.split('-').map(Number);
    return new Intl.DateTimeFormat('tr-TR', { day: 'numeric', month: 'short' })
      .format(new Date(y ?? 2000, (m ?? 1) - 1, d ?? 1));
  };
  return toLabel(start) + ' – ' + toLabel(end);
}
