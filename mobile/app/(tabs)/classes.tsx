import React,{useState} from 'react';
import {Alert,Text,View} from 'react-native';
import {router} from 'expo-router';
import {MotionTextField,MotionSelect} from '@native-motion/react-native';
import {Screen,Card,Heading,Label,Muted,Pill,AppButton,AppProgress,uiStyles} from '@/components/ui';
import {useApp} from '@/app/AppProvider';
import {useAppTheme} from '@/theme';
import {plans,findPlan,defaultWeek} from '@/features/catalog/queries';
import {classSummary} from '@/features/progress/model';
import {BackupPanel} from '@/features/backup/BackupPanel';

export default function ClassesScreen() {
 const p=useAppTheme();
 const {snapshot,loading,error,createClass,deleteClass,setSelectedClass}=useApp();
 const [name,setName]=useState('');
 const [planId,setPlanId]=useState(plans[0]?.id??'');
 const [startWeek,setStartWeek]=useState<number|null>(null);
 const [busy,setBusy]=useState(false);
 const [message,setMessage]=useState<string|null>(null);
 const plan=findPlan(planId);
 const week=startWeek&&plan?.weeks.some(w=>w.number===startWeek)?startWeek:
   plan?defaultWeek(plan)?.number??1:1;
 async function add() {
   if(busy||!plan)return;
   setBusy(true);setMessage(null);
   try{
     await createClass(name,plan.id,week);
     setName('');setStartWeek(null);
     router.push('/');
   }catch(e){setMessage(e instanceof Error?e.message:String(e));}
   finally{setBusy(false);}
 }
 async function open(id:string) {
   try{await setSelectedClass(id);router.push('/');}
   catch(e){Alert.alert('Şube açılamadı',String(e));}
 }
 function confirmDelete(id:string,label:string) {
   Alert.alert('Şube silinsin mi?',label+' şubesinin bütün ders saati ve not kayıtları silinecek. Geri alınamaz.',[
     {text:'Vazgeç',style:'cancel'},
     {text:'Şubeyi sil',style:'destructive',onPress:()=>{
       void (async()=>{
         setBusy(true);
         try{await deleteClass(id);}
         catch(e){Alert.alert('Silme hatası',String(e));}
         finally{setBusy(false);}
       })();
     }}
   ]);
 }
 return <Screen eyebrow="ŞUBE YÖNETİMİ" title="Sınıflarım"
   subtitle="Aynı yıllık planı kullanan şubeleri birbirinden bağımsız takip edin.">
   {error?<Card><Pill tone="warning">Yerel veritabanı açılamadı</Pill><Muted>{error}</Muted></Card>:null}
   {loading?<Card><Muted>Şube kayıtları yükleniyor…</Muted></Card>:null}
   <View style={uiStyles.between}><Heading>Kayıtlı şubeler</Heading><Pill>{snapshot.classes.length} şube</Pill></View>
   {snapshot.classes.length===0?<Card>
     <Heading>Henüz bir şubeniz yok</Heading>
     <Muted>11/A gibi bir şube oluşturun. Seçtiğiniz haftadan sonra işlediğiniz ders saatlerini kaydedebilirsiniz.</Muted>
   </Card>:snapshot.classes.map(group=>{
     const linked=findPlan(group.planId);
     const summary=linked?classSummary(group,linked,snapshot.records):null;
     return <Card key={group.id}>
       <View style={uiStyles.between}>
         <View style={{flex:1,gap:5}}>
           <Label>ŞUBE</Label>
           <Text style={{color:p.ink,fontSize:22,fontWeight:'800'}}>{group.name}</Text>
           <Muted>{linked?linked.grade+'. sınıf · '+linked.school:'Plan kullanılamıyor'}</Muted>
         </View>
         {snapshot.selectedClassId===group.id?<Pill tone="success">Aktif</Pill>:null}
       </View>
       {summary?<React.Fragment>
         <AppProgress label="Tamamlanan dersler" value={summary.done} max={summary.total}/>
         <Muted>{summary.complete?'Takip edilen tüm dersler tamamlandı':
           summary.next?summary.next.week+'. hafta, '+summary.next.lesson+'. bölümden devam':
           'Henüz ilerleme kaydı yok'}</Muted>
       </React.Fragment>:<Pill tone="warning">Kaynak plan doğrulanmadı; kayıtlar korunuyor.</Pill>}
       <View style={{gap:8}}>
         <AppButton label="Şubeyi aç" disabled={busy||!linked} onPress={()=>void open(group.id)}/>
         <AppButton label="Şubeyi ve kayıtlarını sil" variant="destructive" disabled={busy}
           onPress={()=>confirmDelete(group.id,group.name)}/>
       </View>
     </Card>;
   })}
   <Card style={{gap:14}}>
     <Heading>Yeni şube oluştur</Heading>
     <MotionTextField label="Şube adı" value={name} onChangeText={setName}
       placeholder="Örneğin 11/A" error={name.length>40?'En fazla 40 karakter':undefined}/>
     {plans.length>0?<React.Fragment>
       <MotionSelect label="Yıllık plan" options={plans.map(x=>({
         id:x.id,label:x.grade+'. sınıf · '+x.school
       }))} selection={planId} onChange={id=>{setPlanId(id);setStartWeek(null);}}/>
       <MotionSelect label="Takip başlangıcı" options={plan?.weeks.map(w=>({
         id:String(w.number),label:w.number+'. hafta'
       }))??[]} selection={String(week)} onChange={v=>setStartWeek(Number(v))}/>
       <Muted>Önceki haftalar otomatik olarak tamamlandı sayılmaz. Başlangıç haftasını ihtiyacınıza göre seçebilirsiniz.</Muted>
     </React.Fragment>:<Muted>Şube oluşturmak için önce doğrulanmış bir yıllık plan gereklidir.</Muted>}
     {message?<Text accessibilityRole="alert" style={{color:p.danger,fontSize:13}}>{message}</Text>:null}
     <AppButton label={busy?'Kaydediliyor…':'Şubeyi oluştur'} loading={busy}
       disabled={busy||!plan||name.trim().length<2||name.trim().length>40}
       onPress={()=>void add()}/>
   </Card>
   <BackupPanel/>
 </Screen>;
}
