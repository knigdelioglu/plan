import assert from "node:assert/strict";
import { test } from "node:test";
import {
  classSummary, normalizeClassName, progressForLesson, progressKey,
  trackedWeeks, validCompletedHours, weekHours
} from "../src/features/progress/progress-model.ts";

const plan = {
  id: "test-plan",
  weeks: [
    { week_number: 1, total_hours: 5, lessons: [
      { lesson_index: 1, hours: 3 },
      { lesson_index: 2, hours: 2 }
    ] },
    { week_number: 2, total_hours: 5, lessons: [
      { lesson_index: 1, hours: 5 }
    ] },
    { week_number: 3, total_hours: 5, lessons: [
      { lesson_index: 1, hours: 5 }
    ] }
  ]
};
const group = (id, startWeekNumber = 1) => ({ id, startWeekNumber, planId: "test-plan" });
const record = (classId, weekNumber, lessonIndex, completedHours) =>
  ({ id: progressKey(classId, weekNumber, lessonIndex), classId, weekNumber, lessonIndex, completedHours, note: "", updatedAt: "" });

test("3+2 saatlik hafta bölümleri birbirine karışmaz", () => {
  const rows = [record("11A", 1, 1, 3), record("11A", 1, 2, 1)];
  assert.deepEqual(weekHours(group("11A"), plan.weeks[0], rows), { completed: 4, total: 5 });
  assert.equal(classSummary(group("11A"), plan, rows).nextLessonIndex, 2);
  assert.equal(classSummary(group("11A"), plan, rows).nextWeekNumber, 1);
});

test("iki şubenin ilerlemesi tamamen ayrıdır", () => {
  const records = [record("11A", 1, 1, 3), record("11A", 1, 2, 2), record("11B", 1, 1, 1)];
  assert.equal(classSummary(group("11A"), plan, records).completedHours, 5);
  assert.equal(classSummary(group("11B"), plan, records).completedHours, 1);
  assert.equal(classSummary(group("11A"), plan, records).nextWeekNumber, 2);
  assert.equal(classSummary(group("11B"), plan, records).nextWeekNumber, 1);
  assert.equal(progressForLesson(records, "11B", 1, 2), undefined);
});

test("başlangıç öncesi haftalar tamamlanmış veya takip edilmiş sayılmaz", () => {
  const late = group("11A", 2);
  assert.deepEqual(trackedWeeks(late, plan).map((week) => week.week_number), [2, 3]);
  const summary = classSummary(late, plan, [record("11A", 1, 1, 3)]);
  assert.equal(summary.plannedHours, 10);
  assert.equal(summary.completedHours, 0);
  assert.equal(summary.nextWeekNumber, 2);
});

test("tüm takip edilen dersler işlenince durum tamamlandı olur", () => {
  const rows = [record("11A", 2, 1, 5), record("11A", 3, 1, 5)];
  const result = classSummary(group("11A", 2), plan, rows);
  assert.equal(result.completed, true);
  assert.equal(result.percentage, 100);
  assert.equal(result.nextWeekNumber, null);
});

test("kayıt eksikliği ile sıfır saat aynı kabul edilir", () => {
  assert.equal(classSummary(group("11A"), plan, []).completedHours, 0);
  assert.equal(classSummary(group("11A"), plan, []).percentage, 0);
  assert.equal(classSummary(group("11A"), plan, []).nextWeekNumber, 1);
});

test("saat sınırları aşılmaz ve kesirli saat geçerli değildir", () => {
  assert.equal(validCompletedHours(0, 3), true);
  assert.equal(validCompletedHours(3, 3), true);
  assert.equal(validCompletedHours(4, 3), false);
  assert.equal(validCompletedHours(-1, 3), false);
  assert.equal(validCompletedHours(1.5, 3), false);
  assert.equal(validCompletedHours(Number.NaN, 3), false);
});

test("şube isimleri Türkçe büyük/küçük harf ve boşluk kurallarıyla normalize edilir", () => {
  assert.equal(normalizeClassName(" 11 /  a   "), "11 / A");
  assert.equal(normalizeClassName(" 11/ı "), "11/I");
  assert.notEqual(progressKey("11/A", 1, 2), progressKey("11/B", 1, 2));
});
