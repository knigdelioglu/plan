import Dexie, { type Table } from "dexie";
import type { TeacherClass, LessonProgress, TeacherSnapshot, ProgressPatch } from "../../types/teacher";
import { normalizeClassName, progressKey, validCompletedHours } from "../../features/progress/progress-model";

class TeacherDatabase extends Dexie {
  classes!: Table<TeacherClass, string>;
  lessonProgress!: Table<LessonProgress, string>;

  constructor() {
    super("haftalik-plan-teacher");
    this.version(1).stores({
      classes: "id, planId, &[planId+nameKey], createdAt",
      lessonProgress: "id, classId, [classId+weekNumber]"
    });
  }
}

export const teacherDb = new TeacherDatabase();

export async function loadTeacherSnapshot(): Promise<TeacherSnapshot> {
  const [classes, progress] = await Promise.all([
    teacherDb.classes.toArray(),
    teacherDb.lessonProgress.toArray()
  ]);
  return { classes: classes.sort((a, b) => a.createdAt.localeCompare(b.createdAt)), progress };
}

export async function createTeacherClass(planId: string, label: string, startWeekNumber: number) {
  const clean = label.trim().replace(/\s+/g, " ");
  const nameKey = normalizeClassName(clean);
  if (!planId || clean.length < 2 || clean.length > 50) {
    throw new Error("Şube adı 2–50 karakter arasında olmalıdır.");
  }
  if (!Number.isInteger(startWeekNumber) || startWeekNumber < 1) {
    throw new Error("Geçerli bir başlangıç haftası seçin.");
  }
  const now = new Date().toISOString();
  const record: TeacherClass = {
    id: crypto.randomUUID(), planId, label: clean, nameKey,
    startWeekNumber, createdAt: now, updatedAt: now
  };
  try {
    await teacherDb.classes.add(record);
  } catch (error) {
    if (error instanceof Error && (error.name === "ConstraintError" || /ConstraintError/.test(error.message))) {
      throw new Error("Bu plan için aynı adlı bir şube zaten mevcut.");
    }
    throw error;
  }
  return record;
}

export async function removeTeacherClass(classId: string): Promise<void> {
  await teacherDb.transaction("rw", teacherDb.classes, teacherDb.lessonProgress, async () => {
    await teacherDb.lessonProgress.where("classId").equals(classId).delete();
    await teacherDb.classes.delete(classId);
  });
}

export async function saveLessonProgress(patch: ProgressPatch): Promise<void> {
  const { classId, weekNumber, lessonIndex, maxHours } = patch;
  if (!classId || !Number.isInteger(weekNumber) || !Number.isInteger(lessonIndex) ||
      !Number.isInteger(maxHours) || maxHours <= 0) {
    throw new Error("Geçersiz ders bölümü.");
  }
  if (patch.completedHours !== undefined && !validCompletedHours(patch.completedHours, maxHours)) {
    throw new Error("İşlenen saatler 0 ile planlanan saat arasında tam sayı olmalıdır.");
  }
  if (patch.note !== undefined && patch.note.length > 1000) {
    throw new Error("Ders notu en fazla 1000 karakter olabilir.");
  }

  const id = progressKey(classId, weekNumber, lessonIndex);
  await teacherDb.transaction("rw", teacherDb.classes, teacherDb.lessonProgress, async () => {
    const group = await teacherDb.classes.get(classId);
    if (!group) throw new Error("Şube bulunamadı.");
    if (weekNumber < group.startWeekNumber) {
      throw new Error("Başlangıç haftasından önce kayıt yapılamaz.");
    }
    const previous = await teacherDb.lessonProgress.get(id);
    const completedHours = patch.completedHours ?? previous?.completedHours ?? 0;
    const note = patch.note ?? previous?.note ?? "";
    if (completedHours === 0 && !note.trim()) {
      await teacherDb.lessonProgress.delete(id);
    } else {
      await teacherDb.lessonProgress.put({
        id, classId, weekNumber, lessonIndex,
        completedHours, note, updatedAt: new Date().toISOString()
      });
    }
  });
}
