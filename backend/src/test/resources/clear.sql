DELETE FROM thumbnail;
DELETE FROM sourceCode;
DELETE FROM template_tag;
DELETE FROM tag;
DELETE FROM template;
DELETE FROM category;
DELETE FROM member;

ALTER TABLE thumbnail ALTER COLUMN id RESTART WITH 1;
ALTER TABLE sourceCode ALTER COLUMN id RESTART WITH 1;
ALTER TABLE template_tag ALTER COLUMN template_id RESTART WITH 1;
ALTER TABLE tag ALTER COLUMN id RESTART WITH 1;
ALTER TABLE template ALTER COLUMN id RESTART WITH 1;
ALTER TABLE category ALTER COLUMN id RESTART WITH 1;
ALTER TABLE member ALTER COLUMN id RESTART WITH 1;
