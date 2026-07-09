-- V3_update_messages_table.sql

-- updated messages table

ALTER TABLE messages
ADD COLUMN ciphertext TEXT,
ADD COLUMN encryption_algorithm VARCHAR(100),
ADD COLUMN iv TEXT,
ADD COLUMN auth_tag TEXT;

UPDATE messages
SET ciphertext = body,
    encryption_algorithm = 'PLAINTEXT_DEV',
    iv = 'dev-placeholder'
WHERE ciphertext IS NULL;

ALTER TABLE messages
ALTER COLUMN ciphertext SET NOT NULL,
ALTER COLUMN encryption_algorithm SET NOT NULL,
ALTER COLUMN iv SET NOT NULL;

ALTER TABLE messages
ADD CONSTRAINT chk_messages_cipher_not_blank
CHECK (LENGTH(TRIM(ciphertext)) > 0);

ALTER TABLE messages
DROP CONSTRAINT chk_messages_body_not_blank;

ALTER TABLE messages
DROP COLUMN body;