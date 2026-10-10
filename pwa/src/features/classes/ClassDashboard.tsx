import { useState, type FormEvent } from "react";
import { ArrowRight, BookOpen, Plus, Trash2, UsersRound } from "lucide-react";
import type { PublishedPlan } from "../../types/catalog";
import type { LessonProgress, TeacherClass } from "../../types/teacher";
import { closestWeekIndex } from "../../lib/week-date";
import { classSummary } from "../progress/progress-model";
import { Button } from "../../components/arc/button/button";
import { Badge } from "../../components/arc/badge/badge";
import { Progress } from "../../components/arc/progress/progress";

interface Props {
  plans: PublishedPlan[];
  classes: TeacherClass[];
  progress: LessonProgress[];
  onCreate: (planId: string, label: string, startWeekNumber: number) => Promise<void>;
  onOpen: (id: string) => void;
  onDelete: (id: string) => Promise<void>;
}

export function ClassDashboard({ plans, classes, progress, onCreate, onOpen, onDelete }: Props) {
  const [label, setLabel] = useState("");
  const [planId, setPlanId] = useState("");
  const [startWeek, setStartWeek] = useState<number | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const selectedPlan = plans.find((plan) => plan.id === planId) ?? plans[0];
  const defaultWeek = selectedPlan ? (selectedPlan.weeks[closestWeekIndex(selectedPlan.weeks)]?.week_number ?? 1) : 1;
  const initialWeek = startWeek !== null && selectedPlan?.weeks.some((week) => week.week_number === startWeek) ? startWeek : defaultWeek;

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedPlan || busy) return;
    setBusy(true);
    setError(null);
    try {
      await onCreate(selectedPlan.id, label, initialWeek);
      setLabel("");
      setStartWeek(null);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Şube oluşturulamadı.");
    } finally { setBusy(false); }
  }

  async function remove(group: TeacherClass) {
    if (!window.confirm(group.label + " şubesini ve bu şubenin bütün ilerleme kayıtlarını silmek istiyor musunuz? Bu işlem geri alınamaz.")) return;
    setBusy(true);
    setError(null);
    try { await onDelete(group.id); }
    catch (reason) { setError(reason instanceof Error ? reason.message : "Şube silinemedi."); }
    finally { setBusy(false); }
  }

  return <div className="classes-dashboard">
    <div className="section-title"><h2>Şubelerim</h2><Badge>{classes.length} şube</Badge></div>
    {classes.length === 0 && <div className="empty-surface compact-empty">
      <UsersRound size={32} aria-hidden="true"/>
      <h2>Henüz şube eklenmedi</h2>
      <p>Aynı yıllık planı kullanan şubelerin ilerlemesini birbirinden bağımsız takip edebilirsiniz.</p>
    </div>}
    <div className="class-grid">
      {classes.map((group) => {
        const plan = plans.find((entry) => entry.id === group.planId);
        const summary = plan ? classSummary(group, plan, progress) : null;
        return <article className="class-tile" key={group.id}>
          <div className="class-tile-heading">
            <span className="class-tile-icon"><BookOpen size={19} aria-hidden="true"/></span>
            <div className="class-tile-title"><h3>{group.label}</h3><p>{plan ? plan.grade_level + ". Sınıf · " + plan.school_type : "Plan artık mevcut değil"}</p></div>
            <button type="button" className="icon-discreet" aria-label={group.label + " şubesini sil"}
              title="Şubeyi sil" disabled={busy} onClick={() => void remove(group)}><Trash2 size={17}/></button>
          </div>
          {plan && summary ? <>
            <div className="class-tile-metrics"><span>Takip başlangıcı: {group.startWeekNumber}. hafta</span>
              <strong>{summary.completedHours}/{summary.plannedHours} saat</strong></div>
            <Progress value={summary.completedHours} max={summary.plannedHours || 1}
              label={group.label + " ilerlemesi"} />
            <p className="class-next">{summary.completed ? "Takip edilen dersler tamamlandı" :
              summary.nextWeekNumber !== null ? "Kaldığınız yer: " + summary.nextWeekNumber + ". hafta" : "Kayıt bulunmuyor"}</p>
            <Button variant="secondary" size="sm" onClick={() => onOpen(group.id)}>
              Şubeyi aç <ArrowRight size={16} aria-hidden="true"/>
            </Button>
          </> : <p className="subtle-warning">Bu şubenin bağlı olduğu plan katalogda bulunamadı. Kayıtlar korunur.</p>}
        </article>;
      })}
    </div>
    <form className="class-form" onSubmit={(event) => void submit(event)}>
      <div className="section-title"><h2>Yeni şube ekle</h2><Plus size={18} aria-hidden="true"/></div>
      <div className="class-form-grid">
        <label className="field"><span>Şube adı</span>
          <input value={label} onChange={(event) => setLabel(event.target.value)}
            required minLength={2} maxLength={50} placeholder="Örneğin 11/A" autoComplete="off"/>
        </label>
        <label className="field"><span>Yıllık plan</span>
          <select value={selectedPlan?.id ?? ""} onChange={(event) => { setPlanId(event.target.value); setStartWeek(null); }}
            disabled={!plans.length}>
            {plans.map((plan) => <option key={plan.id} value={plan.id}>
              {plan.grade_level}. Sınıf · {plan.school_type} · {plan.academic_year}
            </option>)}
          </select>
        </label>
        <label className="field"><span>Takibe başlanacak hafta</span>
          <select value={initialWeek} disabled={!selectedPlan}
            onChange={(event) => setStartWeek(Number(event.target.value))}>
            {selectedPlan?.weeks.map((week) => <option key={week.week_number} value={week.week_number}>
              {week.week_number}. hafta
            </option>)}
          </select>
        </label>
      </div>
      <p className="muted compact">Önceki haftalar tamamlanmış sayılmaz; seçtiğiniz haftadan itibaren takip başlar.</p>
      {error && <p className="form-error" role="alert">{error}</p>}
      <Button type="submit" disabled={!selectedPlan || busy || label.trim().length < 2}>
        <Plus size={17} aria-hidden="true"/>{busy ? "Kaydediliyor…" : "Şubeyi ekle"}
      </Button>
    </form>
  </div>;
}
