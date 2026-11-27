// Minimal Android VectorDrawable -> SVG (paths, fill, stroke, aapt gradients)
const fs=require('fs');const [,,src,dst]=process.argv;const x=fs.readFileSync(src,'utf8');
const vw=/viewportWidth="([\d.]+)"/.exec(x)[1], vh=/viewportHeight="([\d.]+)"/.exec(x)[1];
let defs='',body='',g=0;
const attr=(s,n)=>{const m=new RegExp('android:'+n+'="([^"]*)"').exec(s);return m?m[1]:null};
const paths=[...x.matchAll(/<path\b([\s\S]*?)(\/>|>([\s\S]*?)<\/path>)/g)];
for(const p of paths){const a=p[1],inner=p[3]||'';let fill=attr(a,'fillColor')||'none';
 const gm=/<gradient([^>]*)>([\s\S]*?)<\/gradient>/.exec(inner);
 if(gm){const id='g'+(g++);const ga=gm[1];const items=[...gm[2].matchAll(/<item[^>]*android:color="([^"]+)"[^>]*android:offset="([^"]+)"/g)];
  const t=attr(ga,'type')||'linear';
  if(t==='radial'){defs+=`<radialGradient id="${id}" gradientUnits="userSpaceOnUse" cx="${attr(ga,'centerX')}" cy="${attr(ga,'centerY')}" r="${attr(ga,'gradientRadius')}">`;}
  else defs+=`<linearGradient id="${id}" gradientUnits="userSpaceOnUse" x1="${attr(ga,'startX')}" y1="${attr(ga,'startY')}" x2="${attr(ga,'endX')}" y2="${attr(ga,'endY')}">`;
  for(const it of items)defs+=`<stop offset="${it[2]}" stop-color="${it[1].length===9?'#'+it[1].slice(3):it[1]}" ${it[1].length===9?`stop-opacity="${parseInt(it[1].slice(1,3),16)/255}"`:''}/>`;
  defs+=t==='radial'?'</radialGradient>':'</linearGradient>';fill=`url(#${id})`;}
 const fix=c=>c&&c.length===9?'#'+c.slice(3):c;
 body+=`<path d="${attr(a,'pathData')}" fill="${fix(fill)}" ${attr(a,'strokeColor')?`stroke="${fix(attr(a,'strokeColor'))}" stroke-width="${attr(a,'strokeWidth')||1}" stroke-linecap="${attr(a,'strokeLineCap')||'butt'}" stroke-linejoin="${attr(a,'strokeLineJoin')||'miter'}"`:''} ${attr(a,'fillAlpha')?`fill-opacity="${attr(a,'fillAlpha')}"`:''}/>\n`;}
fs.writeFileSync(dst,`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${vw} ${vh}"><defs>${defs}</defs>${body}</svg>`);console.log(dst,paths.length,'paths');
