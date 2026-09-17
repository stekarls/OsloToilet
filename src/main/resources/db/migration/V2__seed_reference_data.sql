-- V2__seed_reference_data.sql

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Features
INSERT INTO features (id, feature_code, description) VALUES
         (gen_random_uuid(), 'WHEELCHAIR_ACCESSIBLE', 'Accessible for wheelchair users'),
         (gen_random_uuid(), 'BABY_CARE', 'Baby changing facilities available'),
         (gen_random_uuid(), 'SHOWERS', 'Shower facilities available'),
         (gen_random_uuid(), 'GENDER_NEUTRAL', 'Gender-neutral facilities'),
         (gen_random_uuid(), 'AUTOMATIC_DOOR', 'Automatic door entry');

-- Payment options
INSERT INTO payment_options (id, payment_option) VALUES
         (gen_random_uuid(), 'VIPPS'),
         (gen_random_uuid(), 'CARD'),
         (gen_random_uuid(), 'CASH'),
         (gen_random_uuid(), 'CONTACTLESS');