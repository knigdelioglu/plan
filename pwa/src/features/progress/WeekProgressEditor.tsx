import { useEffect, useState } from "react";
import { Check, Minus, Plus, Save } from "lucide-react";
import type { PlanWeek } from "../../types/catalog";
import type { LessonProgress, ProgressPatch, TeacherClass } from "../../types/teacher";
import { progressForLesson, weekHours } from "./progress-model";
import { Button } from "../../components/arc/button/button";
import { Badge } from "../../components/arc/badge/badge";
import { Progress } from "../../components/arc/progress/progress";

interface Props {
  group: TeacherClass;
  week: PlanWeek;
  records: LessonProgress[];
  onSave: (patch: ProgressPatch) => Promise<void>;
}

interface EditorProps {
  group: TeacherClass;
  weekNumber: number;
  lesson: PlanWeek["lessons"][number];
  record?: LessonProgress;
  onSave: (patch: ProgressPatch) => Promise<void>;
}

function LessonEditor({ group, weekNumber, lesson, record, onSave }: EditorProps) {
  const [note, setNote] = useState(record?.note ?? "");
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const hours = record?.completedHours ?? 0;
  const isDirty = note !== (record?.note ?? "");

  useEffect(() => {
    setNote(record?.note ?? "");
    setMessage(null);
  }, [group.id, weekNumber, lesson.lesson_index]);

  async function commit(change: { completedHours?: number; note?: string }) {
    if (busy) return;
    setBusy(true);
    setMessage(null);
    try {
      await onSave({
        classId: group.id, weekNumber, lessonIndex: lesson.lesson_index,
        maxHours: lesson.hours, ...change
      });
      setMessage("Kaydedildi");
    } catch (reason) {
      setMessage(reason instanceof Error ? reason.message : "Kayıt başarısız.");
    } finally { setBusy(false); }
  }

  return <div className="tracking-lesson">
    <div className="tracking-lesson-header">
      <div><span className="eyebrow">{lesson.learning_area || "Ders bölümü"}</span>
        <strong>{lesson.topic_title || lesson.learning_area || "Ders"}</strong></div>
      <Badge tone={hours === lesson.hours ? "success" : "neutral"}>{hours}/{lesson.hours} saat</Badge>
    </div>
    <div className="tracking-hours">
      <Button type="button" variant="secondary" size="sm" aria-label="İşlenen saati azalt"
        disabled={busy || hours === 0} onClick={() => void commit({ completedHours: hours - 1 })}><Minus size={17}/></Button>
      <span aria-live="polite">{hours} saat işlendi</span>
      <Button type="button" variant="secondary" size="sm" aria-label="İşlenen saati artır"
        disabled={busy || hours >= lesson.hours} onClick={() => void commit({ completedHours: hours + 1 })}><Plus size={17}/></Button>
      <Button type="button" variant="ghost" size="sm" disabled={busy || hours === lesson.hours}
        onClick={() => void commit({ completedHours: lesson.hours })}><Check size={15}/> Tamamla</Button>
    </div>
    <label className="field note-field"><span>Ders notu (isteğe bağlı)</span>
      <textarea rows={2} value={note} maxLength={1000}
        onChange={(event) => { setNote(event.target.value); setMessage(null); }}
        placeholder="Örneğin drama uygulaması gelecek derse kaldı."/>
    </label>
    <div className="tracking-note-actions">
      {message && <span className={message === "Kaydedildi" ? "save-ok" : "form-error"} role="status">{message}</span>}
      <Button size="sm" variant="secondary" type="button" disabled={!isDirty || busy}
        onClick={() => void commit({ note })}><Save size={15}/>{busy ? "Kaydediliyor…" : "Notu kaydet"}</Button>
    </div>
  </div>;
}

export function WeekProgressEditor({ group, week, records, onSave }: Props) {
  const totals = weekHours(group, week, records);
  const beforeStart = week.week_number < group.startWeekNumber;
  return <section className="tracking-panel" aria-labelledby="progress-heading">
    <div className="tracking-heading">
      <div><span className="eyebrow">ŞUBE İLERLEMESİ · {group.label}</span>
        <h3 id="progress-heading">İşlenen dersler</h3></div>
      <Badge tone={totals.completed === totals.total && !beforeStart ? "success" : "neutral"}>
        {beforeStart ? "Takip dışı" : totals.completed + "/" + totals.total + " saat"}
      </Badge>
    </div>
    {beforeStart ? <p className="muted compact">
      Bu şubeyi {group.startWeekNumber}. haftadan itibaren takip etmeye başladınız.
      Önceki haftalar tamamlanmış kabul edilmez.
    </p> : <>
      <Progress label={group.label + " " + week.week_number + ". hafta ilerlemesi"}
        value={totals.completed} max={totals.total}/>
      <div className="tracking-lessons">
        {week.lessons.map((lesson) =>
          <LessonEditor key={[group.id, week.week_number, lesson.lesson_index].join(":")}
            group={group} weekNumber={week.week_number} lesson={lesson}
            record={progressForLesson(records, group.id, week.week_number, lesson.lesson_index)}
            onSave={onSave}/>
        )}
      </div>
    </>}
  </section>;
}
