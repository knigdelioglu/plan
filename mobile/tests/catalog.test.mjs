import test from 'node:test';
import assert from 'node:assert/strict';
import {mkdtempSync,mkdirSync,writeFileSync,rmSync} from 'node:fs';
import {tmpdir} from 'node:os';
import {join} from 'node:path';
import {buildCatalog} from '../scripts/build-catalog.mjs';

function fixture(school,metadataPatch={},statuses=[],modify=()=>{}) {
 const root=mkdtempSync(join(tmpdir(),'plan-expo-test-'));
 const folder=join(root,'2026-2027','turk-dili-ve-edebiyati','11',school);
 mkdirSync(folder,{recursive:true});
 const m={metadata:{
   academic_year:'2026-2027',grade_level:11,school_type:'Fen Lisesi',
   source_file:'fen.xlsx',course_name:'Türk Dili ve Edebiyatı',...metadataPatch
 },weeks:[{
   week_number:1,start_date:'2026-09-14',end_date:'2026-09-18',total_hours:5,
   theme:{theme_name:'Bir Diyeceğim Var'},
   special_days_and_weeks:{present:true,text:'15 Temmuz Demokrasi ve Millî Birlik Günü'},
   lessons:[{lesson_index:1,hours:3,learning_area:'OKUMA',learning_outcomes:[]},
     {lesson_index:2,hours:2,learning_area:'YAZMA',learning_outcomes:[]}]
 }]};
 modify(m);
 writeFileSync(join(folder,'annual-plan.json'),JSON.stringify(m));
 if(statuses.length)writeFileSync(join(folder,'review-queue.json'),JSON.stringify({items:statuses.map(status=>({status}))}));
 return {root,cleanup:()=>rmSync(root,{recursive:true,force:true})};
}
test('correct source keeps distinct 3+2 sections and intentional opening commemoration',()=>{
 const f=fixture('fen-lisesi');
 try{
   const c=buildCatalog(f.root);
   assert.equal(c.plans.length,1);
   assert.deepEqual(c.plans[0].weeks[0].lessons.map(l=>l.hours),[3,2]);
   assert.equal(c.plans[0].weeks[0].specialDays[0],'15 Temmuz Demokrasi ve Millî Birlik Günü');
 }finally{f.cleanup();}
});
test('Anadolu title with Fen identity is withheld',()=>{
 const f=fixture('anadolu-lisesi');
 try{const c=buildCatalog(f.root);assert.equal(c.plans.length,0);assert.equal(c.withheld.length,1);}
 finally{f.cleanup();}
});
test('metadata relabeling without content verification is withheld',()=>{
 const f=fixture('anadolu-lisesi',{school_type:'Anadolu Lisesi'},
   ['metadata_corrected_content_unverified']);
 try{assert.equal(buildCatalog(f.root).plans.length,0);}
 finally{f.cleanup();}
});
test('nonmatching lesson hours cannot be published',()=>{
 const f=fixture('fen-lisesi',[],[],m=>{m.weeks[0].total_hours=6;});
 try{assert.equal(buildCatalog(f.root).plans.length,0);}
 finally{f.cleanup();}
});
