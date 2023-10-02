INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES
    (17, 'some_email@exam.com', 'Password', 'FirstName', 'SecondName', false);
INSERT INTO books (id, title, author, isbn, price, is_deleted) VALUES
    (1, 'some_title', 'some_author', 'some_isbn', 99.99, false);
INSERT INTO shopping_carts (user_id, is_deleted) VALUES
    (17, false);
INSERT INTO cart_item (id, shopping_cart_id, book_id, quantity, is_deleted) VALUES
    (1, 17, 1, 1, false);
