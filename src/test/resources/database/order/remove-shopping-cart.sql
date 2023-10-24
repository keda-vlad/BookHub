DELETE FROM cart_item WHERE id = 1;
DELETE FROM shopping_carts WHERE user_id = 17;
DELETE FROM order_items WHERE order_id IN(SELECT id FROM orders WHERE user_id = 17);
DELETE FROM orders WHERE user_id = 17;
DELETE FROM users WHERE id = 17;
DELETE FROM books WHERE id = 1;
