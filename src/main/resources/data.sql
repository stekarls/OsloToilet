INSERT INTO features (id, feature_Code, description) VALUES
(gen_random_uuid(), 'WHEELCHAIR_ACCESSIBLE', 'Wheelchair accessible'),
(gen_random_uuid(), 'BABY_CARE', 'Baby care facilities'),
(gen_random_uuid(), 'SHOWERS', 'Shower facilities'),
(gen_random_uuid(), 'GENDER_NEUTRAL', 'Gender neutral toilet'),
(gen_random_uuid(), 'AUTOMATIC_DOOR', 'Door can open automatically')
ON CONFLICT (feature_code) DO NOTHING;

INSERT INTO payment_options(id, payment_option)
VALUES
    (gen_random_uuid(), 'VIPPS'),
    (gen_random_uuid(), 'CARD'),
    (gen_random_uuid(), 'CASH'),
    (gen_random_uuid(), 'CONTACTLESS')
ON CONFLICT (payment_option) DO NOTHING;


