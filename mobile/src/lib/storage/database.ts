import * as SQLite from 'expo-sqlite';
import * as Crypto from 'expo-crypto';
import {validateBackup,type BackupFile} from '../../features/backup/backup-format';
import { findPlan, findWeek } from '../../features/catalog/queries';
import { classNameKey, validateHours, type ClassRecord, type Snapshot } from '../../features/progress/model';

let connection: Promise<SQLite.SQLiteDatabase> | undefined;
async function open() {
  const db=await SQLite.openDatabaseAsync('haftalik-plan.db');
  await db.execAsync(`
    PRAGMA journal_mode=WAL;
    PRAGMA foreign_keys=ON;
    CREATE TABLE IF NOT EXISTS classes (
      id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, name_key TEXT NOT NULL,
      plan_id TEXT NOT NULL, start_week INTEGER NOT NULL, created_at TEXT NOT NULL,
      UNIQUE(plan_id,name_key)
    );
    CREATE TABLE IF NOT EXISTS lesson_progress (
      class_id TEXT NOT NULL, week INTEGER NOT NULL, lesson INTEGER NOT NULL,
      done_hours INTEGER NOT NULL DEFAULT 0 CHECK(done_hours>=0),
      note TEXT NOT NULL DEFAULT '', updated_at TEXT NOT NULL,
      PRIMARY KEY(class_id,week,lesson),
      FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE
    );
    CREATE TABLE IF NOT EXISTS preferences (key TEXT PRIMARY KEY NOT NULL,value TEXT NOT NULL);
    PRAGMA user_version=1;
  `);
  return db;
}
function getDatabase():Promise<SQLite.SQLiteDatabase> {
  if(!connection) connection=open().catch(e=>{connection=undefined;throw e;});
  return connection;
}
type ClassRow={id:string;name:string;name_key:string;plan_id:string;start_week:number;created_at:string};
type RecordRow={class_id:string;week:number;lesson:number;done_hours:number;note:string;updated_at:string};
export async function loadSnapshot():Promise<Snapshot> {
  const db=await getDatabase();
  const [rows,entries,active]=await Promise.all([
    db.getAllAsync<ClassRow>('SELECT * FROM classes ORDER BY created_at,name'),
    db.getAllAsync<RecordRow>('SELECT * FROM lesson_progress ORDER BY week,lesson'),
    db.getFirstAsync<{value:string}>("SELECT value FROM preferences WHERE key='active_class'")
  ]);
  return {
    classes:rows.map(r=>({id:r.id,name:r.name,nameKey:r.name_key,planId:r.plan_id,startWeek:r.start_week,createdAt:r.created_at})),
    records:entries.map(r=>({classId:r.class_id,week:r.week,lesson:r.lesson,doneHours:r.done_hours,note:r.note,updatedAt:r.updated_at})),
    selectedClassId:active?.value||null
  };
}
export async function addClass(name:string,planId:string,startWeek:number):Promise<ClassRecord> {
  const plan=findPlan(planId),clean=name.trim().replace(/\s+/g,' ');
  if(!plan||!findWeek(plan,startWeek))throw new Error('Geçerli plan ve başlangıç haftası seçin.');
  if(clean.length<2||clean.length>40)throw new Error('Şube adı 2–40 karakter olmalı.');
  const item:ClassRecord={id:Crypto.randomUUID(),name:clean,nameKey:classNameKey(clean),
    planId,startWeek,createdAt:new Date().toISOString()};
  const db=await getDatabase();
  try{
    await db.runAsync('INSERT INTO classes(id,name,name_key,plan_id,start_week,created_at) VALUES (?,?,?,?,?,?)',
      [item.id,item.name,item.nameKey,item.planId,item.startWeek,item.createdAt]);
  }catch(e){
    if(String(e).includes('UNIQUE'))throw new Error('Bu plan için aynı adlı şube zaten var.');
    throw e;
  }
  return item;
}
export async function removeClass(id:string):Promise<void> {
  const db=await getDatabase();
  await db.withExclusiveTransactionAsync(async tx=>{
    await tx.runAsync('DELETE FROM lesson_progress WHERE class_id=?',[id]);
    await tx.runAsync('DELETE FROM classes WHERE id=?',[id]);
    await tx.runAsync("DELETE FROM preferences WHERE key='active_class' AND value=?",[id]);
  });
}
export async function persistActiveClass(id:string|null):Promise<void> {
  const db=await getDatabase();
  await db.runAsync("INSERT INTO preferences(key,value) VALUES('active_class',?) ON CONFLICT(key) DO UPDATE SET value=excluded.value",[id??'']);
}
export async function persistLesson(classId:string,weekNumber:number,lessonIndex:number,doneHours:number,note:string):Promise<void> {
  const db=await getDatabase();
  const group=await db.getFirstAsync<ClassRow>('SELECT * FROM classes WHERE id=?',[classId]);
  const plan=group?findPlan(group.plan_id):undefined;
  const week=plan?findWeek(plan,weekNumber):undefined;
  const lesson=week?.lessons.find(l=>l.index===lessonIndex);
  if(!group||!week||!lesson||weekNumber<group.start_week)throw new Error('Şube veya ders bulunamadı.');
  if(!validateHours(doneHours,lesson.hours))throw new Error('İşlenen saat planlanan sınırın dışında.');
  if(note.length>1200)throw new Error('Ders notu 1200 karakteri aşamaz.');
  await db.withExclusiveTransactionAsync(async tx=>{
    if(doneHours===0&&!note.trim()){
      await tx.runAsync('DELETE FROM lesson_progress WHERE class_id=? AND week=? AND lesson=?',
        [classId,weekNumber,lessonIndex]);
    }else{
      await tx.runAsync(`INSERT INTO lesson_progress(class_id,week,lesson,done_hours,note,updated_at)
        VALUES(?,?,?,?,?,?) ON CONFLICT(class_id,week,lesson) DO UPDATE SET
        done_hours=excluded.done_hours,note=excluded.note,updated_at=excluded.updated_at`,
        [classId,weekNumber,lessonIndex,doneHours,note,new Date().toISOString()]);
    }
  });
}

/** Atomic replacement: SQLite rolls back all deletes/inserts if any write fails. */
export async function replaceFromBackup(document: BackupFile): Promise<void> {
  // Revalidate at the persistence boundary; a caller can never bypass validation.
  const backup = validateBackup(document, findPlan);
  const db = await getDatabase();
  await db.withExclusiveTransactionAsync(async tx => {
    await tx.runAsync('DELETE FROM lesson_progress');
    await tx.runAsync('DELETE FROM classes');
    await tx.runAsync("DELETE FROM preferences WHERE key='active_class'");
    for (const group of backup.classes) {
      await tx.runAsync(
        'INSERT INTO classes(id,name,name_key,plan_id,start_week,created_at) VALUES (?,?,?,?,?,?)',
        [group.id,group.name,group.nameKey,group.planId,group.startWeek,group.createdAt]
      );
    }
    for (const record of backup.records) {
      await tx.runAsync(
        'INSERT INTO lesson_progress(class_id,week,lesson,done_hours,note,updated_at) VALUES (?,?,?,?,?,?)',
        [record.classId,record.week,record.lesson,record.doneHours,record.note,record.updatedAt]
      );
    }
    if (backup.selectedClassId) {
      await tx.runAsync(
        "INSERT INTO preferences(key,value) VALUES('active_class',?)",
        [backup.selectedClassId]
      );
    }
  });
}
