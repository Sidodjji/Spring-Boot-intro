insert into categories (id, name, description, is_deleted)
values (1, 'Horror', 'Horror category', false);

ALTER TABLE categories ALTER COLUMN id RESTART WITH 2;