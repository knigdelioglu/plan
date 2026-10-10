import React from 'react';
import {Tabs} from 'expo-router';
import {Ionicons} from '@expo/vector-icons';
import {useMotionPalette} from '@native-motion/react-native';

export default function TabsLayout() {
 const p=useMotionPalette();
 return <Tabs screenOptions={{
   headerShown:false,
   tabBarActiveTintColor:p.accent,tabBarInactiveTintColor:p.secondaryText,
   tabBarLabelStyle:{fontSize:11,fontWeight:'700',marginTop:3},
   tabBarStyle:{backgroundColor:p.surface,borderTopColor:p.border,height:64,paddingBottom:8,paddingTop:6},
 }}>
   <Tabs.Screen name="index" options={{title:'Haftam',tabBarIcon:({color,size})=><Ionicons name="calendar-outline" size={size} color={color}/>}}/>
   <Tabs.Screen name="classes" options={{title:'Sınıflarım',tabBarIcon:({color,size})=><Ionicons name="people-outline" size={size} color={color}/>}}/>
   <Tabs.Screen name="plans" options={{title:'Planlar',tabBarIcon:({color,size})=><Ionicons name="library-outline" size={size} color={color}/>}}/>
 </Tabs>;
}
