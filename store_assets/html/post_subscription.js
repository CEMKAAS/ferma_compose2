// Картинка к посту-опросу "реклама vs подписка".
// usage: node post_subscription.js <outDir>
const fs = require('fs'); const { execFileSync } = require('child_process');
const HERE = __dirname.replace(/\\/g, '/');
const OUT = (process.argv[2] || HERE + '/../social').replace(/\\/g, '/');
const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';
fs.mkdirSync(OUT + '/html', { recursive: true });
// Логотип: PNG 520x520 с белыми полями и тонкой рамкой по краю —
// показываем через background-size, чтобы обрезать рамку и подтянуть петуха к краям плашки.
const logo = '../../icon_512x512.png';

// k — общий множитель размеров (база: квадрат 1080x1080)
const page = ({ w, h, k = 1, wide = false }) => `<!doctype html><html lang="ru"><head><meta charset="utf-8"><style>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@600;700;800;900&display=swap');
*{box-sizing:border-box;margin:0;padding:0}
html,body{width:${w}px;height:${h}px;overflow:hidden;position:relative;
  font-family:'Nunito','Segoe UI',Arial,sans-serif;color:#fff;
  background:linear-gradient(155deg,#0A3A20 0%,#0F6B3A 52%,#1C9B55 100%)}
.blob{position:absolute;border-radius:50%;filter:blur(${110 * k}px);pointer-events:none}
.b1{width:${760 * k}px;height:${760 * k}px;background:#63C98A;opacity:.45;right:${-220 * k}px;top:${-260 * k}px}
.b2{width:${620 * k}px;height:${620 * k}px;background:#CDEDA3;opacity:.22;left:${-260 * k}px;bottom:${-240 * k}px}
.grain{position:absolute;inset:0;background:radial-gradient(circle at 22% 8%,rgba(255,255,255,.20),transparent 48%)}
.wrap{position:absolute;inset:0;z-index:2;display:flex;flex-direction:column;padding:${58 * k}px ${60 * k}px}

.head{display:flex;align-items:center;justify-content:space-between}
.brand{display:flex;align-items:center;gap:${20 * k}px}
.brand .mark{width:${92 * k}px;height:${92 * k}px;border-radius:${27 * k}px;background:#fff;
  display:flex;align-items:center;justify-content:center;box-shadow:0 ${14 * k}px ${34 * k}px rgba(0,0,0,.35)}
.brand .mark .glyph{width:${72 * k}px;height:${72 * k}px;background:url('${logo}') center/166% no-repeat}
.brand b{font-size:${38 * k}px;font-weight:900;letter-spacing:-.01em}
.brand small{display:block;font-size:${23 * k}px;font-weight:700;color:#BFE3C6;margin-top:${2 * k}px}
.tag{display:flex;align-items:center;gap:${12 * k}px;font-size:${24 * k}px;font-weight:900;letter-spacing:.10em;
  text-transform:uppercase;color:#0A3A20;background:#CDEDA3;padding:${13 * k}px ${27 * k}px;border-radius:999px}
.tag i{width:${13 * k}px;height:${13 * k}px;border-radius:50%;background:#0A3A20;display:block}

.mid{flex:1;display:flex;${wide ? `flex-direction:row;align-items:center;gap:${52 * k}px` : 'flex-direction:column;justify-content:center'}}
.copy{${wide ? 'flex:1.08' : ''}}
h1{font-size:${98 * k}px;font-weight:900;line-height:1.02;letter-spacing:-.025em}
h1 em{font-style:normal;color:#CDEDA3}
.sub{font-size:${35 * k}px;font-weight:700;line-height:1.30;color:#D3E9D8;margin-top:${22 * k}px;max-width:${940 * k}px}

.cards{display:flex;gap:${22 * k}px;${wide ? 'flex:1;flex-direction:column' : `margin-top:${40 * k}px`}}
.card{flex:1;border-radius:${34 * k}px;padding:${28 * k}px ${30 * k}px ${30 * k}px}
.card .lab{font-size:${22 * k}px;font-weight:900;letter-spacing:.12em;text-transform:uppercase;opacity:.75}
.card .ttl{display:flex;align-items:center;gap:${16 * k}px;font-size:${44 * k}px;font-weight:900;
  margin:${11 * k}px 0 ${10 * k}px;letter-spacing:-.02em}
.card .ttl span{width:${56 * k}px;height:${56 * k}px;border-radius:${19 * k}px;display:flex;align-items:center;
  justify-content:center;font-size:${32 * k}px;font-weight:900}
.card p{font-size:${28 * k}px;font-weight:700;line-height:1.3}
.now{background:rgba(255,255,255,.10);border:${2 * k}px solid rgba(255,255,255,.16);color:#fff}
.now .ttl span{background:rgba(255,255,255,.16);color:#fff}
.now p{color:#C6DCCB}
.new{background:#F3FBE6;color:#0A3A20;box-shadow:0 ${26 * k}px ${56 * k}px rgba(0,0,0,.30)}
.new .lab{color:#0A8F3C;opacity:1}
.new .ttl span{background:#0A8F3C;color:#fff}
.new p{color:#2C4A32}

.cta{margin-top:${34 * k}px;display:flex;align-items:center;gap:${26 * k}px;background:#fff;color:#0A3A20;
  border-radius:999px;padding:${26 * k}px ${38 * k}px;box-shadow:0 ${22 * k}px ${50 * k}px rgba(0,0,0,.28)}
.cta b{font-size:${40 * k}px;font-weight:900;letter-spacing:-.01em;line-height:1.1}
.cta u{text-decoration:none;display:block;font-size:${27 * k}px;font-weight:700;color:#476B50;margin-top:${4 * k}px}
.cta .arrow{margin-left:auto;flex:none;width:${84 * k}px;height:${84 * k}px;border-radius:50%;background:#0A8F3C;
  color:#fff;display:flex;align-items:center;justify-content:center;font-size:${44 * k}px;font-weight:900;line-height:1}
</style></head><body>
<div class="blob b1"></div><div class="blob b2"></div><div class="grain"></div>
<div class="wrap">
  <div class="head">
    <div class="brand">
      <div class="mark"><div class="glyph"></div></div>
      <div><b>Моё хозяйство</b><small>ферма и инкубатор</small></div>
    </div>
    <div class="tag"><i></i>Опрос</div>
  </div>

  <div class="mid">
    <div class="copy">
      <h1>Реклама<br>или <em>подписка?</em></h1>
      <p class="sub">Приложение развивается за счёт рекламы. Думаем над другим вариантом — и хотим услышать вас.</p>
    </div>
    <div class="cards">
      <div class="card now">
        <div class="lab">Сейчас</div>
        <div class="ttl"><span>&#10005;</span>Реклама</div>
        <p>Держит проект на плаву,<br>но нравится не всем</p>
      </div>
      <div class="card new">
        <div class="lab">Обсуждаем</div>
        <div class="ttl"><span>&#10003;</span>Подписка</div>
        <p>Без рекламы<br>и с новыми функциями</p>
      </div>
    </div>
  </div>

  <div class="cta">
    <div><b>Готовы поддержать подпиской?</b><u>И какая сумма в месяц была бы комфортной?</u></div>
    <div class="arrow">&#8595;</div>
  </div>
</div>
</body></html>`;

const render = (file, w, h, out) => execFileSync(CHROME, ['--headless=new', '--disable-gpu', '--hide-scrollbars',
  '--force-device-scale-factor=1', `--window-size=${w},${h}`, '--virtual-time-budget=8000',
  `--screenshot=${out}`, `file:///${file}`], { stdio: 'ignore' });

const VARIANTS = [
  { name: 'post_1080x1080', w: 1080, h: 1080, k: 1 },
  { name: 'post_1280x720', w: 1280, h: 720, k: 0.74, wide: true },
];
for (const v of VARIANTS) {
  const f = `${OUT}/html/${v.name}.html`;
  fs.writeFileSync(f, page(v));
  render(f, v.w, v.h, `${OUT}/${v.name}.png`);
  console.log('rendered', v.name);
}
