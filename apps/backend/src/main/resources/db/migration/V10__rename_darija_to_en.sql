-- Rename Darija columns to En for all tables
ALTER TABLE alerts RENAME COLUMN msg_darija TO msg_en;
ALTER TABLE crops RENAME COLUMN name_darija TO name_en;
ALTER TABLE crops RENAME COLUMN description_darija TO description_en;
ALTER TABLE crop_growth_stages RENAME COLUMN name_darija TO name_en;
ALTER TABLE crop_growth_stages RENAME COLUMN description_darija TO description_en;
ALTER TABLE crop_nutrients RENAME COLUMN fertilizer_type_darija TO fertilizer_type_en;
ALTER TABLE crop_issues RENAME COLUMN name_darija TO name_en;
ALTER TABLE crop_issues RENAME COLUMN symptoms_darija TO symptoms_en;
ALTER TABLE crop_issues RENAME COLUMN treatment_darija TO treatment_en;
ALTER TABLE diagnoses RENAME COLUMN treatment_darija TO treatment_en;

-- Update User Language enum if it was a check constraint or just text
-- In V1, it was defined as VARCHAR(10) with possible values checked or just implicit.
-- Let's update any existing data
UPDATE users SET lang = 'EN' WHERE lang = 'DARIJA';
