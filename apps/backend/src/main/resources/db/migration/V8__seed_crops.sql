-- V8: Seed crop database with common Moroccan crops
-- Each crop includes multilingual data (FR/AR/Darija), requirements, growth stages, nutrients, and common issues

-- ============================================================
-- 1. TOMATO (Tomate / طماطم / Matisha)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000001-0000-0000-0000-000000000001', 'Tomate', 'طماطم', 'Matisha', 'Solanum lycopersicum', 'VEGETABLE',
  'Légume-fruit incontournable de la cuisine marocaine, cultivé dans tout le pays.',
  'من أهم الخضروات في المطبخ المغربي، تزرع في جميع أنحاء البلاد.',
  'Matisha hiya mn ahsen khodar li kayn f lmghrib, katzre3 f kolla blassa.',
  'Mars - Juin', 75, 'EASY');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 40, 70, 18.0, 30.0, 2000, 8000, 50.0, 80.0, 'Loam', 6.0, 6.8, 'Tous les 2-3 jours');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 1, 'Semis', 'البذر', 'Zri3a', 10, 'Semer les graines en intérieur à 1cm de profondeur.', 'زرع البذور في الداخل على عمق 1 سم.', 'Zre3 lbdor dakhel dar, 1cm f trab.'),
('c0000002-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 2, 'Plantule', 'الشتلة', 'Chtila', 20, 'Les premières vraies feuilles apparaissent.', 'تظهر الأوراق الحقيقية الأولى.', 'Bdaw ybanw lwraq lloula.'),
('c0000003-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 3, 'Croissance', 'النمو', 'Nmow', 25, 'La plante grandit rapidement, tuteurer si nécessaire.', 'تنمو النبتة بسرعة، استخدم الدعامات إذا لزم الأمر.', 'Nbta katbda tkber bzzaf, dir support ila khass.'),
('c0000004-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 4, 'Floraison', 'التزهير', 'Tzhir', 10, 'Les fleurs jaunes apparaissent.', 'تظهر الأزهار الصفراء.', 'Kaybanw zwahri sfra.'),
('c0000005-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 5, 'Récolte', 'الحصاد', '7sad', 10, 'Récolter quand les fruits sont rouges et fermes.', 'الحصاد عندما تصبح الثمار حمراء وصلبة.', 'Qte3 matisha mlli twlli 7amra w qas7a.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 'MEDIUM', 'HIGH', 'HIGH', 'NPK 10-20-20 + compost', 'سماد NPK 10-20-20 + كمبوست', 'Smed NPK 10-20-20 + compost', 'Toutes les 2 semaines');

INSERT INTO crop_issues (id, crop_id, issue_type, name, name_ar, name_darija, symptoms, symptoms_ar, symptoms_darija, treatment, treatment_ar, treatment_darija, prevention) VALUES
('e0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 'DISEASE', 'Mildiou', 'البياض الزغبي', 'Mildiou', 'Taches brunes sur les feuilles, pourriture des fruits.', 'بقع بنية على الأوراق، تعفن الثمار.', 'Tbo9a3 9ahwiya f lwra9, tmatisha kat-khwnz.', 'Traiter au cuivre, supprimer les parties atteintes.', 'معالجة بالنحاس وإزالة الأجزاء المصابة.', 'Dir n7as, 9te3 lwra9 li fiha lmrad.', 'Bonne aération, éviter mouiller le feuillage.'),
('e0000002-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 'PEST', 'Mouche blanche', 'الذبابة البيضاء', 'Dbbana byda', 'Petits insectes blancs sous les feuilles, feuilles jaunissantes.', 'حشرات بيضاء صغيرة تحت الأوراق، اصفرار الأوراق.', '7ashrat byda s8ar t7t lwra9, lwra9 kaytsfru.', 'Savon noir, huile de neem.', 'صابون أسود، زيت النيم.', 'Sabon khal, zit neem.', 'Installer des pièges jaunes collants.');

-- ============================================================
-- 2. MINT (Menthe / نعناع / Na3na3)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000002-0000-0000-0000-000000000002', 'Menthe', 'نعناع', 'Na3na3', 'Mentha spicata', 'HERB',
  'Herbe aromatique essentielle pour le thé marocain, très facile à cultiver.',
  'عشبة عطرية أساسية للشاي المغربي، سهلة الزراعة جداً.',
  'Na3na3 howa asas d atay lmghribi, sahel bzzaf yzre3.',
  'Toute l''année', 30, 'EASY');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000002-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 50, 80, 15.0, 30.0, 1000, 6000, 50.0, 70.0, 'Humifère', 6.0, 7.0, 'Tous les jours en été');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 1, 'Bouturage', 'العقل', 'Bouture', 7, 'Planter des boutures dans un sol humide.', 'غرس العقل في تربة رطبة.', 'Dir bouture f trab fih lma.'),
('c0000002-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 2, 'Enracinement', 'التجذير', 'Tjdir', 14, 'Les racines se développent, garder le sol humide.', 'تتطور الجذور، حافظ على رطوبة التربة.', 'Ljdor katbda tkhdem, khelli trab fih rtouba.'),
('c0000003-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 3, 'Récolte continue', 'حصاد مستمر', '7sad mtwa9ef', 9, 'Couper les tiges régulièrement pour stimuler la croissance.', 'قطع السيقان بانتظام لتحفيز النمو.', '9te3 lqsab b nidham bach yzid ynmu.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000002-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 'MEDIUM', 'LOW', 'LOW', 'Compost organique', 'كمبوست عضوي', 'Compost organique', 'Une fois par mois');

INSERT INTO crop_issues (id, crop_id, issue_type, name, name_ar, name_darija, symptoms, symptoms_ar, symptoms_darija, treatment, treatment_ar, treatment_darija, prevention) VALUES
('e0000001-0000-0000-0000-000000000002', 'a0000002-0000-0000-0000-000000000002', 'DISEASE', 'Rouille', 'الصدأ', 'Rouille', 'Points orange sous les feuilles.', 'نقاط برتقالية تحت الأوراق.', 'No9at orangiya t7t lwra9.', 'Supprimer les feuilles atteintes, améliorer aération.', 'إزالة الأوراق المصابة وتحسين التهوية.', '9te3 lwra9 li fiha mrad, dir thaweya mezyana.', 'Espacement suffisant entre les plants.');

-- ============================================================
-- 3. BASIL (Basilic / ريحان / 7ba9)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000003-0000-0000-0000-000000000003', 'Basilic', 'ريحان', '7ba9', 'Ocimum basilicum', 'HERB',
  'Herbe aromatique populaire au Maroc, utilisée fraîche et comme répulsif naturel.',
  'عشبة عطرية شائعة في المغرب، تستخدم طازجة وكطارد طبيعي.',
  '7ba9 mchhor f lmghrib, kayst3mloh tri w bach ybaa3ed nnamoss.',
  'Avril - Septembre', 40, 'EASY');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000003-0000-0000-0000-000000000003', 'a0000003-0000-0000-0000-000000000003', 40, 65, 20.0, 35.0, 3000, 8000, 40.0, 60.0, 'Bien drainé', 6.0, 7.0, 'Tous les 2 jours');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000003', 'a0000003-0000-0000-0000-000000000003', 1, 'Germination', 'الإنبات', 'Inbat', 10, 'Semer à 0,5cm, maintenir humide et chaud.', 'زرع على عمق 0.5 سم، الحفاظ على الرطوبة والدفء.', 'Zre3 f 0.5cm, khelli trab fih lma w skhon.'),
('c0000002-0000-0000-0000-000000000003', 'a0000003-0000-0000-0000-000000000003', 2, 'Croissance', 'النمو', 'Nmow', 20, 'Pincer les sommets pour un buisson touffu.', 'قص القمم للحصول على نبتة كثيفة.', '9te3 rras bach ykoun ghli9.'),
('c0000003-0000-0000-0000-000000000003', 'a0000003-0000-0000-0000-000000000003', 3, 'Récolte', 'الحصاد', '7sad', 10, 'Récolter les feuilles avant la floraison.', 'حصاد الأوراق قبل التزهير.', '9te3 lwra9 9bel ma yzher.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000003-0000-0000-0000-000000000003', 'a0000003-0000-0000-0000-000000000003', 'LOW', 'LOW', 'LOW', 'Compost léger', 'كمبوست خفيف', 'Compost khfif', 'Toutes les 4 semaines');

-- ============================================================
-- 4. OLIVE (Olivier / زيتون / Zitoun)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000004-0000-0000-0000-000000000004', 'Olivier', 'زيتون', 'Zitoun', 'Olea europaea', 'FRUIT',
  'Arbre emblématique du Maroc, premier producteur africain d''huile d''olive.',
  'شجرة رمزية للمغرب، أول منتج أفريقي لزيت الزيتون.',
  'Zitoun howa rramz d lmghrib, nmer 1 f Africa f zit.',
  'Plantation: Novembre-Février', 365, 'MEDIUM');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000004-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 20, 50, 5.0, 40.0, 5000, 10000, 30.0, 60.0, 'Calcaire, drainé', 6.5, 8.5, 'Une fois par semaine en été');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 1, 'Plantation', 'الغرس', 'Ghers', 30, 'Planter en hiver dans un sol bien drainé.', 'الغرس في فصل الشتاء في تربة جيدة التصريف.', 'Ghers f chta f trab li kaychred lma.'),
('c0000002-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 2, 'Croissance juvénile', 'النمو الأولي', 'Nmow lowwel', 1095, 'L''arbre se développe pendant 3-5 ans avant la première production.', 'ينمو الشجر لمدة 3-5 سنوات قبل الإنتاج الأول.', 'Chejra katbda tkber 3-5 snin 9bel ma t3ti.'),
('c0000003-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 3, 'Production', 'الإنتاج', 'Intaj', 180, 'Floraison au printemps, récolte en automne.', 'التزهير في الربيع والحصاد في الخريف.', 'Kayzher f rbi3, w n7sdo f lkhrif.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000004-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 'MEDIUM', 'LOW', 'MEDIUM', 'Fumure organique + NPK', 'سماد عضوي + NPK', 'Smed organique + NPK', 'Deux fois par an');

INSERT INTO crop_issues (id, crop_id, issue_type, name, name_ar, name_darija, symptoms, symptoms_ar, symptoms_darija, treatment, treatment_ar, treatment_darija, prevention) VALUES
('e0000001-0000-0000-0000-000000000004', 'a0000004-0000-0000-0000-000000000004', 'PEST', 'Mouche de l''olive', 'ذبابة الزيتون', 'Dbbana d zitoun', 'Trous dans les olives, chute prématurée des fruits.', 'ثقوب في الزيتون وسقوط الثمار المبكر.', 'Thqob f zitoun, kaytay7 9bel lwa9t.', 'Pièges à phéromones, traitement au kaolin.', 'مصائد الفيرومونات ومعالجة بالكاولين.', 'Pièges, dir kaolin.', 'Récolter tôt, ramasser les olives tombées.');

-- ============================================================
-- 5. CORIANDER (Coriandre / كزبرة / 9osbor)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000005-0000-0000-0000-000000000005', 'Coriandre', 'كزبرة', '9osbor', 'Coriandrum sativum', 'HERB',
  'Herbe indispensable de la cuisine marocaine, utilisée dans la chermoula et les tajines.',
  'عشبة لا غنى عنها في المطبخ المغربي، تستخدم في الشرمولة والطاجين.',
  '9osbor makayghnasch f lmghrib, kayst3mloh f chermoula w tajin.',
  'Septembre - Mai', 45, 'EASY');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000005-0000-0000-0000-000000000005', 'a0000005-0000-0000-0000-000000000005', 40, 60, 10.0, 25.0, 2000, 6000, 40.0, 60.0, 'Léger, drainé', 6.2, 6.8, 'Tous les 2-3 jours');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000005', 'a0000005-0000-0000-0000-000000000005', 1, 'Germination', 'الإنبات', 'Inbat', 14, 'Semer directement en place, les graines sont lentes à germer.', 'زرع مباشرة في المكان، البذور بطيئة في الإنبات.', 'Zre3 direct f blastou, lbdor kaydiru chwiya.'),
('c0000002-0000-0000-0000-000000000005', 'a0000005-0000-0000-0000-000000000005', 2, 'Croissance foliaire', 'النمو الورقي', 'Nmow d lwra9', 21, 'Les feuilles se développent, récolter progressivement.', 'تنمو الأوراق، يمكن الحصاد تدريجياً.', 'Lwra9 katbda tkber, 9te3 chwiya b chwiya.'),
('c0000003-0000-0000-0000-000000000005', 'a0000005-0000-0000-0000-000000000005', 3, 'Montée en graines', 'تكوين البذور', 'Lbdor', 10, 'La plante monte et fleurit, les graines de coriandre se forment.', 'ترتفع النبتة وتزهر، تتكون بذور الكزبرة.', 'Nbta katl3a w katzher, lbdor d 9osbor katjma3.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000005-0000-0000-0000-000000000005', 'a0000005-0000-0000-0000-000000000005', 'LOW', 'LOW', 'LOW', 'Compost naturel', 'كمبوست طبيعي', 'Compost tabi3i', 'Au semis uniquement');

-- ============================================================
-- 6. HOT PEPPER (Piment / فلفل حار / Flafla 7arra)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000006-0000-0000-0000-000000000006', 'Piment', 'فلفل حار', 'Flafla 7arra', 'Capsicum annuum', 'VEGETABLE',
  'Ingrédient de base pour la harissa marocaine, cultivé dans les régions chaudes.',
  'مكون أساسي للهريسة المغربية، يزرع في المناطق الحارة.',
  'Flafla hiya lasas d harissa lmghribiya, katzre3 f lblayess skhonin.',
  'Mars - Juillet', 90, 'MEDIUM');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000006-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 40, 65, 20.0, 35.0, 3000, 8000, 50.0, 70.0, 'Riche, drainé', 6.0, 6.8, 'Tous les 2-3 jours');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 1, 'Semis', 'البذر', 'Zri3a', 14, 'Semer en intérieur, maintenir à 25°C.', 'زرع في الداخل عند 25 درجة.', 'Zre3 dakhel dar, khelli sokhkhana 25°C.'),
('c0000002-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 2, 'Repiquage', 'الشتل', 'Chtil', 20, 'Transplanter quand les plants ont 4-6 feuilles.', 'نقل الشتلات عندما يكون لها 4-6 أوراق.', 'N99el chtila mlli ykoun fiha 4-6 wra9at.'),
('c0000003-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 3, 'Fructification', 'الإثمار', 'Ithmar', 40, 'Les fruits apparaissent et mûrissent.', 'تظهر الثمار وتنضج.', 'Flafla katbda tban w katnyed.'),
('c0000004-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 4, 'Récolte', 'الحصاد', '7sad', 16, 'Récolter quand les piments sont rouges.', 'الحصاد عندما تصبح الثمار حمراء.', '9te3 flafla mlli twlli 7amra.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000006-0000-0000-0000-000000000006', 'a0000006-0000-0000-0000-000000000006', 'MEDIUM', 'MEDIUM', 'HIGH', 'NPK 5-10-15 + fumier', 'سماد NPK 5-10-15 + سماد عضوي', 'NPK 5-10-15 + ghbar', 'Toutes les 2 semaines');

-- ============================================================
-- 7. FIGUIER DE BARBARIE (Cactus / صبار / Hndiya)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000007-0000-0000-0000-000000000007', 'Figuier de Barbarie', 'صبار', 'Hndiya', 'Opuntia ficus-indica', 'FRUIT',
  'Cactus emblématique du Maroc, produit des figues de Barbarie et de l''huile précieuse.',
  'صبار رمزي للمغرب، ينتج التين الشوكي وزيتاً ثميناً.',
  'Hndiya mchhoura f lmghrib, kat3ti lhndiya w zit ghalya.',
  'Plantation: Mars-Avril', 365, 'EASY');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000007-0000-0000-0000-000000000007', 'a0000007-0000-0000-0000-000000000007', 10, 30, 5.0, 45.0, 5000, 12000, 20.0, 50.0, 'Sableux, très drainé', 6.0, 7.5, 'Rarement, une fois par mois max');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000007', 'a0000007-0000-0000-0000-000000000007', 1, 'Bouturage', 'العقل', 'Bouture', 30, 'Planter une raquette dans le sol sec.', 'غرس لوح الصبار في تربة جافة.', 'Ghers war9a d hndiya f trab jaf.'),
('c0000002-0000-0000-0000-000000000007', 'a0000007-0000-0000-0000-000000000007', 2, 'Établissement', 'الاستقرار', 'Isti9rar', 365, 'Le cactus s''enracine et grandit la première année.', 'يتجذر الصبار وينمو في السنة الأولى.', 'Hndiya katbda ttjdder w tkber f sana loula.'),
('c0000003-0000-0000-0000-000000000007', 'a0000007-0000-0000-0000-000000000007', 3, 'Fructification', 'الإثمار', 'Ithmar', 120, 'Les fruits mûrissent en été (juillet-septembre).', 'تنضج الثمار في الصيف (يوليو-سبتمبر).', 'Lhndiya katnyed f ssif (yulyu-shtanbr).');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000007-0000-0000-0000-000000000007', 'a0000007-0000-0000-0000-000000000007', 'LOW', 'LOW', 'LOW', 'Aucun ou compost léger', 'بدون أو كمبوست خفيف', 'Bla smed wla compost khfif', 'Une fois par an max');

-- ============================================================
-- 8. DATE PALM (Palmier dattier / نخلة / Nkhla)
-- ============================================================
INSERT INTO crops (id, name, name_ar, name_darija, scientific_name, category, description, description_ar, description_darija, growing_season, days_to_harvest, difficulty)
VALUES ('a0000008-0000-0000-0000-000000000008', 'Palmier dattier', 'نخلة', 'Nkhla', 'Phoenix dactylifera', 'FRUIT',
  'Arbre roi des oasis du sud marocain, producteur des dattes Mejhoul renommées.',
  'شجرة ملكة واحات جنوب المغرب، منتجة تمور المجهول الشهيرة.',
  'Nkhla hiya lmalika d waha f jnoub lmghrib, kat3ti tmer mejhoul.',
  'Pollinisation: Mars-Avril', 200, 'HARD');

INSERT INTO crop_requirements (id, crop_id, soil_moisture_min, soil_moisture_max, temp_min, temp_max, light_min, light_max, humidity_min, humidity_max, soil_type, ph_min, ph_max, water_frequency)
VALUES ('b0000008-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 20, 50, 0.0, 50.0, 6000, 12000, 20.0, 50.0, 'Sableux-limoneux', 7.0, 8.0, 'Irrigation régulière en profondeur');

INSERT INTO crop_growth_stages (id, crop_id, stage_order, name, name_ar, name_darija, duration_days, description, description_ar, description_darija) VALUES
('c0000001-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 1, 'Plantation', 'الغرس', 'Ghers', 30, 'Planter un rejeton au printemps.', 'غرس فسيلة في الربيع.', 'Ghers fessila f rbi3.'),
('c0000002-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 2, 'Croissance juvénile', 'النمو الأولي', 'Nmow lowwel', 1825, 'Le palmier pousse pendant 5 ans avant la première récolte.', 'ينمو النخل لمدة 5 سنوات قبل أول حصاد.', 'Nkhla katnmu 5 snin 9bel ma t3ti.'),
('c0000003-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 3, 'Pollinisation', 'التلقيح', 'Tlqi7', 3, 'Pollinisation manuelle au printemps.', 'تلقيح يدوي في الربيع.', 'Tlqi7 b lyed f rbi3.'),
('c0000004-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 4, 'Maturation', 'النضج', 'Nyed', 180, 'Les dattes mûrissent de juin à octobre.', 'تنضج التمور من يونيو إلى أكتوبر.', 'Tmer kaynyed mn yunyu 7ta oktobar.');

INSERT INTO crop_nutrients (id, crop_id, nitrogen_need, phosphorus_need, potassium_need, fertilizer_type, fertilizer_type_ar, fertilizer_type_darija, application_frequency)
VALUES ('d0000008-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 'HIGH', 'MEDIUM', 'HIGH', 'Fumier + NPK riche en potasse', 'سماد عضوي + NPK غني بالبوتاسيوم', 'Ghbar + NPK fih potasyom bzzaf', 'Trois fois par an');

INSERT INTO crop_issues (id, crop_id, issue_type, name, name_ar, name_darija, symptoms, symptoms_ar, symptoms_darija, treatment, treatment_ar, treatment_darija, prevention) VALUES
('e0000001-0000-0000-0000-000000000008', 'a0000008-0000-0000-0000-000000000008', 'DISEASE', 'Bayoud', 'البيوض', 'Bayoud', 'Dessèchement des palmes d''un côté, puis mort progressive.', 'جفاف السعف من جانب واحد ثم موت تدريجي.', 'Jrida katibss mn jnb wa7ed, w nkhla katmout chwiya b chwiya.', 'Aucun remède efficace. Arracher et brûler les arbres atteints.', 'لا يوجد علاج فعال. اقتلاع وحرق الأشجار المصابة.', 'Ma3ndou dwa. Ql3 nkhla w 7r9ha.', 'Utiliser des variétés résistantes (Mejhoul résiste partiellement).');
