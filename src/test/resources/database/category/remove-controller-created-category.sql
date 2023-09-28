DELETE FROM book_category WHERE category_id = (SELECT id FROM categories c WHERE c.name = 'test_name');
DELETE FROM categories c WHERE c.name = 'test_name';
