import { useEffect, useMemo, useState } from "react";
import type { TeacherSnapshot, ProgressPatch } from "@/types/teacher";
import { createTeacherClass, loadTeacherSnapshot, removeTeacherClass, saveLessonProgress } from "@/lib/storage/teacher-db";
import { classSummary } from "@/features/progress/progress-model";
import { ClassDashboard } from "@/features/classes/ClassDashboard";
import { WeekProgressEditor } from "@/features/progress/WeekProgressEditor";
import {
  ArrowLeft, ArrowRight, BookMarked, BookOpen, CalendarDays, ChevronLeft, ChevronRight,
  CloudOff, FolderOpen, GraduationCap, Home, Layers3, Moon, Sun, UsersRound
} from "lucide-react";
import type { PublishedPlan } from "@/types/catalog";
import type { PlanCatalog, PlanWeek } from "@/types/catalog";
import { todayKey, closestWeekIndex, currentBreak, shortDate, weekRange } from "@/lib/week-date";
import { Button } from "@/components/arc/button/button";
import { Badge } from "@/components/arc/badge/badge";
import SegmentedControl from "@/components/arc/segmented-control/segmented-control";
import { Progress } from "@/components/arc/progress/progress";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/arc/tabs/tabs";

type Page = "haftam" | "siniflarim" | "planlar";
type ViewMode = "ozet" | "ayrinti";

const navigation = [
  { id: "haftam", label: "Haftam", Icon: Home },
  { id: "siniflarim", label: "Sınıflarım", Icon: UsersRound },
  { id: "planlar", label: "Planlar", Icon: CalendarDays }
] as const;

function pageFromHash(): Page {
  const hash = window.location.hash.slice(1);
  return hash === "siniflarim" || hash === "planlar" ? hash : "haftam";
}

function getStored(key: string, fallback: string): string {
  try { return localStorage.getItem(key) || fallback; } catch { return fallback; }
}

function setStored(key: string, value: string) {
  try { localStorage.setItem(key, value); } catch { /* private mode: non-fatal */ }
}

function Navigation({ page, onChange, mobile = false }: {
  page: Page; onChange: (value: Page) => void; mobile?: boolean;
}) {
  return <nav className={mobile ? "mobile-navigation" : "sidebar-navigation"} aria-label="Ana gezinme">
    {navigation.map(({ id, label, Icon }) =>
      <button type="button" key={id} className={"nav-link" + (page === id ? " is-active" : "")}
        aria-current={page === id ? "page" : undefined} onClick={() => onChange(id)}>
        <Icon size={19} strokeWidth={1.9} aria-hidden="true" />
        <span>{label}</span>
      </button>
    )}
  </nav>;
}

function WeekLessons({ week }: { week: PlanWeek }) {
  return <Tabs key={week.week_number} defaultValue="dersler" className="lesson-tabs">
    <TabsList aria-label="Haftalık plan ayrıntıları">
      <TabsTrigger value="dersler">Ders bölümleri</TabsTrigger>
      <TabsTrigger value="kazanimlar">Öğrenme çıktıları</TabsTrigger>
    </TabsList>
    <TabsContent value="dersler">
      <div className="lesson-list">
        {week.lessons.map((lesson) =>
          <article className="lesson-card" key={lesson.lesson_index}>
            <div className="lesson-card-top">
              <div>
                <span className="eyebrow">{lesson.learning_area || "Ders bölümü"}</span>
                <h3>{lesson.topic_title || lesson.learning_area || "Ders içeriği"}</h3>
              </div>
              <Badge tone="neutral">{lesson.hours} saat</Badge>
            </div>
            {lesson.texts_and_materials.length > 0 ?
              <ul className="material-list">
                {lesson.texts_and_materials.map((material, i) => <li key={i}>{material}</li>)}
              </ul> :
              <p className="muted compact">Bu bölüm için kaynakta ayrıntılı materyal belirtilmemiş.</p>}
          </article>
        )}
      </div>
    </TabsContent>
    <TabsContent value="kazanimlar">
      <div className="outcome-list">
        {week.lessons.map((lesson) =>
          <section key={lesson.lesson_index} className="outcome-section">
            <div className="outcome-heading">
              <span>{lesson.learning_area || lesson.topic_title}</span>
              <Badge tone="neutral">{lesson.hours} saat</Badge>
            </div>
            {lesson.outcomes.length ? lesson.outcomes.map((outcome, i) =>
              <div className="outcome-item" key={outcome.code + i}>
                {outcome.code ? <span className="outcome-code">{outcome.code}</span> : null}
                <p>{outcome.text}</p>
              </div>
            ) : <p className="muted compact">Bu bölümde öğrenme çıktısı belirtilmemiş.</p>}
          </section>
        )}
      </div>
    </TabsContent>
  </Tabs>;
}

function WeekSummary({ week, plan, expanded }: { week: PlanWeek; plan: PublishedPlan; expanded: boolean }) {
  return <div className="week-panel">
    <div className="week-panel-head">
      <div>
        <span className="eyebrow">{plan.grade_level}. SINIF · {plan.school_type.toLocaleUpperCase("tr-TR")}</span>
        <h2>{week.theme_name}</h2>
        <p className="muted">{weekRange(week)}</p>
      </div>
      <Badge tone="info">{week.total_hours} ders saati</Badge>
    </div>
    {week.special_days.length > 0 &&
      <div className="special-days">
        <CalendarDays size={17} aria-hidden="true" />
        <div><strong>Belirli gün ve haftalar</strong><p>{week.special_days.join(" · ")}</p></div>
      </div>
    }
    {week.hours_assumed &&
      <p className="subtle-warning">Bu haftanın ders saati kaynak planda açıkça belirtilmediği için varsayılan değer kullanılmaktadır.</p>}
    {expanded ? <WeekLessons week={week}/> :
      <div className="compact-lessons">
        {week.lessons.map((lesson) =>
          <div className="compact-lesson" key={lesson.lesson_index}>
            <div className="compact-lesson-icon"><BookOpen size={18} aria-hidden="true"/></div>
            <div className="compact-lesson-title">
              <strong>{lesson.learning_area || lesson.topic_title || "Ders"}</strong>
              <span>{lesson.texts_and_materials[0] || lesson.topic_title || "Planlanan ders içeriği"}</span>
            </div>
            <span className="compact-lesson-hours">{lesson.hours} sa.</span>
          </div>
        )}
      </div>}
  </div>;
}

function PlanPicker({ plans, selectedId, onSelect }: {
  plans: PublishedPlan[]; selectedId: string; onSelect: (id: string) => void;
}) {
  return <div className="plan-grid">
    {plans.map((plan) =>
      <button type="button" key={plan.id}
        className={"plan-card" + (selectedId === plan.id ? " selected" : "")}
        onClick={() => onSelect(plan.id)} aria-pressed={selectedId === plan.id}>
        <div className="plan-card-top"><span className="plan-icon"><BookMarked size={21}/></span>
          {selectedId === plan.id && <Badge tone="success">Seçili</Badge>}</div>
        <strong>{plan.grade_level}. Sınıf · {plan.school_type}</strong>
        <span>{plan.course_name}</span>
        <div className="plan-card-bottom"><small>{plan.academic_year} · {plan.total_weeks} hafta</small><ArrowRight size={17}/></div>
      </button>
    )}
  </div>;
}

export function App() {
  const [page, setPage] = useState<Page>(pageFromHash);
  const [catalog, setCatalog] = useState<PlanCatalog | null>(null);
  const [catalogError, setCatalogError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState(() => getStored("plan.pwa.selected-plan", ""));
  const [weekNumber, setWeekNumber] = useState<number | null>(null);
  const [teacherData, setTeacherData] = useState<TeacherSnapshot | null>(null);
  const [teacherError, setTeacherError] = useState<string | null>(null);
  const [selectedGroupId, setSelectedGroupId] = useState(() => getStored("plan.pwa.selected-group", ""));
  const [viewMode, setViewMode] = useState<ViewMode>("ozet");
  const [theme, setTheme] = useState(() => getStored("plan.pwa.theme", "light") === "dark" ? "dark" : "light");
  const [online, setOnline] = useState(navigator.onLine);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    document.documentElement.dataset.accent = "green";
    setStored("plan.pwa.theme", theme);
  }, [theme]);

  useEffect(() => {
    const onHashChange = () => setPage(pageFromHash());
    const onOnline = () => setOnline(true);
    const onOffline = () => setOnline(false);
    window.addEventListener("hashchange", onHashChange);
    window.addEventListener("online", onOnline);
    window.addEventListener("offline", onOffline);
    return () => {
      window.removeEventListener("hashchange", onHashChange);
      window.removeEventListener("online", onOnline);
      window.removeEventListener("offline", onOffline);
    };
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    fetch(import.meta.env.BASE_URL + "data/catalog.json", { signal: controller.signal })
      .then((response) => {
        if (!response.ok) throw new Error("HTTP " + response.status);
        return response.json() as Promise<PlanCatalog>;
      })
      .then((value) => {
        if (value.schema_version !== 1 || !Array.isArray(value.plans)) throw new Error("Geçersiz plan kataloğu.");
        setCatalog(value);
      })
      .catch((error: unknown) => {
        if (controller.signal.aborted) return;
        setCatalogError(error instanceof Error ? error.message : "Bilinmeyen hata");
      });
    return () => controller.abort();
  }, []);

  useEffect(() => {
    if (catalog?.plans.length && !catalog.plans.some((p) => p.id === selectedId)) {
      setSelectedId(catalog.plans[0]!.id);
    }
  }, [catalog, selectedId]);

  useEffect(() => { if (selectedId) setStored("plan.pwa.selected-plan", selectedId); }, [selectedId]);
  useEffect(() => { setStored("plan.pwa.selected-group", selectedGroupId); }, [selectedGroupId]);

  useEffect(() => {
    let alive = true;
    loadTeacherSnapshot().then((value) => {
      if (alive) setTeacherData(value);
    }).catch((reason: unknown) => {
      if (alive) setTeacherError(reason instanceof Error ? reason.message : "Yerel veritabanına erişilemiyor.");
    });
    return () => { alive = false; };
  }, []);

  useEffect(() => {
    if (teacherData && selectedGroupId && !teacherData.classes.some((entry) => entry.id === selectedGroupId)) {
      setSelectedGroupId("");
      setWeekNumber(null);
    }
  }, [teacherData, selectedGroupId]);

  async function refreshTeacherData() {
    const snapshot = await loadTeacherSnapshot();
    setTeacherData(snapshot);
    setTeacherError(null);
  }

  async function addGroup(planId: string, label: string, startWeekNumber: number) {
    const plan = catalog?.plans.find((entry) => entry.id === planId);
    if (!plan || !plan.weeks.some((week) => week.week_number === startWeekNumber)) {
      throw new Error("Geçerli bir yıllık plan ve başlangıç haftası seçin.");
    }
    const group = await createTeacherClass(planId, label, startWeekNumber);
    await refreshTeacherData();
    setSelectedGroupId(group.id);
    setWeekNumber(startWeekNumber);
    openPage("haftam");
  }

  async function deleteGroup(id: string) {
    await removeTeacherClass(id);
    await refreshTeacherData();
    if (selectedGroupId === id) {
      setSelectedGroupId("");
      setWeekNumber(null);
    }
  }

  async function recordProgress(patch: ProgressPatch) {
    const group = teacherData?.classes.find((entry) => entry.id === patch.classId);
    const plan = catalog?.plans.find((entry) => entry.id === group?.planId);
    const lesson = plan?.weeks.find((week) => week.week_number === patch.weekNumber)
      ?.lessons.find((item) => item.lesson_index === patch.lessonIndex);
    if (!group || !lesson || lesson.hours !== patch.maxHours) {
      throw new Error("Ders bölümü planla eşleşmiyor.");
    }
    // Keep the edited week visible even when completing it changes "kaldığım yer".
    if (weekNumber === null) setWeekNumber(patch.weekNumber);
    await saveLessonProgress(patch);
    await refreshTeacherData();
  }

  function selectGroup(id: string) {
    const group = teacherData?.classes.find((item) => item.id === id);
    const plan = catalog?.plans.find((item) => item.id === group?.planId);
    const summary = group && plan && teacherData ? classSummary(group, plan, teacherData.progress) : null;
    setSelectedGroupId(id);
    setWeekNumber(summary?.nextWeekNumber ?? (group?.startWeekNumber ?? null));
    setViewMode("ozet");
  }

  const selectedPlan = catalog?.plans.find((item) => item.id === selectedId);
  const selectedGroup = teacherData?.classes.find((item) => item.id === selectedGroupId);
  const activePlan = selectedGroup ? catalog?.plans.find((item) => item.id === selectedGroup.planId) : selectedPlan;
  const groupSummary = selectedGroup && activePlan && teacherData
    ? classSummary(selectedGroup, activePlan, teacherData.progress)
    : null;
  const currentIndex = useMemo(() => closestWeekIndex(activePlan?.weeks || []), [activePlan]);
  const defaultWeekIndex = selectedGroup && activePlan && groupSummary
    ? Math.max(0, activePlan.weeks.findIndex((week) =>
        week.week_number === (groupSummary.nextWeekNumber ?? activePlan.weeks[activePlan.weeks.length - 1]?.week_number)))
    : currentIndex;
  const requestedIndex = activePlan?.weeks.findIndex((week) => week.week_number === weekNumber) ?? -1;
  const weekIndex = requestedIndex >= 0 ? requestedIndex : defaultWeekIndex;
  const selectedWeek = activePlan?.weeks[weekIndex];
  const holiday = activePlan ? currentBreak(activePlan) : undefined;
  const inSchedule = selectedWeek && selectedWeek.start_date <= todayKey() && todayKey() <= selectedWeek.end_date;

  function moveWeek(delta: number) {
    if (!activePlan) return;
    const next = activePlan.weeks[weekIndex + delta];
    if (next) setWeekNumber(next.week_number);
  }


  function openPage(next: Page) {
    setPage(next);
    window.location.hash = next;
    window.scrollTo({ top: 0, behavior: "auto" });
  }

  function selectPlan(id: string) {
    setSelectedId(id);
    setSelectedGroupId("");
    setWeekNumber(null);
    setViewMode("ozet");
  }

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-icon"><BookOpen size={23} strokeWidth={1.9}/></div>
        <div className="brand-text"><strong>Haftalık Plan</strong><span>Öğretmen çalışma alanı</span></div>
      </div>
      <div className="sidebar-section-label">ÇALIŞMA ALANI</div>
      <Navigation page={page} onChange={openPage}/>
      <div className="sidebar-bottom">
        <p>2026–2027 öğretim yılı</p>
        <span>Planlar kaynak arşivden salt okunur yüklenir.</span>
      </div>
    </aside>

    <div className="main-column">
      <header className="topbar">
        <div className="topbar-identity">
          <div className="mobile-brand-icon"><BookOpen size={20} aria-hidden="true"/></div>
          <div><span className="topbar-kicker">ÇALIŞMA ALANI</span><strong>{navigation.find((item) => item.id === page)?.label}</strong></div>
        </div>
        <div className="topbar-controls">
          {!online && <span className="connection-status"><CloudOff size={15}/> Çevrimdışı</span>}
          <Button variant="ghost" size="sm" className="theme-button"
            aria-label={theme === "light" ? "Koyu temaya geç" : "Açık temaya geç"}
            title={theme === "light" ? "Koyu tema" : "Açık tema"}
            onClick={() => setTheme(theme === "light" ? "dark" : "light")}>
            {theme === "light" ? <Moon size={18}/> : <Sun size={18}/>}
          </Button>
        </div>
      </header>

      <main className="page" id="main-content">
        {page === "haftam" && <>
          <div className="page-heading">
            <span className="eyebrow">HAFTALIK BAKIŞ</span>
            <h1>Haftam</h1>
            <p>Yıllık planını incele, şubeni seç ve hangi derste kaldığını takip et.</p>
          </div>
          {!catalog && !catalogError && <div className="notice-surface">Plan kataloğu yükleniyor…</div>}
          {catalogError && <div className="notice-surface error" role="alert">Planlar yüklenemedi: {catalogError}. Bağlantınızı veya katalog üretimini kontrol edin.</div>}
          {catalog && !selectedPlan && <div className="empty-surface">
            <FolderOpen size={36}/><h2>Henüz doğrulanmış bir plan yok</h2>
            <p>Kaynak planlar doğrulanınca burada haftalık içerikler listelenecek.</p>
          </div>}
          {teacherError && <div className="notice-surface error" role="alert">
            Yerel ilerleme veritabanına erişilemedi: {teacherError}. Kayıt özellikleri devre dışı.
          </div>}
          {teacherData && teacherData.classes.length > 0 && <div className="active-class-select">
            <label className="field"><span>Görüntülenecek şube</span>
              <select value={selectedGroupId} onChange={(event) => selectGroup(event.target.value)}>
                <option value="">Yalnızca yıllık plan</option>
                {teacherData.classes.map((group) =>
                  <option key={group.id} value={group.id}>{group.label}</option>)}
              </select>
            </label>
            {selectedGroup && groupSummary &&
              <Button variant="secondary" size="sm" disabled={groupSummary.nextWeekNumber === null}
                onClick={() => setWeekNumber(groupSummary.nextWeekNumber)}>
                Kaldığım yere git
              </Button>}
          </div>}
          {selectedGroup && !activePlan && <div className="notice-surface error" role="alert">
            Bu şubenin yıllık planı artık katalogda bulunmuyor. Kayıtlarınız saklanıyor.
          </div>}
          {activePlan && selectedWeek && <>
            <div className="week-top-controls">
              <div className="week-dates">
                <span className="week-label">ÖĞRETİM HAFTASI</span>
                <strong>{selectedWeek.week_number}. hafta</strong>
                <span className="muted">{weekRange(selectedWeek)}</span>
              </div>
              <div className="week-actions">
                <Button size="sm" variant="secondary" aria-label="Önceki hafta"
                  disabled={weekIndex === 0} onClick={() => moveWeek(-1)}><ChevronLeft size={19}/></Button>
                <Button size="sm" variant="secondary" onClick={() => setWeekNumber(activePlan.weeks[currentIndex]?.week_number ?? null)}>Bugüne git</Button>
                <Button size="sm" variant="secondary" aria-label="Sonraki hafta"
                  disabled={weekIndex === activePlan.weeks.length - 1} onClick={() => moveWeek(1)}><ChevronRight size={19}/></Button>
              </div>
            </div>
            {holiday && <div className="holiday-notice"><CalendarDays size={18}/><span>Bugün {holiday.name.toLocaleLowerCase("tr-TR")} döneminde. Gösterilen hafta sıradaki öğretim haftasıdır.</span></div>}
            <div className="week-status-line">
              <Badge tone="info">{inSchedule ? "Bu haftanın planı" : "Planlanan hafta"}</Badge>
              <span>{selectedGroup && groupSummary ? selectedGroup.label + " · " +
                groupSummary.completedHours + "/" + groupSummary.plannedHours + " takip edilen ders saati" :
                "Şube seçilmedi · salt okunur plan"}</span>
            </div>
            <div className="view-controls">
              <SegmentedControl label="Haftalık plan görünümü" value={viewMode} onValueChange={(value) => setViewMode(value as ViewMode)}
                options={[{ value: "ozet", label: "Özet" }, { value: "ayrinti", label: "Ayrıntı" }]}/>
            </div>
            <WeekSummary week={selectedWeek} plan={activePlan} expanded={viewMode === "ayrinti"}/>
            {selectedGroup && teacherData && <WeekProgressEditor group={selectedGroup}
              week={selectedWeek} records={teacherData.progress} onSave={recordProgress}/>}
            {!selectedGroup && teacherData?.classes.length === 0 &&
              <div className="tracking-start">
                <div><strong>Şubelerinizde ilerlemeyi takip edin</strong>
                  <p className="muted compact">İşlenen ders saatleri yalnızca cihazınızda saklanır.</p></div>
                <Button variant="secondary" size="sm" onClick={() => openPage("siniflarim")}>Şube ekle <ArrowRight size={16}/></Button>
              </div>}
            <div className="timeline-surface">
              <div className="timeline-label"><span>Yıllık plan takvimi</span><strong>{selectedWeek.week_number} / {activePlan.total_weeks} hafta</strong></div>
              <Progress label="Yıllık plandaki hafta konumu" value={selectedWeek.week_number} max={activePlan.total_weeks}/>
              <p className="muted compact">Bu gösterge takvimdeki konumu gösterir; işlenen ders ilerlemesi değildir.</p>
            </div>
          </>}
        </>}

        {page === "siniflarim" && <>
          <div className="page-heading">
            <span className="eyebrow">ŞUBE YÖNETİMİ</span>
            <h1>Sınıflarım</h1>
            <p>Her şubenin ders saatlerini, kaldığınız haftayı ve ders notlarınızı ayrı takip edin.</p>
          </div>
          {teacherError && <div className="notice-surface error" role="alert">
            Yerel veritabanı açılamadı: {teacherError}. Tarayıcınızın depolama izinlerini kontrol edin.
          </div>}
          {!teacherData && !teacherError && <div className="notice-surface">Şubeler yükleniyor…</div>}
          {teacherData && <ClassDashboard plans={catalog?.plans || []}
            classes={teacherData.classes} progress={teacherData.progress}
            onCreate={addGroup}
            onOpen={(id) => { selectGroup(id); openPage("haftam"); }}
            onDelete={deleteGroup}/>}
        </>}

        {page === "planlar" && <>
          <div className="page-heading">
            <span className="eyebrow">DOĞRULANMIŞ KAYNAKLAR</span>
            <h1>Planlar</h1>
            <p>GitHub arşivindeki uygun yıllık planları incele ve haftalık görünüme geç.</p>
          </div>
          {catalogError && <div className="notice-surface error" role="alert">{catalogError}</div>}
          {!catalog && !catalogError && <div className="notice-surface">Plan kataloğu yükleniyor…</div>}
          {catalog && <>
          {teacherData && teacherData.classes.length > 0 &&
            <p className="muted compact">{teacherData.classes.length} kayıtlı şubeniz var. Plan seçimi, bu şubelerin ilerlemesini değiştirmez.</p>}
            <div className="section-title"><h2>Kullanılabilir planlar</h2><Badge>{catalog.plans.length} plan</Badge></div>
            {catalog.plans.length > 0 ?
              <PlanPicker plans={catalog.plans} selectedId={selectedId} onSelect={selectPlan}/> :
              <div className="notice-surface">Şu anda yayımlanabilir plan bulunamadı.</div>}
            {selectedPlan && <>
              <div className="selected-plan-overview">
                <div className="section-title"><h2>Seçili plan</h2><Badge tone="success">Kaynak eşleşti</Badge></div>
                <p><strong>{selectedPlan.document_title}</strong></p>
                <p className="muted">{selectedPlan.total_weeks} öğretim haftası · Haftada {selectedPlan.weekly_hours} ders saati</p>
                <div className="plan-overview-actions">
                  <Button onClick={() => openPage("haftam")}>Haftalık plana geç <ArrowRight size={16}/></Button>
                </div>
              </div>
            </>}
            {catalog.skipped.length > 0 && <p className="archive-note">Kaynak veya veri uyuşmazlığı nedeniyle {catalog.skipped.length} plan yayımlanmadı. Bunlar yanlış okul türünde gösterilmez.</p>}
          </>}
        </>}
      </main>
      <footer className="footer"><span>Haftalık Plan · Kaynak: knigdelioglu/plan</span><span>Resmî MEB uygulaması değildir.</span></footer>
    </div>
    <Navigation page={page} onChange={openPage} mobile/>
  </div>;
}
