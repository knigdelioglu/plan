import { readdirSync, readFileSync, mkdirSync, writeFileSync } from "node:fs";
import { join, resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const pwaRoot = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const defaultSourceRoot = resolve(pwaRoot, "..", "plans");
const outputFile = resolve(pwaRoot, "public", "data", "catalog.json");

const schoolTypes = {
  "anadolu-lisesi": "Anadolu Lisesi",
  "fen-lisesi": "Fen Lisesi",
  "sosyal-bilimler-lisesi": "Sosyal Bilimler Lisesi"
};

function directories(root) {
  try {
    return readdirSync(root, { withFileTypes: true }).filter((entry) => entry.isDirectory()).map((entry) => entry.name).sort();
  } catch (error) {
    if (error && error.code === "ENOENT") return [];
    throw error;
  }
}

function validDate(value) {
  return typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value) && Number.isFinite(Date.parse(value + "T00:00:00Z"));
}

function readPlan(file) {
  try {
    return JSON.parse(readFileSync(file, "utf8"));
  } catch (error) {
    if (error && error.code === "ENOENT") return null;
    throw error;
  }
}

function normalizeSpecials(special) {
  if (!special?.present) return [];
  if (Array.isArray(special.events) && special.events.length) {
    return special.events.map((event) => event.name || event.title || event.text || event.raw || "").filter(Boolean);
  }
  return (special.text || special.raw || "").split(/\n+/).map((entry) => entry.trim()).filter(Boolean);
}

function validWeeks(weeks) {
  if (!Array.isArray(weeks) || weeks.length === 0) return false;
  const numbers = new Set();
  for (const week of weeks) {
    if (!Number.isInteger(week.week_number) || numbers.has(week.week_number) ||
        !validDate(week.start_date) || !validDate(week.end_date) ||
        typeof week.total_hours !== "number" || !Array.isArray(week.lessons)) return false;
    if (week.lessons.reduce((sum, lesson) => sum + (Number(lesson.hours) || 0), 0) !== week.total_hours) return false;
    numbers.add(week.week_number);
  }
  return true;
}

/**
 * Convert only verified, source-matched archive plans to read-only app summaries.
 * No writes to sourceRoot; any output stays inside the PWA workspace.
 */
export function buildCatalog(sourceRoot = defaultSourceRoot) {
  const plans = [];
  const skipped = [];
  for (const year of directories(sourceRoot)) {
    for (const course of directories(join(sourceRoot, year))) {
      for (const grade of directories(join(sourceRoot, year, course))) {
        for (const schoolSlug of directories(join(sourceRoot, year, course, grade))) {
          const relative = [year, course, grade, schoolSlug].join("/");
          const source = readPlan(join(sourceRoot, relative, "annual-plan.json"));
          if (!source) continue;
          const meta = source.metadata || {};
          const reviewQueue = readPlan(join(sourceRoot, relative, "review-queue.json"));
          const blockedStatuses = new Set([
            "open_source_type_mismatch", "metadata_corrected_content_unverified",
            "source_verification_pending", "content_unverified"
          ]);
          const contentNotVerified = (reviewQueue?.items || []).some((item) =>
            blockedStatuses.has(item.status)
          );
          if (meta.archive_path_school_type_mismatch === true || contentNotVerified ||
              meta.academic_year !== year ||
              meta.grade_level !== Number(grade) ||
              meta.school_type !== schoolTypes[schoolSlug] ||
              !validWeeks(source.weeks)) {
            skipped.push({ path: relative, reason: "Kaynak kimliği veya haftalık veri doğrulanamadı" });
            continue;
          }
          const id = relative;
          plans.push({
            id,
            academic_year: year,
            course_name: meta.course_name,
            grade_level: meta.grade_level,
            school_type: meta.school_type,
            document_title: meta.document_title,
            source_file: meta.source_file,
            total_weeks: source.weeks.length,
            weekly_hours: meta.weekly_lesson_hours,
            breaks: (source.academic_calendar_breaks || []).map((br) => ({
              name: br.break_name,
              start_date: br.start_date,
              end_date: br.end_date
            })),
            weeks: source.weeks.map((week) => ({
              week_number: week.week_number,
              start_date: week.start_date,
              end_date: week.end_date,
              total_hours: week.total_hours,
              hours_assumed: Boolean(week.is_hours_assumed || week.lessons.some((lesson) => /kaynak hücrede|varsayım|sayısal saat yok/i.test(String(lesson.hours_raw || "")))),
              theme_name: week.theme?.theme_name || "Yıl sonu / Diğer",
              special_days: normalizeSpecials(week.special_days_and_weeks),
              lessons: week.lessons.map((lesson) => ({
                lesson_index: lesson.lesson_index,
                hours: lesson.hours,
                learning_area: lesson.learning_area,
                topic_title: lesson.topic_title,
                texts_and_materials: lesson.texts_and_materials || [],
                outcomes: (lesson.learning_outcomes || []).map((outcome) => ({
                  code: outcome.code,
                  text: outcome.text
                }))
              }))
            }))
          });
        }
      }
    }
  }
  return { schema_version: 1, plans, skipped };
}

if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const catalog = buildCatalog();
  mkdirSync(dirname(outputFile), { recursive: true });
  writeFileSync(outputFile, JSON.stringify(catalog, null, 2) + "\n", "utf8");
  console.log("PWA katalog: " + catalog.plans.length + " geçerli plan, " + catalog.skipped.length + " atlanan kayıt.");
  if (catalog.plans.length === 0) console.warn("Uyarı: PWA'da gösterilecek doğrulanmış plan bulunamadı.");
}
