// Seeds a realistic demo dataset into a copy of the app DB (schema v7).
// usage: node seed.js <src item_database> <out db>
const { DatabaseSync } = require('node:sqlite');
const fs = require('fs');
const [,, src, out] = process.argv;
fs.copyFileSync(src, out);
for (const s of ['-wal', '-shm']) { if (fs.existsSync(src + s)) fs.copyFileSync(src + s, out + s); }
const db = new DatabaseSync(out);
db.exec('PRAGMA journal_mode=DELETE');
db.exec('PRAGMA foreign_keys=OFF');

// ---- helpers
const TODAY = new Date(2026, 8, 7); // 07.09.2026
const pad = n => String(n).padStart(2, '0');
const dmy = d => `${pad(d.getDate())}.${pad(d.getMonth() + 1)}.${d.getFullYear()}`;
const daysAgo = n => { const d = new Date(TODAY); d.setDate(d.getDate() - n); return d; };
const rnd = (a, b) => a + Math.random() * (b - a);
const ri = (a, b) => Math.round(rnd(a, b));
let seed = 42; Math.random = () => { seed = (seed * 9301 + 49297) % 233280; return seed / 233280; };

const S = { PIECES: 1, HEADS: 2, UNITS: 3, GRAM: 4, KG: 5, ML: 7, L: 8, CM: 11, RUB: 13, KG_DAY: 18, NO: 22 };
const ICON = { LIVESTOCK: 1, RABBIT: 8, FARMING_1: 12, CHICKEN: 15, DUCK: 17, GOOSE: 18, QUAIL: 19, COW: 22, GOAT: 24, EGG: 27 };

const run = (sql, ...p) => db.prepare(sql).run(...p);

// wipe
for (const t of ['project_table','settings_table','add_table','template_table','sale_table','expenses_table','write_off_table','animal_table','animal_count_table','animal_size_table','animal_vaccination_table','animal_weight_table','note_table','ExpensesAnimalTable','incubator_table','bookmark_incubator','time_notification_project_table','time_notification_incubator_table','incubator_parameters','profile_table']) db.exec(`DELETE FROM ${t}`);
db.exec("DELETE FROM sqlite_sequence");

// ---- app settings / profile
run("UPDATE app_settings_table SET is_first_launch=0, last_version_app=current_version_app");
run("INSERT INTO profile_table(id,name) VALUES(1,'Семён')");

// ---- projects
run("INSERT INTO project_table(id,title,date,date_end,mode,archive,imagePath,currentIcon) VALUES(1,'Ферма «Зелёный луг»','15.03.2025','',1,0,NULL,?)", ICON.FARMING_1);
run("INSERT INTO project_table(id,title,date,date_end,mode,archive,imagePath,currentIcon) VALUES(2,'Инкубатор «Блиц 72»','20.08.2026','',0,0,NULL,?)", ICON.EGG);
run("INSERT INTO project_table(id,title,date,date_end,mode,archive,imagePath,currentIcon) VALUES(3,'Дача, сезон 2025','01.05.2025','30.09.2025',1,1,NULL,?)", ICON.LIVESTOCK);
for (const id of [1, 2, 3]) run("INSERT INTO settings_table(currency_suffix,weight_suffix,volume_suffix,linear_suffix,idPT) VALUES(?,?,?,?,?)", S.RUB, S.KG, S.L, S.CM, id);
run("INSERT INTO time_notification_project_table(time,note,project_id) VALUES('08:00','Сбор яиц и утреннее кормление',1)");
run("INSERT INTO time_notification_project_table(time,note,project_id) VALUES('19:30','Вечерняя дойка',1)");

// ---- animals (project 1)
const animal = (id, name, type, date, group, sex, icon, food, note = '') =>
  run("INSERT INTO animal_table(id,name,type,date,date_factory,is_group,sex,note,image_path,icon,archive,food_day,food_day_suffix,idPT) VALUES(?,?,?,?,NULL,?,?,?,NULL,?,0,?,?,1)", id, name, type, date, group, sex, note, icon, food, S.KG_DAY);
animal(1, 'Несушки Ломан Браун', 'Куры', '12.04.2025', 1, 0, ICON.CHICKEN, 0.12, 'Стадо 48 голов, пик яйценоскости');
animal(2, 'Зорька', 'Коровы', '03.05.2023', 0, 0, ICON.COW, 18, 'Чёрно-пёстрая, 2-я лактация');
animal(3, 'Марта', 'Козы', '20.06.2024', 0, 0, ICON.GOAT, 2.5, 'Зааненская');
animal(4, 'Гуси Линда', 'Гуси', '05.05.2026', 1, 0, ICON.GOOSE, 0.3, '');
animal(5, 'Кролики Фландр', 'Кролики', '18.02.2026', 1, 0, ICON.RABBIT, 0.15, '');
animal(6, 'Буян', 'Коровы', '11.09.2024', 0, 1, ICON.COW, 12, 'Бычок на откорме');

const count = (animalId, c, date, note = '', version = null) =>
  run("INSERT INTO animal_count_table(count,suffix,date,note,version,animal_id) VALUES(?,?,?,?,?,?)", c, S.HEADS, date, note, version, animalId);
count(1, '50', '12.04.2025', 'Куплены молодки 16 нед.');
count(1, '48', '20.06.2026', 'Списание 2 гол.', 3);
count(2, '1', '03.05.2023'); count(3, '1', '20.06.2024'); count(6, '1', '11.09.2024');
count(4, '24', '05.05.2026', 'Гусята суточные'); count(4, '22', '01.07.2026', '', 3);
count(5, '14', '18.02.2026'); count(5, '26', '15.06.2026', 'Окрол Дымки');

const weight = (animalId, w, date, note = '') => run("INSERT INTO animal_weight_table(weight,suffix,date,animal_id,note) VALUES(?,?,?,?,?)", w, S.KG, date, animalId, note);
[['462', '01.03.2026'], ['471', '01.04.2026'], ['478', '01.05.2026'], ['486', '01.06.2026'], ['493', '01.07.2026'], ['501', '01.08.2026'], ['508', '01.09.2026']].forEach(([w, d]) => weight(2, w, d));
[['214', '01.03.2026'], ['236', '01.04.2026'], ['259', '01.05.2026'], ['284', '01.06.2026'], ['310', '01.07.2026'], ['338', '01.08.2026'], ['365', '01.09.2026']].forEach(([w, d]) => weight(6, w, d));
[['52', '01.05.2026'], ['54', '01.07.2026'], ['55', '01.09.2026']].forEach(([w, d]) => weight(3, w, d));
const size = (animalId, s, date) => run("INSERT INTO animal_size_table(size,suffix,date,animal_id,note) VALUES(?,?,?,?,'')", s, S.CM, date, animalId);
size(2, '132', '01.03.2026'); size(2, '134', '01.09.2026'); size(6, '118', '01.03.2026'); size(6, '127', '01.09.2026');
const vac = (animalId, v, date, next, note = '') => run("INSERT INTO animal_vaccination_table(vaccination,count_vaccination,date,next_vaccination,animal_id,note) VALUES(?,?,?,?,?,?)", v, 1, date, next, animalId, note);
vac(2, 'Сибирская язва', '10.04.2026', '10.04.2027', 'Ветстанция, доктор Иванова');
vac(2, 'Лептоспироз', '22.05.2026', '22.11.2026');
vac(6, 'Сибирская язва', '10.04.2026', '10.04.2027');
vac(1, 'Ньюкаслская болезнь', '15.05.2026', '15.11.2026', 'Выпаивание');
vac(3, 'Бруцеллёз (проба)', '02.06.2026', '02.12.2026');
vac(5, 'ВГБК + миксоматоз', '20.07.2026', '20.01.2027');

// ---- production (add_table)
const add = (title, c, d, suffix, cat, animalId, note = '') =>
  run("INSERT INTO add_table(title,count,day,month,year,price,count_suffix,price_suffix,category,animal_id,note,idPT,animal_count_id) VALUES(?,?,?,?,?,0,?,?,?,?,?,1,NULL)", title, c, d.getDate(), d.getMonth() + 1, d.getFullYear(), suffix, S.RUB, cat, animalId, note);
for (let i = 0; i < 90; i++) {
  const d = daysAgo(i);
  add('Яйцо', ri(38, 46), d, S.PIECES, 'Яичная продукция', 1);
  add('Молоко', +rnd(17.5, 22.5).toFixed(1), d, S.L, 'Молочная продукция', 2);
  if (i % 2 === 0) add('Козье молоко', +rnd(2.1, 3.0).toFixed(1), d, S.L, 'Молочная продукция', 3);
  if (i % 7 === 3) add('Творог', +rnd(2.5, 4).toFixed(1), d, S.KG, 'Молочная продукция', 2, 'Из вчерашнего молока');
  if (i % 11 === 5) add('Сметана', +rnd(1.2, 2).toFixed(1), d, S.KG, 'Молочная продукция', 2);
  if (i % 30 === 12) add('Крольчатина', +rnd(6, 9).toFixed(1), d, S.KG, 'Мясо', 5);
}

// ---- sales
const sale = (title, c, suffix, price, d, cat, buyer, animalId, note = '') =>
  run("INSERT INTO sale_table(title,count,count_suffix,price,price_all,price_suffix,day,month,year,category,product_origin,buyer,note,idPT,animal_id,animal_count_id) VALUES(?,?,?,?,?,?,?,?,?,?,0,?,?,1,?,NULL)", title, c, suffix, price, +(price * c).toFixed(2), S.RUB, d.getDate(), d.getMonth() + 1, d.getFullYear(), cat, buyer, note, animalId);
const buyers = ['Рынок, ряд 4', 'Магазин «У дома»', 'Галина Петровна', 'Постоянные клиенты', 'Кафе «Печка»'];
for (let i = 0; i < 90; i++) {
  const d = daysAgo(i);
  if (i % 3 === 0) sale('Яйцо', ri(60, 120), S.PIECES, 13, d, 'Яичная продукция', buyers[i % 5], 1);
  if (i % 2 === 1) sale('Молоко', ri(10, 18), S.L, 95, d, 'Молочная продукция', buyers[(i + 1) % 5], 2);
  if (i % 7 === 4) sale('Творог', +rnd(2, 3.5).toFixed(1), S.KG, 480, d, 'Молочная продукция', buyers[(i + 2) % 5], 2);
  if (i % 5 === 2) sale('Козье молоко', ri(3, 6), S.L, 160, d, 'Молочная продукция', 'Галина Петровна', 3);
  if (i % 11 === 6) sale('Сметана', +rnd(1, 2).toFixed(1), S.KG, 520, d, 'Молочная продукция', buyers[i % 5], 2);
  if (i % 30 === 14) sale('Крольчатина', +rnd(5, 8).toFixed(1), S.KG, 650, d, 'Мясо', 'Кафе «Печка»', 5);
}
sale('Гусята 1 мес.', 6, S.HEADS, 900, daysAgo(60), 'Молодняк', 'Соседи, д. Липовка', 4);

// ---- expenses
const exp = (title, c, suffix, price, d, cat, opts = {}) =>
  run("INSERT INTO expenses_table(title,count,day,month,year,price,price_all,count_suffix,price_suffix,category,note,is_food,is_show_food,feed_food,feed_food_suffix,count_animal,food_designed_day,last_day_food,weight,weight_suffix,idPT,animalId,animal_vaccination_id,animal_count_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,NULL,NULL)",
    title, c, d.getDate(), d.getMonth() + 1, d.getFullYear(), price, +(price * c).toFixed(2), suffix, S.RUB, cat, opts.note || '', opts.food ? 1 : 0, opts.food ? 1 : 0, opts.feed ?? null, opts.feed ? S.KG_DAY : null, opts.countAnimal ?? null, opts.days ?? null, opts.lastDay ?? null, null, null, opts.animalId ?? null);
for (let i = 0; i < 90; i += 14) exp('Комбикорм ПК-1 (несушки)', 240, S.KG, 34, daysAgo(i), 'Корма', { food: true, feed: 5.8, countAnimal: 48, days: 41, lastDay: dmy(daysAgo(i - 41)), animalId: 1, note: 'Мешки по 40 кг' });
for (let i = 3; i < 90; i += 30) exp('Сено луговое', 900, S.KG, 9, daysAgo(i), 'Корма', { food: true, feed: 30, countAnimal: 2, days: 30, lastDay: dmy(daysAgo(i - 30)) });
for (let i = 7; i < 90; i += 21) exp('Зерносмесь для КРС', 300, S.KG, 21, daysAgo(i), 'Корма', { food: true, feed: 14, countAnimal: 2, days: 21, lastDay: dmy(daysAgo(i - 21)), animalId: 2 });
exp('Приём ветврача', 1, S.UNITS, 2500, daysAgo(5), 'Ветеринария', { animalId: 2, note: 'Плановый осмотр' });
exp('Вакцина ВГБК', 30, S.PIECES, 45, daysAgo(49), 'Ветеринария', { animalId: 5 });
exp('Электроэнергия', 1, S.UNITS, 2140, daysAgo(6), 'Коммунальные');
exp('Электроэнергия', 1, S.UNITS, 1980, daysAgo(37), 'Коммунальные');
exp('Электроэнергия', 1, S.UNITS, 2310, daysAgo(68), 'Коммунальные');
exp('Опилки', 12, S.UNITS, 180, daysAgo(12), 'Подстилка', { note: 'Мешки 60 л' });
exp('Ракушка кормовая', 25, S.KG, 26, daysAgo(20), 'Добавки', { animalId: 1 });
exp('Соль-лизунец', 4, S.PIECES, 210, daysAgo(33), 'Добавки', { animalId: 2 });
exp('Доильный аппарат (запчасти)', 1, S.UNITS, 3400, daysAgo(52), 'Оборудование');
exp('Сетка-рабица', 20, S.METERS ?? 12, 95, daysAgo(75), 'Стройматериалы');

// ---- write-offs
const wo = (title, c, suffix, price, d, cat, status, note = '') =>
  run("INSERT INTO write_off_table(title,count,count_suffix,price,price_all,price_suffix,category,product_origin,day,month,year,status,note,idPT,animal_count_id) VALUES(?,?,?,?,?,?,?,0,?,?,?,?,?,1,NULL)", title, c, suffix, price, +(price * c).toFixed(2), S.RUB, cat, d.getDate(), d.getMonth() + 1, d.getFullYear(), status, note);
for (let i = 0; i < 90; i += 6) wo('Яйцо', ri(20, 30), S.PIECES, 13, daysAgo(i), 'Яичная продукция', 1, 'Для семьи');
for (let i = 1; i < 90; i += 5) wo('Молоко', ri(3, 5), S.L, 95, daysAgo(i), 'Молочная продукция', 1, 'Для семьи');
wo('Яйцо', 6, S.PIECES, 13, daysAgo(2), 'Яичная продукция', 0, 'Бой при сборе');
wo('Молоко', 8, S.L, 95, daysAgo(9), 'Молочная продукция', 0, 'Скисло');
wo('Творог', 1.5, S.KG, 480, daysAgo(17), 'Молочная продукция', 1, 'Гостям');

// ---- notes
run("INSERT INTO note_table(title,note,date,idPT) VALUES('Купить сено','Договориться с Иваном о 20 тюках до дождей. Цена 9 ₽/кг.','05.09.2026',1)");
run("INSERT INTO note_table(title,note,date,idPT) VALUES('Запуск Зорьки','Запуск примерно 20 октября, отёл в конце декабря.','01.09.2026',1)");
run("INSERT INTO note_table(title,note,date,idPT) VALUES('Утеплить курятник','Заменить плёнку на окнах, проверить обогреватель.','28.08.2026',1)");

// ---- templates
const tpl = (type, name, title, c, suffix, price, cat, buyer, pinned, animalId, animalName) =>
  run("INSERT INTO template_table(template_type,name_template,title,count,count_suffix,price,price_all,price_suffix,category,is_date,animal_id,animal_name,buyer,note,write_off_status,product_origin,is_pinned,is_multi_project_template,idPT) VALUES(?,?,?,?,?,?,?,?,?,1,?,?,?,'',NULL,0,?,0,1)",
    type, name, title, c, suffix, price, price != null && c != null ? price * c : null, S.RUB, cat, animalId, animalName, buyer, pinned);
tpl(0, 'Утренний сбор яиц', 'Яйцо', 42, S.PIECES, null, 'Яичная продукция', null, 1, 1, 'Несушки Ломан Браун');
tpl(0, 'Вечерняя дойка', 'Молоко', 20, S.L, null, 'Молочная продукция', null, 1, 2, 'Зорька');
tpl(1, 'Продажа яиц на рынке', 'Яйцо', 90, S.PIECES, 13, 'Яичная продукция', 'Рынок, ряд 4', 1, 1, 'Несушки Ломан Браун');
tpl(1, 'Молоко в магазин', 'Молоко', 15, S.L, 95, 'Молочная продукция', 'Магазин «У дома»', 0, 2, 'Зорька');
tpl(3, 'Комбикорм 6 мешков', 'Комбикорм ПК-1 (несушки)', 240, S.KG, 34, 'Корма', null, 1, 1, 'Несушки Ломан Браун');
tpl(2, 'Яйца для семьи', 'Яйцо', 20, S.PIECES, 13, 'Яичная продукция', null, 0, null, null);

// ---- incubator project
run("INSERT INTO incubator_table(id,title,brand,model,capacity,price,note,is_auto_rotation,is_auto_ventilation,currency_suffix,idPT) VALUES(1,'Блиц 72','Блиц','72 Цифровой',72,12900,'Автоповорот, цифровой терморегулятор',1,0,?,2)", S.RUB);
run("INSERT INTO incubator_table(id,title,brand,model,capacity,price,note,is_auto_rotation,is_auto_ventilation,currency_suffix,idPT) VALUES(2,'Несушка 104','Несушка','104',104,8500,'',1,1,?,2)", S.RUB);
const bm = (id, incId, title, type, breed, c, rej, start, end, time, price, active, early = 0, note = '') =>
  run("INSERT INTO bookmark_incubator(id,title,type,breed,count,rejected_count,start_date,end_date,is_early_completion_status,time,price,price_all,price_suffix,chick_price,note,is_auto_rotation,is_auto_ventilation,is_activity_bookmark,idPT) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,1,0,?,?)",
    id, title, type, breed, c, rej, start, end, early, time, price, price * c, S.RUB, note, active, incId);
bm(1, 1, 'Куры Ломан Браун', 0, 'Ломан Браун', 60, 3, '27.08.2026', '17.09.2026', '07:00', 45, 1, 0, 'Яйцо от своих несушек, овоскоп на 7-й день: 3 неоплода');
bm(2, 2, 'Гуси Линда', 1, 'Линда', 40, 0, '01.09.2026', '01.10.2026', '08:00', 120, 1);
bm(3, 1, 'Перепела техасские', 2, 'Техасский белый', 72, 5, '01.07.2026', '18.07.2026', '09:00', 18, 0, 0, 'Вывод 64 из 72');
bm(4, 2, 'Утки мускусные', 3, 'Мускусная', 30, 2, '10.06.2026', '15.07.2026', '08:00', 60, 0);
run("INSERT INTO time_notification_incubator_table(time,note,bookmark_id) VALUES('07:00','Проверить температуру и влажность',1)");
run("INSERT INTO time_notification_incubator_table(time,note,bookmark_id) VALUES('19:00','Проветривание 2 × 5 мин',1)");
run("INSERT INTO time_notification_incubator_table(time,note,bookmark_id) VALUES('08:00','Охлаждение яиц',2)");

// incubator day presets straight from EntryBookmarkViewModel.setIncubator
const kt = fs.readFileSync('D:/Work/_Project/ferma_compose2/app/src/main/java/com/zaroslikov/fermacompose2/ui/incubator_project/bookmark/entry/EntryBookmarkViewModel.kt', 'utf8');
const presets = {};
for (const m of kt.matchAll(/TypeEgg\.(\w+) -> return listOf\(([\s\S]*?)\n\s*\)/g)) {
  presets[m[1]] = [...m[2].matchAll(/ParameterDay\((\d+), "([^"]*)", "([^"]*)", "([^"]*)", "([^"]*)"\)/g)].map(x => ({ day: +x[1], temp: x[2], damp: x[3], over: x[4], airing: x[5] }));
}
const typeName = { 0: 'CHICKENS', 1: 'GEESE', 2: 'QUAILS', 3: 'DUCKS', 4: 'TURKEYS' };
const jitter = (v, a) => (parseFloat(v) + rnd(-a, a)).toFixed(1);
for (const [bid, type, doneDays] of [[1, 0, 12], [2, 1, 7], [3, 2, 99], [4, 3, 99]]) {
  const list = presets[typeName[type]] || [];
  if (!list.length) throw new Error('no preset for ' + typeName[type]);
  list.forEach(p => {
    const done = p.day <= doneDays;
    run("INSERT INTO incubator_parameters(day,temp,damp,over,airing,temp_fact,damp_fact,over_fact,airing_fact,note,idPT) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
      p.day, p.temp, p.damp, p.over, p.airing,
      done ? jitter(p.temp, 0.15) : '', done ? String(Math.round(parseFloat(p.damp) + rnd(-3, 3))) : '', done ? p.over : '', done ? p.airing : '',
      done && p.day === 7 && bid === 1 ? 'Овоскопирование: 3 яйца неоплод, убраны' : '', bid);
  });
}
console.log('presets:', Object.fromEntries(Object.entries(presets).map(([k, v]) => [k, v.length])));

db.exec('PRAGMA foreign_keys=ON');
const fk = db.prepare('PRAGMA foreign_key_check').all();
if (fk.length) { console.error('FK violations', fk.slice(0, 5)); process.exit(1); }
for (const t of ['project_table','animal_table','add_table','sale_table','expenses_table','write_off_table','template_table','bookmark_incubator','incubator_parameters']) console.log(t, db.prepare(`select count(*) c from ${t}`).get().c);
db.exec('VACUUM');
db.close();
for (const s of ['-wal', '-shm']) { if (fs.existsSync(out + s)) fs.unlinkSync(out + s); }
console.log('OK', out);
