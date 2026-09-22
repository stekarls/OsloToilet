-- =========================================================
-- Where feature and payment option data comes from
-- =========================================================

-- The source now only says who added the data. Whether it is confirmed is tracked by verified_at,
-- so ADMIN_VERIFIED is renamed to ADMIN
alter table toilet_has_features
    drop constraint toilet_has_features_source_check;
alter table toilet_has_features
    add constraint toilet_has_features_source_check
        check (source in ('USER_CONTRIBUTION','ADMIN','OFFICIAL_DATA'));

alter table toilet_has_payment_options
    drop constraint toilet_has_payment_options_source_check;
alter table toilet_has_payment_options
    add constraint toilet_has_payment_options_source_check
        check (source in ('USER_CONTRIBUTION','ADMIN','OFFICIAL_DATA'));

-- =========================================================
-- Features and payment options a user selects when requesting a new toilet.
-- They become toilet_has_features / toilet_has_payment_options rows when the request is approved.
-- =========================================================

create table location_request_features (
       location_request_id uuid not null references location_requests on delete cascade,
       feature_id uuid not null references features,
       primary key (location_request_id, feature_id)
);

create table location_request_payment_options (
       location_request_id uuid not null references location_requests on delete cascade,
       payment_option_id uuid not null references payment_options,
       primary key (location_request_id, payment_option_id)
);
