import test from 'node:test';
import assert from 'node:assert/strict';
import {classNameKey,classSummary,validateHours,weekSummary} from '../src/features/progress/model.ts';

const plan={weeks:[
 {number:1,hours:5,lessons:[{index:1,hours:3},{index:2,hours:2}]},
 {number:2,hours:5,lessons:[{index:1,hours:5}]}
]};
const group=(id,startWeek=1)=>({id,name:id,nameKey:id,planId:'plan1',startWeek,createdAt:''});
const entry=(classId,week,lesson,doneHours)=>({classId,week,lesson,doneHours,note:'',updatedAt:''});

test('3+2 split remains independent',()=>{
 const records=[entry('11A',1,1,3),entry('11A',1,2,1)];
 assert.deepEqual(weekSummary(plan.weeks[0],'11A',records),{done:4,total:5});
 assert.deepEqual(classSummary(group('11A'),plan,records).next,{week:1,lesson:2});
});
test('separate classes never share progress',()=>{
 const records=[entry('11A',1,1,3),entry('11B',1,1,1)];
 assert.equal(classSummary(group('11A'),plan,records).done,3);
 assert.equal(classSummary(group('11B'),plan,records).done,1);
});
test('start week does not silently mark earlier classes done',()=>{
 const result=classSummary(group('11A',2),plan,[entry('11A',1,1,3)]);
 assert.equal(result.done,0);assert.equal(result.total,5);
 assert.deepEqual(result.next,{week:2,lesson:1});
});
test('completion calculated only from persisted hours',()=>{
 const rows=[entry('11A',1,1,3),entry('11A',1,2,2),entry('11A',2,1,5)];
 const s=classSummary(group('11A'),plan,rows);
 assert.equal(s.complete,true);assert.equal(s.percent,100);
});
test('hours are bounded integers',()=>{
 for(const invalid of [-1,3.2,4,NaN,Infinity])assert.equal(validateHours(invalid,3),false);
 for(const valid of [0,1,2,3])assert.equal(validateHours(valid,3),true);
});
test('locale-aware normalized class names',()=>{
 assert.equal(classNameKey('  11 / a  '),'11 / A');
 assert.equal(classNameKey('11/ı'),'11/I');
});
