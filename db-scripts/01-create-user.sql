CREATE USER 'ecommerceapp'@'localhost' IDENTIFIED BY 'ecommerceapp';

GRANT ALL PRIVILEGES ON * . * TO 'ecommerceapp'@'localhost';

ALTER USER 'ecommerceapp'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'ecommerceapp';