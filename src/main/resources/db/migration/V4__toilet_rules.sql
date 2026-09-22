-- =========================================================
-- Toilet rules enforced by the database
-- =========================================================

-- Every toilet must have a description
alter table toilets
    alter column description set not null;

-- Toilet names are unique regardless of case, so "Oslo S" and "oslo s" cannot both exist.
-- This replaces the case-sensitive unique constraint from V1.
alter table toilets
    drop constraint toilets_name_key;
create unique index ux_toilets_name_lower on toilets (lower(name));

-- has_conditions is derived from conditions (a toilet has conditions when the text is present),
-- so it is no longer stored and can never disagree with the conditions text
alter table toilets
    drop column has_conditions;
