import type { PublishedPlan, PlanWeek } from "../../types/catalog";
import type { LessonProgress, TeacherClass } from "../../types/teacher";

export function progressKey(classId: string, weekNumber: number, lessonIndex: number): string {
  return JSON.stringify([classId, weekNumber, lessonIndex]);
}

export function normalizeClassName(label: string): string {
  return label.trim().replace(/\s+/g, " ").toLocaleUpperCase("tr-TR");
}

export function validCompletedHours(value: number, maxHours: number): boolean {
  return Number.isInteger(value) && Number.isInteger(maxHours) && maxHours > 0 && value >= 0 && value <= maxHours;
}

export function trackedWeeks(group: TeacherClass, plan: PublishedPlan): PlanWeek[] {
  return plan.weeks.filter((week) => week.week_number >= group.startWeekNumber);
}

export function progressForLesson(records: LessonProgress[], classId: string, weekNumber: number, lessonIndex: number): LessonProgress | undefined {
  return records.find((record) => record.classId === classId && record.weekNumber === weekNumber && record.lessonIndex === lessonIndex);
}

export function weekHours(group: TeacherClass, week: PlanWeek, records: LessonProgress[]) {
  let completed = 0;
  for (const lesson of week.lessons) {
    const value = progressForLesson(records, group.id, week.week_number, lesson.lesson_index)?.completedHours ?? 0;
    completed += Math.min(Math.max(value, 0), lesson.hours);
  }
  return { completed, total: week.total_hours };
}

export function classSummary(group: TeacherClass, plan: PublishedPlan, records: LessonProgress[]) {
  const weeks = trackedWeeks(group, plan);
  let plannedHours = 0;
  let completedHours = 0;
  let nextWeekNumber: number | null = null;
  let nextLessonIndex: number | null = null;

  for (const week of weeks) {
    plannedHours += week.total_hours;
    const totals = weekHours(group, week, records);
    completedHours += totals.completed;
    if (nextWeekNumber === null && totals.completed < totals.total) {
      const nextLesson = week.lessons.find((lesson) =>
        (progressForLesson(records, group.id, week.week_number, lesson.lesson_index)?.completedHours ?? 0) < lesson.hours
      );
      nextWeekNumber = week.week_number;
      nextLessonIndex = nextLesson?.lesson_index ?? null;
    }
  }

  return {
    plannedHours,
    completedHours,
    percentage: plannedHours ? Math.round((completedHours / plannedHours) * 100) : 0,
    nextWeekNumber,
    nextLessonIndex,
    completed: nextWeekNumber === null && weeks.length > 0
  };
}
