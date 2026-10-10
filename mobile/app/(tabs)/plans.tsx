import React,{useState} from 'react';
import {Alert,Text,View} from 'react-native';
import {router} from 'expo-router';
import {MotionSearchField} from '@native-motion/react-native';
import {Screen,Card,Heading,Label,Muted,Pill,AppButton,uiStyles} from '@/components/ui';
import {useApp} from '@/app/AppProvider';
import {useAppTheme} from '@/theme';
import {catalog,plans} from '@/features/catalog/queries';

export default function PlansScreen(){
 const p=useAppTheme();
 const {snapshot,selectedPlanId,setSelectedPlan,setSelectedClass}=useApp();
 const [query,setQuery]=useState('');
 const [opening,setOpening]=useState(false);
 const matches=plans.filter(x=>(
   x.course+' '+x.school+' '+x.grade+' '+x.year+' '+x.weeks.map(w=>w.theme).join(' ')
 ).toLocaleLowerCase('tr-TR').includes(query.toLocaleLowerCase('tr-TR').trim()));
 async function open(id:string){
   if(opening)return;
   setOpening(true);
   try{
     setSelectedPlan(id);
     await setSelectedClass(null);
     router.push('/');
   }catch(e){Alert.alert('Plan açılamadı',String(e));}
   finally{setOpening(false);}
 }
 return <Screen eyebrow="SALT OKUNUR KAYNAKLAR" title="Planlar"
   subtitle="Doğrulanmış yıllık planları bulun, haftaları ve öğrenme çıktılarını inceleyin.">
   <MotionSearchField label="Plan ara" query={query} onChange={setQuery}
     placeholder="Ders, sınıf, okul türü veya tema…"/>
   <View style={uiStyles.between}>
     <Heading>Kullanılabilir planlar</Heading>
     <Pill tone="success">{plans.length} plan</Pill>
   </View>
   {matches.length===0?<Card><Muted>Aramanızla eşleşen doğrulanmış plan bulunamadı.</Muted></Card>:null}
   {matches.map(plan=><Card key={plan.id}>
     <View style={uiStyles.between}>
       <Label>{plan.year} · {plan.grade}. SINIF</Label>
       {selectedPlanId===plan.id&&!snapshot.selectedClassId?<Pill tone="success">Açık plan</Pill>:null}
     </View>
     <Text style={{color:p.ink,fontSize:21,fontWeight:'800'}}>{plan.course}</Text>
     <Muted>{plan.school} · {plan.weeks.length} öğretim haftası</Muted>
     <View style={{flexDirection:'row',gap:9,flexWrap:'wrap'}}>
       <Pill tone="success">Kimlik ve içerik doğrulandı</Pill>
       <Pill>{plan.weeks.reduce((s,w)=>s+w.lessons.length,0)} ders bölümü</Pill>
     </View>
     <Label>KAYNAK</Label><Muted>{plan.sourceFile}</Muted>
     <AppButton label="Haftaları incele" loading={opening} onPress={()=>void open(plan.id)}/>
   </Card>)}
   {catalog.withheld.length>0?<Card>
     <Heading>Doğrulama bekleyen kaynaklar</Heading>
     <Muted>Yanlış okul türünde plan göstermemek için bu belgeler seçim listesinde kullanılmıyor.</Muted>
     {catalog.withheld.map(item=><View key={item.path} style={{gap:5}}>
       <Pill tone="warning">Doğrulama bekleniyor</Pill>
       <Muted>{item.path.split('/').slice(-2).join(' · ')} — {item.reason}</Muted>
     </View>)}
   </Card>:null}
   <Card><Label>VERİ POLİTİKASI</Label>
     <Muted>Yıllık planlar uygulamanın içinde çevrimdışı, salt okunur olarak bulunur. Öğretmen notları ve şube ilerlemesi ayrı bir yerel SQLite veritabanında saklanır.</Muted>
   </Card>
 </Screen>;
}
