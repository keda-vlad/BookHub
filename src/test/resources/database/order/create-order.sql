INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES
    (17, 'some_email@exam.com', 'Password', 'FirstName', 'SecondName', false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES
    (13, 'some_title', 'some_author', 'some-isbn', 99.99, false);
INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted) VALUES
    (1, 17, 'PENDING', 99.99, '2012-09-08', 'some-address', false);
INSERT INTO order_items (id, order_id, book_id, quantity, price, is_deleted) VALUES
    (1, 1, 13, 1, 99.99, false);