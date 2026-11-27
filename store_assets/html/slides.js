// Generates store graphics (RuStore / Google Play) from raw emulator screenshots.
// usage: node slides.js <outDir>
const fs = require('fs'); const path = require('path'); const { execFileSync } = require('child_process');
const SCRATCH = __dirname.replace(/\\/g, '/');
const OUTDIR = process.argv[2].replace(/\\/g, '/');
const SHOTS = fs.existsSync(SCRATCH + '/shots') ? SCRATCH + '/shots' : SCRATCH + '/../raw';
const LOGO_SRC = fs.existsSync(SCRATCH + '/assets/splash.svg') ? SCRATCH + '/assets/splash.svg' : SCRATCH + '/../raw/logo.svg';
const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';
fs.mkdirSync(OUTDIR + '/html', { recursive: true });
fs.mkdirSync(OUTDIR + '/screenshots', { recursive: true });

fs.mkdirSync(OUTDIR + '/raw', { recursive: true });
const USED = new Set();
const shot = n => { USED.add(n); if (`${SHOTS}/${n}.png` !== `${OUTDIR}/raw/${n}.png`) fs.copyFileSync(`${SHOTS}/${n}.png`, `${OUTDIR}/raw/${n}.png`); return `../raw/${n}.png`; };
if (LOGO_SRC !== `${OUTDIR}/raw/logo.svg`) fs.copyFileSync(LOGO_SRC, `${OUTDIR}/raw/logo.svg`);
const logo = '../raw/logo.svg';

const THEMES = {
  green: { bg: 'linear-gradient(165deg,#F9FAEF 0%,#E4F0D2 55%,#CDEDA3 100%)', blob1: '#B1D18A', blob2: '#63C98A', text: '#14260C', sub: '#44483D', accent: '#0A8F3C', chip: '#0A8F3C' },
  deep: { bg: 'linear-gradient(160deg,#0B3D22 0%,#0F6B3A 60%,#1C9B55 100%)', blob1: '#63C98A', blob2: '#CDEDA3', text: '#FFFFFF', sub: '#D6EBD9', accent: '#CDEDA3', chip: '#CDEDA3', dark: true },
  orange: { bg: 'linear-gradient(160deg,#FFF6E5 0%,#FFE1B3 55%,#FFC46B 100%)', blob1: '#FFB020', blob2: '#FF7A00', text: '#2B1A00', sub: '#5C4620', accent: '#E86A00', chip: '#E86A00' },
  ember: { bg: 'linear-gradient(160deg,#7A2E00 0%,#E85D00 60%,#FF9A1F 100%)', blob1: '#FFC46B', blob2: '#FFE1B3', text: '#FFFFFF', sub: '#FFE7CF', accent: '#FFE1B3', chip: '#FFE1B3', dark: true },
  navy: { bg: 'linear-gradient(160deg,#101B2C 0%,#1E2F4A 60%,#2E4A6B 100%)', blob1: '#00C853', blob2: '#63C98A', text: '#FFFFFF', sub: '#C9D4E3', accent: '#7CF2A6', chip: '#7CF2A6', dark: true },
};

// Slides: 1080x1920 portrait.
const SLIDES = [
  { file: '01_hero', theme: 'deep', hero: true, kicker: 'Моё хозяйство', title: 'Ферма и инкубатор<br>в одном приложении', sub: 'Продукция, продажи, расходы, животные, склад и инкубация — всё под контролем, офлайн и без лишних кнопок.', img: 'home' },
  { file: '02_sklad', theme: 'green', kicker: 'Склад', title: 'Остатки всегда<br>под рукой', sub: 'Сколько яиц, молока и корма есть прямо сейчас — считается автоматически по каждой записи.', img: 'warehouse', bullets: ['Продукция и корма отдельно', 'Предупредит, когда корм закончится'] },
  { file: '03_journal', theme: 'green', kicker: 'Журнал', title: 'Каждая запись —<br>пара касаний', sub: 'Продукция, продажи, покупки, списания и животные — пять разделов с поиском и группировкой.', img: 'journal_add', bullets: ['Привязка к животному и категории', 'Быстрый ввод по шаблону'] },
  { file: '04_finance', theme: 'navy', kicker: 'Финансы', title: 'Считает деньги<br>вместо бухгалтера', sub: 'Баланс, доходы и расходы за месяц, сэкономленное на собственных нуждах и потери — на одном экране.', img: 'finance' },
  { file: '05_analytics', theme: 'green', kicker: 'Аналитика', title: 'Видно, что реально<br>приносит доход', sub: 'Разбивка по категориям и товарам за любой период: молоко, яйцо, мясо — с долями в процентах.', img: 'fin_income', bullets: ['Анализ по каждому товару', 'Себестоимость и средняя цена'] },
  { file: '06_animals', theme: 'green', kicker: 'Животные', title: 'Карточка на каждое<br>животное и стадо', sub: 'Возраст, поголовье, вес, размеры, прививки и расход корма. Видно, сколько продукции дало каждое животное.', img: 'animal_cow' },
  { file: '07_incubator', theme: 'ember', kicker: 'Инкубатор', title: 'Инкубация<br>день за днём', sub: 'Счётчик дней, прогресс закладки, даты вывода и нормы температуры, влажности и переворота на сегодня.', img: 'inc_main' },
  { file: '08_schedule', theme: 'orange', kicker: 'Расписание', title: 'Готовые режимы<br>для любой птицы', sub: 'Куры, гуси, утки, перепела, индейки: таблица температуры, влажности и переворота на весь срок инкубации.', img: 'inc_schedule', bullets: ['Факт рядом с нормой', 'Уведомления в нужное время'] },
  { file: '09_qr', theme: 'green', kicker: 'Шаблоны и QR', title: 'Шаблоны улетают<br>по QR‑коду', sub: 'Сохраняйте частые операции и делитесь ими между устройствами — данные зашифрованы.', img: 'tpl_qr' },
  { file: '10_offline', theme: 'deep', kicker: 'Ваши данные', title: 'Работает офлайн.<br>Данные только у вас', sub: 'Всё хранится на устройстве. Экспорт и импорт проектов в файл — резервная копия в одно касание.', img: 'settings', bullets: ['Без регистрации и аккаунтов', 'Ежедневные уведомления по хозяйству'] },
];

const css = t => `
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700;800;900&display=swap');
*{box-sizing:border-box;margin:0;padding:0}
html,body{width:1080px;height:1920px;overflow:hidden;font-family:'Nunito','Segoe UI',Arial,sans-serif;background:${t.bg};color:${t.text};position:relative}body{display:flex;flex-direction:column}
.blob{position:absolute;border-radius:50%;filter:blur(90px);opacity:.55;pointer-events:none}
.b1{width:720px;height:720px;background:${t.blob1};right:-260px;top:-220px}
.b2{width:640px;height:640px;background:${t.blob2};left:-300px;bottom:380px;opacity:.35}
.grain{position:absolute;inset:0;background:radial-gradient(circle at 20% 10%,rgba(255,255,255,.25),transparent 45%)}
.top{position:relative;padding:112px 84px 0;z-index:2}
.kicker{display:inline-flex;align-items:center;gap:14px;font-weight:800;font-size:34px;letter-spacing:.06em;text-transform:uppercase;color:${t.chip};background:${t.dark ? 'rgba(255,255,255,.12)' : 'rgba(10,143,60,.10)'};padding:16px 30px;border-radius:999px;margin-bottom:36px}
.kicker i{width:16px;height:16px;border-radius:50%;background:${t.chip};display:inline-block}
h1{font-weight:900;font-size:84px;line-height:1.08;letter-spacing:-.015em;margin-bottom:30px}
.sub{font-size:38px;line-height:1.35;font-weight:600;color:${t.sub};max-width:900px}
.bullets{display:flex;flex-wrap:wrap;gap:16px;margin-top:34px}
.bullets span{font-size:30px;font-weight:700;padding:14px 26px;border-radius:999px;background:${t.dark ? 'rgba(255,255,255,.14)' : 'rgba(255,255,255,.75)'};color:${t.dark ? '#fff' : t.text};box-shadow:0 6px 24px rgba(0,0,0,${t.dark ? '.18' : '.06'})}
.bullets span::before{content:'✓';color:${t.accent};font-weight:900;margin-right:12px}
.phone{position:relative;flex:none;margin:60px auto 0;width:760px;border-radius:78px;background:#0e0e10;padding:16px;box-shadow:0 60px 120px rgba(0,0,0,${t.dark ? '.55' : '.28'}),0 0 0 2px rgba(255,255,255,.08) inset;z-index:3}
.phone .screen{border-radius:62px;overflow:hidden;background:#000;position:relative}
.phone img{display:block;width:100%}
.phone .notch{position:absolute;top:22px;left:50%;transform:translateX(-50%);width:34px;height:34px;border-radius:50%;background:#000;z-index:4}
.brand{position:absolute;right:84px;top:100px;display:flex;align-items:center;gap:18px;z-index:5}
.brand img{width:110px;height:110px;filter:drop-shadow(0 8px 20px rgba(0,0,0,.25))}
`;

const html = s => {
  const t = THEMES[s.theme];
  return `<!doctype html><html lang="ru"><head><meta charset="utf-8"><style>${css(t)}
.top{padding-top:${s.hero ? 150 : 112}px}
${s.hero ? `.logo{width:200px;height:200px;background:#fff;border-radius:52px;display:flex;align-items:center;justify-content:center;box-shadow:0 30px 60px rgba(0,0,0,.35);margin-bottom:44px}.logo img{width:150px}
h1{font-size:84px}.sub{font-size:40px}` : ''}
</style></head><body>
<div class="blob b1"></div><div class="blob b2"></div><div class="grain"></div>
<div class="top">
  ${s.hero ? `<div class="logo"><img src="${logo}"></div>` : `<div class="kicker"><i></i>${s.kicker}</div>`}
  <h1>${s.title}</h1>
  <p class="sub">${s.sub}</p>
  ${s.bullets ? `<div class="bullets">${s.bullets.map(b => `<span>${b}</span>`).join('')}</div>` : ''}
</div>
<div class="phone"><div class="screen"><div class="notch"></div><img src="${shot(s.img)}"></div></div>
</body></html>`;
};

const render = (file, w, h, out) => {
  execFileSync(CHROME, ['--headless=new', '--disable-gpu', '--hide-scrollbars', '--force-device-scale-factor=1', `--window-size=${w},${h}`, '--virtual-time-budget=8000', `--screenshot=${out}`, `file:///${file}`], { stdio: 'ignore' });
};

for (const s of SLIDES) {
  const f = `${OUTDIR}/html/${s.file}.html`; fs.writeFileSync(f, html(s));
  render(f, 1080, 1920, `${OUTDIR}/screenshots/${s.file}.png`); console.log('rendered', s.file);
}

// ---- Feature graphic 1024x500 (Google Play) + RuStore cover 1440x810
const banner = (w, h, scale) => `<!doctype html><html lang="ru"><head><meta charset="utf-8"><style>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@600;800;900&display=swap');
*{box-sizing:border-box;margin:0;padding:0}
html,body{width:${w}px;height:${h}px;overflow:hidden;font-family:'Nunito','Segoe UI',Arial,sans-serif;background:linear-gradient(120deg,#0B3D22 0%,#0F6B3A 55%,#1C9B55 100%);color:#fff;position:relative}
.blob{position:absolute;border-radius:50%;filter:blur(${70*scale}px);opacity:.5}
.b1{width:${520*scale}px;height:${520*scale}px;background:#63C98A;right:${-120*scale}px;top:${-200*scale}px}
.b2{width:${420*scale}px;height:${420*scale}px;background:#CDEDA3;left:${-160*scale}px;bottom:${-220*scale}px;opacity:.3}
.wrap{position:absolute;inset:0;display:flex;align-items:center;padding:0 ${330*scale}px 0 ${56*scale}px;gap:${36*scale}px}
.logo{flex:none;width:${170*scale}px;height:${170*scale}px;background:#fff;border-radius:${46*scale}px;display:flex;align-items:center;justify-content:center;box-shadow:0 ${24*scale}px ${60*scale}px rgba(0,0,0,.35)}
.logo img{width:${126*scale}px}
h1{font-weight:900;font-size:${52*scale}px;line-height:1.05;letter-spacing:-.01em;white-space:nowrap}
p{font-size:${23*scale}px;font-weight:600;color:#D6EBD9;margin-top:${12*scale}px;max-width:${420*scale}px;line-height:1.3}
.chips{display:flex;gap:${10*scale}px;margin-top:${22*scale}px;flex-wrap:wrap}
.chips span{font-size:${17*scale}px;font-weight:800;padding:${8*scale}px ${18*scale}px;border-radius:999px;background:rgba(255,255,255,.14);color:#CDEDA3}
.phone{position:absolute;right:${36*scale}px;top:${50*scale}px;width:${225*scale}px;border-radius:${30*scale}px;background:#0e0e10;padding:${6*scale}px;box-shadow:0 ${30*scale}px ${60*scale}px rgba(0,0,0,.5);transform:rotate(-8deg)}
.phone div{border-radius:${24*scale}px;overflow:hidden}.phone img{display:block;width:100%}
</style></head><body><div class="blob b1"></div><div class="blob b2"></div>
<div class="wrap"><div class="logo"><img src="${logo}"></div><div><h1>Моё хозяйство</h1><p>Ферма и инкубатор в одном приложении: продукция, продажи, финансы, животные, склад.</p>
<div class="chips"><span>Офлайн</span><span>Склад</span><span>Финансы</span><span>Инкубатор</span><span>QR-шаблоны</span></div></div></div>
<div class="phone"><div><img src="${shot('finance')}"></div></div>
</body></html>`;
fs.writeFileSync(`${OUTDIR}/html/feature_graphic.html`, banner(1024, 500, 1));
render(`${OUTDIR}/html/feature_graphic.html`, 1024, 500, `${OUTDIR}/feature_graphic_1024x500.png`); console.log('rendered feature graphic');
fs.writeFileSync(`${OUTDIR}/html/cover_rustore.html`, banner(1440, 810, 1.5));
render(`${OUTDIR}/html/cover_rustore.html`, 1440, 810, `${OUTDIR}/cover_1440x810.png`); console.log('rendered cover');

// ---- Icon 512x512
fs.writeFileSync(`${OUTDIR}/html/icon.html`, `<!doctype html><html><head><meta charset="utf-8"><style>*{margin:0}html,body{width:512px;height:512px;overflow:hidden;background:#FDFDFF;display:flex;align-items:center;justify-content:center}img{width:400px}</style></head><body><img src="${logo}"></body></html>`);
render(`${OUTDIR}/html/icon.html`, 512, 512, `${OUTDIR}/icon_512x512.png`); console.log('rendered icon');
