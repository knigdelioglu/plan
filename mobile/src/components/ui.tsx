import React, {type ReactNode} from 'react';
import {View,Text,ScrollView,StyleSheet,type ViewStyle} from 'react-native';
import {useAppTheme,spacing,radii} from '../theme';
import {MotionButton,MotionProgress,MotionStatusIndicator} from '@native-motion/react-native';
import {useSafeAreaInsets} from 'react-native-safe-area-context';

export function Screen({eyebrow,title,subtitle,children}:{eyebrow:string;title:string;subtitle?:string;children:ReactNode}) {
 const p=useAppTheme(),insets=useSafeAreaInsets();
 return <ScrollView style={{flex:1,backgroundColor:p.background}}
   keyboardShouldPersistTaps="handled"
   contentContainerStyle={{paddingHorizontal:20,paddingTop:insets.top+14,paddingBottom:42,gap:19,maxWidth:940,width:'100%',alignSelf:'center'}}>
   <View style={{gap:6}}>
     <Text style={{color:p.accent,fontSize:11,fontWeight:'800',letterSpacing:1.5}}>{eyebrow}</Text>
     <Text accessibilityRole="header" style={{color:p.ink,fontSize:34,fontWeight:'800',letterSpacing:-1}}>{title}</Text>
     {subtitle?<Text style={{color:p.muted,fontSize:14,lineHeight:21}}>{subtitle}</Text>:null}
   </View>
   {children}
 </ScrollView>;
}
export function Card({children,style}:{children:ReactNode;style?:ViewStyle}) {
 const p=useAppTheme();
 return <View style={[{padding:18,borderWidth:1,borderColor:p.border,borderRadius:radii.card,
   backgroundColor:p.surface,gap:13},style]}>{children}</View>;
}
export function Label({children}:{children:ReactNode}) {
 const p=useAppTheme();return <Text style={{color:p.muted,fontSize:11,fontWeight:'700',letterSpacing:0.6}}>{children}</Text>;
}
export function Heading({children}:{children:ReactNode}) {
 const p=useAppTheme();return <Text style={{color:p.ink,fontSize:20,fontWeight:'700',letterSpacing:-0.5}}>{children}</Text>;
}
export function Muted({children}:{children:ReactNode}) {
 const p=useAppTheme();return <Text style={{color:p.muted,fontSize:13,lineHeight:20}}>{children}</Text>;
}
export function Pill({children,tone='neutral'}:{children:ReactNode;tone?:'neutral'|'success'|'warning'}) {
 const p=useAppTheme();const color=tone==='success'?p.success:tone==='warning'?p.warning:p.accent;
 return <View style={{borderRadius:30,borderWidth:1,borderColor:color,paddingVertical:5,paddingHorizontal:10,alignSelf:'flex-start'}}>
   <Text style={{color,fontSize:11,fontWeight:'700'}}>{children}</Text>
 </View>;
}
export function AppButton({label,onPress,disabled=false,loading=false,variant='primary'}:{
 label:string;onPress:()=>void;disabled?:boolean;loading?:boolean;variant?:'primary'|'secondary'|'ghost'|'destructive';
}) {
 return <MotionButton label={label} onPress={onPress} disabled={disabled}
   phase={loading?'loading':'idle'} variant={variant}/>;
}
export function AppProgress({label,value,max}:{label:string;value:number;max:number}) {
 return <MotionProgress label={label} value={max>0?value/max:0} detail={value+' / '+max+' ders saati'}/>;
}
export function SaveStatus({state}:{state:'saving'|'saved'|'error'}) {
 return <MotionStatusIndicator label={state==='saving'?'Kaydediliyor':state==='saved'?'Kaydedildi':'Kayıt başarısız'}
   status={state==='saving'?'processing':state==='saved'?'success':'failure'}/>;
}
export const uiStyles=StyleSheet.create({
 row:{flexDirection:'row',alignItems:'center',gap:10},
 between:{flexDirection:'row',justifyContent:'space-between',alignItems:'center',gap:10},
 line:{height:1,backgroundColor:'#D5DDE8'},
});
