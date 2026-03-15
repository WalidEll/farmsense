-- V17: Seed breeds with complete template data
-- Feature: 002-breed-catalogue

-- ============================================================
-- COMMERCIAL BREEDS
-- ============================================================

-- 1. Sasso (French broiler breed)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000001-0000-0000-0000-000000000001', 'Sasso', 'ساسو', 'Sasso', 'COMMERCIAL', 'BROILERS', 'France',
  'Sasso est une race française à croissance lente, prisée pour sa chair savoureuse et sa rusticité. Idéal pour l''élevage traditionnel.',
  'ساسو هي سلالة فرنسية بطيئة النمو، تحظى بشعبية بسبب لحمها اللذيذ ومقاومتها. مثالي للتربية التقليدية.',
  'Sasso is a French slow-growing breed prized for its flavorful meat and hardiness. Ideal for traditional farming.',
  '/images/breeds/sasso.jpg', 'Hot/Arid', 4.50, 3.20, TRUE);

-- Sasso Feeding Programs
INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000001-0000-0000-0000-000000000001', 'STARTER', 0, 21, 'Démarr crumble 22% protéine', 35, 3, 'Aliment de démarrage haute énergie', 0),
('a0000001-0000-0000-0000-000000000001', 'GROWER', 22, 45, 'Croissance granulés 20% protéine', 90, 2, 'Phase de croissance rapide', 1),
('a0000001-0000-0000-0000-000000000001', 'FINISHER', 46, 84, 'Finition granulés 18% protéine', 120, 2, 'Dernière phase avant abattage', 2);

-- Sasso Vaccination Schedule
INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000001-0000-0000-0000-000000000001', 'Newcastle (B1/La Sota)', 7, '1 dose', 'EYE_DROP', TRUE, 'Première vaccination Newcastle', 0),
('a0000001-0000-0000-0000-000000000001', 'Newcastle (La Sota)', 21, '1 dose', 'DRINKING_WATER', TRUE, 'Rappel Newcastle', 1),
('a0000001-0000-0000-0000-000000000001', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, 'Vaccination Gumboro', 2),
('a0000001-0000-0000-0000-000000000001', 'Bronchite Infectieuse', 10, '1 dose', 'EYE_DROP', TRUE, 'Vaccination IB', 3),
('a0000001-0000-0000-0000-000000000001', 'Variole', 21, '1 dose', 'INJECTION', TRUE, 'Vaccination variole animale', 4),
('a0000001-0000-0000-0000-000000000001', 'Mycoplasme', 7, '0.2 ml', 'INJECTION', FALSE, 'Optionnel selon région', 5);

-- Sasso Production Benchmarks
INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000001-0000-0000-0000-000000000001', 'DAILY_WEIGHT_GAIN', 35.0000, 'g/day', 0, 84, 'Gain moyen quotidien', 0),
('a0000001-0000-0000-0000-000000000001', 'FEED_CONVERSION_RATIO', 2.10, 'ratio', 0, 84, 'Indice de consommation', 1),
('a0000001-0000-0000-0000-000000000001', 'MORTALITY_RATE', 5.00, '%', 0, 84, 'Taux de mortalité attendu', 2),
('a0000001-0000-0000-0000-000000000001', 'PEAK_PRODUCTION_AGE', 56.00, 'days', 0, 84, 'Âge optimal d''abattage', 3);

-- Sasso Housing Guidelines
INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000001-0000-0000-0000-000000000001', 'SPACE_PER_BIRD', '0.1', 'm²', 'ALL', 'Augmenter à 0.15 m² en climat chaud', 0),
('a0000001-0000-0000-0000-000000000001', 'TEMPERATURE_RANGE', '18-24', '°C', 'ALL', 'Température idéale', 1),
('a0000001-0000-0000-0000-000000000001', 'HUMIDITY_RANGE', '50-70', '%', 'ALL', 'Humidité relative', 2),
('a0000001-0000-0000-0000-000000000001', 'LIGHTING_SCHEDULE', '18-20', 'hours', 'ALL', 'Durée d''éclairage quotidien', 3),
('a0000001-0000-0000-0000-000000000001', 'VENTILATION_RATE', '4-6', 'm³/h/kg', 'ALL', 'Débit de ventilation minimal', 4);

-- ============================================================
-- 2. Isa Brown (French layer breed)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000002-0000-0000-0000-000000000002', 'Isa Brown', 'آيزا براون', 'Isa Brown', 'COMMERCIAL', 'LAYERS', 'France',
  'Isa Brown est une race française hybride, excellente pondeuse avec 300 à 320 œufs par an. Rustique et facile à élever.',
  'آيزا براون هي سلالة فرنسية هجينة، ممتازة في وضع 300 إلى 320 بيضة في السنة. سهلة التربية.',
  'Isa Brown is a French hybrid breed, excellent layer with 300-320 eggs per year. Hardy and easy to raise.',
  '/images/breeds/isa-brown.jpg', 'Temperate', 2.80, 2.20, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000002-0000-0000-0000-000000000002', 'STARTER', 0, 42, 'Démarr ponte 20% protéine', 30, 3, 'Aliment démarrage pour futures pondeuses', 0),
('a0000002-0000-0000-0000-000000000002', 'GROWER', 43, 112, 'Croissance 17% protéine', 80, 2, 'Phase croissance', 1),
('a0000002-0000-0000-0000-000000000002', 'FINISHER', 113, 9999, 'Pondaison 16% protéine', 110, 2, 'Aliment ponte', 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000002-0000-0000-0000-000000000002', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, 'Première dose', 0),
('a0000002-0000-0000-0000-000000000002', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, 'Immunité de base', 1),
('a0000002-0000-0000-0000-000000000002', 'Bronchite Infectieuse', 21, '1 dose', 'EYE_DROP', TRUE, 'IB primodose', 2),
('a0000002-0000-0000-0000-000000000002', 'Newcastle', 35, '1 dose', 'DRINKING_WATER', TRUE, 'Rappel', 3),
('a0000002-0000-0000-0000-000000000002', 'Variole', 56, '1 dose', 'INJECTION', TRUE, 'Prévention variole', 4),
('a0000002-0000-0000-0000-000000000002', 'Encephalomyélite', 70, '1 dose', 'DRINKING_WATER', TRUE, 'AE vacciation', 5);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000002-0000-0000-0000-000000000002', 'EGG_PRODUCTION_RATE', 85.00, '%', 140, 500, 'Pic de ponte', 0),
('a0000002-0000-0000-0000-000000000002', 'DAILY_WEIGHT_GAIN', 15.0000, 'g/day', 0, 140, 'GMQ', 1),
('a0000002-0000-0000-0000-000000000002', 'FEED_CONVERSION_RATIO', 2.20, 'ratio', 0, 140, 'IC ponte', 2),
('a0000002-0000-0000-0000-000000000002', 'PEAK_PRODUCTION_AGE', 30.00, 'weeks', 0, 500, 'Début ponte', 3);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000002-0000-0000-0000-000000000002', 'SPACE_PER_BIRD', '0.15', 'm²', 'ALL', 'Espace par ponteuse', 0),
('a0000002-0000-0000-0000-000000000002', 'TEMPERATURE_RANGE', '16-22', '°C', 'ALL', 'Température optimale ponte', 1),
('a0000002-0000-0000-0000-000000000002', 'LIGHTING_SCHEDULE', '16', 'hours', 'ALL', 'Éclairage ponte', 2),
('a0000002-0000-0000-0000-000000000002', 'NEST_BOX_RATIO', '1:4', 'birds/nest', 'ALL', 'Ratio pondoir', 3),
('a0000002-0000-0000-0000-000000000002', 'HUMIDITY_RANGE', '55-65', '%', 'ALL', 'Humidité coop', 4);

-- ============================================================
-- 3. Cobb 500 (US broiler)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000003-0000-0000-0000-000000000003', 'Cobb 500', 'كوب 500', 'Cobb 500', 'COMMERCIAL', 'BROILERS', 'USA',
  'Cobb 500 est la race de poulet de chair la plus utilisée au monde. Croissance ultra-rapide, excellent IC.',
  'كوب 500 هو أكثر سلالة دجاج لحم استخدامًا في العالم. نمو سريع جدًا، ومؤشر استهلاك ممتاز.',
  'Cobb 500 is the most used broiler breed worldwide. Ultra-fast growth, excellent FCR.',
  '/images/breeds/cobb500.jpg', 'Temperate', 5.50, 4.20, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000003-0000-0000-0000-000000000003', 'STARTER', 0, 10, 'Pre-starter 23% protéine', 45, 4, 'Aliment prémium premier âge', 0),
('a0000003-0000-0000-0000-000000000003', 'STARTER', 11, 21, 'Démarr 22% protéine', 75, 3, 'Démarrage classique', 1),
('a0000003-0000-0000-0000-000000000003', 'GROWER', 22, 35, 'Croissance 21% protéine', 130, 2, 'Croissance rapide', 2),
('a0000003-0000-0000-0000-000000000003', 'FINISHER', 36, 42, 'Finition 19% protéine', 160, 2, 'Phase finale', 3);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000003-0000-0000-0000-000000000003', 'Newcastle + Bronchite', 1, '1 dose', 'EYE_DROP', TRUE, 'Jour 1', 0),
('a0000003-0000-0000-0000-000000000003', 'Gumboro', 7, '1 dose', 'DRINKING_WATER', TRUE, 'Gumboro mild', 1),
('a0000003-0000-0000-0000-000000000003', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, 'Rappel', 2),
('a0000003-0000-0000-0000-000000000003', 'Newcastle', 18, '1 dose', 'DRINKING_WATER', TRUE, 'Rappel', 3),
('a0000003-0000-0000-0000-000000000003', 'Syndrome BW', 21, '1 dose', 'INJECTION', FALSE, 'Optionnel', 4);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000003-0000-0000-0000-000000000003', 'DAILY_WEIGHT_GAIN', 55.0000, 'g/day', 0, 42, 'Croissance exceptionnelle', 0),
('a0000003-0000-0000-0000-000000000003', 'FEED_CONVERSION_RATIO', 1.65, 'ratio', 0, 42, 'IC excellent', 1),
('a0000003-0000-0000-0000-000000000003', 'MORTALITY_RATE', 3.50, '%', 0, 42, 'Faible mortalité', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000003-0000-0000-0000-000000000003', 'SPACE_PER_BIRD', '0.08', 'm²', 'STARTER', 'Espace démarrage', 0),
('a0000003-0000-0000-0000-000000000003', 'SPACE_PER_BIRD', '0.12', 'm²', 'FINISHER', 'Espace finition', 1),
('a0000003-0000-0000-0000-000000000003', 'TEMPERATURE_RANGE', '20-28', '°C', 'ALL', 'Température élevée', 2),
('a0000003-0000-0000-0000-000000000003', 'VENTILATION_RATE', '5-8', 'm³/h/kg', 'ALL', 'Ventilation importante', 3);

-- ============================================================
-- 4. Ross 308 (European broiler)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000004-0000-0000-0000-000000000004', 'Ross 308', 'روس 308', 'Ross 308', 'COMMERCIAL', 'BROILERS', 'UK',
  'Ross 308 est une race britannique высокопродуктивная. Croissance rapide et bonne conformation carcassique.',
  'روس 308 هي سلالة بريطانية عالية الإنتاج. نمو سريع وجيدة تشكيل الذبيحة.',
  'Ross 308 is a British high-producing breed. Fast growth and good carcass conformation.',
  '/images/breeds/ross308.jpg', 'Temperate', 5.20, 4.00, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000004-0000-0000-0000-000000000004', 'STARTER', 0, 10, 'Pre-starter 23% protéine', 40, 4, NULL, 0),
('a0000004-0000-0000-0000-000000000004', 'GROWER', 11, 24, 'Croissance 21% protéine', 110, 3, NULL, 1),
('a0000004-0000-0000-0000-000000000004', 'FINISHER', 25, 40, 'Finition 19% protéine', 145, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000004-0000-0000-0000-000000000004', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000004-0000-0000-0000-000000000004', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000004-0000-0000-0000-000000000004', 'Bronchite', 21, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000004-0000-0000-0000-000000000004', 'Variole', 28, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000004-0000-0000-0000-000000000004', 'DAILY_WEIGHT_GAIN', 52.0000, 'g/day', 0, 40, NULL, 0),
('a0000004-0000-0000-0000-000000000004', 'FEED_CONVERSION_RATIO', 1.70, 'ratio', 0, 40, NULL, 1),
('a0000004-0000-0000-0000-000000000004', 'MORTALITY_RATE', 4.00, '%', 0, 40, NULL, 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000004-0000-0000-0000-000000000004', 'SPACE_PER_BIRD', '0.10', 'm²', 'ALL', NULL, 0),
('a0000004-0000-0000-0000-000000000004', 'TEMPERATURE_RANGE', '18-24', '°C', 'ALL', NULL, 1),
('a0000004-0000-0000-0000-000000000004', 'LIGHTING_SCHEDULE', '23', 'hours', 'ALL', NULL, 2),
('a0000004-0000-0000-0000-000000000004', 'HUMIDITY_RANGE', '50-70', '%', 'ALL', NULL, 3);

-- ============================================================
-- 5. Lohmann Brown (German layer)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000005-0000-0000-0000-000000000005', 'Lohmann Brown', 'لومان براون', 'Lohmann Brown', 'COMMERCIAL', 'LAYERS', 'Germany',
  'Lohmann Brown est une race allemande élite. 320-330 œufs par an, excellente qualité de coquille.',
  'لومان براون هي سلالة ألمانية متفوقة. 320-330 بيضة في السنة، جودة قشرة ممتازة.',
  'Lohmann Brown is an elite German breed. 320-330 eggs per year, excellent shell quality.',
  '/images/breeds/lohmann.jpg', 'Temperate', 3.00, 2.30, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000005-0000-0000-0000-000000000005', 'STARTER', 0, 35, 'Démarr ponte 19% protéine', 35, 3, NULL, 0),
('a0000005-0000-0000-0000-000000000005', 'GROWER', 36, 105, 'Croissance 16% protéine', 75, 2, NULL, 1),
('a0000005-0000-0000-0000-000000000005', 'FINISHER', 106, 9999, 'Pondaison 17% protéine', 115, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000005-0000-0000-0000-000000000005', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000005-0000-0000-0000-000000000005', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000005-0000-0000-0000-000000000005', 'Bronchite', 28, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000005-0000-0000-0000-000000000005', 'Variole', 42, '1 dose', 'INJECTION', TRUE, NULL, 3),
('a0000005-0000-0000-0000-000000000005', 'Mycoplasmose', 56, '0.3 ml', 'INJECTION', FALSE, NULL, 4);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000005-0000-0000-0000-000000000005', 'EGG_PRODUCTION_RATE', 90.00, '%', 150, 450, 'Pic ponte', 0),
('a0000005-0000-0000-0000-000000000005', 'EGG_PRODUCTION_RATE', 320.00, 'eggs/year', 0, 450, 'Production annuelle', 1),
('a0000005-0000-0000-0000-000000000005', 'FEED_CONVERSION_RATIO', 2.10, 'ratio', 0, 450, 'IC ponte', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000005-0000-0000-0000-000000000005', 'SPACE_PER_BIRD', '0.14', 'm²', 'ALL', NULL, 0),
('a0000005-0000-0000-0000-000000000005', 'TEMPERATURE_RANGE', '15-20', '°C', 'ALL', NULL, 1),
('a0000005-0000-0000-0000-000000000005', 'LIGHTING_SCHEDULE', '16-17', 'hours', 'ALL', NULL, 2),
('a0000005-0000-0000-0000-000000000005', 'HUMIDITY_RANGE', '55-65', '%', 'ALL', NULL, 3),
('a0000005-0000-0000-0000-000000000005', 'NEST_BOX_RATIO', '1:5', 'birds/nest', 'ALL', NULL, 4);

-- ============================================================
-- 6. Hubbard (French broiler)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000006-0000-0000-0000-000000000006', 'Hubbard', 'هوبارد', 'Hubbard', 'COMMERCIAL', 'BROILERS', 'France',
  'Hubbard est une race française polyvalente. Excellente conversion alimentaire et chair de qualité.',
  'هوبارد هي سلالة فرنسية متعددة الاستخدامات. تحويل غذائي ممتاز ولحم عالي الجودة.',
  'Hubbard is a versatile French breed. Excellent feed conversion and quality meat.',
  '/images/breeds/hubbard.jpg', 'Temperate', 4.80, 3.60, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000006-0000-0000-0000-000000000006', 'STARTER', 0, 18, 'Démarr 22% protéine', 50, 3, NULL, 0),
('a0000006-0000-0000-0000-000000000006', 'GROWER', 19, 35, 'Croissance 20% protéine', 110, 2, NULL, 1),
('a0000006-0000-0000-0000-000000000006', 'FINISHER', 36, 56, 'Finition 18% protéine', 135, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000006-0000-0000-0000-000000000006', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000006-0000-0000-0000-000000000006', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000006-0000-0000-0000-000000000006', 'Bronchite', 21, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000006-0000-0000-0000-000000000006', 'Variole', 28, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000006-0000-0000-0000-000000000006', 'DAILY_WEIGHT_GAIN', 48.0000, 'g/day', 0, 56, NULL, 0),
('a0000006-0000-0000-0000-000000000006', 'FEED_CONVERSION_RATIO', 1.80, 'ratio', 0, 56, NULL, 1),
('a0000006-0000-0000-0000-000000000006', 'MORTALITY_RATE', 4.50, '%', 0, 56, NULL, 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000006-0000-0000-0000-000000000006', 'SPACE_PER_BIRD', '0.10', 'm²', 'ALL', NULL, 0),
('a0000006-0000-0000-0000-000000000006', 'TEMPERATURE_RANGE', '18-24', '°C', 'ALL', NULL, 1),
('a0000006-0000-0000-0000-000000000006', 'VENTILATION_RATE', '4-6', 'm³/h/kg', 'ALL', NULL, 2),
('a0000006-0000-0000-0000-000000000006', 'LIGHTING_SCHEDULE', '20', 'hours', 'ALL', NULL, 3);

-- ============================================================
-- 7. Arbor Acres (US broiler)
-- ============================================================
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000007-0000-0000-0000-000000000007', 'Arbor Acres', 'أربور أكيرز', 'Arbor Acres', 'COMMERCIAL', 'BROILERS', 'USA',
  'Arbor Acres est une race américaine très productive. Croissance rapide et rendements élevés.',
  'أربور أكيرز هي سلالة أمريكية عالية الإنتاج. نمو سريع وعوائد مرتفعة.',
  'Arbor Acres is a highly productive American breed. Fast growth and high yields.',
  '/images/breeds/arbor-acres.jpg', 'Hot/Arid', 5.30, 4.10, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000007-0000-0000-0000-000000000007', 'STARTER', 0, 10, 'Pre-starter 23% protéine', 45, 4, NULL, 0),
('a0000007-0000-0000-0000-000000000007', 'GROWER', 11, 28, 'Croissance 21% protéine', 120, 2, NULL, 1),
('a0000007-0000-0000-0000-000000000007', 'FINISHER', 29, 42, 'Finition 19% protéine', 155, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000007-0000-0000-0000-000000000007', 'Newcastle + IB', 1, '1 dose', 'EYE_DROP', TRUE, 'Jour 1', 0),
('a0000007-0000-0000-0000-000000000007', 'Gumboro', 7, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000007-0000-0000-0000-000000000007', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, 'Rappel', 2),
('a0000007-0000-0000-0000-000000000007', 'Newcastle', 21, '1 dose', 'DRINKING_WATER', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000007-0000-0000-0000-000000000007', 'DAILY_WEIGHT_GAIN', 53.0000, 'g/day', 0, 42, NULL, 0),
('a0000007-0000-0000-0000-000000000007', 'FEED_CONVERSION_RATIO', 1.68, 'ratio', 0, 42, NULL, 1),
('a0000007-0000-0000-0000-000000000007', 'MORTALITY_RATE', 3.80, '%', 0, 42, NULL, 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000007-0000-0000-0000-000000000007', 'SPACE_PER_BIRD', '0.09', 'm²', 'ALL', NULL, 0),
('a0000007-0000-0000-0000-000000000007', 'TEMPERATURE_RANGE', '20-26', '°C', 'ALL', 'Adapté climat chaud', 1),
('a0000007-0000-0000-0000-000000000007', 'VENTILATION_RATE', '5-7', 'm³/h/kg', 'ALL', NULL, 2),
('a0000007-0000-0000-0000-000000000007', 'LIGHTING_SCHEDULE', '22', 'hours', 'ALL', NULL, 3);

-- ============================================================
-- HERITAGE BREEDS
-- ============================================================

-- 8. Beldi (Moroccan heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000008-0000-0000-0000-000000000008', 'Beldi', 'بلدي', 'Beldi', 'HERITAGE', 'DUAL_PURPOSE', 'Morocco',
  'Beldi est la race locale traditionnelle du Maroc. Rustique, bien adaptée au climat marocain, chair savoureuse.',
  'بلدي هي السلالة المحلية التقليدية في المغرب. مقاومة ومناسبة للمناخ المغربي، لحم لذيذ.',
  'Beldi is the traditional local breed of Morocco. Hardy, well-adapted to Moroccan climate, flavorful meat.',
  '/images/breeds/beldi.jpg', 'Hot/Arid', 2.50, 1.80, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000008-0000-0000-0000-000000000008', 'STARTER', 0, 30, 'Démarr naturel 18%', 25, 2, 'Peut utiliser grains entiers', 0),
('a0000008-0000-0000-0000-000000000008', 'GROWER', 31, 90, 'Croissance 16%', 60, 2, 'Mélange grains + verts', 1),
('a0000008-0000-0000-0000-000000000008', 'FINISHER', 91, 9999, 'Maintenance 14%', 80, 2, 'Pondi+viande', 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000008-0000-0000-0000-000000000008', 'Newcastle', 14, '1 dose', 'EYE_DROP', TRUE, 'Essentiel', 0),
('a0000008-0000-0000-0000-000000000008', 'Gumboro', 21, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000008-0000-0000-0000-000000000008', 'Variole', 45, '1 dose', 'INJECTION', FALSE, 'Recommandé', 2),
('a0000008-0000-0000-0000-000000000008', 'Choléra', 60, '1 dose', 'INJECTION', FALSE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000008-0000-0000-0000-000000000008', 'EGG_PRODUCTION_RATE', 120.00, 'eggs/year', 180, 9999, 'Ponte modérée', 0),
('a0000008-0000-0000-0000-000000000008', 'DAILY_WEIGHT_GAIN', 18.0000, 'g/day', 0, 180, 'Croissance lente', 1),
('a0000008-0000-0000-0000-000000000008', 'MORTALITY_RATE', 8.00, '%', 0, 180, 'Très rustique', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000008-0000-0000-0000-000000000008', 'SPACE_PER_BIRD', '0.20', 'm²', 'ALL', 'Espace généreux', 0),
('a0000008-0000-0000-0000-000000000008', 'TEMPERATURE_RANGE', '10-35', '°C', 'ALL', 'Très tolérant', 1),
('a0000008-0000-0000-0000-000000000008', 'ACCESS_RANGE', 'Free-range', 'hours', 'ALL', 'Élevage extensif', 2),
('a0000008-0000-0000-0000-000000000008', 'HUMIDITY_RANGE', '30-80', '%', 'ALL', 'Tolérant', 3);

-- 9. Fayoumi (Egyptian heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000009-0000-0000-0000-000000000009', 'Fayoumi', 'فيومي', 'Fayoumi', 'HERITAGE', 'LAYERS', 'Egypt',
  'Fayoumi est une race égyptienne ancienne. Excellente pondeuse en climat chaud, très active.',
  'فيومي هي سلالة مصرية قديمة. ممتازة في البيض في المناخ الحار، نشطة جداً.',
  'Fayoumi is an ancient Egyptian breed. Excellent layer in hot climate, very active.',
  '/images/breeds/fayoumi.jpg', 'Hot/Arid', 2.20, 1.60, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000009-0000-0000-0000-000000000009', 'STARTER', 0, 28, 'Démarr 19% protéine', 25, 3, NULL, 0),
('a0000009-0000-0000-0000-000000000009', 'GROWER', 29, 90, 'Croissance 16%', 55, 2, NULL, 1),
('a0000009-0000-0000-0000-000000000009', 'FINISHER', 91, 9999, 'Pondaison 17%', 70, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000009-0000-0000-0000-000000000009', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000009-0000-0000-0000-000000000009', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000009-0000-0000-0000-000000000009', 'Variole', 35, '1 dose', 'INJECTION', FALSE, NULL, 2),
('a0000009-0000-0000-0000-000000000009', 'Mycoplasme', 21, '0.2 ml', 'INJECTION', FALSE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000009-0000-0000-0000-000000000009', 'EGG_PRODUCTION_RATE', 180.00, 'eggs/year', 150, 9999, 'Bonne ponte', 0),
('a0000009-0000-0000-0000-000000000009', 'DAILY_WEIGHT_GAIN', 16.0000, 'g/day', 0, 150, NULL, 1),
('a0000009-0000-0000-0000-000000000009', 'FEED_CONVERSION_RATIO', 2.80, 'ratio', 0, 150, 'IC économique', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000009-0000-0000-0000-000000000009', 'SPACE_PER_BIRD', '0.15', 'm²', 'ALL', NULL, 0),
('a0000009-0000-0000-0000-000000000009', 'TEMPERATURE_RANGE', '15-38', '°C', 'ALL', 'Tolérance chaleur', 1),
('a0000009-0000-0000-0000-000000000009', 'ACCESS_RANGE', 'Free-range', 'hours', 'ALL', 'Aime vaguer', 2),
('a0000009-0000-0000-0000-000000000009', 'VENTILATION_RATE', '6-8', 'm³/h/kg', 'ALL', 'Bien ventilé', 3);

-- 10. Plymouth Rock (American heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000010-0000-0000-0000-000000000010', 'Plymouth Rock', 'بlymouth روك', 'Plymouth Rock', 'HERITAGE', 'DUAL_PURPOSE', 'USA',
  'Plymouth Rock est une race américaine classique. Bonne ponte et chair savoureuse, rustique.',
  'بlymouth روك هي سلالة أمريكية كلاسيكية. جيدة في البيض ولحم لذيذ، ومقاومة.',
  'Plymouth Rock is a classic American breed. Good layer and flavorful meat, hardy.',
  '/images/breeds/plymouth-rock.jpg', 'Temperate', 3.50, 2.70, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000010-0000-0000-0000-000000000010', 'STARTER', 0, 35, 'Démarr 19% protéine', 30, 3, NULL, 0),
('a0000010-0000-0000-0000-000000000010', 'GROWER', 36, 100, 'Croissance 16%', 70, 2, NULL, 1),
('a0000010-0000-0000-0000-000000000010', 'FINISHER', 101, 9999, 'Maintenance 15%', 90, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000010-0000-0000-0000-000000000010', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000010-0000-0000-0000-000000000010', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000010-0000-0000-0000-000000000010', 'Bronchite', 28, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000010-0000-0000-0000-000000000010', 'Variole', 42, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000010-0000-0000-0000-000000000010', 'EGG_PRODUCTION_RATE', 200.00, 'eggs/year', 160, 9999, 'Bonne ponte', 0),
('a0000010-0000-0000-0000-000000000010', 'DAILY_WEIGHT_GAIN', 20.0000, 'g/day', 0, 160, NULL, 1),
('a0000010-0000-0000-0000-000000000010', 'MORTALITY_RATE', 6.00, '%', 0, 160, NULL, 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000010-0000-0000-0000-000000000010', 'SPACE_PER_BIRD', '0.15', 'm²', 'ALL', NULL, 0),
('a0000010-0000-0000-0000-000000000010', 'TEMPERATURE_RANGE', '10-28', '°C', 'ALL', NULL, 1),
('a0000010-0000-0000-0000-000000000010', 'ACCESS_RANGE', 'Partial', 'hours', 'ALL', 'Apprécie accès extérieur', 2),
('a0000010-0000-0000-0000-000000000010', 'LIGHTING_SCHEDULE', '15', 'hours', 'ALL', NULL, 3);

-- 11. Rhode Island Red (American heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000011-0000-0000-0000-000000000011', 'Rhode Island Red', 'رود آيلاند ريد', 'Rhode Island Red', 'HERITAGE', 'DUAL_PURPOSE', 'USA',
  'Rhode Island Red est une race américaine emblématique. Excellente pondeuse et bonne chair, très robuste.',
  'رود آيلاند ريد هي سلالة أمريكية أيقونية. ممتازة في وضع البيض ولحم جيد، قوية جداً.',
  'Rhode Island Red is an iconic American breed. Excellent layer and good meat, very robust.',
  '/images/breeds/rhode-island-red.jpg', 'Temperate', 3.80, 2.80, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000011-0000-0000-0000-000000000011', 'STARTER', 0, 42, 'Démarr 19% protéine', 32, 3, NULL, 0),
('a0000011-0000-0000-0000-000000000011', 'GROWER', 43, 112, 'Croissance 16%', 75, 2, NULL, 1),
('a0000011-0000-0000-0000-000000000011', 'FINISHER', 113, 9999, 'Pondaison 17%', 100, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000011-0000-0000-0000-000000000011', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000011-0000-0000-0000-000000000011', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000011-0000-0000-0000-000000000011', 'Bronchite', 21, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000011-0000-0000-0000-000000000011', 'Variole', 56, '1 dose', 'INJECTION', TRUE, NULL, 3),
('a0000011-0000-0000-0000-000000000011', 'Laryngotrachéite', 70, '1 dose', 'EYE_DROP', FALSE, NULL, 4);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000011-0000-0000-0000-000000000011', 'EGG_PRODUCTION_RATE', 250.00, 'eggs/year', 150, 9999, 'Excellente ponte', 0),
('a0000011-0000-0000-0000-000000000011', 'DAILY_WEIGHT_GAIN', 22.0000, 'g/day', 0, 150, NULL, 1),
('a0000011-0000-0000-0000-000000000011', 'FEED_CONVERSION_RATIO', 2.30, 'ratio', 0, 150, NULL, 2),
('a0000011-0000-0000-0000-000000000011', 'EGG_WEIGHT', '60.00', 'grams', 0, 9999, 'Poids oeuf', 3);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000011-0000-0000-0000-000000000011', 'SPACE_PER_BIRD', '0.14', 'm²', 'ALL', NULL, 0),
('a0000011-0000-0000-0000-000000000011', 'TEMPERATURE_RANGE', '12-26', '°C', 'ALL', NULL, 1),
('a0000011-0000-0000-0000-000000000011', 'HUMIDITY_RANGE', '50-70', '%', 'ALL', NULL, 2),
('a0000011-0000-0000-0000-000000000011', 'NEST_BOX_RATIO', '1:4', 'birds/nest', 'ALL', NULL, 3),
('a0000011-0000-0000-0000-000000000011', 'LIGHTING_SCHEDULE', '15-16', 'hours', 'ALL', NULL, 4);

-- 12. Leghorn (Italian heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000012-0000-0000-0000-000000000012', 'Leghorn', 'ليغورن', 'Leghorn', 'HERITAGE', 'LAYERS', 'Italy',
  'Leghorn est une race italienne légendaire. Record de ponte, jusqu''à 300 œufs blancs par an.',
  'ليغورن هي سلالة إيطالية أسطورية. رقم قياسي في البيض، حتى 300 بيضة بيضاء في السنة.',
  'Leghorn is a legendary Italian breed. Laying record, up to 300 white eggs per year.',
  '/images/breeds/leghorn.jpg', 'Temperate', 2.70, 2.00, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000012-0000-0000-0000-000000000012', 'STARTER', 0, 42, 'Démarr 19% protéine', 28, 3, NULL, 0),
('a0000012-0000-0000-0000-000000000012', 'GROWER', 43, 120, 'Croissance 16%', 65, 2, NULL, 1),
('a0000012-0000-0000-0000-000000000012', 'FINISHER', 121, 9999, 'Pondaison 17%', 95, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000012-0000-0000-0000-000000000012', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000012-0000-0000-0000-000000000012', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000012-0000-0000-0000-000000000012', 'Bronchite', 28, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000012-0000-0000-0000-000000000012', 'Variole', 42, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000012-0000-0000-0000-000000000012', 'EGG_PRODUCTION_RATE', 300.00, 'eggs/year', 150, 9999, 'Excellent record', 0),
('a0000012-0000-0000-0000-000000000012', 'EGG_WEIGHT', 55.00, 'grams', 0, 9999, 'Oeufs blancs', 1),
('a0000012-0000-0000-0000-000000000012', 'FEED_CONVERSION_RATIO', 2.00, 'ratio', 0, 9999, 'IC ponte', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000012-0000-0000-0000-000000000012', 'SPACE_PER_BIRD', '0.12', 'm²', 'ALL', NULL, 0),
('a0000012-0000-0000-0000-000000000012', 'TEMPERATURE_RANGE', '15-24', '°C', 'ALL', 'Sensible chaleur', 1),
('a0000012-0000-0000-0000-000000000012', 'LIGHTING_SCHEDULE', '16', 'hours', 'ALL', NULL, 2),
('a0000012-0000-0000-0000-000000000012', 'NEST_BOX_RATIO', '1:3', 'birds/nest', 'ALL', NULL, 3);

-- 13. Cornish Cross (UK heritage meat)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000013-0000-0000-0000-000000000013', 'Cornish Cross', 'كوورنيش كروس', 'Cornish Cross', 'HERITAGE', 'BROILERS', 'UK',
  'Cornish Cross est une race britannique emblématique. Chair exceptionnelle, croissance modérée.',
  'كوورنيش كروس هي سلالة بريطانية أيقونية. لحم استثنائي، نمو معتدل.',
  'Cornish Cross is an iconic British breed. Exceptional meat, moderate growth.',
  '/images/breeds/cornish-cross.jpg', 'Temperate', 4.50, 3.50, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000013-0000-0000-0000-000000000013', 'STARTER', 0, 21, 'Démarr 22% protéine', 35, 4, NULL, 0),
('a0000013-0000-0000-0000-000000000013', 'GROWER', 22, 49, 'Croissance 20% protéine', 100, 3, NULL, 1),
('a0000013-0000-0000-0000-000000000013', 'FINISHER', 50, 84, 'Finition 18% protéine', 130, 2, 'Croissance lente qualité', 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000013-0000-0000-0000-000000000013', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000013-0000-0000-0000-000000000013', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000013-0000-0000-0000-000000000013', 'Bronchite', 21, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000013-0000-0000-0000-000000000013', 'Variole', 35, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000013-0000-0000-0000-000000000013', 'DAILY_WEIGHT_GAIN', 28.0000, 'g/day', 0, 84, 'Croissance lente qualité', 0),
('a0000013-0000-0000-0000-000000000013', 'FEED_CONVERSION_RATIO', 2.50, 'ratio', 0, 84, 'IC qualité', 1),
('a0000013-0000-0000-0000-000000000013', 'MORTALITY_RATE', 6.00, '%', 0, 84, NULL, 2),
('a0000013-0000-0000-0000-000000000013', 'BREAST_MEAT_YIELD', 30.00, '%', 0, 84, 'Rendement filet', 3);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000013-0000-0000-0000-000000000013', 'SPACE_PER_BIRD', '0.12', 'm²', 'ALL', 'Espace suffisant', 0),
('a0000013-0000-0000-0000-000000000013', 'TEMPERATURE_RANGE', '16-22', '°C', 'ALL', 'Froid sensible', 1),
('a0000013-0000-0000-0000-000000000013', 'LIGHTING_SCHEDULE', '18', 'hours', 'ALL', NULL, 2),
('a0000013-0000-0000-0000-000000000013', 'HUMIDITY_RANGE', '50-65', '%', 'ALL', 'Sec préfère', 3);

-- 14. Sussex (UK heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000014-0000-0000-0000-000000000014', 'Sussex', 'ساسكس', 'Sussex', 'HERITAGE', 'DUAL_PURPOSE', 'UK',
  'Sussex est une race britannique classique. Bonne ponte, chair excellente, aspect élégant.',
  'ساسكس هي سلالة كلاسيكية البريطانية. جيدة في البيض، لحم ممتاز، مظهر أنيق.',
  'Sussex is a classic British breed. Good layer, excellent meat, elegant appearance.',
  '/images/breeds/sussex.jpg', 'Temperate', 3.60, 2.70, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000014-0000-0000-0000-000000000014', 'STARTER', 0, 35, 'Démarr 19% protéine', 30, 3, NULL, 0),
('a0000014-0000-0000-0000-000000000014', 'GROWER', 36, 100, 'Croissance 16%', 70, 2, NULL, 1),
('a0000014-0000-0000-0000-000000000014', 'FINISHER', 101, 9999, 'Maintenance 15%', 90, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000014-0000-0000-0000-000000000014', 'Newcastle', 7, '1 dose', 'EYE_DROP', TRUE, NULL, 0),
('a0000014-0000-0000-0000-000000000014', 'Gumboro', 14, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000014-0000-0000-0000-000000000014', 'Bronchite', 28, '1 dose', 'EYE_DROP', TRUE, NULL, 2),
('a0000014-0000-0000-0000-000000000014', 'Variole', 42, '1 dose', 'INJECTION', TRUE, NULL, 3);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000014-0000-0000-0000-000000000014', 'EGG_PRODUCTION_RATE', 220.00, 'eggs/year', 150, 9999, 'Bonne ponte', 0),
('a0000014-0000-0000-0000-000000000014', 'DAILY_WEIGHT_GAIN', 22.0000, 'g/day', 0, 150, NULL, 1),
('a0000014-0000-0000-0000-000000000014', 'MORTALITY_RATE', 5.00, '%', 0, 150, NULL, 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000014-0000-0000-0000-000000000014', 'SPACE_PER_BIRD', '0.15', 'm²', 'ALL', NULL, 0),
('a0000014-0000-0000-0000-000000000014', 'TEMPERATURE_RANGE', '10-24', '°C', 'ALL', NULL, 1),
('a0000014-0000-0000-0000-000000000014', 'ACCESS_RANGE', 'Free-range', 'hours', 'ALL', 'Apprécie plein air', 2),
('a0000014-0000-0000-0000-000000000014', 'LIGHTING_SCHEDULE', '15', 'hours', 'ALL', NULL, 3);

-- 15. Kabyle (Algerian/North African heritage)
INSERT INTO breeds (id, name, name_ar, name_en, category, purpose, origin, description, description_ar, description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system)
VALUES ('a0000015-0000-0000-0000-000000000015', 'Kabyle', 'قبائلي', 'Kabyle', 'HERITAGE', 'DUAL_PURPOSE', 'Algeria',
  'Kabyle est une race kabyle (nord-africaine) rustique. Excellente adaptation aux montagnes, ponte correcte.',
  'قبائلي هي سلالة قبائلية (شمال أفريقية) مقاومة. تأقلم ممتاز مع الجبال، بيض جيد.',
  'Kabyle is a Kabyle (North African) hardy breed. Excellent mountain adaptation, decent egg production.',
  '/images/breeds/kabyle.jpg', 'Hot/Arid', 2.40, 1.70, TRUE);

INSERT INTO feeding_program_templates (breed_id, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order)
VALUES
('a0000015-0000-0000-0000-000000000015', 'STARTER', 0, 30, 'Démarr 18% protéine', 22, 2, 'Rustique sobre', 0),
('a0000015-0000-0000-0000-000000000015', 'GROWER', 31, 90, 'Croissance 15%', 55, 2, NULL, 1),
('a0000015-0000-0000-0000-000000000015', 'FINISHER', 91, 9999, 'Maintenance 14%', 70, 2, NULL, 2);

INSERT INTO vaccination_templates (breed_id, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order)
VALUES
('a0000015-0000-0000-0000-000000000015', 'Newcastle', 14, '1 dose', 'EYE_DROP', TRUE, 'Essentiel', 0),
('a0000015-0000-0000-0000-000000000015', 'Gumboro', 21, '1 dose', 'DRINKING_WATER', TRUE, NULL, 1),
('a0000015-0000-0000-0000-000000000015', 'Variole', 45, '1 dose', 'INJECTION', FALSE, NULL, 2);

INSERT INTO production_benchmarks (breed_id, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order)
VALUES
('a0000015-0000-0000-0000-000000000015', 'EGG_PRODUCTION_RATE', 150.00, 'eggs/year', 170, 9999, 'Ponte modérée', 0),
('a0000015-0000-0000-0000-000000000015', 'DAILY_WEIGHT_GAIN', 16.0000, 'g/day', 0, 170, NULL, 1),
('a0000015-0000-0000-0000-000000000015', 'MORTALITY_RATE', 10.00, '%', 0, 170, 'Très rustique', 2);

INSERT INTO housing_guidelines (breed_id, parameter_name, recommended_value, unit, growth_stage, notes, sort_order)
VALUES
('a0000015-0000-0000-0000-000000000015', 'SPACE_PER_BIRD', '0.20', 'm²', 'ALL', 'Espace libre', 0),
('a0000015-0000-0000-0000-000000000015', 'TEMPERATURE_RANGE', '5-40', '°C', 'ALL', 'Très tolérant', 1),
('a0000015-0000-0000-0000-000000000015', 'ACCESS_RANGE', 'Free-range', 'hours', 'ALL', 'Élevage extensif', 2),
('a0000015-0000-0000-0000-000000000015', 'HUMIDITY_RANGE', '20-85', '%', 'ALL', 'Très tolérant', 3);
