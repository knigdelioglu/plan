import type { Plan, PlanWeek } from '../catalog/types';

export type ClassRecord = {
  id: string; name: string; nameKey: string; planId: string;
  startWeek: number; createdAt: string;
};
export type LessonRecord = {
  classId: string; week: number; lesson: number;
  doneHours: number; note: string; updatedAt: string;
};
export type Snapshot = { classes: ClassRecord[]; records: LessonRecord[]; selectedClassId: string | null };

export const classNameKey = (name: string): string =>
  name.trim().replace(/\s+/g, ' ').toLocaleUpperCase('tr-TR');

export const progressFor = (records: LessonRecord[], classId: string, week: number, lesson: number) =>
  records.find(r => r.classId === classId && r.week === week && r.lesson === lesson);

export function weekSummary(week: PlanWeek, classId: string, records: LessonRecord[]) {
  const done = week.lessons.reduce((acc, lesson) => acc +
    Math.min(lesson.hours, Math.max(0, progressFor(records, classId, week.number, lesson.index)?.doneHours ?? 0)), 0);
  return { done, total: week.hours };
}

export function classSummary(group: ClassRecord, plan: Plan, records: LessonRecord[]) {
  const weeks = plan.weeks.filter(w => w.number >= group.startWeek);
  let done = 0, total = 0;
  let next: { week: number; lesson: number } | null = null;
  for (const week of weeks) {
    total += week.hours;
    for (const lesson of week.lessons) {
      const hours = progressFor(records, group.id, week.number, lesson.index)?.doneHours ?? 0;
      done += Math.min(lesson.hours, Math.max(0, hours));
      if (next === null && hours < lesson.hours) next = { week: week.number, lesson: lesson.index };
    }
  }
  return { done, total, percent: total ? Math.round(done / total * 100) : 0,
    next, complete: weeks.length > 0 && next === null };
}

export function validateHours(doneHours: number, max: number) {
  return Number.isInteger(max) && max > 0 && Number.isInteger(doneHours) &&
    doneHours >= 0 && doneHours <= max;
}
