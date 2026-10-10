export interface TeacherClass {
  id: string;
  planId: string;
  label: string;
  nameKey: string;
  startWeekNumber: number;
  createdAt: string;
  updatedAt: string;
}

export interface LessonProgress {
  id: string;
  classId: string;
  weekNumber: number;
  lessonIndex: number;
  completedHours: number;
  note: string;
  updatedAt: string;
}

export interface TeacherSnapshot {
  classes: TeacherClass[];
  progress: LessonProgress[];
}

export type ProgressPatch = {
  classId: string;
  weekNumber: number;
  lessonIndex: number;
  maxHours: number;
  completedHours?: number;
  note?: string;
};
