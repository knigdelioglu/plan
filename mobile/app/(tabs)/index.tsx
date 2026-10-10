import React,{useEffect,useMemo,useState} from 'react';
import {Alert,Text,View} from 'react-native';
import {router} from 'expo-router';
import {Ionicons} from '@expo/vector-icons';
import {MotionIconButton,MotionSegmentedControl} from '@native-motion/react-native';
import {Screen,Card,Label,Heading,Muted,Pill,AppButton,AppProgress,uiStyles} from '@/components/ui';
import {useApp} from '@/app/AppProvider';
import {useAppTheme} from '@/theme';
import {plans,findPlan,defaultWeek,currentBreak,dateLabel} from '@/features/catalog/queries';
import {classSummary,weekSummary} from '@/features/progress/model';
import {LessonCard} from '@/features/weeks/LessonCard';

export default function WeekScreen() {
 const p=useAppTheme();
 const {snapshot,loading,error,selectedClassId,selectedPlanId,setSelectedClass}=useApp();
 const [chosenWeek,setChosenWeek]=useState<number|null>(null);
 const [changing,setChanging]=useState(false);
 const group=snapshot.classes.find(c=>c.id===selectedClassId);
 const plan=group?findPlan(group.planId):(findPlan(selectedPlanId??'')??plans[0]);
 const summary=group&&plan?classSummary(group,plan,snapshot.records):null;
 const initial=group&&plan?plan.weeks.find(w=>w.number===(summary?.next?.week??group.startWeek)):
   plan?defaultWeek(plan):undefined;
 const week=plan?.weeks.find(w=>w.number===chosenWeek)??initial;
 const weekIndex=plan&&week?plan.weeks.findIndex(w=>w.number===week.number):-1;
 const holiday=plan?currentBreak(plan):undefined;
 useEffect(()=>{setChosenWeek(null);},[selectedClassId,selectedPlanId]);
 const segments=useMemo(()=>[
   {id:'preview',label:'Plan önizleme'},
   ...snapshot.classes.map(c=>({id:c.id,label:c.name}))
 ],[snapshot.classes]);
 async function changeGroup(id:string) {
   if(changing)return;
   setChanging(true);
   try{await setSelectedClass(id==='preview'?null:id);}
   catch(e){Alert.alert('Şube seçilemedi',String(e));}
   finally{setChanging(false);}
 }
 function shiftWeek(delta:number) {
   if(!plan||weekIndex<0)return;
   const next=plan.weeks[weekIndex+delta];
   if(next)setChosenWeek(next.number);
 }
 if(loading)return <Screen eyebrow="HAFTALIK PLAN" title="Haftam"><Card><Muted>Yerel kayıtlar yükleniyor…</Muted></Card></Screen>;
 return <Screen eyebrow="ÖĞRETMEN ÇALIŞMA ALANI" title="Haftam"
   subtitle="Dersinizin akışını görün, her şubenin kaldığı yeri ayrı takip edin.">
   {error?<Card><Pill tone="warning">Depolama hatası</Pill><Muted>{error}</Muted></Card>:null}
   {snapshot.classes.length>0?<Card>
     <Label>HANGİ ŞUBE?</Label>
     <MotionSegmentedControl label="Görüntülenen şube" segments={segments}
       selection={selectedClassId??'preview'} onChange={id=>void changeGroup(id)} disabled={changing}/>
     {group?<View style={uiStyles.between}>
       <Muted>{group.name} · {plan?.grade??''}. sınıf</Muted>
       <Pill tone="success">Yerel kayıt</Pill>
     </View>:<Muted>Yalnızca kaynağı görüntülüyorsunuz; ilerleme kaydı yapılmaz.</Muted>}
   </Card>:<Card style={{gap:11}}>
     <Heading>İlk şubenizi ekleyin</Heading>
     <Muted>11/A ve 11/B gibi şubeleri bağımsız izleyebilir, işlenen ders saatlerini saklayabilirsiniz.</Muted>
     <AppButton label="Şube oluşturmaya başla" onPress={()=>router.push('/classes')}/>
   </Card>}
   {group&&!plan?<Card><Pill tone="warning">Plan bulunamadı</Pill>
     <Muted>Bu şubenin planı doğrulanmış katalogda yok. Yerel kayıtlarınız korunuyor.</Muted>
   </Card>:null}
   {!plan?<Card><Muted>Henüz yayımlanabilir bir yıllık plan yok.</Muted></Card>:null}
   {plan&&week?<React.Fragment>
     <Card style={{backgroundColor:p.elevatedSurface}}>
       <View style={uiStyles.between}>
         <Label>2026–2027 · {plan.grade}. SINIF</Label>
         <Pill>{plan.school}</Pill>
       </View>
       <Text style={{color:p.ink,fontWeight:'800',fontSize:26,letterSpacing:-0.6}}>{week.theme}</Text>
       <View style={uiStyles.between}>
         <View style={{gap:5}}>
           <Label>ÖĞRETİM HAFTASI</Label>
           <Text style={{color:p.ink,fontSize:17,fontWeight:'700'}}>{week.number}. hafta</Text>
           <Muted>{dateLabel(week.start,week.end)}</Muted>
         </View>
         <View style={{flexDirection:'row',alignItems:'center',gap:7}}>
           <MotionIconButton label="Önceki haftaya git" disabled={weekIndex<=0}
             icon={<Ionicons name="chevron-back" color={p.accent} size={21}/>} onPress={()=>shiftWeek(-1)}/>
           <MotionIconButton label="Sonraki haftaya git" disabled={weekIndex>=plan.weeks.length-1}
             icon={<Ionicons name="chevron-forward" color={p.accent} size={21}/>} onPress={()=>shiftWeek(1)}/>
         </View>
       </View>
       <AppButton label="Bugünün haftasına dön" variant="ghost"
         onPress={()=>setChosenWeek(defaultWeek(plan)?.number??null)}/>
     </Card>
     {holiday?<Card><Pill tone="warning">Tatil dönemi</Pill>
       <Muted>{holiday.name} ({dateLabel(holiday.start,holiday.end)}). Tatiller öğretim haftası sayılmaz.</Muted>
     </Card>:null}
     {week.hoursAssumed?<Card><Pill tone="warning">Varsayılan ders saati</Pill>
       <Muted>Bu haftanın ders saati kaynak belgede açıkça doğrulanmadı.</Muted>
     </Card>:null}
     {week.specialDays.length>0?<Card>
       <Label>BELİRLİ GÜN VE HAFTALAR</Label>
       {week.specialDays.map((d,i)=><Muted key={i}>{'• '+d}</Muted>)}
     </Card>:null}
     {group&&summary?<React.Fragment>
       <AppProgress label={group.name+' · yıl boyu ilerleme'} value={summary.done} max={summary.total}/>
       {summary.next?<Card style={{gap:9}}>
         <Label>İLK TAMAMLANMAMIŞ BÖLÜM</Label>
         <Heading>{summary.next.week}. hafta · {summary.next.lesson}. ders bölümü</Heading>
         <AppButton label="Kaldığım yere git" variant="secondary"
           onPress={()=>setChosenWeek(summary.next!.week)}/>
       </Card>:<Card><Pill tone="success">Takip edilen tüm dersler tamamlandı</Pill></Card>}
     </React.Fragment>:null}
     <View style={uiStyles.between}>
       <Heading>Haftanın dersleri</Heading>
       <Pill>{week.lessons.length} bölüm · {week.hours} saat</Pill>
     </View>
     {group&&week.number>=group.startWeek?<React.Fragment>
       <AppProgress label={group.name+' · '+week.number+'. hafta'} value={weekSummary(week,group.id,snapshot.records).done} max={week.hours}/>
     </React.Fragment>:null}
     {week.lessons.map(l=><LessonCard key={[group?.id??'preview',week.number,l.index].join(':')}
       lesson={l} weekNumber={week.number} classId={group?.id??null}
       onBeforeSave={()=>setChosenWeek(week.number)}
       canTrack={Boolean(group&&week.number>=group.startWeek)}/>)}
     {group&&week.number<group.startWeek?<Muted>
       Bu şube {group.startWeek}. haftadan itibaren takip ediliyor. Önceki haftalar tamamlanmış sayılmaz.
     </Muted>:null}
   </React.Fragment>:null}
 </Screen>;
}
