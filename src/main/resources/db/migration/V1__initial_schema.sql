-- =========================================================
-- Reference tables
-- =========================================================

create table features (
      id uuid not null,
      feature_code varchar(32) not null unique
          check (feature_code in ('WHEELCHAIR_ACCESSIBLE','BABY_CARE','SHOWERS','GENDER_NEUTRAL','AUTOMATIC_DOOR')),
      description varchar(255) not null,
      primary key (id)
);

create table payment_options (
     id uuid not null,
     payment_option varchar(32) not null unique
         check (payment_option in ('VIPPS','CARD','CASH','CONTACTLESS')),
     primary key (id)
);

-- =========================================================
-- Users
-- =========================================================

create table users (
       id uuid not null,
       nickname varchar(12) not null unique,
       email varchar(255) not null unique,
       password varchar(255) not null,
       role varchar(20) not null
           check (role in ('USER','MODERATOR','ADMIN')),
       contribution_points bigint not null,
       banned boolean not null,
       created_at timestamp(6) with time zone not null,
       primary key (id)
);

-- =========================================================
-- Toilets
-- =========================================================

create table toilets (
     id uuid not null,
     name varchar(64) not null unique,
     latitude numeric(9,6) not null,
     longitude numeric(9,6) not null,
     has_fee boolean not null,
     fee numeric(10,2),
     description TEXT,
     has_conditions boolean not null,
     conditions TEXT,
     always_open boolean not null,
     is_seasonal boolean not null,
     is_closed boolean not null,
     added timestamp(6) with time zone not null,
     updated_at timestamp(6) with time zone not null,
     primary key (id)
);

-- =========================================================
-- Toilet relationships (join tables)
-- =========================================================

create table toilet_has_features (
         id uuid not null,
         toilet_id uuid not null,
         feature_id uuid not null,
         verified_at timestamp(6) with time zone,
         source varchar(255) not null
             check (source in ('USER_CONTRIBUTION','ADMIN_VERIFIED','OFFICIAL_DATA')),
         primary key (id),
         unique (toilet_id, feature_id)
);

create table toilet_has_payment_options (
        id uuid not null,
        toilet_id uuid not null,
        payment_option_id uuid not null,
        verified_at timestamp(6) with time zone,
        source varchar(32) not null
            check (source in ('USER_CONTRIBUTION','ADMIN_VERIFIED','OFFICIAL_DATA')),
        primary key (id),
        unique (toilet_id, payment_option_id)
);

create table opening_hours (
       id uuid not null,
       toilet_id uuid not null,
       day_of_week varchar(9) not null
           check (day_of_week in ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
       opening_time time(0) not null,
       closing_time time(0) not null,
       primary key (id),
       unique (toilet_id, day_of_week)
);

-- =========================================================
-- Reviews & reports
-- =========================================================

create table reviews (
     id uuid not null,
     toilet_id uuid not null,
     user_id uuid not null,
     rating_cleanliness smallint not null check (rating_cleanliness >= 1 and rating_cleanliness <= 5),
     rating_equipment smallint not null check (rating_equipment >= 1 and rating_equipment <= 5),
     rating_access smallint not null check (rating_access >= 1 and rating_access <= 5),
     average_rating float(53),
     comment TEXT,
     created_at timestamp(6) with time zone not null,
     primary key (id),
     unique (toilet_id, user_id)
);

create table error_reports (
       id uuid not null,
       toilet_id uuid not null,
       user_id uuid not null,
       description TEXT not null,
       request_status varchar(255) not null
           check (request_status in ('PENDING','UNDER_REVIEW','APPROVED','REJECTED','DUPLICATE','FIXED','UNSOLVABLE')),
       admin_comment varchar(255),
       created_at timestamp(6) with time zone not null,
       updated_at timestamp(6) with time zone not null,
       primary key (id)
);

create table location_requests (
   id uuid not null,
   user_id uuid not null,
   name varchar(64) not null,
   latitude numeric(9,6) not null,
   longitude numeric(9,6) not null,
   has_fee boolean not null,
   fee numeric(10,2),
   description TEXT not null,
   request_status varchar(255) not null
       check (request_status in ('PENDING','UNDER_REVIEW','APPROVED','REJECTED','DUPLICATE','FIXED','UNSOLVABLE')),
   admin_comment varchar(255),
   created_at timestamp(6) with time zone not null,
   updated_at timestamp(6) with time zone not null,
   primary key (id)
);

-- =========================================================
-- Foreign keys
-- =========================================================

alter table toilet_has_features
    add constraint fk_toilet_has_features_toilet foreign key (toilet_id) references toilets;
alter table toilet_has_features
    add constraint fk_toilet_has_features_feature foreign key (feature_id) references features;

alter table toilet_has_payment_options
    add constraint fk_toilet_payment_options_toilet foreign key (toilet_id) references toilets;
alter table toilet_has_payment_options
    add constraint fk_toilet_payment_options_payment_option foreign key (payment_option_id) references payment_options;

alter table opening_hours
    add constraint fk_opening_hours_toilet foreign key (toilet_id) references toilets;

alter table reviews
    add constraint fk_reviews_toilet foreign key (toilet_id) references toilets;
alter table reviews
    add constraint fk_reviews_user foreign key (user_id) references users;

alter table error_reports
    add constraint fk_error_reports_toilet foreign key (toilet_id) references toilets;
alter table error_reports
    add constraint fk_error_reports_user foreign key (user_id) references users;

alter table location_requests
    add constraint fk_location_requests_user foreign key (user_id) references users;