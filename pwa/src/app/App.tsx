import { useEffect, useMemo, useState } from "react";
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
  const [weekOffset, setWeekOffset] = useState(0);
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

  const selectedPlan = catalog?.plans.find((item) => item.id === selectedId);
  const currentIndex = useMemo(() => closestWeekIndex(selectedPlan?.weeks || []), [selectedPlan]);
  const weekIndex = selectedPlan ? Math.max(0, Math.min(selectedPlan.weeks.length - 1, currentIndex + weekOffset)) : 0;
  const selectedWeek = selectedPlan?.weeks[weekIndex];
  const holiday = selectedPlan ? currentBreak(selectedPlan) : undefined;
  const inSchedule = selectedWeek && selectedWeek.start_date <= todayKey() && todayKey() <= selectedWeek.end_date;

  function openPage(next: Page) {
    setPage(next);
    window.location.hash = next;
    window.scrollTo({ top: 0, behavior: "auto" });
  }

  function selectPlan(id: string) {
    setSelectedId(id);
    setWeekOffset(0);
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
            <p>Yıllık planını görüntüle, dersine hazırlan. Şube ilerlemesi sonraki aşamada eklenecek.</p>
          </div>
          {!catalog && !catalogError && <div className="notice-surface">Plan kataloğu yükleniyor…</div>}
          {catalogError && <div className="notice-surface error" role="alert">Planlar yüklenemedi: {catalogError}. Bağlantınızı veya katalog üretimini kontrol edin.</div>}
          {catalog && !selectedPlan && <div className="empty-surface">
            <FolderOpen size={36}/><h2>Henüz doğrulanmış bir plan yok</h2>
            <p>Kaynak planlar doğrulanınca burada haftalık içerikler listelenecek.</p>
          </div>}
          {selectedPlan && selectedWeek && <>
            <div className="week-top-controls">
              <div className="week-dates">
                <span className="week-label">ÖĞRETİM HAFTASI</span>
                <strong>{selectedWeek.week_number}. hafta</strong>
                <span className="muted">{weekRange(selectedWeek)}</span>
              </div>
              <div className="week-actions">
                <Button size="sm" variant="secondary" aria-label="Önceki hafta"
                  disabled={weekIndex === 0} onClick={() => setWeekOffset(weekOffset - 1)}><ChevronLeft size={19}/></Button>
                <Button size="sm" variant="secondary" onClick={() => setWeekOffset(0)}>Bugüne git</Button>
                <Button size="sm" variant="secondary" aria-label="Sonraki hafta"
                  disabled={weekIndex === selectedPlan.weeks.length - 1} onClick={() => setWeekOffset(weekOffset + 1)}><ChevronRight size={19}/></Button>
              </div>
            </div>
            {holiday && <div className="holiday-notice"><CalendarDays size={18}/><span>Bugün {holiday.name.toLocaleLowerCase("tr-TR")} döneminde. Gösterilen hafta sıradaki öğretim haftasıdır.</span></div>}
            <div className="week-status-line">
              <Badge tone="info">{inSchedule ? "Bu haftanın planı" : "Planlanan hafta"}</Badge>
              <span>Şube ilerlemesi henüz kaydedilmiyor</span>
            </div>
            <div className="view-controls">
              <SegmentedControl label="Haftalık plan görünümü" value={viewMode} onValueChange={(value) => setViewMode(value as ViewMode)}
                options={[{ value: "ozet", label: "Özet" }, { value: "ayrinti", label: "Ayrıntı" }]}/>
            </div>
            <WeekSummary week={selectedWeek} plan={selectedPlan} expanded={viewMode === "ayrinti"}/>
            <div className="timeline-surface">
              <div className="timeline-label"><span>Yıllık plan takvimi</span><strong>{selectedWeek.week_number} / {selectedPlan.total_weeks} hafta</strong></div>
              <Progress label="Yıllık plandaki hafta konumu" value={selectedWeek.week_number} max={selectedPlan.total_weeks}/>
              <p className="muted compact">Bu gösterge takvimdeki konumu gösterir; işlenen ders ilerlemesi değildir.</p>
            </div>
          </>}
        </>}

        {page === "siniflarim" && <>
          <div className="page-heading">
            <span className="eyebrow">ŞUBE YÖNETİMİ</span>
            <h1>Sınıflarım</h1>
            <p>Her şubenin gerçek ilerlemesi, işlendiği saatler ve kişisel notlar burada tutulacak.</p>
          </div>
          <div className="empty-surface">
            <div className="empty-icon"><GraduationCap size={30}/></div>
            <h2>Şube takibi henüz etkin değil</h2>
            <p>Bu sürüm uygulamanın çalışan kabuğudur. Şube ekleme ve cihazda ilerleme kaydı sonraki geliştirme aşamasında açılacak.</p>
            <Button variant="secondary" onClick={() => openPage("planlar")}>Mevcut planlara bak <ArrowRight size={16}/></Button>
          </div>
          <div className="feature-preview-grid">
            <div className="feature-preview"><Layers3 size={19}/><strong>Her şube ayrı</strong><span>Aynı planı kullanan sınıflarda bağımsız ilerleme</span></div>
            <div className="feature-preview"><BookMarked size={19}/><strong>Ders saati bazında</strong><span>3+2 bölünmüş haftaları koruyan kayıt modeli</span></div>
          </div>
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
