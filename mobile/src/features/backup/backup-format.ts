import type { Plan } from '../catalog/types';
import type { ClassRecord, LessonRecord, Snapshot } from '../progress/model';
import { classNameKey, validateHours } from '../progress/model.ts';

export const BACKUP_FORMAT = 'stream.sunum.plan.teacher-backup';
export const BACKUP_VERSION = 1;
export const MAX_BACKUP_BYTES = 6 * 1024 * 1024;

export type BackupFile = {
  format: typeof BACKUP_FORMAT;
  version: typeof BACKUP_VERSION;
  exportedAt: string;
  classes: ClassRecord[];
  records: LessonRecord[];
  selectedClassId: string | null;
};

const object = (value: unknown): value is Record<string, unknown> =>
  typeof value === 'object' && value !== null && !Array.isArray(value);
const safeText = (value: unknown, max: number): value is string =>
  typeof value === 'string' && value.length <= max;
const timestamp = (value: unknown): value is string => typeof value === 'string' &&
  /^\d{4}-\d{2}-\d{2}T/.test(value) && Number.isFinite(Date.parse(value));

const integer = (value: unknown): value is number => typeof value === 'number' && Number.isInteger(value);

export function createBackup(snapshot: Snapshot, now = new Date().toISOString()): BackupFile {
  return {
    format: BACKUP_FORMAT, version: BACKUP_VERSION, exportedAt: now,
    classes: snapshot.classes.map(c => ({ ...c })),
    records: snapshot.records.map(r => ({ ...r })),
    selectedClassId: snapshot.selectedClassId
  };
}

// Validate ALL records before any database mutation. Unknown plans are rejected;
// silently dropping data would make a "successful" restore incomplete.
export function validateBackup(input: unknown, getPlan: (id: string) => Plan | undefined): BackupFile {
  if (!object(input) || input.format !== BACKUP_FORMAT || input.version !== BACKUP_VERSION ||
      !timestamp(input.exportedAt) || !Array.isArray(input.classes) ||
      !Array.isArray(input.records) || input.classes.length > 300 ||
      input.records.length > 40000) {
    throw new Error('Yedek biçimi veya sürümü geçersiz.');
  }
  const ids = new Set<string>(), labels = new Set<string>();
  const classes: ClassRecord[] = [];
  for (const c of input.classes) {
    if (!object(c) || !safeText(c.id, 100) || !c.id ||
        !safeText(c.planId, 250) || !c.planId ||
        !safeText(c.name, 40) || !c.name.trim() ||
        !safeText(c.nameKey, 80) || !timestamp(c.createdAt) ||
        !integer(c.startWeek)) {
      throw new Error('Yedekte geçersiz şube kaydı var.');
    }
    const plan = getPlan(c.planId);
    if (!plan) throw new Error('Yedekte artık doğrulanmış katalogda olmayan bir plan var.');
    if (!plan.weeks.some(w => w.number === c.startWeek)) {
      throw new Error('Yedekte şube başlangıç haftası geçersiz.');
    }
    const normalized = classNameKey(c.name);
    const labelKey = JSON.stringify([c.planId, normalized]);
    if (ids.has(c.id) || labels.has(labelKey)) throw new Error('Yedekte yinelenen şube var.');
    ids.add(c.id); labels.add(labelKey);
    classes.push({id:c.id, planId:c.planId, name:c.name.trim(), nameKey:normalized,
      startWeek:c.startWeek, createdAt:c.createdAt});
  }
  const groups = new Map(classes.map(c => [c.id, c]));
  const keys = new Set<string>();
  const records: LessonRecord[] = [];
  for (const r of input.records) {
    if (!object(r) || !safeText(r.classId, 100) || !r.classId ||
        !integer(r.week) || !integer(r.lesson) ||
        !safeText(r.note, 1200) || !timestamp(r.updatedAt) || !integer(r.doneHours)) {
      throw new Error('Yedekte geçersiz ders kaydı var.');
    }
    const group = groups.get(r.classId);
    const week = group && getPlan(group.planId)?.weeks.find(w => w.number === r.week);
    const lesson = week?.lessons.find(l => l.index === r.lesson);
    if (!group || !lesson || r.week < group.startWeek ||
        !validateHours(r.doneHours, lesson.hours)) {
      throw new Error('Yedekte planla eşleşmeyen işlenen saat veya ders bölümü var.');
    }
    const key = JSON.stringify([r.classId, r.week, r.lesson]);
    if (keys.has(key)) throw new Error('Yedekte yinelenen ders kaydı var.');
    keys.add(key);
    records.push({classId:r.classId, week:r.week, lesson:r.lesson,
      doneHours:r.doneHours, note:r.note, updatedAt:r.updatedAt});
  }
  const selectedClassId = input.selectedClassId;
  if (selectedClassId !== null && (typeof selectedClassId !== 'string' || !ids.has(selectedClassId))) {
    throw new Error('Yedekte seçili şube bulunamadı.');
  }
  return {format:BACKUP_FORMAT,version:BACKUP_VERSION,exportedAt:input.exportedAt,
    classes,records,selectedClassId};
}
export function parseBackup(text: string, getPlan: (id: string) => Plan | undefined): BackupFile {
  if (new TextEncoder().encode(text).length > MAX_BACKUP_BYTES) {
    throw new Error('Yedek dosyası 6 MB sınırını aşıyor.');
  }
  let value: unknown;
  try { value=JSON.parse(text); }
  catch { throw new Error('Seçilen dosya geçerli JSON değil.'); }
  return validateBackup(value,getPlan);
}
