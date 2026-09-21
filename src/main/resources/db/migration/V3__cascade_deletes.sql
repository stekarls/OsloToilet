-- =========================================================
-- Align foreign keys with the JPA model.
-- Every association mapped with cascade = ALL and orphanRemoval = true
-- (User -> reviews/error_reports/location_requests, Toilet -> its children)
-- now cascades in the database as well, so deletes outside Hibernate
-- behave the same as deletes through the application.
--
-- The reference tables are deliberately left alone: removing a feature or a
-- payment option must not silently strip it from every toilet.
-- =========================================================

-- Reviews
alter table reviews
    drop constraint fk_reviews_toilet;
alter table reviews
    add constraint fk_reviews_toilet foreign key (toilet_id) references toilets on delete cascade;

alter table reviews
    drop constraint fk_reviews_user;
alter table reviews
    add constraint fk_reviews_user foreign key (user_id) references users on delete cascade;

-- Error reports
alter table error_reports
    drop constraint fk_error_reports_toilet;
alter table error_reports
    add constraint fk_error_reports_toilet foreign key (toilet_id) references toilets on delete cascade;

alter table error_reports
    drop constraint fk_error_reports_user;
alter table error_reports
    add constraint fk_error_reports_user foreign key (user_id) references users on delete cascade;

-- Location requests
alter table location_requests
    drop constraint fk_location_requests_user;
alter table location_requests
    add constraint fk_location_requests_user foreign key (user_id) references users on delete cascade;

-- Opening hours
alter table opening_hours
    drop constraint fk_opening_hours_toilet;
alter table opening_hours
    add constraint fk_opening_hours_toilet foreign key (toilet_id) references toilets on delete cascade;

-- Toilet relationships (join tables)
alter table toilet_has_features
    drop constraint fk_toilet_has_features_toilet;
alter table toilet_has_features
    add constraint fk_toilet_has_features_toilet foreign key (toilet_id) references toilets on delete cascade;

alter table toilet_has_payment_options
    drop constraint fk_toilet_payment_options_toilet;
alter table toilet_has_payment_options
    add constraint fk_toilet_payment_options_toilet foreign key (toilet_id) references toilets on delete cascade;
