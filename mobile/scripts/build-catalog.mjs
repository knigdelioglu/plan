import { readdirSync, readFileSync, mkdirSync, writeFileSync } from 'node:fs';
import { join, dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const archive = resolve(root, '..', 'plans');
const target = resolve(root, 'src/features/catalog/catalog.json');
const schools = {
  'anadolu-lisesi': 'Anadolu Lisesi',
  'fen-lisesi': 'Fen Lisesi',
  'sosyal-bilimler-lisesi': 'Sosyal Bilimler Lisesi'
};
const blocked = new Set([
  'open_source_type_mismatch', 'metadata_corrected_content_unverified',
  'source_verification_pending', 'content_unverified'
]);
const dirs = (path) => {
  try { return readdirSync(path, { withFileTypes: true })
    .filter(f => f.isDirectory()).map(f => f.name).sort(); }
  catch (error) { if (error?.code === 'ENOENT') return []; throw error; }
};
const readJSON = (path) => {
  try { return JSON.parse(readFileSync(path, 'utf8')); }
  catch (error) { if (error?.code === 'ENOENT') return null; throw error; }
};
const iso = v => typeof v === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(v)
  && !Number.isNaN(Date.parse(v + 'T00:00:00Z'));
const specialDays = raw => {
  if (!raw?.present) return [];
  if (Array.isArray(raw.events) && raw.events.length)
    return raw.events.map(x => x.name ?? x.text ?? '').filter(Boolean);
  return (raw.text ?? raw.raw ?? '').split(/\n+/).map(s => s.trim()).filter(Boolean);
};
export function validWeeks(weeks) {
  if (!Array.isArray(weeks) || !weeks.length) return false;
  const numbers = new Set();
  for (const w of weeks) {
    if (!Number.isInteger(w.week_number) || numbers.has(w.week_number) ||
        !iso(w.start_date) || !iso(w.end_date) || w.start_date > w.end_date ||
        !Array.isArray(w.lessons) || !w.lessons.length ||
        !Number.isInteger(w.total_hours) || w.total_hours <= 0) return false;
    if (w.lessons.some(l => !Number.isInteger(l.lesson_index) || !Number.isInteger(l.hours) || l.hours <= 0) ||
      new Set(w.lessons.map(l => l.lesson_index)).size !== w.lessons.length ||
      w.lessons.reduce((sum,l) => sum + l.hours,0) !== w.total_hours) return false;
    numbers.add(w.week_number);
  }
  return weeks.every((w,i) => w.week_number === i + 1);
}
function normalizePlan(path, source) {
  const m = source.metadata;
  return {
    id: path, year: m.academic_year, course: m.course_name, grade: m.grade_level,
    school: m.school_type, sourceFile: m.source_file, verified: true,
    breaks: (source.academic_calendar_breaks ?? []).map(b => ({
      name: b.break_name, start: b.start_date, end: b.end_date
    })),
    weeks: source.weeks.map(w => ({
      number: w.week_number, start: w.start_date, end: w.end_date,
      hours: w.total_hours, hoursAssumed: Boolean(
        w.is_hours_assumed || w.lessons.some(l => /varsayım|sayısal saat yok|kaynak hücrede/i.test(l.hours_raw ?? ''))
      ), theme: w.theme?.theme_name ?? 'Diğer', specialDays: specialDays(w.special_days_and_weeks),
      lessons: w.lessons.map(l => ({
        index: l.lesson_index, hours: l.hours, area: l.learning_area ?? '',
        title: l.topic_title ?? '', materials: l.texts_and_materials ?? [],
        outcomes: (l.learning_outcomes ?? []).map(o => ({code:o.code ?? '',text:o.text ?? ''})),
        processes: (l.process_components ?? []).flatMap(c => 
          (c.sub_components ?? []).map(s => ({
            title: (s.sub_code ?? '') + ' ' + (s.title ?? ''),
            items: s.items ?? []
          }))
        )
      }))
    }))
  };
}
export function buildCatalog(sourceRoot = archive) {
  const plans = [], withheld = [];
  for (const year of dirs(sourceRoot))
    for (const course of dirs(join(sourceRoot, year)))
      for (const grade of dirs(join(sourceRoot, year, course)))
        for (const schoolSlug of dirs(join(sourceRoot, year, course, grade))) {
          const rel = [year, course, grade, schoolSlug].join('/');
          const folder = join(sourceRoot, rel);
          const source = readJSON(join(folder, 'annual-plan.json'));
          if (!source) continue;
          const meta = source.metadata ?? {};
          const reviews = readJSON(join(folder, 'review-queue.json'));
          const notVerified = (reviews?.items ?? []).some(item => blocked.has(item.status)) ||
            /içerik(?:leri| kökeni).*doğrulanmadı|contents? unverified/i.test(meta.archive_path_note ?? '');
          const valid = !meta.archive_path_school_type_mismatch && !notVerified &&
            meta.academic_year === year && meta.grade_level === Number(grade) &&
            meta.school_type === schools[schoolSlug] && Boolean(meta.source_file) &&
            validWeeks(source.weeks);
          if (!valid) {
            withheld.push({path: rel, reason: 'Kaynak kimliği veya içerik doğrulaması tamamlanmamış'});
            continue;
          }
          plans.push(normalizePlan(rel, source));
        }
  return {schema:1,generatedFrom:'plans/** (salt okunur)',plans,withheld};
}
if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const result = buildCatalog();
  mkdirSync(dirname(target), {recursive:true});
  writeFileSync(target,JSON.stringify(result,null,2) + '\n','utf8');
  console.log('Plan kataloğu: '+result.plans.length+' kullanılabilir; '+result.withheld.length+' doğrulama bekliyor.');
}
