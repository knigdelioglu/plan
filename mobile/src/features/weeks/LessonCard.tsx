import React,{useEffect,useState} from 'react';
import {Alert,TextInput,Text,View} from 'react-native';
import {Ionicons} from '@expo/vector-icons';
import {MotionExpandableCard,MotionIconButton} from '@native-motion/react-native';
import {useAppTheme} from '../../theme';
import {AppButton,Card,Heading,Label,Muted,Pill,SaveStatus} from '../../components/ui';
import {useApp} from '../../app/AppProvider';
import type {Lesson,PlanWeek} from '../catalog/types';
import {progressFor} from '../progress/model';

export function LessonCard({lesson,weekNumber,classId,canTrack,onBeforeSave}:{lesson:Lesson;weekNumber:number;classId:string|null;canTrack:boolean;onBeforeSave:()=>void}) {
 const p=useAppTheme(),{snapshot,saveLesson}=useApp();
 const record=classId?progressFor(snapshot.records,classId,weekNumber,lesson.index):undefined;
 const hours=record?.doneHours??0;
 const [expanded,setExpanded]=useState(false);
 const [note,setNote]=useState(record?.note??'');
 const [busy,setBusy]=useState(false);
 const [status,setStatus]=useState<'saving'|'saved'|'error'|null>(null);
 useEffect(()=>{setNote(record?.note??'');setStatus(null);},[record?.updatedAt,classId,weekNumber,lesson.index]);
 async function persist(nextHours:number,nextNote=note) {
   if(!classId||busy)return;
   onBeforeSave();
   setBusy(true);setStatus('saving');
   try{await saveLesson(classId,weekNumber,lesson.index,nextHours,nextNote);setStatus('saved');}
   catch(error){setStatus('error');Alert.alert('Kayıt yapılamadı',error instanceof Error?error.message:String(error));}
   finally{setBusy(false);}
 }
 const title=lesson.area||lesson.title||'Ders bölümü';
 return <Card style={{gap:13}}>
   <View style={{flexDirection:'row',justifyContent:'space-between',gap:12,alignItems:'center'}}>
     <View style={{flex:1,gap:5}}>
       <Label>{lesson.index}. DERS BÖLÜMÜ</Label>
       <Text style={{fontWeight:'700',fontSize:17,color:p.ink}}>{title}</Text>
     </View>
     <Pill tone={canTrack&&hours===lesson.hours?'success':'neutral'}>{canTrack?hours+'/':''}{lesson.hours} saat</Pill>
   </View>
   {lesson.materials.slice(0,2).map((m,i)=><Muted key={i}>{'• '+m}</Muted>)}
   <MotionExpandableCard title="İçerik ve öğrenme çıktıları" expanded={expanded} onChange={setExpanded}>
     <View style={{gap:12,paddingTop:9}}>
       {lesson.outcomes.length>0?<View style={{gap:7}}>
         <Label>ÖĞRENME ÇIKTILARI</Label>
         {lesson.outcomes.map((o,i)=><Text key={o.code+i} style={{color:p.ink,fontSize:13,lineHeight:20}}>
           {o.text||o.code}
         </Text>)}
       </View>:null}
       {lesson.processes.length>0?<View style={{gap:10}}>
         <Label>ÖĞRETİM SÜRECİ</Label>
         {lesson.processes.map((process,i)=><View key={i} style={{gap:5}}>
           <Text style={{fontWeight:'700',color:p.ink,fontSize:13}}>{process.title}</Text>
           {process.items.map((item,j)=><Muted key={j}>{'• '+item}</Muted>)}
         </View>)}
       </View>:null}
       {!lesson.outcomes.length&&!lesson.processes.length?<Muted>Kaynakta bu bölüm için ek ayrıntı bulunmuyor.</Muted>:null}
     </View>
   </MotionExpandableCard>
   {classId&&canTrack?<View style={{borderTopWidth:1,borderColor:p.border,paddingTop:14,gap:13}}>
     <View style={{flexDirection:'row',justifyContent:'space-between',alignItems:'center'}}>
       <Label>İŞLENEN DERS SAATİ</Label>
       <Text style={{color:p.ink,fontWeight:'700'}}>{hours} / {lesson.hours}</Text>
     </View>
     <View style={{flexDirection:'row',alignItems:'center',gap:12,justifyContent:'space-between'}}>
       <MotionIconButton label="İşlenen saati azalt" disabled={busy||hours===0}
         icon={<Ionicons name="remove" color={p.accent} size={22}/>} onPress={()=>void persist(hours-1)}/>
       <View style={{alignItems:'center',flex:1,justifyContent:'center'}}>
         <Text style={{fontSize:25,fontWeight:'800',color:p.ink}}>{hours}</Text>
         <Muted>işlenen saat</Muted>
       </View>
       <MotionIconButton label="İşlenen saati artır" disabled={busy||hours>=lesson.hours}
         icon={<Ionicons name="add" color={p.accent} size={22}/>} onPress={()=>void persist(hours+1)}/>
     </View>
     {hours<lesson.hours?<AppButton label="Bu bölümü tamamla" variant="secondary"
       onPress={()=>void persist(lesson.hours)} disabled={busy}/>:null}
     <Label>DERS NOTU · EN FAZLA 1200 KARAKTER</Label>
     <TextInput value={note} onChangeText={t=>{setNote(t);setStatus(null);}}
       multiline maxLength={1200} placeholder="Bu derste ne işlendi? Sonraki derse ne kaldı?"
       placeholderTextColor={p.muted} accessibilityLabel="Ders notu"
       style={{minHeight:85,textAlignVertical:'top',borderWidth:1,borderColor:p.border,
         backgroundColor:p.background,color:p.ink,borderRadius:12,padding:13,fontSize:14,lineHeight:21}}/>
     <AppButton label="Notu kaydet" variant="secondary" loading={busy}
       disabled={busy||note===(record?.note??'')} onPress={()=>void persist(hours)}/>
     {status?<SaveStatus state={status}/>:null}
   </View>:classId&&!canTrack?<Muted>Bu şubenin takibi daha sonraki bir haftadan başlıyor.</Muted>:null}
 </Card>;
}
