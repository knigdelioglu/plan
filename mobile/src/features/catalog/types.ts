export type Outcome = { code: string; text: string };
export type Lesson = {
  index: number; hours: number; area: string; title: string;
  materials: string[]; outcomes: Outcome[];
  processes: { title: string; items: string[] }[];
};
export type PlanWeek = {
  number: number; start: string; end: string; hours: number;
  hoursAssumed: boolean; theme: string; specialDays: string[];
  lessons: Lesson[];
};
export type SchoolBreak = { name: string; start: string; end: string };
export type Plan = {
  id: string; year: string; course: string; grade: number;
  school: string; sourceFile: string; verified: true;
  weeks: PlanWeek[]; breaks: SchoolBreak[];
};
export type Catalog = {
  schema: 1; generatedFrom: string; plans: Plan[];
  withheld: { path: string; reason: string }[];
};
