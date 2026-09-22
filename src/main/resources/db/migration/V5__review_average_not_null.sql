-- =========================================================
-- Review rules enforced by the database
-- =========================================================

-- The average rating is always calculated from the three required ratings when a review is created,
-- so a review can never be without one
alter table reviews
    alter column average_rating set not null;
