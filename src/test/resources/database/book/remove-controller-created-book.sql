DELETE FROM book_category bc WHERE book_id = (SELECT id FROM books b WHERE b.isbn = '9788845292613');
DELETE FROM books b WHERE b.isbn = '9788845292613';
