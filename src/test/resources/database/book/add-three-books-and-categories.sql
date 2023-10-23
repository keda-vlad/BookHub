INSERT INTO categories (id, name, description, is_deleted) VALUES
(1, 'Fantasy Adventure', 'Fantasy Adventure', false),
(2, 'Philosophy', 'Philosophy', false),
(3, 'Social science fiction', 'Social science fiction', false);

INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES
(1, 'The Hobbit', 'J.R.R. Tolkien', 9780395647394, 20.22, false),
(2, 'Republic', 'Plato', 9788830104716, 21.99, false),
(3, '1984', 'George Orwell', 9780007123810, 19.55, false);


INSERT INTO book_category (book_id, category_id) VALUES (1, 1), (2, 2), (3, 3);
