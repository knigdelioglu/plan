import assert from "node:assert/strict";
import { test } from "node:test";
import { mkdtempSync, mkdirSync, writeFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { buildCatalog } from "../scripts/build-plan-catalog.mjs";

function withFixture(schoolSlug, metadataOverrides, run, modify = () => {}, reviewItems = []) {
  const root = mkdtempSync(join(tmpdir(), "plan-pwa-test-"));
  const folder = join(root, "2026-2027", "turk-dili-ve-edebiyati", "11", schoolSlug);
  mkdirSync(folder, { recursive: true });
  const record = {
    metadata: { academic_year: "2026-2027", grade_level: 11, school_type: "Fen Lisesi", course_name: "Türk Dili ve Edebiyatı", ...metadataOverrides },
    weeks: [{
      week_number: 7, start_date: "2026-10-26", end_date: "2026-10-30", total_hours: 5,
      theme: { theme_name: "Bir Diyeceğim Var!" },
      special_days_and_weeks: { present: false },
      lessons: [
        { lesson_index: 1, hours: 3, learning_area: "Dinleme/İzleme", learning_outcomes: [{ code: "TDE1.3", text: "Örnek" }] },
        { lesson_index: 2, hours: 2, learning_area: "Yazma", learning_outcomes: [] }
      ]
    }]
  };
  modify(record);
  if (reviewItems.length) writeFileSync(join(folder, "review-queue.json"), JSON.stringify({ items: reviewItems }));
  writeFileSync(join(folder, "annual-plan.json"), JSON.stringify(record));
  try { run(buildCatalog(root)); } finally { rmSync(root, { recursive: true, force: true }); }
}

test("kaynak kimliği doğru plan yayınlanır ve 3+2 ayrımı korunur", () => {
  withFixture("fen-lisesi", {}, (result) => {
    assert.equal(result.plans.length, 1);
    assert.equal(result.plans[0].weeks[0].lessons.length, 2);
    assert.deepEqual(result.plans[0].weeks[0].lessons.map((lesson) => lesson.hours), [3, 2]);
  });
});

test("Anadolu Lisesi klasöründeki Fen Lisesi kaynağı yayınlanmaz", () => {
  withFixture("anadolu-lisesi", { archive_path_school_type_mismatch: true }, (result) => {
    assert.equal(result.plans.length, 0);
    assert.equal(result.skipped.length, 1);
  });
});

test("bozuk ders saati toplamı katalogdan çıkarılır", () => {
  withFixture("fen-lisesi", {}, (result) => {
    assert.equal(result.plans.length, 0);
    assert.equal(result.skipped.length, 1);
  }, (record) => { record.weeks[0].total_hours = 6; });
});

test("metadata düzeltilmiş ama içerik doğrulanmamış kaynak yayınlanmaz", () => {
  withFixture("anadolu-lisesi", { school_type: "Anadolu Lisesi", archive_path_school_type_mismatch: false }, (result) => {
    assert.equal(result.plans.length, 0);
    assert.equal(result.skipped.length, 1);
  }, () => {}, [{ id: "RQ-013", status: "metadata_corrected_content_unverified" }]);
});
