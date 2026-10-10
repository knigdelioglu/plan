import test from 'node:test';
import assert from 'node:assert/strict';
import {BACKUP_FORMAT,BACKUP_VERSION,MAX_BACKUP_BYTES,createBackup,parseBackup,validateBackup}
  from '../src/features/backup/backup-format.ts';

const plan = {
  id:'valid-plan',
  weeks:[{
    number:1,hours:5,lessons:[{index:1,hours:3},{index:2,hours:2}]
  },{
    number:2,hours:5,lessons:[{index:1,hours:5}]
  }]
};
const plans = id => id === plan.id ? plan : undefined;
const group = (id='a',startWeek=1,name='11/A') => ({
  id, name, nameKey:name, planId:'valid-plan', startWeek, createdAt:'2026-10-10T10:00:00.000Z'
});
const record = (classId='a',week=1,lesson=1,doneHours=2) => ({
  classId,week,lesson,doneHours,note:'Karagöz çalışıldı',updatedAt:'2026-10-10T11:00:00.000Z'
});
const snapshot = () => ({classes:[group()],records:[record()],selectedClassId:'a'});
const backup = () => createBackup(snapshot(),'2026-10-10T12:00:00.000Z');

test('full backup round trip preserves notes, class selection and 3+2 lesson identity',()=>{
  const input=backup(), output=parseBackup(JSON.stringify(input),plans);
  assert.equal(output.format,BACKUP_FORMAT);
  assert.equal(output.version,BACKUP_VERSION);
  assert.deepEqual(output,input);
});

test('missing or unverifiable plan blocks the entire restore',()=>{
  const input=backup();input.classes[0].planId='unverified-anadolu';
  assert.throws(()=>validateBackup(input,plans),/plan/);
});

test('per-lesson bounded hours reject values from the wrong 3+2 section',()=>{
  const input=backup();input.records[0].lesson=2;input.records[0].doneHours=3;
  assert.throws(()=>validateBackup(input,plans),/saat|ders/);
});

test('before-start records do not appear as completed after restore',()=>{
  const input=backup();input.classes[0].startWeek=2;
  assert.throws(()=>validateBackup(input,plans),/saat|ders/);
});

test('duplicate class identifiers or duplicate case-insensitive names are rejected',()=>{
  const input=backup();input.classes.push(group('b',1,'11/a'));
  assert.throws(()=>validateBackup(input,plans),/yinelenen şube/);
  const other=backup();other.classes.push(group('a',1,'11/B'));
  assert.throws(()=>validateBackup(other,plans),/yinelenen şube/);
});

test('duplicate lesson records cannot silently overwrite other data',()=>{
  const input=backup();input.records.push({...input.records[0]});
  assert.throws(()=>validateBackup(input,plans),/yinelenen ders/);
});

test('unmatched selection, invalid timestamp and extra-long note are rejected',()=>{
  const a=backup();a.selectedClassId='missing';
  assert.throws(()=>validateBackup(a,plans),/seçili şube/);
  const b=backup();b.classes[0].createdAt='not-a-date';
  assert.throws(()=>validateBackup(b,plans),/şube/);
  const c=backup();c.records[0].note='x'.repeat(1201);
  assert.throws(()=>validateBackup(c,plans),/ders kaydı/);
});

test('malformed JSON and unsupported version are rejected',()=>{
  assert.throws(()=>parseBackup('{bad JSON',plans),/JSON/);
  const input=backup();input.version=2;
  assert.throws(()=>validateBackup(input,plans),/sürümü/);
});

test('large payload is rejected before parsing',()=>{
  assert.throws(()=>parseBackup('x'.repeat(MAX_BACKUP_BYTES+1),plans),/6 MB/);
});

test('empty backup remains a valid explicit replacement',()=>{
  const input=createBackup({classes:[],records:[],selectedClassId:null},'2026-10-10T00:00:00Z');
  assert.deepEqual(validateBackup(input,plans).classes,[]);
  assert.equal(input.selectedClassId,null);
});
