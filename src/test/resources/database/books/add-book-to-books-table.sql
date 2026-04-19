insert into books (id, title, author, isbn, price, description, cover_image, is_deleted)
values (1, 'Book A', 'Author A', '23132143212', 55.15, 'Book', 'img.png', false);

insert into books (id, title, author, isbn, price, description, cover_image, is_deleted)
values (2, 'Book B', 'Author A', '23132143213', 45.15, 'Book', 'img.png', false);

ALTER TABLE books ALTER COLUMN id RESTART WITH 3;