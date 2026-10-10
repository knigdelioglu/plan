import React,{useState} from 'react';
import {Alert,Text,View} from 'react-native';
import {Card,Heading,Label,Muted,AppButton} from '../../components/ui';
import {useApp} from '../../app/AppProvider';
import {useAppTheme} from '../../theme';
import {pickBackup,restoreBackup,shareBackup} from './backup-files';
import type {BackupFile} from './backup-format';

export function BackupPanel() {
  const p = useAppTheme();
  const {snapshot,refresh,loading,error} = useApp();
  const [busy,setBusy] = useState(false);
  const [message,setMessage] = useState<string|null>(null);
  const disabled=busy||loading||Boolean(error);

  async function exportData() {
    if(disabled)return;
    setBusy(true);setMessage(null);
    try {
      await shareBackup();
      setMessage('Paylaşım ekranı kapandı. Yedek dosyasının güvenli bir yere kaydedildiğini kontrol edin.');
    }catch(e) {
      Alert.alert('Yedek aktarılamadı',e instanceof Error?e.message:String(e));
    }finally{setBusy(false);}
  }

  function confirmRestore(backup:BackupFile) {
    Alert.alert(
      'Mevcut kayıtların üzerine yazılsın mı?',
      backup.classes.length+' şube ve '+backup.records.length+' ders kaydı geri yüklenecek. '+
      'Cihazdaki '+snapshot.classes.length+' şube ve '+snapshot.records.length+
      ' ders kaydı tamamen değiştirilecek. Önce mevcut verilerinizi yedeklemeniz önerilir.',
      [
        {text:'Vazgeç',style:'cancel'},
        {text:'Geri yükle',style:'destructive',onPress:()=>{
          void (async()=>{
            setBusy(true);setMessage(null);
            try {
              await restoreBackup(backup);
              await refresh();
              setMessage('Yedekten '+backup.classes.length+' şube ve '+
                backup.records.length+' ders kaydı geri yüklendi.');
            }catch(e) {
              Alert.alert('Geri yükleme başarısız', e instanceof Error?e.message:String(e));
            }finally{setBusy(false);}
          })();
        }}
      ]
    );
  }

  async function importData() {
    if(disabled)return;
    setBusy(true);setMessage(null);
    try {
      const backup=await pickBackup();
      if(backup)confirmRestore(backup);
    }catch(e){
      Alert.alert('Yedek dosyası okunamadı',e instanceof Error?e.message:String(e));
    }finally{setBusy(false);}
  }

  return <Card>
    <Label>VERİ GÜVENLİĞİ</Label>
    <Heading>Yedekleme ve geri yükleme</Heading>
    <Muted>Şube adları, işlenen saatler, ders notları ve seçili şube tek bir JSON dosyasına aktarılır.
      Dosya cihazınızın dışına ancak siz paylaşmayı seçtiğinizde çıkar.</Muted>
    <View style={{gap:10}}>
      <AppButton label={busy?'İşlem sürüyor…':'Yedeği dışa aktar'}
        loading={busy} disabled={disabled} onPress={()=>void exportData()}/>
      <AppButton label="Dosyadan geri yükle" variant="secondary"
        disabled={disabled} onPress={()=>void importData()}/>
    </View>
    <Muted>Geri yükleme önce bütün dersleri ve saatleri doğrular. Yalnızca mevcut doğrulanmış
      yıllık planlarla uyumlu yedekler kabul edilir. Onay verirseniz mevcut kayıtlar tek işlemde değiştirilir.</Muted>
    {message?<Text accessibilityRole="text" style={{color:p.success,fontSize:13,lineHeight:20}}>
      {message}
    </Text>:null}
  </Card>;
}
