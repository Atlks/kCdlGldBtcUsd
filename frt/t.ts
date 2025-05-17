console.log(111)

var s=123
var t
console.log(t)

// t.ts
function greet(name: string ) {
    console.log(name.toUpperCase()); // ❌ 应该报错：Object is possibly 'null'
}

greet("33");


//"C:\Program Files\nodejs\npm.cmd" install -g typescript
// "C:\Program Files\nodejs\npm.cmd" install typescript --save-dev