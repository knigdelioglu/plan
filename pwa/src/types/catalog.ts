export interface LessonSummary {
  lesson_index: number;
  hours: number;
  learning_area: string;
  topic_title: string;
  texts_and_materials: string[];
  outcomes: { code: string; text: string }[];
}

export interface PlanWeek {
  week_number: number;
  start_date: string;
  end_date: string;
  total_hours: number;
  hours_assumed: boolean;
  theme_name: string;
  special_days: string[];
  lessons: LessonSummary[];
}

export interface PublishedPlan {
  id: string;
  academic_year: string;
  course_name: string;
  grade_level: number;
  school_type: string;
  document_title: string;
  source_file: string;
  total_weeks: number;
  weekly_hours: number;
  breaks: { name: string; start_date: string; end_date: string }[];
  weeks: PlanWeek[];
}

export interface PlanCatalog {
  schema_version: number;
  plans: PublishedPlan[];
  skipped: { path: string; reason: string }[];
}
