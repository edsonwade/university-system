-- Add Degree table
CREATE TABLE IF NOT EXISTS degree (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    duration_years INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
-- Add degree_id to student
ALTER TABLE student
ADD COLUMN IF NOT EXISTS degree_id BIGINT REFERENCES degree(id);