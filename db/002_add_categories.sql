-- Create Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Add Category to Posts
ALTER TABLE posts ADD COLUMN category_id BIGINT;
ALTER TABLE posts ADD CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id);
