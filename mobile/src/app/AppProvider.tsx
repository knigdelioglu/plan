import React,{createContext,useCallback,useContext,useEffect,useMemo,useState,type ReactNode} from 'react';
import {addClass,loadSnapshot,persistActiveClass,persistLesson,removeClass} from '../lib/storage/database';
import {plans} from '../features/catalog/queries';
import type {Snapshot} from '../features/progress/model';

interface AppState {
 snapshot:Snapshot;loading:boolean;error:string|null;
 selectedClassId:string|null;selectedPlanId:string|null;
 setSelectedClass:(id:string|null)=>Promise<void>;
 setSelectedPlan:(id:string)=>void;
 createClass:(name:string,planId:string,startWeek:number)=>Promise<void>;
 deleteClass:(id:string)=>Promise<void>;
 saveLesson:(classId:string,week:number,lesson:number,hours:number,note:string)=>Promise<void>;
 refresh:()=>Promise<void>;
}
const Context=createContext<AppState|null>(null);
const empty:Snapshot={classes:[],records:[],selectedClassId:null};
export function AppProvider({children}:{children:ReactNode}) {
 const [snapshot,setSnapshot]=useState<Snapshot>(empty);
 const [loading,setLoading]=useState(true),[error,setError]=useState<string|null>(null);
 const [selectedPlanId,setSelectedPlanId]=useState<string|null>(plans[0]?.id??null);
 const refresh=useCallback(async()=>{const next=await loadSnapshot();setSnapshot(next);setError(null);},[]);
 useEffect(()=>{
   let mounted=true;
   loadSnapshot().then(s=>{if(mounted){setSnapshot(s);setLoading(false);}})
     .catch(e=>{if(mounted){setError(String(e));setLoading(false);}});
   return()=>{mounted=false;};
 },[]);
 const setSelectedClass=useCallback(async(id:string|null)=>{
   await persistActiveClass(id);await refresh();
 },[refresh]);
 const createClass=useCallback(async(name:string,planId:string,startWeek:number)=>{
   const item=await addClass(name,planId,startWeek);
   await persistActiveClass(item.id);await refresh();
 },[refresh]);
 const deleteClass=useCallback(async(id:string)=>{await removeClass(id);await refresh();},[refresh]);
 const saveLesson=useCallback(async(classId:string,week:number,lesson:number,hours:number,note:string)=>{
   await persistLesson(classId,week,lesson,hours,note);await refresh();
 },[refresh]);
 const value=useMemo<AppState>(()=>({
   snapshot,loading,error,selectedClassId:snapshot.selectedClassId,selectedPlanId,
   setSelectedClass,setSelectedPlan:setSelectedPlanId,createClass,deleteClass,saveLesson,refresh
 }),[snapshot,loading,error,selectedPlanId,setSelectedClass,createClass,deleteClass,saveLesson,refresh]);
 return <Context.Provider value={value}>{children}</Context.Provider>;
}
export function useApp():AppState {
 const state=useContext(Context);if(!state)throw new Error('AppProvider eksik');
 return state;
}
